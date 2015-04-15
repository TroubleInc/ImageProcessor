package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class EdgeAngle extends ImageFilter{
	public boolean conservative = false;
	public boolean sizeConservative = false;
	private int margin;
	private double [][] horizMask;
	private double [][] vertMask;
	public String name = "Edge Angle";
	
	public EdgeAngle(){
	}
	
	public String toString(){
		return "Edge Angle Detect";
	}
		
	public BufferedImage filter(BufferedImage image){
		int [][] red,green,blue;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		
		int [][] newRed,newGreen,newBlue;
		newRed = new int[red.length - margin * 2][];
		newGreen = new int[red.length - margin * 2][];
		newBlue = new int[red.length - margin * 2][];
		
		for(int i = margin; i < red.length - margin; i++){
			newRed[i-margin]= new int[red[0].length - margin * 2];
			newGreen[i-margin]= new int[red[0].length - margin * 2];
			newBlue[i-margin]= new int[red[0].length - margin * 2];
			for(int j = margin; j<red[0].length - margin; j++){
				double hr = 0, hg = 0, hb = 0;
				double vr = 0, vg = 0, vb = 0;
				for(int k = -1 * margin; k <= margin; k ++){
					for(int l = -1 * margin; l <= margin; l ++){
						hr += red[i + k][j + l] * horizMask[margin-k][margin-l];
						hg += green[i + k][j + l] * horizMask[margin-k][margin-l];
						hb += blue[i + k][j + l] * horizMask[margin-k][margin-l];
						vr += red[i + k][j + l] * vertMask[margin-k][margin-l];
						vg += green[i + k][j + l] * vertMask[margin-k][margin-l];
						vb += blue[i + k][j + l] * vertMask[margin-k][margin-l];
					}
				}
				newRed[i-margin][j-margin] = (int)Math.abs(Math.atan(hr/vr) * 2 / Math.PI * 255);
				newGreen[i-margin][j-margin] = (int)Math.abs(Math.atan(hg/vg) * 2 / Math.PI * 255);
				newBlue[i-margin][j-margin] = (int)Math.abs(Math.atan(hb/vb) * 2 / Math.PI * 255);
			}
		}
		return toColorImage(newRed,newGreen,newBlue);
	}
	
}

