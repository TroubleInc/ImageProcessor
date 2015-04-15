package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class HistogramStretch extends ImageFilter{
	double lowEnd;
	double highEnd;
	public String name = "Histogram Stretch";
	
	public HistogramStretch(double low, double high){
		lowEnd = low;
		highEnd = high;
	}
	
	public String toString(){
		return "Histogram Stretch low:" + ((lowEnd<0)?(-1*lowEnd)+"%":lowEnd+"") +" high:" + ((highEnd<0)?(-1*highEnd)+"%":highEnd+"");
	}
		
	public BufferedImage filter(BufferedImage image){
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		if(lowEnd < 0 || highEnd <= 0){
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
					highEnd = 255.5;
				}
			}
		}
		
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j<red[0].length; j++){
				int r = (int)(255.0 / (highEnd-lowEnd) * (red[i][j]-lowEnd));
				int g = (int)(255.0 / (highEnd-lowEnd) * (green[i][j]-lowEnd));
				int b = (int)(255.0 / (highEnd-lowEnd) * (blue[i][j]-lowEnd));
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

