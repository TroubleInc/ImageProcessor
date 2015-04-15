package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class Posterize extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "posterize";
	public int levels;
	
	public Posterize(int levels){
		this.levels = levels;
	}
	
	public String toString(){
		return "Posterize with " + levels + " levels";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		for(int i = 0; i < grey.length; i++){
			for(int j = 0; j < grey[0].length; j++){
				for(int c = 0; c < levels; c++){
					if(grey[i][j] > (c * 255) / levels && grey[i][j] <= ((c + 1) * 255) / levels){
						grey[i][j] = (int)((c+1.0/2) * 255 / levels);
					}
				}
			}
		}
		return toGreyImage(grey);
	}
}

