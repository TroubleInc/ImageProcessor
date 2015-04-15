package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class BWBThreshold extends ImageFilter{
	int mask = 255;
	int threshold,saturation;
	public String name = "High-Low Threshold";
	
	public BWBThreshold(int low, int high){
		this.threshold = low;
		this.saturation = high;
	}
	
	public String toString(){
		return "Three Level Threshold " + threshold + "/" + saturation;
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		for(int i = grey.length-1; i >= 0; i--){
			for(int j = grey[0].length-1; j >= 0; j--){
				if(grey[i][j] < threshold || grey[i][j] > saturation){
					grey[i][j] = 0;
				 } else {
					grey[i][j] = 255;
				 }
			}
		}
		return toGreyImage(grey);
	}
}

