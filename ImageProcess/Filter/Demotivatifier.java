package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.font.GlyphVector;

public class Demotivatifier extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Demotivator";
	String title;
	String[] subtext;
	
	public Demotivatifier(String title, String subtext){
		this.title = title;
		this.subtext = subtext.split("`");
	}
	
	public String toString(){
		return "Demotivator:" + title;
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		int topSpace = (int)(red.length * 1.0/12);
		int sideSpace = topSpace * 3/2;
		int bottomSpace = topSpace * (2 + subtext.length);
		int [][] newRed = new int[red.length + 2 * sideSpace][];
		int [][] newGreen = new int[red.length + 2 * sideSpace][];
		int [][] newBlue = new int[red.length + 2 * sideSpace][];
		int [][] newAlpha = new int[red.length + 2 * sideSpace][];
		for(int i = 0; i < newRed.length; i++){
			newRed[i] = new int[red[0].length + topSpace + bottomSpace];
			newGreen[i] = new int[red[0].length + topSpace + bottomSpace];
			newBlue[i] = new int[red[0].length + topSpace + bottomSpace];
			newAlpha[i] = new int[red[0].length + topSpace + bottomSpace];
			for(int j = 0; j< newRed[0].length; j++){
				newRed[i][j] = 0;
				newGreen[i][j] = 0;
				newBlue[i][j] = 0;
				newAlpha[i][j] = 255;
				
				if(j >= topSpace && j < red[0].length + topSpace && i >= sideSpace && i < sideSpace + red.length){
					newRed[i][j] = red[i-sideSpace][j-topSpace];
					newGreen[i][j] = green[i-sideSpace][j-topSpace];
					newBlue[i][j] = blue[i-sideSpace][j-topSpace];
					newAlpha[i][j] = alpha[i-sideSpace][j-topSpace];
				}
			}
			
			
		}
		
		image = toAlphaImage(newRed,newGreen,newBlue,newAlpha);
		
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		Font fontTop;
		FontMetrics fmTop;
		int fontSize = 180;
		int width;
		do{
			width = 0;
			fontSize = fontSize * 4 / 5;
			fontTop = new Font("Arial", Font.BOLD,fontSize);
			fmTop = g2d.getFontMetrics(fontTop);
			width = fmTop.stringWidth(title);
		}while(width > (image.getWidth() * .8) || fmTop.getHeight() > image.getHeight()/6);
		
		int titleBottom = topSpace + red[0].length + (int)(fmTop.getHeight()*.7) + ((fontSize > 12)?(int)(fontSize/6.0f):2);
		
		g2d = image.createGraphics();
		g2d.setFont(fontTop);
		//GlyphVector gv = fontTop.createGlyphVector(g2d.getFontRenderContext(), title);
		//Shape shape = gv.getOutline();
		//g2d.setStroke(new BasicStroke((fontSize > 12)?(int)(fontSize/6.0f)*1.0f:2.0f));
		g2d.translate(image.getWidth()/2-fmTop.stringWidth(title)/2, 
			(int)(titleBottom));
		//g2d.setColor(Color.BLACK);
		//g2d.draw(shape);
		g2d.setColor(Color.WHITE);
		g2d.drawString(title,0,0);
		g2d.translate(-1*image.getWidth()/2-fmTop.stringWidth(title)/2, 
			-1*(int)(titleBottom));
		
		
		titleBottom += (int)(fmTop.getHeight() * .45);
		
		Font fontBottom;
		FontMetrics fmBottom;
		fontSize = fontSize * 9 / 10;
		do{
			width = 0;
			fontSize = fontSize - 1;
			fontBottom = new Font("Serif", Font.BOLD,fontSize);
			fmBottom = g2d.getFontMetrics(fontBottom);
			for(int i = 0; i < subtext.length; i++){
				int thisWidth = fmBottom.stringWidth(subtext[i]);
				if(thisWidth > width){
					width = thisWidth;
				}
			}
		}while(width > (image.getWidth() * .8) || fmBottom.getHeight() * 1.1 * subtext.length * 1.05 >= image.getHeight()-titleBottom);
		
		
		for(int i = 0; i < subtext.length; i++){
			g2d = image.createGraphics();
			g2d.setFont(fontBottom);
			//gv = fontBottom.createGlyphVector(g2d.getFontRenderContext(), subtext[i]);
			//shape = gv.getOutline();
			//g2d.setStroke(new BasicStroke((fontSize > 12)?(int)(fontSize/6.0)*1.0f:2.0f));
			g2d.translate(image.getWidth()/2-fmBottom.stringWidth(subtext[i])/2, 
				(int)(titleBottom + fmBottom.getHeight() * 1.1 * (.2 + i) + (image.getHeight()-titleBottom) / 2) 
				- fmBottom.getHeight() * .55 * (subtext.length));
			//g2d.setColor(Color.BLACK);
			//g2d.draw(shape);
			g2d.setColor(Color.WHITE);
			g2d.drawString(subtext[i],0,0);
			g2d.translate(-1*(image.getWidth()/2-fmBottom.stringWidth(subtext[i])/2), 
				-1*(int)(titleBottom + fmBottom.getHeight() * 1.1 * (.2 + i)));
		}
		
		return image;
	}
	
}

