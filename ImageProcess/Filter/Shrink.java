package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class Shrink extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	int zoom;
	
	public Shrink(int zoomAmount){
		zoom = zoomAmount;
	}
	
	public String toString(){
		return "Shrink " + zoom + "x";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		int [][] newRed = new int[red.length / zoom][];
		int [][] newGreen = new int[green.length / zoom][];
		int [][] newBlue = new int[blue.length / zoom][];
		int [][] newAlpha = new int[alpha.length / zoom][];
		for(int i = 0; i < newRed.length; i++){
			newRed[i] = new int[red[0].length / zoom];
			newGreen[i] = new int[red[0].length / zoom];
			newBlue[i] = new int[red[0].length / zoom];
			newAlpha[i] = new int[red[0].length / zoom];
			for(int j = 0; j< newRed[0].length; j++){
				newRed[i][j] = red[i * zoom][j * zoom];
				newGreen[i][j] = green[i * zoom][j * zoom];
				newBlue[i][j] = blue[i * zoom][j * zoom];
				newAlpha[i][j] = alpha[i * zoom][j * zoom];
			}
		}
		return toAlphaImage(newRed,newGreen,newBlue,newAlpha);
		
	}
}

