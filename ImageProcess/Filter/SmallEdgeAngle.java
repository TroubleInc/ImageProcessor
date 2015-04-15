package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class SmallEdgeAngle extends ImageFilter{

	public String toString(){
		return "Edge Angle Detection";
	}

	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		int [][] edges = new int[grey.length - 1][];
		int hd, vd;
		for(int i = 0; i < grey.length - 1; i++){
			edges[i] = new int[grey[i].length - 1];
			for(int j = 0; j < grey[i].length - 1; j++){
				hd = grey[i+1][j] + grey[i][j] - grey[i][j+1] - grey[i+1][j+1];
				vd = grey[i][j] + grey[i][j+1] - grey[i+1][j] - grey[i+1][j+1];
				
				if(vd != 0){
					edges[i][j] = (int)(Math.atan( hd / vd ) * 2 / Math.PI * 127) + 128;
				} else {
					edges[i][j] = (hd == 0)?128:(int)(128 + Math.signum(hd) * 127);
				}
			}
		}
		return toGreyImage(edges);
	}
}
