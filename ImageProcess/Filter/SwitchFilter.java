package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class SwitchFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	public static final int RG = 1;
	public static final int RB = 2; 
	public static final int GB = 3;
	public static final int YCRCB_TO_RGB = 4;
	public static final int RGB_TO_YCRCB = 5;
	int type;
	
	public SwitchFilter(int type){
		this.type = type;
	}
	
	public String toString(){
		return ((type == RG)?"R<->G":"")+((type == RB)?"R<->B":"")+((type == GB)?"G<->B":"")+((type == YCRCB_TO_RGB)?"YCrCb->RGB":"")+((type == RGB_TO_YCRCB)?"RGB<->YCrCb":"");
	}
	
	public BufferedImage filter(BufferedImage image){
		resetImage();
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		resetImage();
		
		//1 = R<->G
		if(type == RG){
			return toAlphaImage(green, red, blue, alpha);
		}
		//2 = R<->B
		if(type == RB){
			return toAlphaImage(blue,green,red, alpha);
		}
		//3 = G<->B
		if(type == GB){
			return toAlphaImage(red,blue,green, alpha);
		}
		
		if(type == YCRCB_TO_RGB){
			for(int i = 0; i < red.length; i++){
				for(int j = 0; j < red[i].length; j++){
					int y = red[i][j];
					int pb = green[i][j]-128;
					int pr = blue[i][j]-128;
					//int r = (y<<8) + 359 * pr >>8;
					//int g = (y<<8) - 88 * pb - 183 * pr >> 8;
					//int b = (y<<8) + 454 * pb >>8;
					
					int r = (int)(y + 1.402 * pr);
					int g = (int)(y - .34414 * pb - .71414*pr);
					int b = (int)(y + 1.772 *pb);
					
					if(r > 255){
						r = 255;
					} else if(r < 0) {
						r = 0;
					}
					if(g > 255){
						g = 255;
					} else if(g < 0) {
						g = 0;
					}
					if(b > 255){
						b = 255;
					} else if(b < 0) {
						b = 0;
					}
					
					red[i][j] = r;
					green[i][j] = g;
					blue[i][j] = b;
				}
			}
		
			return toAlphaImage(red, green, blue, alpha);
		}
		
		if(type == RGB_TO_YCRCB){
			for(int i = 0; i < red.length; i++){
				for(int j = 0; j < red[i].length; j++){
					int r = red[i][j];
					int g = green[i][j];
					int b = blue[i][j];
					int y = (int)(.299 * r + .587 * g + .114 * b);
					int pb = (int)(128 - .168736 * r - .331264 * g + .5 * b);
					int pr = (int)(128 + .5 * r - .418688 * g - 0.081312 * b);
					
					//int y = (int)(1.0/138830.0 *(41541.0 * r  + 81493.0 * g + 15796.0 * b));
					//int pb = (int)(1.0/138830.0 *(-23424.0 *r -45952.0 *g + 69376.0*b));
					//int pr = (int)(1.0/138830.0 *( 69376.0 *r -58112.0*g -11264.0 * b));
					
					if(y > 255){
						y = 255;
					} else if(y < 0) {
						y = 0;
					}
					if(pb > 255){
						pb = 255;
					} else if(pb < 0) {
						pb = 0;
					}
					if(pr > 255){
						pr = 255;
					} else if(pr < 0) {
						pr = 0;
					}
					
					red[i][j] = y;
					green[i][j] = pb;
					blue[i][j] = pr;
				}
			}
		
			return toAlphaImage(red, green, blue, alpha);
		}
		
		return toAlphaImage(red, green, blue, alpha);
	}
}

