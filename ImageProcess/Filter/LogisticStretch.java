package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class LogisticStretch extends ImageFilter{
	public String name = "Logistic Stretch";
	
	public String toString(){
		return "Logistic Stretch Contrast Adjustment";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j<red[0].length; j++){
				int r = (int)(336.1/(1.0+Math.exp(-4.0 * red[i][j]/255.0 + 2))-40.06);
				int g = (int)(336.1/(1.0+Math.exp(-4.0 * green[i][j]/255.0 + 2))-40.06);
				int b = (int)(336.1/(1.0+Math.exp(-4.0 * blue[i][j]/255.0 + 2))-40.06);
				if(r < 0){
					r = 0;
				}
				if(r > 255){
					r = 255;
				}
				if(g < 0){
					g = 0;
				}
				if(g > 255){
					g = 255;
				}
				if(b < 0){
					b = 0;
				}
				if(b > 255){
					b = 255;
				}
				red[i][j] = (int)r;
				green[i][j] = (int)g;
				blue[i][j] = (int)b;
			}
		}
		return toAlphaImage(red,green,blue,alpha);
	}
	
}

