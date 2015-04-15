package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class TiltShift extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public int radius;
	public double intensity;
	
	public TiltShift(){
		
	}
	
	public String toString(){
		return "Tilt Shift";
	}
	
	public BufferedImage filter(BufferedImage image){
		double [][] mask = new double[radius*2 + 1][];
		double sum = 0;
		for(int i = 0; i <= radius; i++){
			mask[radius - i] = new double[radius * 2 + 1];
			mask[radius + i] = new double[radius * 2 + 1];
			
			for(int j = 0; j <= radius; j++){
				if(i !=0 || j !=0){
					double val = 1.0 * Math.exp(-1.0 * (i * i + j * j) / intensity);
					mask[radius - i][radius - j] = val;
					mask[radius - i][radius + j] = val;
					mask[radius + i][radius - j] = val;
					mask[radius + i][radius + j] = val;
				}
			}
		}
		
		return (new ImageMask(mask,ImageMask.NORMALIZE+ImageMask.DOALPHA)).filter(image);
	}
}

