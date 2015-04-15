package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class PTileLogisticStretch extends ImageFilter{
	public String name = "Logistic Stretch";
	private double lowEnd;
	private double highEnd;
	
	public PTileLogisticStretch(double low, double high){
		lowEnd = low;
		highEnd = high;
	}
	
	public String toString(){
		return "Percentile Logistic Stretch " + (-1 * lowEnd) + "% dark and " + (-1*highEnd) + "% light";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		int[] grey;
		grey = toHist(toGreyMatrix(image));
		int count = 0;
		
		for(int i = 0; i < grey.length; i++){
			count += grey[i];
		}
		int cumCount = 0;
		int spot = 0;
		while(lowEnd<0){
			cumCount += grey[spot];
			if(cumCount > lowEnd/-100.0 * count){
				lowEnd = spot;
			} else {
				spot++;
			}
			if(spot > 255){
				lowEnd = 0.0;
			}
		}
		
		spot = grey.length - 1;
		cumCount = 0;
		while(highEnd<=0){
			cumCount += grey[spot];
			if(cumCount > highEnd/-100.0 * count){
				highEnd = spot;
			} else {
				spot--;
			}
			if(spot<0){
				highEnd = 255.0;
			}
		}
		
		double middle = (lowEnd + highEnd) / 2.0;
		double adder = -1.0/(1.0 + Math.exp(-4.0 * lowEnd/255.0 + 2));
		double multiplier = 255/(1.0/(1.0+Math.exp(-4.0*highEnd/255+2)));
		
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j<red[0].length; j++){
				int r = (int)(multiplier * (1.0/(1.0+Math.exp(-4.0 * red[i][j]/255.0 + 2))+adder));
				int g = (int)(multiplier * (1.0/(1.0+Math.exp(-4.0 * green[i][j]/255.0 + 2))+adder));
				int b = (int)(multiplier * (1.0/(1.0+Math.exp(-4.0 * blue[i][j]/255.0 + 2))+adder));
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

