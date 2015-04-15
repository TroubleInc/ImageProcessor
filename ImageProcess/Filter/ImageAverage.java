package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageAverage extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Image Average";
	public BufferedImage diffImage;
	
	
	public ImageAverage (BufferedImage diffImage){
		this.diffImage = diffImage;
	}
	
	public String toString(){
		return "Average of images";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		int [][] diffred = toRedMatrix(diffImage);
		int [][] diffgreen = toGreenMatrix(diffImage);
		int [][] diffblue = toBlueMatrix(diffImage);
		int [][] diffalpha = toBlueMatrix(diffImage);
		
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
			
			temp = alpha;
			alpha = diffalpha;
			diffalpha = temp;
		}
		
		for(int i = 0; i < red.length && i < diffred.length; i++){
			for(int j = 0; j < red[0].length && j < diffred[0].length; j++){
				red[i][j] = Math.abs(red[i][j] + diffred[i][j])/2;
				green[i][j] = Math.abs(green[i][j] + diffgreen[i][j])/2;
				blue[i][j] = Math.abs(blue[i][j] + diffblue[i][j])/2;
				alpha[i][j] = Math.abs(alpha[i][j] + diffalpha[i][j])/2;
			}
		}
		
		return toAlphaImage(red,green,blue,alpha);
	}
}

