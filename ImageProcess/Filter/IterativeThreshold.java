package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class IterativeThreshold extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "iterative thresholding";
	
	public String toString(){
		return "Iterative Threshold";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		long below = 1,above = 1,sumBelow = 255,sumAbove = 0;
		boolean done = false;
		int threshold = -1000000;
		int count = 0;
		while(Math.abs(threshold - ((int)((1.0 * sumAbove / above + 1.0 * sumBelow / below)/2.0))) > 3 || count < 5){
			count++;
			threshold = (int)((1.0 * sumAbove / above + 1.0 * sumBelow / below)/2);
			above = 0;
			below = 0;
			sumBelow = 0;
			sumAbove = 0;
			for(int i = 0; i < grey.length; i++){
				for(int j = 0; j < grey[i].length; j++){
					if(grey[i][j] < threshold){
						sumBelow += grey[i][j];
						below ++;
					} else {
						sumAbove += grey[i][j];
						above++;
					}
				}
			}
			threshold = (int)((1.0 * sumAbove / above + 1.0 * sumBelow / below)/2);
		}
		for(int i = 0; i < grey.length; i++){
			for(int j = 0; j < grey[i].length; j++){
				if(grey[i][j] < threshold){
					grey[i][j] = 0;
				} else {
					grey[i][j] = 255;
				}
			}
		}
		return toGreyImage(grey);
	}
}

