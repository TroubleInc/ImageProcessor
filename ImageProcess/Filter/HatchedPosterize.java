package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class HatchedPosterize extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "posterize";
	public int levels;
	public int[] levelOrder;
	
	public HatchedPosterize(int levels){
		this.levels = levels - 1;
		levelOrder = new int[this.levels];
		for(int i = 0; i < this.levels; i++){
			levelOrder[i] = i;
		}
		for(int i = 0; i < levels *3 + 5; i++){
			int spot1 = (int)(Math.random() * (this.levels));
			int spot2 = (int)(Math.random() * (this.levels));
			int tmp = levelOrder[spot1];
			levelOrder[spot1] = levelOrder[spot2];
			levelOrder[spot2] = tmp;
		}
	}
	
	public String toString(){
		return "Hatched Posterize " + levels + " levels";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		for(int i = 0; i < grey.length; i++){
			for(int j = 0; j < grey[0].length; j++){
				if(grey[i][j] <= ((levelOrder[((i+j)%levels)] + 1.0/2) * 255) / levels){
					grey[i][j] = 0;
				}
				if(grey[i][j] > 0){
					grey[i][j] = 255;
				}
			}
		}
		return toGreyImage(grey);
	}
}

