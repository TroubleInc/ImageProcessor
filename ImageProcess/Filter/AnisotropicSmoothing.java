package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class AnisotropicSmoothing extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	public double proportion;
	public double gaussianVariance;
	
	public AnisotropicSmoothing(double prop, double varia){
		proportion = prop;
		gaussianVariance = varia;
	}
	
	public String toString(){
		return "Anisotropic Smoothing proportion:" + proportion + " variance:" + gaussianVariance;
	}
	
	public BufferedImage filter(BufferedImage image){
		return toAlphaImage(smooth(toRedMatrix(image)),smooth(toGreenMatrix(image)),smooth(toBlueMatrix(image)),smooth(toAlphaMatrix(image)));
	}
	
	private int[][] smooth(int [][] channel){
		int[][] out = new int[channel.length][];
		for(int i = 0; i < channel.length; i++){
			out[i] = new int[channel[i].length];
			for(int j = 0; j < channel[i].length; j++){
				int val = channel[i][j];
				if(i > 0){
					val += proportion * Math.exp(-1 * ((channel[i][j] - channel[i-1][j])/gaussianVariance) * ((channel[i][j] - channel[i-1][j])/gaussianVariance))*(channel[i-1][j] - channel[i][j]);
				}
				if(j > 0){
					val += proportion * Math.exp(-1 * ((channel[i][j] - channel[i][j-1])/gaussianVariance) * ((channel[i][j] - channel[i][j-1])/gaussianVariance))*(channel[i][j-1] - channel[i][j]);
				}
				if(i < channel.length - 1){
					val += proportion * Math.exp(-1 * ((channel[i][j] - channel[i+1][j])/gaussianVariance) * ((channel[i][j] - channel[i+1][j])/gaussianVariance))*(channel[i+1][j] - channel[i][j]);
				}
				if(j < channel[i].length - 1){
					val += proportion * Math.exp(-1 * ((channel[i][j] - channel[i][j+1])/gaussianVariance) * ((channel[i][j] - channel[i][j+1])/gaussianVariance))*(channel[i][j+1] - channel[i][j]);
				}
				if(val > 255){
					val = 255;
				}
				if(val < 0){
					val = 0;
				}
				out[i][j] = val;
			}
		}
		return out;
	}
}

