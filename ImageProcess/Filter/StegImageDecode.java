package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class StegImageDecode extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Steganography Image Encode";
	public int bitsToUse;
	
	
	public StegImageDecode(int bits){
		bitsToUse = bits;
	}
	
	public String toString(){
		return "Steganography Image Decoding" + bitsToUse + " bits";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j < red[0].length; j++){
				red[i][j] = decode(red[i][j],bitsToUse) << (8-bitsToUse);
				green[i][j] = decode(green[i][j],bitsToUse) << (8-bitsToUse);
				blue[i][j] = decode(blue[i][j],bitsToUse) << (8-bitsToUse);
			}
		}
		
		return toColorImage(red,green,blue);
	}
	
	public int encode(int source, int message, int bits){
		if(message > Math.pow(2,bits)){
			debugPrintln("Error, too large of a message");
		}
		
		int mask = (0xFF << bits) & 0xFF;
		if(message > 0){
			//debugPrintln(source + "->" + ((source & mask) | message));
		}
		return (source & mask) | message;
	}
	
	public int decode(int source, int bits){
		int mask = (0xFF >> (8 -  bits)) & 0xFF;
		return (source & mask);
	}
}

