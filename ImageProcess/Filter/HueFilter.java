package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class HueFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Hue Channel";
	}
	
	public BufferedImage filter(BufferedImage image){
		double[][] hue = toHueMatrix(image);
		int [][] scaledHue = new int[hue.length][];
		for(int i = 0; i < hue.length; i++){
			scaledHue[i]=new int[hue[0].length];
			for(int j = 0; j < hue[0].length;j++){
				scaledHue[i][j] = ImageFilter.fixRange((int)(hue[i][j] * 255.0/360));
			}
		}
		return toGreyImage(scaledHue);
	}
}

