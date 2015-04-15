package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class HoughCircle extends ImageFilter{
	public boolean conservative = false;
	public boolean sizeConservative = false;
	public String name = "Hough Circle Detect";
	public int littleR = 10;
	public int bigR = 100;
	public int[] bestA, bestB, bestR, bestVal;
	public int numBest = 20;
	
	public HoughCircle(){
		bestA = new int[numBest];
		bestB = new int[numBest];
		bestR = new int[numBest];
		bestVal = new int[numBest];
	}
	
	public String toString(){
		return "Hough Circle Detect";
	}
		
	public BufferedImage filter(BufferedImage image){
		int [][] grey;
		grey = toGreyMatrix((new RobertEdgeDetect()).filter((new BWThreshold(20)).filter(image)));
		
		int [][][] circles = new int[grey.length][][]; //[i][j][r]
		
		
		for(int i = 0; i < grey.length; i++){
			circles[i] = new int[grey[0].length][];
			for(int j = 0; j < grey[0].length; j++){
				circles[i][j] = new int[bigR - littleR];
			}
		}
		for(double theta = 0; theta < 2 * Math.PI; theta += .05){
			double costheta = Math.cos(theta);
			double sintheta = Math.sin(theta);
			for(int i = 0; i < grey.length; i++){
				for(int j = 0; j < grey[0].length; j++){
					if(grey[i][j] > 0){
						for(int r = littleR; r < bigR - littleR; r++){
							int a = (int)(i - r * costheta);
							int b = (int)(j - r * sintheta);
							if(a >= 0 && a < circles.length && b >= 0 && b < circles[0].length){
								circles[a][b][r-littleR]++;
							}
						}
					}
				}
			}
		}
		
		for(int a = 0; a < circles.length; a++){
			for(int b = 0; b < circles[0].length; b++){
				for(int r = littleR; r < bigR - littleR; r++){
					if(circles[a][b][r - littleR] > 0){
						boolean found = false;
						int tempA = 0, tempB = 0, tempR = 0, tempVal = 0;
						for(int i = 0; i < numBest; i++){
							if(!found){
								if(circles[a][b][r - littleR] > bestVal[i]){
									found = true;
									tempA = bestA[i];
									tempB = bestB[i];
									tempR = bestR[i];
									tempVal = bestVal[i];
									bestA[i] = a;
									bestB[i] = b;
									bestR[i] = r;
									bestVal[i] = circles[a][b][r-littleR];
								}
							} else {
								int temp;
								temp = bestA[i];
								bestA[i] = tempA;
								tempA = temp;
								temp = bestB[i];
								bestB[i] = tempB;
								tempB = temp;
								temp = bestR[i];
								bestR[i] = tempR;
								tempR = temp;
								temp = bestVal[i];
								bestVal[i] = tempVal;
								tempVal = temp;
							}
						}
					}
				}
			}
		}
		
		Graphics g = image.getGraphics();
		for(int i = 0; i < 10; i++){
			g.drawOval(bestA[i] - bestR[i], bestB[i] -bestR[i], bestR[i] * 2, bestR[i] * 2);
		}
		
		return image;
	}
	
}

