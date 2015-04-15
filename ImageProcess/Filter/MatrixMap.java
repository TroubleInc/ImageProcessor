package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.font.GlyphVector;

public class MatrixMap extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "CharMap";
	public String text;
	public int textSize = 14;
	public boolean average = false;
	public Font textFont;
	public boolean fit = false;
	public int threshold = 0;
	public boolean doublePass = true;
	
	public MatrixMap(){
		textFont = new Font("TakaoGothic", Font.BOLD,textSize);
	}
	
	public String toString(){
		return "Matrixify Image";
	}
	
	public char randomChar(){
		return (char)(0x3040 + (0x30f0 - 0x3040) * Math.random());
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		int x = 0;
		int y = 0;
		
		BufferedImage outImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = outImage.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0,0,image.getWidth(), image.getHeight());
		Font font;
		FontMetrics fm;
		fm = g2d.getFontMetrics(textFont);
		g2d.setFont(textFont);
		int height = fm.getHeight() ;
		int index = 0;
		int width = fm.stringWidth(""+randomChar());
		//background matrix
		while( x < image.getWidth()){
			y = (int)(height * Math.random());
			int r = 0;
			int g = 0; 
			int b = 0;
			while(y < image.getHeight() + height){
				int h = height;
				int w = width;
				
				double rand = Math.random();
				int iHeight = image.getHeight();
				//debugPrintln(1.0*y * (image.getHeight() - y) / (image.getHeight() * image.getHeight()) * 4);
				r += (int)(rand * 50 * y * (iHeight - y) / (iHeight * iHeight) * 4);
				g += (int)(rand * 100 * y * (iHeight - y) / (iHeight * iHeight) * 4);
				b += (int)(rand * 50 * y * (iHeight - y) / (iHeight * iHeight) * 4);
				
				rand = Math.random() + 1.2;
				r = (int)(r/rand);
				g = (int)(g/rand);
				b = (int)(b/rand);
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
				g2d.setColor(new Color(r,g,b));
				g2d.drawString("" + randomChar(),x,y);
				
				//y+= (int)(height *( Math.random()*.5 + 1));
				y+= height;
			}
			x += (int)(width *( Math.random()*Math.random() + .9));
		} 
		
		x = 0;
		y = 0;
		
		while( x < image.getWidth()){
			y = (int)(height * Math.random());
			int r = 0;
			int g = 0; 
			int b = 0;
			while(y < image.getHeight() + height){
				int h = height;
				int w = width;
				if(x+width > image.getWidth()){
					w = image.getWidth() - x;
				}
				if(y > image.getHeight()){
					h = y - image.getHeight();
				}
				int size = h * w;
				r *= size;
				g *= size;
				b *= size;
				for(int i = 0; i < height; i++){
					for(int j = 0; j < width; j++){
						if(x+j < red.length && y-i < red[0].length && x+j >= 0 && y-i >= 0){
							r += red[x+j][y-i];
							g += green[x+j][y-i];
							b += blue[x+j][y-i];
						}
					}
				}
				
				double rand = Math.random();
				/*
				r += (int)(rand * 80 * size * y * (image.getHeight() - y) / (image.getHeight() * image.getHeight()) * 4);
				g += (int)(rand * 160 * size * y * (image.getHeight() - y) / (image.getHeight() * image.getHeight()) * 4);
				b += (int)(rand * 80 * size * y * (image.getHeight() - y) / (image.getHeight() * image.getHeight()) * 4);
				*/
				rand = Math.random() + 1.5;
				r /= (int)(size*rand);
				g /= (int)(size*rand);
				b /= (int)(size*rand);
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
				g2d.setColor(new Color(r,g,b));
				g2d.drawString("" + randomChar(),x,y);
				
				//y+= (int)(height *( Math.random()*.5 + 1));
				y+= height;
			}
			x += (int)(width *( Math.random()*Math.random() + .9));
		} 
		x = 0;
		y = 0;



		
		return outImage;
	}
	
}

