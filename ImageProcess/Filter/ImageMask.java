package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageMask extends ImageFilter{
	public boolean conservative = false;
	public boolean sizeConservative = false;
	private int margin;
	private double [][] mask;
	public String name = "Mask";
	public boolean absResult = false;
	public int baseLevel = 0;
	public boolean doAlpha = false;
	public boolean normalize = false;
	public boolean saveEdges = true;
	
	public static int NORMALIZE 	= 1 << 0;
	public static int ABSOLUTE 	= 1 << 1;
	public static int DOALPHA		= 1 << 2;
	public static int SAVEEDGES	= 1 << 3;
	
	public ImageMask(double [][] maskValues, boolean normalize){
		if(maskValues.length %2 == 0 || maskValues[0].length %2 == 0 || maskValues.length != maskValues[0].length){
			debugPrintln("Error, maskValues not odd square");
		}
		
		mask = new double[maskValues.length][];
		double sum = 0.0;
		for(int i = maskValues.length - 1; i >= 0; i--){
			mask[i] = new double[maskValues[0].length];
			for(int j = maskValues[0].length -1; j >= 0; j--){
				mask[i][j] = maskValues[i][j];
				sum += mask[i][j];
			}
		}
		
		this.normalize = normalize;
		
		margin = maskValues.length / 2;
	}
	
	public String toString(){
		return "Image Mask";
	}
		
	public ImageMask(double [][] maskValues, boolean normalize, boolean abs){
		this(maskValues,normalize);
		absResult = abs;
	}
	
	public ImageMask(double [][] maskValues, boolean normalize, boolean abs, int baseLevel){
		this(maskValues, normalize, abs);
		this.baseLevel = baseLevel;
	}
	
	public ImageMask(double [][] maskValues, boolean normalize, int baseLevel){
		this(maskValues, normalize);
		this.baseLevel = baseLevel;
	}
	
	public ImageMask(double [][] maskValues, int options){
		this(maskValues, (options & NORMALIZE) > 0);
		absResult = (options & ABSOLUTE) > 0;
		doAlpha = (options & DOALPHA) > 0;
	}
	
	public ImageMask(double [][] maskValues, int options, int baseLevel){
		this(maskValues,options);
		this.baseLevel = baseLevel;
	}
	
	public BufferedImage filter(BufferedImage image){
		if(normalize){
			return normalizeFilter(image);
		} else if(saveEdges) {
			return conserveFilter(image);
		}
		
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		int [][] newRed,newGreen,newBlue,newAlpha;
		newRed = new int[red.length - margin * 2][];
		newGreen = new int[red.length - margin * 2][];
		newBlue = new int[red.length - margin * 2][];
		newAlpha = new int[red.length - margin *2][];
		
		for(int i = margin; i < red.length - margin; i++){
			newRed[i-margin]= new int[red[0].length - margin * 2];
			newGreen[i-margin]= new int[red[0].length - margin * 2];
			newBlue[i-margin]= new int[red[0].length - margin * 2];
			newAlpha[i-margin]= new int[red[0].length - margin * 2];
			for(int j = margin; j<red[0].length - margin; j++){
				double r = 0, g = 0, b = 0, a = 0;
				for(int k = -1 * margin; k <= margin; k ++){
					for(int l = -1 * margin; l <= margin; l ++){
						r += red[i + k][j + l] * mask[margin-k][margin-l];
						g += green[i + k][j + l] * mask[margin-k][margin-l];
						b += blue[i + k][j + l] * mask[margin-k][margin-l];
						if(doAlpha){
							a += alpha[i + k][j + l] * mask[margin-k][margin-l];
						}
					}
				}
				if(!doAlpha){
					a = alpha[i][j];
				}
				if(absResult){
					r = Math.abs(r);
					g = Math.abs(g);
					b = Math.abs(b);
					a = Math.abs(a);
				}
				if(baseLevel != 0){
					r += baseLevel;
					g += baseLevel;
					b += baseLevel;
				}
				if(r < 0){
					r = 0;
				}
				if(r > 255){
					r = 255;
				}
				if(g < 0){
					g = 0;
				}
				if(g > 255){
					g = 255;
				}
				if(b < 0){
					b = 0;
				}
				if(b > 255){
					b = 255;
				}
				if(a < 0){
					a = 0;
				}
				if(a > 255){
					a = 255;
				}
				newRed[i-margin][j-margin] = (int)r;
				newGreen[i-margin][j-margin] = (int)g;
				newBlue[i-margin][j-margin] = (int)b;
				newAlpha[i-margin][j-margin] = (int)a;
			}
		}
		return toAlphaImage(newRed,newGreen,newBlue,newAlpha);
	}
	
	public BufferedImage conserveFilter(BufferedImage image){
		
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		int [][] newRed,newGreen,newBlue,newAlpha;
		newRed = new int[red.length][];
		newGreen = new int[red.length][];
		newBlue = new int[red.length][];
		newAlpha = new int[red.length][];
		
		for(int i = 0; i < red.length; i++){
			newRed[i]= new int[red[0].length];
			newGreen[i]= new int[red[0].length];
			newBlue[i]= new int[red[0].length];
			newAlpha[i]= new int[red[0].length];
			for(int j = 0; j<red[0].length; j++){
				double r = 0, g = 0, b = 0, a = 0;
				for(int k = -1 * margin; k <= margin; k ++){
					for(int l = -1 * margin; l <= margin; l ++){
						if(i+k >= 0 && i + k < red.length && j+l >= 0 && j+l <red[0].length){
							r += red[i + k][j + l] * mask[margin-k][margin-l];
							g += green[i + k][j + l] * mask[margin-k][margin-l];
							b += blue[i + k][j + l] * mask[margin-k][margin-l];
							if(doAlpha){
								a += alpha[i + k][j + l] * mask[margin-k][margin-l];
							}
						}
					}
				}
				if(!doAlpha){
					a = alpha[i][j];
				}
				if(absResult){
					r = Math.abs(r);
					g = Math.abs(g);
					b = Math.abs(b);
					a = Math.abs(a);
				}
				if(baseLevel != 0){
					r += baseLevel;
					g += baseLevel;
					b += baseLevel;
				}
				if(r < 0){
					r = 0;
				}
				if(r > 255){
					r = 255;
				}
				if(g < 0){
					g = 0;
				}
				if(g > 255){
					g = 255;
				}
				if(b < 0){
					b = 0;
				}
				if(b > 255){
					b = 255;
				}
				if(a < 0){
					a = 0;
				}
				if(a > 255){
					a = 255;
				}
				newRed[i][j] = (int)r;
				newGreen[i][j] = (int)g;
				newBlue[i][j] = (int)b;
				newAlpha[i][j] = (int)a;
			}
		}
		return toAlphaImage(newRed,newGreen,newBlue,newAlpha);
	}
	
	public BufferedImage normalizeFilter(BufferedImage image){
		int [][] red,green,blue,alpha;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		int [][] newRed,newGreen,newBlue,newAlpha;
		newRed = new int[red.length][];
		newGreen = new int[red.length][];
		newBlue = new int[red.length][];
		newAlpha = new int[red.length][];
		
		double sum = 0;
		
		for(int i = 0; i < red.length; i++){
			newRed[i]= new int[red[0].length];
			newGreen[i]= new int[red[0].length];
			newBlue[i]= new int[red[0].length];
			newAlpha[i]= new int[red[0].length];
			for(int j = 0; j<red[0].length; j++){
				double r = 0, g = 0, b = 0, a = 0;
				sum = 0;
				for(int k = -1 * margin; k <= margin; k ++){
					for(int l = -1 * margin; l <= margin; l ++){
						if(i+k >= 0 && i + k < red.length && j+l >= 0 && j+l <red[0].length){
							sum += mask[margin-k][margin-l];
							r += red[i + k][j + l] * mask[margin-k][margin-l];
							g += green[i + k][j + l] * mask[margin-k][margin-l];
							b += blue[i + k][j + l] * mask[margin-k][margin-l];
							if(doAlpha){
								a += alpha[i + k][j + l] * mask[margin-k][margin-l];
							}
						}
					}
				}
				r /= sum;
				g /= sum;
				b /= sum;
				if(!doAlpha){
					a = alpha[i][j];
				} else {
					a /= sum;
				}
				if(absResult){
					r = Math.abs(r);
					g = Math.abs(g);
					b = Math.abs(b);
					a = Math.abs(a);
				}
				if(baseLevel != 0){
					r += baseLevel;
					g += baseLevel;
					b += baseLevel;
				}
				if(r < 0){
					r = 0;
				}
				if(r > 255){
					r = 255;
				}
				if(g < 0){
					g = 0;
				}
				if(g > 255){
					g = 255;
				}
				if(b < 0){
					b = 0;
				}
				if(b > 255){
					b = 255;
				}
				if(a < 0){
					a = 0;
				}
				if(a > 255){
					a = 255;
				}
				newRed[i][j] = (int)r;
				newGreen[i][j] = (int)g;
				newBlue[i][j] = (int)b;
				newAlpha[i][j] = (int)a;
			}
		}
		return toAlphaImage(newRed,newGreen,newBlue,newAlpha);
	}
	
}

