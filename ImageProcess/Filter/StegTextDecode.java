package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class StegTextDecode extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Steganography Text Decode";
	public String text;
	public char [] textArray;
	public int bitsToUse;
	
	public StegTextDecode(){
		text = "";
		bitsToUse = 1;
	}

	public String toString(){
		return "Steganography Text Decoding" + bitsToUse + " bits";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		textArray = new char[red.length * red[0].length * 3 / 8];
		
		//assuming enough pixels to encode the string, fix this.
		int n = 0;
		int mask = 1;
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j < red[0].length; j++){
				bitIntoArray(n++,decode(red[i][j],bitsToUse));
				bitIntoArray(n++,decode(green[i][j],bitsToUse));
				bitIntoArray(n++,decode(blue[i][j],bitsToUse));
			}
		}
		
		finalizeText();
		
		return image;
	}
	
	public boolean nthBit(int n){
		if(n > text.length() * 8){
			return false;
		} else {
			return (text.charAt(n/8) & (1 << n%8)) > 0;
		}
	}
	
	public void addToArray(int position, boolean bit){
		textArray[position/8] = (char)((textArray[position/8] ) | (char)(((bit)?1:0)<< (position%8)));
	}
	
	public void bitIntoArray(int pos, int bit){
		if(pos/8 >= textArray.length){
			return;
		}
		int mask = 0xFF ^ (0x01 << pos%8);
		textArray[pos/8] = (char)((textArray[pos/8] & mask) | (char)(bit << pos%8));
	}
	
	public void finalizeText(){
		for(int i = 0; i < textArray.length;i++){
			if(textArray[i] > (char)(0)){
				//debugPrintln(i + ":" + textArray[i]);
				text += textArray[i];
			}
		}
	}
	
	public int decode(int source, int bits){
		int mask = (0xFF >> (8 -  bits)) & 0xFF;
		return (source & mask);
	}
}

