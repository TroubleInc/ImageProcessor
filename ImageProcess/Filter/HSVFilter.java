package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class HSVFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	public static final int SATURATION_MULTIPLY = 1;
	public static final int VALUE_MULTIPLY = 1<<1;
	public static final int HUE_SHIFT = 1<<2;
	
	public double satMultiple;
	public double valMultiple;
	public double hueShift;
	
	public HSVFilter(int type, double multiplier){
		if(type == SATURATION_MULTIPLY){
			satMultiple = multiplier;
			valMultiple = 1;
			hueShift = 0;
		} else if (type == VALUE_MULTIPLY){
			satMultiple = 1;
			valMultiple = multiplier;
			hueShift = 0;
		} else if (type == HUE_SHIFT){
			satMultiple = 1;
			valMultiple = 1;
			hueShift = multiplier;
		} else if (type == SATURATION_MULTIPLY + VALUE_MULTIPLY){
			satMultiple = multiplier;
			valMultiple = multiplier;
			hueShift = 0;
		}
	}
	
	public HSVFilter(int type, double multiplier1, double multiplier2){
		if(type == SATURATION_MULTIPLY){
			satMultiple = multiplier1;
			valMultiple = 1;
			hueShift = 0;
		} else if (type == VALUE_MULTIPLY){
			satMultiple = 1;
			valMultiple = multiplier1;
			hueShift = 0;
		} else if (type == SATURATION_MULTIPLY + VALUE_MULTIPLY){
			satMultiple = multiplier1;
			valMultiple = multiplier2;
			hueShift = 0;
		}
	}
	
	public HSVFilter(int type, double multiplier1, double multiplier2, double shift){
		if (type == SATURATION_MULTIPLY + VALUE_MULTIPLY + HUE_SHIFT){
			satMultiple = multiplier1;
			valMultiple = multiplier2;
			hueShift = shift;
		}
	}
	
	public String toString(){
		return "HSV Modifications";
	}
	
	public BufferedImage filter(BufferedImage image){
		return toHSVAlphaImage(scalarAddMod(toHueMatrix(image),hueShift,360),scalarMultiply(toSaturationMatrix(image),satMultiple,0,1),scalarMultiply(toValueMatrix(image),valMultiple,0,1),toAlphaMatrix(image));
	}
	
	private double[][] scalarMultiply(double [][] matrix, double multiple, double min, double max){
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[i].length; j++){
				matrix[i][j] = matrix[i][j] * multiple;
				if(matrix[i][j]< min){
					matrix[i][j] = min;
				}
				if(matrix[i][j]> max){
					matrix[i][j] = max;
				}
			}
		}
		return matrix;
	}
	
	private double[][] scalarAddMod(double [][] matrix, double shift, double mod){
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[i].length; j++){
				matrix[i][j] = matrix[i][j] + shift;
				while(matrix[i][j] < 0){
					matrix[i][j] += mod;
				}
				while(matrix[i][j] > mod){
					matrix[i][j] -= mod;
				}
			}
		}
		return matrix;
	}
}

