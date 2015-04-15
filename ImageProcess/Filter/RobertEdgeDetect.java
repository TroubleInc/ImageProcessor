package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class RobertEdgeDetect extends ImageFilter{
	
	public String toString(){
		return "Roberts Edge Detect";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		int [][] newRed,newGreen,newBlue,newAlpha;
		newRed = new int[red.length - 1][];
		newGreen = new int[red.length - 1][];
		newBlue = new int[red.length - 1][];
		newAlpha = new int[red.length - 1][];
		
		for(int i = 0; i < red.length - 1; i++){
			newRed[i] = new int[red[i].length - 1];
			newGreen[i] = new int[green[i].length - 1];
			newBlue[i] = new int[blue[i].length - 1];
			newAlpha[i] = new int[alpha[i].length - 1];
			for(int j = 0; j < red[i].length - 1; j++){
				newRed[i][j] = (Math.abs(red[i][j] - red[i+1][j+1]) + Math.abs(red[i+1][j] - red[i][j+1]))&255;
				newGreen[i][j] = (Math.abs(green[i][j] - green[i+1][j+1]) + Math.abs(green[i+1][j] - green[i][j+1]))&255;
				newBlue[i][j] = (Math.abs(blue[i][j] - blue[i+1][j+1]) + Math.abs(blue[i+1][j] - blue[i][j+1]))&255;
				newAlpha[i][j] = alpha[i][j];
			}
		}
		return toAlphaImage(newRed,newGreen,newBlue,newAlpha);
	}
}
