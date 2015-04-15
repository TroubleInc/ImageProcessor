package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class MeanShrink extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	int zoom;
	
	public MeanShrink(){
		zoom = 2;
	}
	
	public String toString(){
		return "Shrink with Averaging";
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
				newRed[i][j] = (red[i * zoom][j * zoom] + red[i * zoom + 1][j * zoom] + red[i * zoom][j * zoom + 1] + red[i * zoom + 1][j * zoom + 1] ) / 4;
				newGreen[i][j] = (green[i * zoom][j * zoom] + green[i * zoom + 1][j * zoom] + green[i * zoom][j * zoom + 1] + green[i * zoom + 1][j * zoom + 1] ) / 4;
				newBlue[i][j] = (blue[i * zoom][j * zoom] + blue[i * zoom + 1][j * zoom] + blue[i * zoom][j * zoom + 1] + blue[i * zoom + 1][j * zoom + 1] ) / 4;
				newAlpha[i][j] = (alpha[i * zoom][j * zoom] + alpha[i * zoom + 1][j * zoom] + alpha[i * zoom][j * zoom + 1] + alpha[i * zoom + 1][j * zoom + 1] ) / 4;
			}
		}
		return toColorImage(newRed,newGreen,newBlue);
		
	}
}

