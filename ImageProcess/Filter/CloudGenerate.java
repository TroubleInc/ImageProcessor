package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class CloudGenerate extends ImageFilter{
	private double smoothness;
	private double uniformity = .99;
	public String name = "Cloud Generator";
	
	public CloudGenerate(double smoothness, double uniformity){
		this.smoothness = smoothness;
		this.uniformity = uniformity;
	}
	
	public String toString(){
		return "Generate Cloud (" + smoothness + "," + uniformity + ")";
	}
	
	public BufferedImage filter(BufferedImage image){
		int g,noise,x,y;
		int [][] grey = toGreyMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		int mask = 255;
		
		debugPrintln((uniformity/2)+"");
		debugPrintln(((uniformity/2)/(1-uniformity/2))+"");
		
		for(x = 0; x < image.getWidth(); x++){
			for(y = 0; y < image.getHeight(); y++){
				g = (int)(Math.random() * 255);
				if((x > 0 && Math.random() < (uniformity/3)) || (y == 0 && x > 0 && Math.random() < ((uniformity/3)/(1-uniformity/3)))){
					g = grey[x-1][y];
				} else if(y > 0 && Math.random() < ((uniformity/3)/(1-uniformity/3))){
					g = grey[x][y-1];
				} else if(x > 0 && y > 0 && Math.random() < ((uniformity/3)/(1-2*uniformity/3))){
					g = (grey[x-1][y] + grey[x][y-1]) /2;
				}
				grey[x][y] = g;
			}
		}
		
		return toAlphaImage(grey,grey,grey,alpha);
	}
}
