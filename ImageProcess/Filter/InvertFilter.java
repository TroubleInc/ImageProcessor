package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class InvertFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Invert Colors";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j < red[i].length; j++){
				red[i][j] = 255 - red[i][j];
				green[i][j] = 255 - green[i][j];
				blue[i][j] = 255 - blue[i][j];
			}
		}
	
		return toAlphaImage(red, green, blue, alpha);
	}
}

