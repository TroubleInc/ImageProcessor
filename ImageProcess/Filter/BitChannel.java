package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class BitChannel extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Steganography Image Encode";
	public int bit;
		
	public BitChannel(int bits){
		bit = bits;
	}
	
	public String toString(){
		return "Extract " + bit + "th bit";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		int mask = 0x01 << bit;
		for(int i = 0; i < red.length; i++){
			for(int j = 0; j < red[0].length; j++){
				red[i][j] = ((red[i][j] & mask)>0)?0xFF:0x00;
				green[i][j] = ((green[i][j] & mask)>0)?0xFF:0x00;
				blue[i][j] = ((blue[i][j] & mask)>0)?0xFF:0x00;
			}
		}
		
		return toColorImage(red,green,blue);
	}
}

