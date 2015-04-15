package ImageProcess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.lang.NumberFormatException;
import ImageProcess.Filter.*;
import ImageProcess.Tool.*;

public class Processor extends JFrame implements Runnable, ActionListener{
	public ImagePanel panel;
	public static String title = "Image Processor - By Will Whiteside";
	public String fileName;
	BufferedImage previous = null;
	JFileChooser chooser = null;
	JTextArea debugConsole = null;
	JFrame debugWindow = null;
	JLabel workingText = new JLabel(" Working...");
	ImageTool currentTool = null;
	JFrame toolOptionWindow = null;
	JPanel toolOptionComponent = null;
	
	public static final boolean logStretch = false;
	
	public static void main(String [] args){
		if(args.length < 1){
			new Processor((String)null);
		} else {
			new Processor(args[0]);
		}
	}
	
	public Processor(String filename){
		super(title);
		chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"Images", "jpg", "gif", "jpeg", "png", "bmp","rle");
		chooser.setFileFilter(filter);
		fileName = filename;
		BufferedImage image = null;
		if(fileName != null){
			try{
				image = ImageIO.read(new File(filename));
			}catch (IOException e){
				image = new BufferedImage(400,300,BufferedImage.TYPE_INT_ARGB);
			}
		} else {
			image = new BufferedImage(400,300,BufferedImage.TYPE_INT_ARGB);
			fileName = "dummy";
		}
		panel = new ImagePanel(image);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setUndecorated(false);
		
		debugConsole = new JTextArea("Image Processor\nBy Will Whiteside\nWEW@CS.UGA.EDU\n",2, 50);
		debugConsole.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
		ImageFilter.debugConsole = debugConsole;
		
		setJMenuBar(createMenuBar());
		
		ImageTool.setContextMenu(createPopupMenu());
		ImageTool.debugConsole = debugConsole;
		ImageTool.processor = this;
		toolOptionWindow = new JFrame("Tool Options");
		toolOptionWindow.setLocation(getLocation());
		toolOptionWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		toolOptionWindow.setVisible(false);
		setCurrentTool(new ImageTool());
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setOpaque(true);
		panel.scrollPane = scrollPane;
		scrollPane.setBackground(Color.white);
		
		getContentPane().add(scrollPane);
		pack();
		repaint(30);
		//setSize(panel.image.getWidth(), panel.image.getHeight());
		setVisible(true);
		//new Thread(this).start();
		
	}
	
	public Processor(BufferedImage image){
		this((String)null);
		panel.imageHolder.setImage(image);
		panel.updateSize();
		pack();
		repaint(30);
		pack();
	}
	
	public void run(){
		//menu();
	}
	
	public void setImageHolder(ImageHolder iH){
		panel.setImageHolder(iH);
		currentTool.setImageHolder(iH);
		//pack();
		repaint(30);
	}
	
	public void applyFilter(ImageFilter f){
		workingText.setVisible(true);
		currentTool.disable();
		debugPrintln("Applying " + f.toString());
		if(debugWindow!=null){
			debugWindow.repaint();
		}
		previous = panel.image();
		BackgroundImageProcessor bIP = new BackgroundImageProcessor(f,panel.imageHolder,false,this,debugConsole);
		bIP.execute();
	}
	
	public void applyFilterToColors(ImageFilter f){
		workingText.setVisible(true);
		debugPrintln("Applying " + f.toString() + " to each color channel");
		if(debugWindow!=null){
			debugWindow.repaint();
		}
		previous = panel.image();
		BackgroundImageProcessor bIP = new BackgroundImageProcessor(f,panel.imageHolder,true,this,debugConsole);
		bIP.execute();
	}

	public void setCurrentTool(ImageTool tool){
		if(currentTool != null){
			panel.removeMouseListener(currentTool);
			panel.removeMouseMotionListener(currentTool);
			currentTool.disable();
		}
		
		if(toolOptionWindow != null && toolOptionComponent != null){
			toolOptionWindow.remove(toolOptionComponent);
		}
		
		toolOptionComponent = tool.createOptionsPanel();
		if(toolOptionComponent != null){
			if(toolOptionWindow == null){
				toolOptionWindow = new JFrame("Tool Options");
				toolOptionWindow.setLocation(getLocation());
				toolOptionWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
			toolOptionWindow.add(toolOptionComponent);
			toolOptionWindow.pack();
			toolOptionWindow.setVisible(true);
		} else if(toolOptionComponent == null){
			toolOptionWindow.setVisible(false);
		}
		panel.addMouseListener(tool);
		panel.addMouseMotionListener(tool);
		currentTool = tool;
		tool.enable();
		tool.setImageHolder(panel.imageHolder);
	}

	public void undo(){
		if(previous != null && !(panel.imageHolder instanceof PyramidImageHolder)){
			debugPrintln("Undoing");
			BufferedImage temp = panel.image();
			panel.imageHolder.setImage(previous);
			previous = temp;
			panel.updateSize();
			pack();
			repaint(30);
		}
	}

	public void showHist(){
		ImagePanel histPanel = new ImagePanel(ImageFilter.drawHist(ImageFilter.toHist(ImageFilter.toGreyMatrix(panel.image())),logStretch, ImageFilter.GREY_HIST));
		JFrame histFrame = new JFrame("CSCI8810 Histogram");
		histFrame.setUndecorated(false);
		
		JScrollPane scrollPane = new JScrollPane(histPanel);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(Color.white);
		
		histFrame.getContentPane().add(scrollPane);
		histFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		histFrame.pack();
		histFrame.setVisible(true);
	}
	
	public void showColorHists(){
		ImagePanel histPanelR = new ImagePanel(ImageFilter.drawHist(ImageFilter.toHist(ImageFilter.toRedMatrix(panel.image())),logStretch, ImageFilter.RED_HIST));
		JFrame histFrameR = new JFrame("CSCI8810 Red Histogram");
		histFrameR.setUndecorated(false);
		
		JScrollPane scrollPane = new JScrollPane(histPanelR);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(Color.white);
		
		histFrameR.getContentPane().add(scrollPane);
		histFrameR.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		histFrameR.pack();
		histFrameR.setVisible(true);
		ImagePanel histPanelG = new ImagePanel(ImageFilter.drawHist(ImageFilter.toHist(ImageFilter.toGreenMatrix(panel.image())),logStretch, ImageFilter.GREEN_HIST));
		JFrame histFrameG = new JFrame("CSCI8810 Green Histogram");
		histFrameG.setUndecorated(false);
		
		scrollPane = new JScrollPane(histPanelG);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(Color.white);
		
		histFrameG.getContentPane().add(scrollPane);
		histFrameG.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		histFrameG.pack();
		histFrameG.setVisible(true);
		
		
		ImagePanel histPanelB = new ImagePanel(ImageFilter.drawHist(ImageFilter.toHist(ImageFilter.toBlueMatrix(panel.image())),logStretch, ImageFilter.BLUE_HIST));
		JFrame histFrameB = new JFrame("CSCI8810 Blue Histogram");
		histFrameB.setUndecorated(false);
		
		scrollPane = new JScrollPane(histPanelB);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(Color.white);
		
		histFrameB.getContentPane().add(scrollPane);
		histFrameB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		histFrameB.pack();
		histFrameB.setVisible(true);
		
		
		ImagePanel histPanelH = new ImagePanel(ImageFilter.drawHist(ImageFilter.toHueHist(ImageFilter.toHueMatrix(panel.image()),ImageFilter.toSaturationMatrix(panel.image())),logStretch, ImageFilter.HUE_HIST));
		JFrame histFrameH = new JFrame("CSCI8810 Hue Histogram");
		histFrameH.setUndecorated(false);
		
		scrollPane = new JScrollPane(histPanelH);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(Color.white);
		
		histFrameH.getContentPane().add(scrollPane);
		histFrameH.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		histFrameH.pack();
		histFrameH.setVisible(true);
	}
	
	public void writeToFile(String filename){
		try{
			ImageIO.write(panel.image(),"png",new File(filename + ".png"));
		}catch(Exception IDONTCARE){
		}
	}
	
	public void debugPrint(String s){
		debugConsole.append(s);
	}
	
	public void debugPrintln(String s){
		debugConsole.append(s);
		debugConsole.append("\n");
	}
	
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu, subMenu;
		JMenuItem menuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Get the Image menu.
		menuBar.add(createImageMenu());

		//Image Encoding
		//menuBar.add(createEncodingMenu());
		
		//Tools
		menuBar.add(createToolsMenu());
		
		//Monochrome Menu
		menuBar.add(createMonochromeMenu());
		
		//high-pass filters
		menuBar.add(createHighPassMenu());
		
		//low pass
		menuBar.add(createLowPassMenu());
		
		//Resize
		menuBar.add(createResizeMenu());
		
		//Colors
		menuBar.add(createColorsMenu());
		
		//Other
		menuBar.add(createOtherMenu());
		
		//?
		menuBar.add(createAboutMenu());
		
		//Working Text
		menuBar.add(workingText);
		workingText.setVisible(false);
		
		return menuBar;
	}
	
	public JPopupMenu createPopupMenu() {
		JPopupMenu context = new JPopupMenu();
		
		context.add(createFiltersMenu());
		
		return context;
	}
	
	public JMenu createFiltersMenu() {
		JMenu filters = new JMenu("Filters");
		
		filters.add(createImageMenu());
		
		//Monochrome Menu
		filters.add(createMonochromeMenu());
		
		//high-pass filters
		filters.add(createHighPassMenu());
		
		//low pass
		filters.add(createLowPassMenu());
		
		//Resize
		filters.add(createResizeMenu());
		
		//Colors
		filters.add(createColorsMenu());
		
		//Other
		filters.add(createOtherMenu());
		
		//?
		filters.add(createAboutMenu());
		
		return filters;
	}
	
	public JMenu createToolsMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		menu.getAccessibleContext().setAccessibleDescription("Tools Menu");
		
		menuItem = new JMenuItem("No Tool", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription("Disable Tools");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Debug Pen", KeyEvent.VK_D);
		menuItem.getAccessibleContext().setAccessibleDescription("Debugging Pen Tool");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Pen", KeyEvent.VK_P);
		menuItem.getAccessibleContext().setAccessibleDescription("Pen Tool");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Debug AEP", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("Pen Tool");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createImageMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Image");
		menu.setMnemonic(KeyEvent.VK_I);
		menu.getAccessibleContext().setAccessibleDescription("File Menu");
		
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription("Open a File");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,java.awt.event.InputEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.getAccessibleContext().setAccessibleDescription("Save a File");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,java.awt.event.InputEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Export", KeyEvent.VK_E);
		menuItem.getAccessibleContext().setAccessibleDescription("Export a File");
		menuItem.addActionListener(this);
	//	menu.add(menuItem); //incomplete
		
		menuItem = new JMenuItem("Import", KeyEvent.VK_E);
		menuItem.getAccessibleContext().setAccessibleDescription("Import a File");
		menuItem.addActionListener(this);
	//	menu.add(menuItem);	//incomplete
		
		menuItem = new JMenuItem("Undo", KeyEvent.VK_U);
		menuItem.getAccessibleContext().setAccessibleDescription("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,java.awt.event.InputEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Clone", KeyEvent.VK_L);
		menuItem.getAccessibleContext().setAccessibleDescription("Clone");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,java.awt.event.InputEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.getAccessibleContext().setAccessibleDescription("Exit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,java.awt.event.InputEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createEncodingMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Encoding");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription("Image Encoding Options");
		
		menuItem = new JMenuItem("Raster", KeyEvent.VK_R);
		menuItem.getAccessibleContext().setAccessibleDescription("Convert to Rasterized Image");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Pyramid", KeyEvent.VK_P);
		menuItem.getAccessibleContext().setAccessibleDescription("Convert to Pyramid/switch levels");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		
		//TODO:
		//add strip-coded
		//add vectorization
		
		return menu;
	}
	
	public JMenu createMonochromeMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Monochrome");
		menu.setMnemonic(KeyEvent.VK_M);
		
		subMenu = new JMenu("Channels");
		subMenu.setMnemonic(KeyEvent.VK_C);
		menu.add(subMenu);
		
		menuItem = new JMenuItem("Red Channel", KeyEvent.VK_R);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Green Channel", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Blue Channel", KeyEvent.VK_B);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Alpha Channel", KeyEvent.VK_A);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Hue Channel", KeyEvent.VK_H);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Saturation Channel", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Value Channel", KeyEvent.VK_V);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Luma Channel", KeyEvent.VK_L);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Iterative Threshold", KeyEvent.VK_I);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Grey Scale", KeyEvent.VK_G);
		menuItem.getAccessibleContext().setAccessibleDescription("To Grey Scale");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Threshold", KeyEvent.VK_T);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Double Threshold", KeyEvent.VK_D);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Percentile Threshold", KeyEvent.VK_P);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Adaptive Threshold", KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Posterize", KeyEvent.VK_Z);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Hatched Posterize", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createHighPassMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("High-Pass");
		menu.setMnemonic(KeyEvent.VK_H);
		
		menuItem = new JMenuItem("Sharpen", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Gaussian Sharpen", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Laplacian", KeyEvent.VK_L);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Laplacian Plus (+)", KeyEvent.VK_P);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Laplacian Cross (x)", KeyEvent.VK_X);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Sobel Vertical (|)", KeyEvent.VK_V);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Sobel Horizontal (-)", KeyEvent.VK_H);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Prewitt Vertical (|)", KeyEvent.VK_T);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Prewitt Horizontal (-)", KeyEvent.VK_W);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Kirsch Negative Slope (\\)", KeyEvent.VK_K);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Kirsch Positive Slope (/)", KeyEvent.VK_O);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Gaussian Edges", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Robert's Cross", KeyEvent.VK_R);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Edge Angle Detect", KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Kirsch All Edge Detect", KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createLowPassMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Low-Pass");
		menu.setMnemonic(KeyEvent.VK_L);
		
		menuItem = new JMenuItem("Gaussian Blur", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Median Filtering", KeyEvent.VK_M);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Median Filtering N5", KeyEvent.VK_D);
		menuItem.addActionListener(this);
		menu.add(menuItem);
	
		menuItem = new JMenuItem("Mean Filtering N5", KeyEvent.VK_N);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Mean Filtering N9", KeyEvent.VK_E);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Mean Filtering N961");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Anisotropic Smoothing", KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("JPEG Smoothing", KeyEvent.VK_J);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Cartoonize", KeyEvent.VK_C);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createResizeMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Resize");
		menu.setMnemonic(KeyEvent.VK_S);
		
		menuItem = new JMenuItem("Integer Zoom", KeyEvent.VK_Z);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Integer Shrink", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Zeroth Order Double", KeyEvent.VK_Z);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("First Order Double", KeyEvent.VK_F);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("First Order Shrink", KeyEvent.VK_K);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Nearest Neighbor Scale", KeyEvent.VK_N);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Best Neighbor Scale", KeyEvent.VK_B);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Linear Scale", KeyEvent.VK_L);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Linear Resize", KeyEvent.VK_L);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Linear Scale X by Y", KeyEvent.VK_L);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("EPX Scale", KeyEvent.VK_E);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createColorsMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Colors");
		menu.setMnemonic(KeyEvent.VK_C);
		
		menuItem = new JMenuItem("Invert Colors", KeyEvent.VK_I);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Add Noise", KeyEvent.VK_N);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Double Components", KeyEvent.VK_D);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Grey Histogram", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Color Histogram", KeyEvent.VK_H);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		subMenu = new JMenu("Contrast Effects");
		subMenu.setMnemonic(KeyEvent.VK_C);
		menu.add(subMenu);
		
		menuItem = new JMenuItem("Histogram Stretch", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Percentile Histogram Stretch", KeyEvent.VK_P);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Logistic Stretch", KeyEvent.VK_L);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Percentile Logistic Stretch", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Logit Stretch", KeyEvent.VK_T);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		//add some more contrast effects
		
		menuItem = new JMenuItem("HSV Modification", KeyEvent.VK_M);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		
		menuItem = new JMenuItem("Color Posterize", KeyEvent.VK_P);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Hatched Color Posterize", KeyEvent.VK_T);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		//menuItem = new JMenuItem("Component Posterize", KeyEvent.VK_E);
		//menuItem.addActionListener(this);
		//menu.add(menuItem);

		menuItem = new JMenuItem("Smooth Component Posterize", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Add Alpha Layer", KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Remove Alpha Layer", KeyEvent.VK_R);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Set Background", KeyEvent.VK_B);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		subMenu = new JMenu("Encoding Switch");
		subMenu.setMnemonic(KeyEvent.VK_E);
		menu.add(subMenu);
		
		menuItem = new JMenuItem("R<->G");
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("R<->B");
		menuItem.addActionListener(this);
		subMenu.add(menuItem);		

		menuItem = new JMenuItem("G<->B");
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("YCrCb -> RGB");
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("RGB -> YCrCb");
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createOtherMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("Other");
		menu.setMnemonic(KeyEvent.VK_O);
		
		menuItem = new JMenuItem("Image Math", KeyEvent.VK_M);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("HSV Image Math", KeyEvent.VK_M);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Component Labeling", KeyEvent.VK_C);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Generate Cloud", KeyEvent.VK_G);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Arbitrary Mask", null);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		subMenu = new JMenu("Steganography");
		subMenu.setMnemonic(KeyEvent.VK_S);
		menu.add(subMenu);
		
		menuItem = new JMenuItem("Steg-Text-Encode", KeyEvent.VK_E);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Steg-Text-Decode", KeyEvent.VK_D);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Steg-Image-Encode", KeyEvent.VK_I);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Steg-Image-Decode", KeyEvent.VK_I);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Bit Channel", KeyEvent.VK_N);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Image Diff", KeyEvent.VK_F);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Image Average", KeyEvent.VK_V);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Interleave", KeyEvent.VK_I);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Hough Circle Detect", KeyEvent.VK_H);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		subMenu = new JMenu("Textify");
		subMenu.setMnemonic(KeyEvent.VK_T);
		menu.add(subMenu);
		
		menuItem = new JMenuItem("Memeify", KeyEvent.VK_M);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Demotivatifier", KeyEvent.VK_D);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Texterizer", KeyEvent.VK_T);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("CharMap", KeyEvent.VK_C);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("CharMap (avg. bkgd)", KeyEvent.VK_B);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("CharMap Fit", null);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("MatrixMap", null);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		return menu;
	}
	
	public JMenu createAboutMenu(){
		JMenu menu, subMenu;
		JMenuItem menuItem;
		
		menu = new JMenu("?");
		menu.getAccessibleContext().setAccessibleDescription("Tools");
		
		menuItem = new JMenuItem("Debug Window", KeyEvent.VK_D);
		menuItem.getAccessibleContext().setAccessibleDescription("Debug Window");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,java.awt.event.InputEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("About Program");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}
	
	public void actionPerformed(ActionEvent e) {
		doAction(e);
	}
	
	public void doAction(ActionEvent e){
		JMenuItem source = (JMenuItem)(e.getSource());
		String option = source.getText();
		//debugPrintln("Working on " + option);
		try{
			if(option.equals("Exit")){
				dispose();
			} 
			else if(option.equals("Save")){
				int returnVal = chooser.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						String filetype = (chooser.getSelectedFile().getName()).substring(chooser.getSelectedFile().getName().lastIndexOf((int)'.')+1);
						debugPrintln("Saving in " + chooser.getSelectedFile().getName());
						if(!filetype.equalsIgnoreCase("jpg") && !filetype.equalsIgnoreCase("jpeg")){
							ImageIO.write(panel.image(),filetype,chooser.getSelectedFile());
						} else {
							ImageIO.write(ImageFilter.removeAlpha(panel.image()),filetype,chooser.getSelectedFile());
						}
					}catch(Exception IDONTCARE){
					}
				} else {
					//System.exit(0);
				}
			} 
			else if(option.equals("Export")){
				int returnVal = chooser.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						RunLengthEncoder rleExport = new RunLengthEncoder(debugConsole);
						rleExport.exportImage(chooser.getSelectedFile(),panel.image());
					}catch(Exception IDONTCARE){
					}
				} else {
					//System.exit(0);
				}
			} 
			else if(option.equals("Import")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						RunLengthEncoder rleExport = new RunLengthEncoder(debugConsole);
						previous = panel.image();
						panel.imageHolder.setImage(rleExport.importImage(chooser.getSelectedFile()));
						panel.updateSize();
						pack();
						repaint(30);
					}catch(Exception IDONTCARE){
					}
				} else {
					//System.exit(0);
				}
			} 
			else if(option.equals("Open")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						panel.imageHolder.setImage(ImageIO.read(chooser.getSelectedFile()));
						fileName = chooser.getSelectedFile().getName();
						debugPrintln("Opening image in " + chooser.getSelectedFile().getName());
					}catch(Exception IDONTCARE){
					}
					if(fileName.substring(fileName.length()-3).equalsIgnoreCase("jpg") || fileName.substring(fileName.length()-4).equalsIgnoreCase("jpeg")){
						try{
							FileReader jpegChecker = new FileReader(chooser.getSelectedFile());
							char[] buff = new char[10];
							jpegChecker.read(buff,0,10);
							if(buff[3] != 0xE0){
								debugPrintln("Possible Jpeg Error: " + (int)buff[3]);
								panel.updateSize();
								repaint(30);
								pack();
								int optionFix = JOptionPane.showConfirmDialog(this,"Did the image load correctly?","Possible Jpeg Error Detected",JOptionPane.YES_NO_OPTION);
								if(optionFix == JOptionPane.NO_OPTION){
									debugPrintln("Converting YCrCb to RGB");
									previous = panel.image();
									panel.imageHolder.setImage(new SwitchFilter(SwitchFilter.YCRCB_TO_RGB).filter(panel.image()));
									setVisible(true);
									panel.repaint(30);
								}
							}
						}catch(Exception IDONTCARE){
						}
					}
					panel.updateSize();
					setVisible(true);
					pack();
					repaint(30);
				} else {
					//System.exit(0);
				}
			} 
			else if(option.equals("Undo")){
				undo();
			} 
			else if(option.equals("Clone")){
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Processor(panel.image());					
					}
				});

			} 
			else if(option.equals("Debug Window")){
				if(debugWindow == null){
					debugWindow = new JFrame("Debug Console");
					debugWindow.setLocation(getLocation());
					JScrollPane scrollPane = new JScrollPane(debugConsole);
					scrollPane.setPreferredSize(new Dimension(400,300));
					debugWindow.getContentPane().add(scrollPane);
					debugWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
				debugWindow.pack();
				debugWindow.setVisible(true);
			} 
			else if(option.equals("About")){
				JOptionPane.showMessageDialog(this,"Created by Will Whiteside\nAll Rights Reserved\nCopyright 2010","About",JOptionPane.INFORMATION_MESSAGE);
			}
			else if(option.equals("Raster")){
				if(!(panel.imageHolder instanceof ImageHolder)){
					setImageHolder(new ImageHolder(panel.imageHolder.getImage()));
				}
			}
			else if(option.equals("Pyramid")){
				if(!(panel.imageHolder instanceof PyramidImageHolder)){
					String levels = JOptionPane.showInputDialog(this,"Number Of levels to pyramid: ");
					setImageHolder(new PyramidImageHolder(panel.imageHolder.getImage(), Integer.parseInt(levels)));
				} else {
					String levels = JOptionPane.showInputDialog(this,"Level to show: ");
					panel.imageHolder.setState(Integer.parseInt(levels));
					panel.updateSize();
					pack();
					repaint(30);
				}
			}
			else if(option.equals("No Tool")){
				setCurrentTool(new ImageTool());
			}
			else if(option.equals("Debug Pen")){
				setCurrentTool(new DebugPenTool());
			}
			else if(option.equals("Pen")){
				setCurrentTool(new PenTool());
			}
			else if(option.equals("Debug AEP")){
				setCurrentTool(new AlongEveryPointTool());
			}
			else if(option.equals("Grey Scale")){
				applyFilter(new GreyScale());
			}
			else if(option.equals("Green Channel")){
				applyFilter(new GreenFilter());
			}
			else if(option.equals("Blue Channel")){
				applyFilter(new BlueFilter());
			}
			else if(option.equals("Alpha Channel")){
				applyFilter(new AlphaFilter());
			}
			else if(option.equals("Hue Channel")){
				applyFilter(new HueFilter());
			}
			else if(option.equals("Saturation Channel")){
				applyFilter(new SaturationFilter());
			}
			else if(option.equals("Value Channel")){
				applyFilter(new ValueFilter());
			}
			else if(option.equals("Luma Channel")){
				applyFilter(new LumaFilter());
			}
			else if(option.equals("Iterative Threshold")){
				applyFilter(new IterativeThreshold());
			}
			else if(option.equals("Laplacian")){
				applyFilter(new ImageMask(new double [][] {{1,1,1},{1,-8,1},{1,1,1}}, 0, 127));
			}
			else if(option.equals("Laplacian Plus (+)")){
				applyFilter(new ImageMask(new double [][] {{0,1,0},{1,-4,1},{0,1,0}}, 0, 127));
			}
			else if(option.equals("Laplacian Cross (x)")){
				applyFilter(new ImageMask(new double [][] {{1,0,1},{0,-4,0},{1,0,1}}, 0, 127));
			}
			else if(option.equals("Sobel Vertical (|)")){
				applyFilter(new ImageMask(new double [][] {{1,2,1},{0,0,0},{-1,-2,-1}}, 0, 127));
			}
			else if(option.equals("Sobel Horizontal (-)")){
				applyFilter(new ImageMask(new double [][] {{1,0,-1},{2,0,-2},{1,0,-1}}, 0, 127));
			}
			else if(option.equals("Prewitt Vertical (|)")){
				applyFilter(new ImageMask(new double [][] {{1,1,1},{0,0,0},{-1,-1,-1}}, 0, 127));
			}
			else if(option.equals("Prewitt Horizontal (-)")){
				applyFilter(new ImageMask(new double [][] {{1,0,-1},{1,0,-1},{1,0,-1}}, 0, 127));
			}
			else if(option.equals("Kirsch Negative Slope (\\)")){
				applyFilter(new ImageMask(new double [][] {{-2,-1,0},{-1,0,1},{0,1,2}}, 0, 127));
			}
			else if(option.equals("Kirsch Positive Slope (/)")){
				applyFilter(new ImageMask(new double [][] {{0,-1,-2},{1,0,-1},{2,1,0}}, 0, 127));
			}
			else if(option.equals("Sharpen")){
				String inputValue = JOptionPane.showInputDialog(this,"Sharpen factor: ");
				double factor = Double.parseDouble(inputValue);
				applyFilter(new ImageMask(new double [][] {{-1*factor,-1*factor,-1*factor},{-1*factor,1 + factor * 8,-1*factor},{-1*factor,-1*factor,-1*factor}}, 0));	
			}
			else if(option.equals("Gaussian Sharpen")){
				String inputValue = JOptionPane.showInputDialog(this,"Sharpen factor: ");
				double factor = Double.parseDouble(inputValue);
				inputValue = JOptionPane.showInputDialog(this,"Radius: ");
				int radius = Integer.parseInt(inputValue);
				applyFilter(new GaussianSharpen(radius,factor));	
			}
			else if(option.equals("Gaussian Edges")){
				String inputValue = JOptionPane.showInputDialog(this,"Edge factor: ");
				double factor = Double.parseDouble(inputValue);
				inputValue = JOptionPane.showInputDialog(this,"Radius: ");
				int radius = Integer.parseInt(inputValue);
				applyFilter(new GaussianEdge(radius,factor));	
			}
			else if(option.equals("Robert's Cross")){
				applyFilter(new RobertEdgeDetect());
			}
			else if(option.equals("Edge Angle Detect")){
				applyFilter(new SmallEdgeAngle());
			}
			else if(option.equals("Kirsch All Edge Detect")){
				applyFilter(new KirschFilter());
			}
			else if(option.equals("Gaussian Blur")){
				String inputValue = JOptionPane.showInputDialog(this,"Blur factor: ");
				double factor = Double.parseDouble(inputValue);
				inputValue = JOptionPane.showInputDialog(this,"Radius: ");
				int radius = Integer.parseInt(inputValue);
				applyFilter(new GaussianBlur(radius,factor));	
			}
			else if(option.equals("Median Filtering")){
				applyFilterToColors(new MedianFiltering());
			}
			else if(option.equals("Median Filtering N5")){
				applyFilterToColors(new MedianFiltering5());
			}
			else if(option.equals("Mean Filtering N5")){
				applyFilter(new ImageMask(new double [][] {{0,1,0},{1,1,1},{0,1,0}},ImageMask.NORMALIZE + ImageMask.DOALPHA));
			}
			else if(option.equals("Mean Filtering N9")){
				applyFilter(new ImageMask(new double [][] {{1,1,1},{1,1,1},{1,1,1}},ImageMask.NORMALIZE + ImageMask.DOALPHA));
			}
			else if(option.equals("Mean Filtering N961")){
				int size = 31;
				double [][] bigmask = new double[size][];
				for(int i = 0; i < size; i++){
					bigmask[i] = new double[size];
					for(int j = 0; j < size; j++){
						bigmask[i][j] = 1.0;
					}
				}
				applyFilter(new ImageMask(bigmask,ImageMask.NORMALIZE + ImageMask.DOALPHA));
			}
			else if(option.equals("Anisotropic Smoothing")){
				String prop = JOptionPane.showInputDialog(this,"Proportion of Smoothness: ");
				String varia = JOptionPane.showInputDialog(this,"Variance of Gaussian: ");
				applyFilter(new AnisotropicSmoothing(Double.parseDouble(prop),Double.parseDouble(varia)));
			}
			else if(option.equals("JPEG Smoothing")){
				String comp = JOptionPane.showInputDialog(this,"JPEG Compression Level: ");
				applyFilter(new JpegSmoothFilter(Integer.parseInt(comp)));
			}
			else if(option.equals("Cartoonize")){
				String inputValue = JOptionPane.showInputDialog(this,"Edge Threshold(1-255, lower is less change): ");
				applyFilter(new ComponentPosterize(Integer.parseInt(inputValue),true));
			}
			else if(option.equals("Integer Zoom")){
				String inputValue = JOptionPane.showInputDialog(this,"Zoom factor: ");
				applyFilter(new Zoom(Integer.parseInt(inputValue)));
			}
			else if(option.equals("Integer Shrink")){
				String inputValue = JOptionPane.showInputDialog(this,"Shrink factor: ");
				applyFilter(new Shrink(Integer.parseInt(inputValue)));
			}
			else if(option.equals("Zeroth Order Double")){
				applyFilter(new Zoom(2));
			}
			else if(option.equals("First Order Double")){
				applyFilter(new FirstOrderZoom());
			}
			else if(option.equals("First Order Shrink")){
				applyFilter(new MeanShrink());
			}
			else if(option.equals("Best Neighbor Scale")){
				String inputValue = JOptionPane.showInputDialog(this,"Zoom factor: ");
				applyFilter(new BestNeighborZoom(Double.parseDouble(inputValue)));
			}
			else if(option.equals("Nearest Neighbor Scale")){
				String inputValue = JOptionPane.showInputDialog(this,"Zoom factor: ");
				applyFilter(new NearestNeighborZoom(Double.parseDouble(inputValue)));
			}
			else if(option.equals("Linear Scale")){
				String inputValue = JOptionPane.showInputDialog(this,"Zoom factor: ");
				applyFilter(new LinearZoom(Double.parseDouble(inputValue)));
			}
			else if(option.equals("Linear Resize")){
				String xValue = JOptionPane.showInputDialog(this,"Width: ");
				String yValue = JOptionPane.showInputDialog(this,"Height: ");
				applyFilter(new LinearZoom(Integer.parseInt(xValue),Integer.parseInt(yValue)));
			}
			else if(option.equals("Linear Scale X by Y")){
				String xValue = JOptionPane.showInputDialog(this,"Zoom factor along X: ");
				String yValue = JOptionPane.showInputDialog(this,"Zoom factor along Y: ");
				applyFilter(new LinearZoom(Double.parseDouble(xValue),Double.parseDouble(yValue)));
			}
			else if(option.equals("EPX Scale")){
				applyFilter(new EPXZoom());
			}
			else if(option.equals("Invert Colors")){
				applyFilter(new InvertFilter());
			}
			else if(option.equals("Add Noise")){
				String noisePercent = JOptionPane.showInputDialog(this,"Percent of the image to add noise to (0-100): ");
				String noiseVolume = JOptionPane.showInputDialog(this,"Volume of the noise (0-255): ");
				applyFilter(new Noisinate(Double.parseDouble(noisePercent)/100.0,Integer.parseInt(noiseVolume)));
			}
			else if(option.equals("Generate Cloud")){
				String uniformity = JOptionPane.showInputDialog(this,"Uniformity (0-1): ");
				applyFilter(new CloudGenerate(1.0,Double.parseDouble(uniformity)));
			}
			else if(option.equals("Double Components")){
				applyFilter(new ImageMask(new double[][] {{2}},0));
			}
			else if(option.equals("Grey Histogram")){
				showHist();
			}
			else if(option.equals("Color Histogram")){
				showColorHists();
			}
			else if(option.equals("Histogram Stretch")){
				String low = JOptionPane.showInputDialog(this,"Low Threshold: ");
				String high = JOptionPane.showInputDialog(this,"High Threshold: ");
				applyFilter(new HistogramStretch(Double.parseDouble(low), Double.parseDouble(high)));
			}
			else if(option.equals("Percentile Histogram Stretch")){
				String low = JOptionPane.showInputDialog(this,"Low Threshold Percentile: ");
				String high = JOptionPane.showInputDialog(this,"High Threshold Percentile: ");
				applyFilter(new HistogramStretch(-1 * Double.parseDouble(low), -1 * Double.parseDouble(high)));
			}
			else if(option.equals("Logistic Stretch")){
				applyFilter(new LogisticStretch());
			}
			else if(option.equals("Logit Stretch")){
				applyFilter(new LogitStretch());
			}
			else if(option.equals("Percentile Logistic Stretch")){
				String low = JOptionPane.showInputDialog(this,"Low Threshold Percentile: ");
				String high = JOptionPane.showInputDialog(this,"High Threshold Percentile: ");
				applyFilter(new HistogramStretch(-1 * Double.parseDouble(low), -1 * Double.parseDouble(high)));
			}
			else if(option.equals("HSV Modification")){
				String hue = JOptionPane.showInputDialog(this,"Hue Shift: ");
				String sat = JOptionPane.showInputDialog(this,"Saturation Multiplier: ");
				String val = JOptionPane.showInputDialog(this,"Value Multiplier: ");
				applyFilter(new HSVFilter(HSVFilter.SATURATION_MULTIPLY+HSVFilter.VALUE_MULTIPLY+HSVFilter.HUE_SHIFT,Double.parseDouble(sat),Double.parseDouble(val),Double.parseDouble(hue)));
			}
			else if(option.equals("Component Labeling")){
				ComponentFilter comp = new ComponentFilter();
				applyFilter(comp);
				JOptionPane.showMessageDialog(this, comp.numComponents + " components of average size " + comp.averageSize);
			}
			else if(option.equals("Image Math")){
				String expR = JOptionPane.showInputDialog(this,"Red Expression (0-255):");
				String expG = JOptionPane.showInputDialog(this,"Green Expression (0-255):");
				String expB = JOptionPane.showInputDialog(this,"Blue Expression (0-255):");
				String expA = JOptionPane.showInputDialog(this,"Alpha Expression (0-255):");
				applyFilter(new ImageMath(ImageMath.COLOR+ImageMath.ALPHA,expR,expG,expB,expA));
			}
			else if(option.equals("HSV Image Math")){
				String expR = JOptionPane.showInputDialog(this,"Hue Expression (0-360):");
				String expG = JOptionPane.showInputDialog(this,"Saturation Expression (0-1):");
				String expB = JOptionPane.showInputDialog(this,"Value Expression (0-1):");
				String expA = JOptionPane.showInputDialog(this,"Alpha Expression (0-255):");
				applyFilter(new ImageMath(ImageMath.HSV+ImageMath.ALPHA,expR,expG,expB,expA));
			}
			else if(option.equals("Component Posterize")){
				String inputValue = JOptionPane.showInputDialog(this,"Edge Threshold(1-255, lower is less change): ");
				applyFilter(new ComponentPosterize(Integer.parseInt(inputValue),false));
			}
			else if(option.equals("Smooth Component Posterize")){
				String inputValue = JOptionPane.showInputDialog(this,"Edge Threshold(1-255, lower is less change): ");
				applyFilter(new ComponentPosterizeFull(Integer.parseInt(inputValue)));
			}
			else if(option.equals("Add Alpha Layer")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						applyFilter(new AlphaLayer(ImageIO.read(chooser.getSelectedFile())));
					}catch(Exception IDONTCARE){
						IDONTCARE.printStackTrace();
					}
				}
			}
			else if(option.equals("Remove Alpha Layer")){
				previous = panel.image();
				panel.imageHolder.setImage(ImageFilter.removeAlpha(panel.imageHolder.getImage()));
				panel.updateSize();
				pack();
				repaint(30);
			}
			else if(option.equals("Set Background")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						applyFilter(new ImageBackground(ImageIO.read(chooser.getSelectedFile())));
					}catch(Exception IDONTCARE){
					}
				}
			}
			else if(option.equals("R<->G")){
				applyFilter(new SwitchFilter(SwitchFilter.RG));
			}
			else if(option.equals("R<->B")){
				applyFilter(new SwitchFilter(SwitchFilter.RB));
			}
			else if(option.equals("G<->B")){
				applyFilter(new SwitchFilter(SwitchFilter.GB));
			}
			else if(option.equals("YCrCb -> RGB")){
				applyFilter(new SwitchFilter(SwitchFilter.YCRCB_TO_RGB));
			}
			else if(option.equals("RGB -> YCrCb")){
				applyFilter(new SwitchFilter(SwitchFilter.RGB_TO_YCRCB));
			}
			else if(option.equals("Arbitrary Mask")){
				//???
			}
			else if(option.equals("Threshold")){
				String thres = JOptionPane.showInputDialog(this,"Threshold: ");
				applyFilter(new BWThreshold(Integer.parseInt(thres)));
			}
			else if(option.equals("Double Threshold")){
				String low = JOptionPane.showInputDialog(this,"Low Threshold: ");
				String high = JOptionPane.showInputDialog(this,"High Threshold: ");
				applyFilter(new BWBThreshold(Integer.parseInt(low), Integer.parseInt(high)));
			}
			else if(option.equals("Percentile Threshold")){
				String thres = JOptionPane.showInputDialog(this,"Percentile Threshold (0-100): ");
				applyFilter(new PTileThreshold(Double.parseDouble(thres)/100.0));
			}
			else if(option.equals("Adaptive Threshold")){
				String low = JOptionPane.showInputDialog(this,"Horizontal Slices: ");
				String high = JOptionPane.showInputDialog(this,"Vertical Slices: ");
				applyFilter(new AdaptiveThresholding(Integer.parseInt(low), Integer.parseInt(high)));
			}
			else if(option.equals("Posterize")){
				String levels = JOptionPane.showInputDialog(this,"Number of levels: ");
				applyFilter(new GreyPosterize(Integer.parseInt(levels)));
			}
			else if(option.equals("Color Posterize")){
				String levels = JOptionPane.showInputDialog(this,"Number of levels: ");
				applyFilterToColors(new GreyPosterize(Integer.parseInt(levels)));
			}
			else if(option.equals("Hatched Posterize")){
				String levels = JOptionPane.showInputDialog(this,"Number of levels: ");
				applyFilter(new HatchedPosterize(Integer.parseInt(levels)));
			}
			else if(option.equals("Hatched Color Posterize")){
				String levels = JOptionPane.showInputDialog(this,"Number of levels: ");
				applyFilterToColors(new HatchedPosterize(Integer.parseInt(levels)));
			}
			else if(option.equals("Steg-Text-Encode")){
				String text = JOptionPane.showInputDialog(this,"Text to hide: ");
				applyFilter(new StegTextEncode(text));
			}
			else if(option.equals("Steg-Text-Decode")){
				StegTextDecode steg = new StegTextDecode();
				applyFilter(steg);
				JOptionPane.showMessageDialog(this, steg.text);
			}
			else if(option.equals("Steg-Image-Encode")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						String bits = JOptionPane.showInputDialog(this,"Bits per color channel: ");
						applyFilter(new StegImageEncode(ImageIO.read(chooser.getSelectedFile()),Integer.parseInt(bits)));
					}catch(Exception IDONTCARE){
					}
				}
				
			}
			else if(option.equals("Steg-Image-Decode")){
				String bits = JOptionPane.showInputDialog(this,"Bits per color channel: ");
				applyFilter(new StegImageDecode(Integer.parseInt(bits)));
			}
			else if(option.equals("Image Diff")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						applyFilter(new ImageDiff(ImageIO.read(chooser.getSelectedFile())));
					}catch(Exception IDONTCARE){
					}
				}
			}
			else if(option.equals("Image Average")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						applyFilter(new ImageAverage(ImageIO.read(chooser.getSelectedFile())));
					}catch(Exception IDONTCARE){
					}
				}
			}
			else if(option.equals("Interleave")){
				String vert = JOptionPane.showInputDialog(this,"Vertical slice size (<=0 for none): ");
				String horiz = JOptionPane.showInputDialog(this,"Horizontal slice size (<=0 for none): ");
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						applyFilter(new ImageInterleave(ImageIO.read(chooser.getSelectedFile()),Integer.parseInt(vert),Integer.parseInt(horiz)));
					}catch(Exception IDONTCARE){
					}
				}
			}
			else if(option.equals("Bit Channel")){
				String bits = JOptionPane.showInputDialog(this,"Bit channel: ");
				applyFilter(new BitChannel(Integer.parseInt(bits)));
			}
			else if(option.equals("Hough Circle Detect")){
				HoughCircle hC = new HoughCircle();
				applyFilter(hC);
				JOptionPane.showMessageDialog(this, "Circles:\n" + "("+hC.bestA[0] + "," + hC.bestB[0] + ")\tr:"+hC.bestR[0]+"\n"
					+"("+hC.bestA[1] + "," + hC.bestB[1] + ")\tr:"+hC.bestR[1]+"\n" 
					+"("+hC.bestA[2] + "," + hC.bestB[2] + ")\tr:"+hC.bestR[2]+"\n");
			}
			else if(option.equals("Memeify")){
				String top = JOptionPane.showInputDialog(this,"Top Text (split lines with `): ");
				String bottom = JOptionPane.showInputDialog(this,"Bottom Text (split lines with `): ");
				applyFilter(new Memeify(top,bottom));
			}
			else if(option.equals("Demotivatifier")){
				String top = JOptionPane.showInputDialog(this,"Title Text: ");
				String bottom = JOptionPane.showInputDialog(this,"Explanitory Text: ");
				applyFilter(new Demotivatifier(top,bottom));
			}
			else if(option.equals("Texterizer")){
				int returnVal = chooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try{
						String text = JOptionPane.showInputDialog(this,"Text (split phrases with `): ");
						String edges = JOptionPane.showInputDialog(this,"Use Edges (0 for no, 1 for yes): ");
						applyFilter(new Texterizer(text.split("`"), ImageIO.read(chooser.getSelectedFile()),!(Integer.parseInt(edges) == 0)));
					}catch(Exception IDONTCARE){
						IDONTCARE.printStackTrace();
					}
				}
			}
			else if(option.equals("CharMap")){
				JFontChooser chooser = new JFontChooser(this);
				int result = chooser.showDialog(new Font("Arial",Font.BOLD,12));
				if (result != JFontChooser.CANCEL_OPTION) 
				{
					String text = JOptionPane.showInputDialog(this,"Text:");
					applyFilter(new CharMap(text, false, chooser.getFont()));
				}
			}
			else if(option.equals("CharMap (avg. bkgd)")){
				JFontChooser chooser = new JFontChooser(this);
				int result = chooser.showDialog(new Font("Arial",Font.BOLD,12));
				if (result != JFontChooser.CANCEL_OPTION) 
				{
					String text = JOptionPane.showInputDialog(this,"Text:");
					applyFilter(new CharMap(text, true, chooser.getFont()));
				}
			}
			else if(option.equals("CharMap Fit")){
				JFontChooser chooser = new JFontChooser(this);
				int result = chooser.showDialog(new Font("Arial",Font.BOLD,12));
				if (result != JFontChooser.CANCEL_OPTION) 
				{
					String text = JOptionPane.showInputDialog(this,"Text:");
					String threshold = JOptionPane.showInputDialog(this,"Threshold:");
					applyFilter(new CharMap(text, false, chooser.getFont(),Integer.parseInt(threshold)));
				}
			}
			else if(option.equals("MatrixMap")){
				applyFilter(new MatrixMap());
			} 
			else {
				JOptionPane.showMessageDialog(this,option + " unimplemented");
			}
		} 
		catch (NumberFormatException n){
			debugConsole.append("Number Format Exception, Canceling\n");
			debugPrintln("Canceled " + option);
		} 
		catch (Exception excep){
			debugConsole.append("* * * * Unhandled Exception * * * *\n");
			debugConsole.append(excep.toString() + "\n");
			debugConsole.append(excep.getStackTrace()[0].toString() +"\n");
			debugConsole.append(excep.getStackTrace()[1].toString() +"\n");
			debugConsole.append(excep.getStackTrace()[2].toString() +"\n");
			debugPrintln("...");
			debugPrintln("Failed " + option);
		}
		ImageFilter.resetImage();
	}
	
	private class ImagePanel extends JPanel{
		public ImageHolder imageHolder;
		public JScrollPane scrollPane;
		
		//public BufferedImage image;
		public ImagePanel(BufferedImage i){
			setOpaque(true);
			imageHolder = new ImageHolder(i);
			setPreferredSize(new Dimension(imageHolder.getImage().getWidth(),imageHolder.getImage().getHeight()));
			setBackground(Color.white);
		}
		
		public void paintComponent(Graphics g){
			g.setColor(Color.WHITE);
			g.drawRect(0,0,getWidth(),getHeight());
			g.setColor(Color.BLACK);
			g.drawRect(0,0,imageHolder.getImage().getWidth()+1,imageHolder.getImage().getHeight()+1);
			g.drawImage(imageHolder.getImage(),1,1,null);
		}
		
		public BufferedImage image(){
			return imageHolder.getImage();
		}
		
		public void setImageHolder(ImageHolder imageHolder){
			this.imageHolder = imageHolder;
			updateSize();
		}
		
		public void updateSize(){
			scrollPane.setPreferredSize(new Dimension(imageHolder.getImage().getWidth()+20,imageHolder.getImage().getHeight()+20));
			setPreferredSize(new Dimension(imageHolder.getImage().getWidth()+2,imageHolder.getImage().getHeight()+2));
			setMaximumSize(new Dimension(imageHolder.getImage().getWidth()+2,imageHolder.getImage().getHeight()+2));
			setMinimumSize(new Dimension(imageHolder.getImage().getWidth()+2,imageHolder.getImage().getHeight()+2));
		}
	}

}

