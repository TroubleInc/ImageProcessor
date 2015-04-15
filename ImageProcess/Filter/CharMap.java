package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.font.GlyphVector;

public class CharMap extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "CharMap";
	public String text;
	public int textSize = 12;
	public boolean average = false;
	public Font textFont;
	public boolean fit = false;
	public int threshold = 0;
	
	public CharMap(String text){
		this.text = text;
		textFont = new Font("Arial", Font.BOLD,textSize);
	}
	
	public CharMap(String text, boolean background){
		this.text = text;
		average = background;
		textFont = new Font("Arial", Font.BOLD,textSize);
	}
	
	public CharMap(String text, boolean background, int size){
		this.text = text;
		average = background;
		textSize = size;
		textFont = new Font("Arial", Font.BOLD,textSize);
	}
	
	public CharMap(String text, boolean background, Font font){
		this.text = text;
		average = background;
		textFont = font;
	}
	
	public CharMap(String text, boolean background, Font font, int threshold){
		this(text,background,font);
		fit = true;
		this.threshold = threshold;
	}
	
	public String toString(){
		return "Character Map";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		int x = 0;
		//int y = image.getHeight()-1;
		int y = 0;
		
		int br = 0;
		int bg = 0;
		int bb = 0;
		
		BufferedImage outImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = outImage.createGraphics();
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		if(average){
			br = 0;
			bg = 0; 
			bb = 0;
			for(int i = 0; i < image.getHeight(); i++){
				for(int j = 0; j < image.getWidth(); j++){
					br += red[j][i];
					bg += green[j][i];
					bb += blue[j][i];
				}
			}
			int size = image.getHeight() * image.getWidth();
			br /= size;
			bg /= size;
			bb /= size;
			g2d.setColor(new Color(br,bg,bb));
		} else {
			br = 255;
			bg = 255;
			bb = 255;
			g2d.setColor(Color.WHITE);
		}
		g2d.fillRect(0,0,image.getWidth(), image.getHeight());
		g2d.setColor(Color.BLACK);
		Font font;
		FontMetrics fm;
		fm = g2d.getFontMetrics(textFont);
		g2d.setFont(textFont);
		int height = fm.getHeight() * 35/50;
		int index = 0;
		int spaceSize = fm.stringWidth("M M") - 2 * fm.stringWidth("M");
		y+= height;
		while(y < image.getHeight() + height){
			x = 0;
			while( x < image.getWidth()){
				int width = 0;
				if(text.charAt(index) != ' '){
					width = fm.stringWidth("" + text.charAt(index));
				} else {
					width = spaceSize;
				}
				int r = 0;
				int g = 0; 
				int b = 0;
				for(int i = 0; i < height; i++){
					for(int j = 0; j < width; j++){
						if(x+j < red.length && y-i < red[0].length && x+j >= 0 && y-i >= 0){
							r += red[x+j][y-i];
							g += green[x+j][y-i];
							b += blue[x+j][y-i];
						}
					}
				}
				int h = height;
				int w = width;
				if(x+width > image.getWidth()){
					w = image.getWidth() - x;
				}
				if(y > image.getHeight()){
					h = y - image.getHeight();
				}
				int size = h * w;
				r /= size;
				g /= size;
				b /= size;
				if(r > 255){
					r = 255;
				} else if (r < 0){
					r = 0;
				}
				if(g > 255){
					g = 255;
				} else if (g < 0){
					g = 0;
				}
				if(b > 255){
					b = 255;
				} else if (b < 0){
					b = 0;
				}
				if(!fit || ((Math.abs(r-br) + Math.abs(g-bg) + Math.abs(b-bb))>(3*threshold))){
					g2d.setColor(new Color(r,g,b));
					g2d.drawString("" + text.charAt(index),x,y);
					index = (index + 1) % text.length();
				}
				x += width;
			}
			y += height;
		} 
		
		return outImage;
	}
	
}

