package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.font.GlyphVector;

public class Memeify extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Memeify";
	String[] top;
	String[] bottom;
	
	public Memeify(String topText, String bottomText){
		top = topText.split("`");
		bottom = bottomText.split("`");
	}
	
	public String toString(){
		return "Memeing \"" + top[0] + "\"";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		image = toAlphaImage(red,green,blue,alpha);
		
		Graphics2D g2d = image.createGraphics();
		Font fontTop;
		FontMetrics fmTop;
		int fontSize = 180;
		int width;
		do{
			width = 0;
			fontSize = fontSize * 4 / 5;
			fontTop = new Font("Arial", Font.BOLD,fontSize);
			fmTop = g2d.getFontMetrics(fontTop);
			for(int i = 0; i < top.length; i++){
				int thisWidth = fmTop.stringWidth(top[i]);
				if(thisWidth > width){
					width = thisWidth;
				}
			}
		}while(width > (image.getWidth() * .9) || fmTop.getHeight() > image.getHeight()/5);
		
		
		for(int i = 0; i < top.length; i++){
			g2d = image.createGraphics();
			g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setFont(fontTop);
			GlyphVector gv = fontTop.createGlyphVector(g2d.getFontRenderContext(), top[i]);
			Shape shape = gv.getOutline();
			g2d.setStroke(new BasicStroke((fontSize > 12)?(int)(fontSize/6.0f)*1.0f:2.0f));
			g2d.translate(image.getWidth()/2-fmTop.stringWidth(top[i])/2, 
				(int)(image.getHeight()/20 + (fmTop.getHeight() + 2) * (i+.7)));
			g2d.setColor(Color.BLACK);
			g2d.draw(shape);
			g2d.setColor(Color.WHITE);
			g2d.drawString(top[i],0,0);
			g2d.translate(-1*image.getWidth()/2-fmTop.stringWidth(top[i])/2, 
				-1*(int)(image.getHeight()/20 + (fmTop.getHeight() + 2) * (i+.7)));
			
		}
		
		Font fontBottom;
		FontMetrics fmBottom;
		fontSize = 180;
		do{
			width = 0;
			fontSize = fontSize * 4 / 5;
			fontBottom = new Font("Arial", Font.BOLD,fontSize);
			fmBottom = g2d.getFontMetrics(fontBottom);
			for(int i = 0; i < bottom.length; i++){
				int thisWidth = fmBottom.stringWidth(bottom[i]);
				if(thisWidth > width){
					width = thisWidth;
				}
			}
		}while(width > (image.getWidth() * .9) || fmBottom.getHeight() > image.getHeight()/5);
		
		
		for(int i = 0; i < bottom.length; i++){
			g2d = image.createGraphics();
			g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setFont(fontBottom);
			GlyphVector gv = fontBottom.createGlyphVector(g2d.getFontRenderContext(), bottom[i]);
			Shape shape = gv.getOutline();
			g2d.setStroke(new BasicStroke((fontSize > 12)?(int)(fontSize/6.0)*1.0f:2.0f));
			g2d.translate(image.getWidth()/2-fmBottom.stringWidth(bottom[i])/2, 
				(int)(image.getHeight()*19/20 + (fmBottom.getHeight() + 2) * (i-bottom.length+1)));
			g2d.setColor(Color.BLACK);
			g2d.draw(shape);
			g2d.setColor(Color.WHITE);
			g2d.drawString(bottom[i],0,0);
			g2d.translate(-1*(image.getWidth()/2-fmBottom.stringWidth(bottom[i])/2), 
				-1*(int)(image.getHeight()*19/20 + (fmBottom.getHeight() + 2) * (i-bottom.length+1)));
		}
		
		return image;
		
		
	}
}

