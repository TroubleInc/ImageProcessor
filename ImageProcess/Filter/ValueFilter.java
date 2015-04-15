package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ValueFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Value Channel";
	}
	
	public BufferedImage filter(BufferedImage image){
		double[][] val = toValueMatrix(image);
		int [][] scaledVal = new int[val.length][];
		for(int i = 0; i < val.length; i++){
			scaledVal[i]=new int[val[0].length];
			for(int j = 0; j < val[0].length;j++){
				scaledVal[i][j] = (int)(val[i][j] * 255.0);
			}
		}
		return toGreyImage(scaledVal);
	}
}

