package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class KirschFilter extends ImageFilter{
	
	public String toString(){
		return "Kirsch All Angle Edge ";
	}
	
	public BufferedImage filter(BufferedImage image){
		image = toGreyImage(toGreyMatrix(image));
		double [][][] filters = new double [][][] {{{5,5,5},{-3,0,-3},{-3,-3,-3}},
			{{-3,5,5},{-3,0,5},{-3,-3,-3}}, {{-3,-3,5},{-3,0,5},{5,-3,-3}},
			{{-3,-3,-3},{-3,0,5},{5,5,-3}},	{{-3,-3,-3},{-3,0,-3},{5,5,5}},
			{{-3,-3,-3},{5,0,-3},{-3,5,5}}, {{5,-3,-3},{5,0,-3},{-3,-3,5}},
			{{5,5,-3},{5,0,-3},{-3,-3,-3}}};
		
		int filtered [][][] = new int[8][][];
		
		for(int i = 0; i < 8; i++){
			filtered[i] = toGreyMatrix((new ImageMask(filters[i], false)).filter(image));
		}
		
		for(int i = 0; i < filtered[0].length; i++){
			for(int j = 0; j < filtered[0][0].length; j++){
				filtered[0][i][j] = Math.max(filtered[0][i][j],Math.max(filtered[1][i][j],
					Math.max(filtered[2][i][j],Math.max(filtered[3][i][j],Math.max(filtered[4][i][j],
					Math.max(filtered[5][i][j],Math.max(filtered[6][i][j],filtered[7][i][j])))))));
			}
		}
		
		return toGreyImage(filtered[0]);
	}
}
