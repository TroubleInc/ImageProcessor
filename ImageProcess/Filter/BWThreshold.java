package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class BWThreshold extends ImageFilter{
	int threshold;
	public String name = "Threshold";
	public BWThreshold(int threshold){
		this.threshold = threshold;
	}
	
	public String toString(){
		return "Threshold " + threshold;
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		for(int i = grey.length-1; i >= 0; i--){
			for(int j = grey[i].length-1; j >= 0; j--){
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

