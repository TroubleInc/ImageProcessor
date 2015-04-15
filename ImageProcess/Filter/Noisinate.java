package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class Noisinate extends ImageFilter{
	private double percentNoise;
	private int noiseRange;
	public String name = "Noisinate";
	
	public Noisinate(double percent, int range){
		percentNoise = percent;
		noiseRange = range;
	}
	
	public String toString(){
		return "Add " + percentNoise + "% noise of volume " + noiseRange;
	}
	
	public BufferedImage filter(BufferedImage image){
		int r,g,b,rgb,noise,x,y;
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		int mask = 255;
		for(int i = 0; i < image.getWidth() * image.getHeight() * percentNoise; i++){
			x = (int)(Math.random() * image.getWidth());
			y = (int)(Math.random() * image.getHeight());
			noise = (int)(Math.random() * 2 * noiseRange - noiseRange);
			r = red[x][y] + noise;
			b = blue[x][y] + noise;
			g = green[x][y] + noise;
			if(r < 0){
				r = 0;
			}
			if(r > 255){
				r = 255;
			}
			if(g < 0){
				g = 0;
			}
			if(g > 255){
				g = 255;
			}
			if(b < 0){
				b = 0;
			}
			if(b > 255){
				b = 255;
			}
			red[x][y] = r;
			green[x][y] = g;
			blue[x][y] = b;
		}
		return toAlphaImage(red,green,blue,alpha);
	}
}
