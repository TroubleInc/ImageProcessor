package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageDiff extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Image Diff";
	public BufferedImage diffImage;
	
	
	public ImageDiff (BufferedImage diffImage){
		this.diffImage = diffImage;
	}
	
	public String toString(){
		return "Difference between two images";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] diffred = toRedMatrix(diffImage);
		int [][] diffgreen = toGreenMatrix(diffImage);
		int [][] diffblue = toBlueMatrix(diffImage);
		
		if(red.length < diffred.length && red[0].length < diffred[0].length){
			int [][] temp = red;
			red = diffred;
			diffred = temp;
			
			temp = green;
			green = diffgreen;
			diffgreen = temp;
			
			temp = blue;
			blue = diffblue;
			diffblue = temp;
		}
		
		for(int i = 0; i < red.length && i < diffred.length; i++){
			for(int j = 0; j < red[0].length && j < diffred[0].length; j++){
				red[i][j] = Math.abs(red[i][j] - diffred[i][j]);
				green[i][j] = Math.abs(green[i][j] - diffgreen[i][j]);
				blue[i][j] = Math.abs(blue[i][j] - diffblue[i][j]);
			}
		}
		
		return toColorImage(red,green,blue);
	}
}

