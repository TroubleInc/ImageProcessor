package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class GaussianSharpen extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public int radius;
	public double intensity;
	
	public GaussianSharpen(int radius, double intensity){
		this.radius = radius;
		this.intensity = intensity;
	}
	
	public String toString(){
		return "Gaussian Sharpen r:" + radius + " intensity:" + intensity;
	}
	
	public BufferedImage filter(BufferedImage image){
		double [][] mask = new double[radius*2 + 1][];
		double sum = 0;
		for(int i = 0; i <= radius; i++){
			mask[radius - i] = new double[radius * 2 + 1];
			mask[radius + i] = new double[radius * 2 + 1];
			
			for(int j = 0; j <= radius; j++){
				if(i !=0 || j !=0){
					//double val = -1.0 * intensity / Math.sqrt(.5 * Math.PI * radius * radius) * Math.exp(-1.0 * (i * i + j * j) / (.5 * radius * radius));
					//double val = -1.0 * intensity * Math.exp(-1.0 * (i * i + j * j) / (.5 * radius * radius));
					double val = -1.0 * intensity * Math.exp(-1.0 * (i * i + j * j));
					mask[radius - i][radius - j] = val;
					mask[radius - i][radius + j] = val;
					mask[radius + i][radius - j] = val;
					mask[radius + i][radius + j] = val;
					if(i!= 0){
						sum += -2 * val;
					}
					if(j!=0){
						sum += -2 * val;
					}
					
				}
			}
		}
		
		mask[radius][radius] = 1 + sum;
		
		return (new ImageMask(mask,ImageMask.NORMALIZE)).filter(image);
	}
}

