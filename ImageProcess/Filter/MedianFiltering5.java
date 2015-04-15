package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class MedianFiltering5 extends ImageFilter{
	
	public String toString(){
		return "Median Filter N5";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] grey = toGreyMatrix(image);
		int [][] median = new int[grey.length][];
		int el1 = 255, el2 = 255, el3 = 255, el4 = 255, el5 = 255;
		int count = 0;
		for(int i = 0; i < grey.length; i++){
			median[i] = new int[grey[i].length];
			for(int j = 0; j < grey[i].length; j++){
				count = 0;
				el2 = 255;
				el3 = 255;
				el4 = 255;
				el5 = 255;
				el1 = grey[i][j];
				
				if(j >= 1){//5
					count++;
					if(grey[i][j-1] < el1){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = el1;
						el1 = grey[i][j-1];
					} else if(grey[i][j-1] < el2){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = grey[i][j-1];
					} else if(grey[i][j-1] < el3){
						el5 = el4;
						el4 = el3;
						el3 = grey[i][j-1];
					} else if(grey[i][j-1] < el4){
						el5 = el4;
						el4 = grey[i][j-1];
					} else if(grey[i][j-1] < el5){
						el5 = grey[i][j-1];
					}
				}
				
				if(i >= 1){//5
					count++;
					if(grey[i-1][j] < el1){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = el1;
						el1 = grey[i-1][j];
					} else if(grey[i-1][j] < el2){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = grey[i-1][j];
					} else if(grey[i-1][j] < el3){
						el5 = el4;
						el4 = el3;
						el3 = grey[i-1][j];
					} else if(grey[i-1][j] < el4){
						el5 = el4;
						el4 = grey[i-1][j];
					} else if(grey[i-1][j] < el5){
						el5 = grey[i-1][j];
					}
				}
				
				if(i < grey.length - 1){//5
					count++;
					if(grey[i+1][j] < el1){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = el1;
						el1 = grey[i+1][j];
					} else if(grey[i+1][j] < el2){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = grey[i+1][j];
					} else if(grey[i+1][j] < el3){
						el5 = el4;
						el4 = el3;
						el3 = grey[i+1][j];
					} else if(grey[i+1][j] < el4){
						el5 = el4;
						el4 = grey[i+1][j];
					} else if(grey[i+1][j] < el5){
						el5 = grey[i+1][j];
					}
				}
				
				if(j < grey[0].length - 1){//5
					count++;
					if(grey[i][j+1] < el1){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = el1;
						el1 = grey[i][j+1];
					} else if(grey[i][j+1] < el2){
						el5 = el4;
						el4 = el3;
						el3 = el2;
						el2 = grey[i][j+1];
					} else if(grey[i][j+1] < el3){
						el5 = el4;
						el4 = el3;
						el3 = grey[i][j+1];
					} else if(grey[i][j+1] < el4){
						el5 = el4;
						el4 = grey[i][j+1];
					} else if(grey[i][j+1] < el5){
						el5 = grey[i][j+1];
					}
				}

				if(count / 2 == 4){
					median[i][j] = el5;
				} else if(count / 2 == 3){
					median[i][j] = el4;
				} else if(count / 2 == 2){
					median[i][j] = el3;
				} else if(count / 2 == 1){
					median[i][j] = el2;
				} else {
					median[i][j] = el1;
				}
			}
		}
		return toGreyImage(median);
	}
}
