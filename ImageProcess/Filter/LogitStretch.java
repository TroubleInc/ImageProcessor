package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class LogitStretch extends ImageFilter{
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
				red[i][j] = fixRange((int)(32*Math.log((red[i][j]/255.0)/(1.0-(red[i][j]/255.0)))+128));
				green[i][j] = fixRange((int)(32*Math.log((green[i][j]/255.0)/(1.0-(green[i][j]/255.0)))+128));
				blue[i][j] = fixRange((int)(32*Math.log((blue[i][j]/255.0)/(1.0-(blue[i][j]/255.0)))+128));
			}
		}
		return toAlphaImage(red,green,blue,alpha);
	}

	public static int fixRange(int in){
		if(in > 255){
			in = 255;
		} else if(in < 0){
			in = 0;
		}
		return in;
	}
}

