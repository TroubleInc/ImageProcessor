package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class PTileThreshold extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "percentile thresholding";
	public double percentile;
	
	public PTileThreshold(double percent){
		percentile = percent;
	}
	
	public String toString(){
		return "Percentile Threshold " + (int)(percentile*100) + "% white";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		long below,above;
		boolean done = false;
		for(int threshold = 255; threshold > 0 && !done; threshold--){
			above = 0;
			below = 0;
			for(int i = 0; i < grey.length; i++){
				for(int j = 0; j < grey[i].length; j++){
					if(grey[i][j] < threshold){
						below ++;
					} else {
						above++;
					}
				}
			}
			if(1.0 * above / (grey.length * grey[0].length) >= percentile){
				for(int i = 0; i < grey.length; i++){
					for(int j = 0; j < grey[i].length; j++){
						if(grey[i][j] < threshold){
							grey[i][j] = 0;
						} else {
							grey[i][j] = 255;
						}
					}
				}
				done = true;
			}
		}
		
		return toGreyImage(grey);
	}
}

