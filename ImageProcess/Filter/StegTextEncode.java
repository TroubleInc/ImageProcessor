package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class StegTextEncode extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Steganography Text Encode";
	public String text;
	public int bitsToUse;
	
	
	public StegTextEncode(String hiddenText){
		text = hiddenText;
		bitsToUse = 1;
	}
	
	public String toString(){
		return "Steganography Text Encoding " + bitsToUse + " bits";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		//assuming enough pixels to encode the string, fix this.
		int n = 0;
		//int mask = 254;
		int mask = 0xFE;
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j < red[0].length; j++){
				red[i][j] = encode(red[i][j], (nthBit(n++))?1:0, bitsToUse);
				green[i][j] = encode(green[i][j], (nthBit(n++))?1:0, bitsToUse);;
				blue[i][j] = encode(blue[i][j], (nthBit(n++))?1:0, bitsToUse);;
			}
		}
		
		return toColorImage(red,green,blue);
	}
	
	public boolean nthBit(int n){
		if(n >= text.length() * 8){
			return false;
		} else {
			//debugPrintln(n + ":" + ((text.charAt(n/8) & (1 << n%8)) > 0));
			return (text.charAt(n/8) & (1 << n%8)) > 0;
		}
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

