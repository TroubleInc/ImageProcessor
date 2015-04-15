// 
//	GifSequenceWriter.java
//	
//	Created by Elliot Kroo on 2009-04-25.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.


import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GifSequenceWriter {
	protected ImageWriter gifWriter;
	protected ImageWriteParam imageWriteParam;
	protected IIOMetadata imageMetaData;
	
	/**
	 * Creates a new GifSequenceWriter
	 * 
	 * @param outputStream the ImageOutputStream to be written to
	 * @param imageType one of the imageTypes specified in BufferedImage
	 * @param timeBetweenFramesMS the time between frames in miliseconds
	 * @param loopContinuously wether the gif should loop repeatedly
	 * @throws IIOException if no gif ImageWriters are found
	 *
	 * @author Elliot Kroo (elliot[at]kroo[dot]net)
	 */
	public GifSequenceWriter( ImageOutputStream outputStream, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IIOException, IOException {
		// my method to create a writer
		gifWriter = getWriter(); 
		imageWriteParam = gifWriter.getDefaultWriteParam();
		ImageTypeSpecifier imageTypeSpecifier =
			ImageTypeSpecifier.createFromBufferedImageType(imageType);

		imageMetaData =
			gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
			imageWriteParam);

		String metaFormatName = imageMetaData.getNativeMetadataFormatName();

		IIOMetadataNode root = (IIOMetadataNode)
			imageMetaData.getAsTree(metaFormatName);

		IIOMetadataNode graphicsControlExtensionNode = getNode(
			root,
			"GraphicControlExtension");

		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute(
			"transparentColorFlag",
			"FALSE");
		graphicsControlExtensionNode.setAttribute(
			"delayTime",
			Integer.toString(timeBetweenFramesMS / 10));
		graphicsControlExtensionNode.setAttribute(
			"transparentColorIndex",
			"0");

		IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
		commentsNode.setAttribute("CommentExtension", "Created by MAH");

		IIOMetadataNode appEntensionsNode = getNode(
			root,
			"ApplicationExtensions");

		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");

		int loop = loopContinuously ? 0 : 1;

		child.setUserObject(new byte[]{ 0x1, (byte) (loop & 0xFF), (byte)
			((loop >> 8) & 0xFF)});
		appEntensionsNode.appendChild(child);

		imageMetaData.setFromTree(metaFormatName, root);

		gifWriter.setOutput(outputStream);

		gifWriter.prepareWriteSequence(null);
	}
	
	public void writeToSequence(RenderedImage img) throws IOException {
		gifWriter.writeToSequence(
			new IIOImage(
				img,
				null,
				imageMetaData),
			imageWriteParam);
	}
	
	/**
	 * Close this GifSequenceWriter object. This does not close the underlying
	 * stream, just finishes off the GIF.
	 */
	public void close() throws IOException {
		gifWriter.endWriteSequence();		
	}

	/**
	 * Returns the first available GIF ImageWriter using 
	 * ImageIO.getImageWritersBySuffix("gif").
	 * 
	 * @return a GIF ImageWriter object
	 * @throws IIOException if no GIF image writers are returned
	 */
	private static ImageWriter getWriter() throws IIOException {
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
		if(!iter.hasNext()) {
			throw new IIOException("No GIF Image Writers Exist");
		} else {
			return iter.next();
		}
	}

	/**
	 * Returns an existing child node, or creates and returns a new child node (if 
	 * the requested node does not exist).
	 * 
	 * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
	 * @param nodeName the name of the child node.
	 * 
	 * @return the child node, if found or a new node created with the given name.
	 */
	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
		int nNodes = rootNode.getLength();
		for (int i = 0; i < nNodes; i++) {
			if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
					== 0) {
				return((IIOMetadataNode) rootNode.item(i));
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return(node);
	}
	
	private ImageFrame[] readGIF(ImageReader reader) throws IOException {
		ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);

		int width = -1;
		int height = -1;

		IIOMetadata metadata = reader.getStreamMetadata();
		if (metadata != null) {
			IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

			NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

			if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
				IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

				if (screenDescriptor != null) {
					width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
					height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
				}
			}
		}

		BufferedImage master = null;
		Graphics2D masterGraphics = null;

		for (int frameIndex = 0;; frameIndex++) {
			BufferedImage image;
			try {
				image = reader.read(frameIndex);
			} catch (IndexOutOfBoundsException io) {
				break;
			}

			if (width == -1 || height == -1) {
				width = image.getWidth();
				height = image.getHeight();
			}

			IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
			IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
			int delay = Integer.valueOf(gce.getAttribute("delayTime"));
			String disposal = gce.getAttribute("disposalMethod");

			int x = 0;
			int y = 0;

			if (master == null) {
				master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				masterGraphics = master.createGraphics();
				masterGraphics.setBackground(new Color(0, 0, 0, 0));
			} else {
				NodeList children = root.getChildNodes();
				for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
					Node nodeItem = children.item(nodeIndex);
					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						NamedNodeMap map = nodeItem.getAttributes();
						x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
						y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
					}
				}
			}
			masterGraphics.drawImage(image, x, y, null);

			BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
			frames.add(new ImageFrame(copy, delay, disposal));

			if (disposal.equals("restoreToPrevious")) {
				BufferedImage from = null;
				for (int i = frameIndex - 1; i >= 0; i--) {
					if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
						from = frames.get(i).getImage();
						break;
					}
				}

				master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
				masterGraphics = master.createGraphics();
				masterGraphics.setBackground(new Color(0, 0, 0, 0));
			} else if (disposal.equals("restoreToBackgroundColor")) {
				masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
			}
		}
		reader.dispose();

		return frames.toArray(new ImageFrame[frames.size()]);
	}

	private class ImageFrame {
		private final int delay;
		private final BufferedImage image;
		private final String disposal;

		public ImageFrame(BufferedImage image, int delay, String disposal) {
			this.image = image;
			this.delay = delay;
			this.disposal = disposal;
		}

		public BufferedImage getImage() {
			return image;
		}

		public int getDelay() {
			return delay;
		}

		public String getDisposal() {
			return disposal;
		}
	}
}
