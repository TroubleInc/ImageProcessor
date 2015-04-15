package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class SaturationFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Saturation Channel";
	}
	
	public BufferedImage filter(BufferedImage image){
		double[][] sat = toSaturationMatrix(image);
		int [][] scaledSat = new int[sat.length][];
		for(int i = 0; i < sat.length; i++){
			scaledSat[i]=new int[sat[0].length];
			for(int j = 0; j < sat[0].length;j++){
				scaledSat[i][j] = (int)(sat[i][j] * 255.0);
			}
		}
		return toGreyImage(scaledSat);
	}
}

