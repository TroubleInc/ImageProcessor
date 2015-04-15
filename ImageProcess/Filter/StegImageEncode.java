package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class StegImageEncode extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Steganography Image Encode";
	public BufferedImage stegImage;
	public int bitsToUse;
	
	
	public StegImageEncode(BufferedImage image, int bits){
		this.stegImage = image;
		bitsToUse = bits;
	}
	
	public String toString(){
		return "Steganography Image Encoding" + bitsToUse + " bits";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] stegred = toRedMatrix(stegImage);
		int [][] steggreen = toGreenMatrix(stegImage);
		int [][] stegblue = toBlueMatrix(stegImage);
		
		for(int i = 0; i < red.length && i < stegred.length; i++){
			for(int j = 0; j < red[0].length && j < stegred[0].length; j++){
				red[i][j] = encode(red[i][j],(stegred[i][j] >> (8- bitsToUse)),bitsToUse) ;
				green[i][j] = encode(green[i][j],(steggreen[i][j] >> (8- bitsToUse)),bitsToUse);
				blue[i][j] = encode(blue[i][j],(stegblue[i][j] >> (8- bitsToUse)),bitsToUse);
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
}

