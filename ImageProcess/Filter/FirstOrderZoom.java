package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class FirstOrderZoom extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	int zoom = 2;
	
	public FirstOrderZoom(){
	}
	
	public String toString(){
		return "First Order Zoom";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] newRed = new int[red.length * zoom][];
		int [][] newGreen = new int[green.length * zoom][];
		int [][] newBlue = new int[blue.length * zoom][];
		for(int i = 0; i < newRed.length; i++){
			newRed[i] = new int[red[0].length * zoom];
			newGreen[i] = new int[red[0].length * zoom];
			newBlue[i] = new int[red[0].length * zoom];
			for(int j = 0; j< newRed[0].length; j++){
				if((i%zoom == 0 && j%zoom == 0) || (i == newRed.length - 1 && j%zoom == 0) || (j == newRed[0].length - 1 && i%zoom == 0)){
					//debugPrintln("c" + i + ":" + j);
					newRed[i][j] = red[i/zoom][j/zoom];
					newGreen[i][j] = green[i/zoom][j/zoom];
					newBlue[i][j] = blue[i/zoom][j/zoom];
				} else if((i%zoom == 0 && j%zoom == 1) || (i == newRed.length - 1 && j%zoom == 1 && j != newRed[0].length - 1)) {
					//debugPrintln("ij" + i + ":" + j);
					newRed[i][j] = (red[i/zoom][j/zoom] + red[i/zoom][j/zoom+1])/2;
					newGreen[i][j] = (green[i/zoom][j/zoom] + green[i/zoom][j/zoom+1])/2;
					newBlue[i][j] = (blue[i/zoom][j/zoom] + blue[i/zoom][j/zoom+1])/2;
				} else if((j%zoom == 0&& i%zoom == 1) || (j == newRed[0].length && i%zoom == 1) && j != newRed[0].length - 1) {
					//debugPrintln("ii" + i + ":" + j);
					newRed[i][j] = (red[i/zoom][j/zoom] + red[i/zoom+1][j/zoom])/2;
					newGreen[i][j] = (green[i/zoom][j/zoom] + green[i/zoom+1][j/zoom])/2;
					newBlue[i][j] = (blue[i/zoom][j/zoom] + blue[i/zoom+1][j/zoom])/2;
				} else if((i%zoom == 1 && j%zoom == 1) && !(i == newRed.length - 1 || j == newRed[0].length - 1)){
					//debugPrintln("ib" + i + ":" + j);
					newRed[i][j] = (red[i/zoom][j/zoom] + red[i/zoom+1][j/zoom] + red[i/zoom][j/zoom+1] + red[i/zoom+1][j/zoom+1])/4;
					newGreen[i][j] = (green[i/zoom][j/zoom] + green[i/zoom+1][j/zoom] + green[i/zoom][j/zoom+1] + green[i/zoom+1][j/zoom+1])/4;
					newBlue[i][j] = (blue[i/zoom][j/zoom] + blue[i/zoom+1][j/zoom] + blue[i/zoom][j/zoom+1] + blue[i/zoom+1][j/zoom+1])/4;
				} else {
					//debugPrintln("c" + i + ":" + j);
					newRed[i][j] = red[i/zoom][j/zoom];
					newGreen[i][j] = green[i/zoom][j/zoom];
					newBlue[i][j] = blue[i/zoom][j/zoom];
				}
			}
		}
		return toColorImage(newRed,newGreen,newBlue);
		
	}
}

