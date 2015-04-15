package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class AdaptiveThresholding extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Adaptive Thresholding";
	public int horizontalSlices;
	public int verticalSlices;
	
	public AdaptiveThresholding(int horiz, int vert){
		horizontalSlices = horiz;
		verticalSlices = vert;
	}
	
	public String toString(){
		return "Adaptive Thresholding " + horizontalSlices + "x" + verticalSlices;
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		for(int h = 0; h < horizontalSlices; h++){
			for(int v = 0; v < verticalSlices; v++){
				long below = 1,above = 1,sumBelow = 255,sumAbove = 0;
				boolean done = false;
				double threshold = -1000000;
				int count = 0;
				while(Math.abs(threshold - (((1.0 * sumAbove / above + 1.0 * sumBelow / below)/2.0))) > 3 || count < 5){
					count++;
					threshold = ((1.0 * sumAbove / above + 1.0 * sumBelow / below)/2);
					above = 0;
					below = 0;
					sumBelow = 0;
					sumAbove = 0;
					for(int i = v * (grey.length / verticalSlices); i < ((v==verticalSlices-1)?grey.length:((v+1) * (grey.length / verticalSlices))); i++){
						for(int j = h * (grey[i].length / horizontalSlices); j < ((h==horizontalSlices-1)?grey[i].length:(h + 1) * (grey[i].length / horizontalSlices)); j++){
							if(grey[i][j] < threshold){
								sumBelow += grey[i][j];
								below ++;
							} else {
								sumAbove += grey[i][j];
								above++;
							}
						}
					}
				}
				for(int i = v * (grey.length / verticalSlices); i < ((v==verticalSlices-1)?grey.length:((v+1) * (grey.length / verticalSlices))); i++){
					for(int j = h * (grey[i].length / horizontalSlices); j < ((h==horizontalSlices-1)?grey[i].length:(h + 1) * (grey[i].length / horizontalSlices)); j++){
						if(grey[i][j] < threshold){
							grey[i][j] = 0;
						} else {
							grey[i][j] = 255;
						}
					}
				}
			}
		}
		return toGreyImage(grey);
	}
}

