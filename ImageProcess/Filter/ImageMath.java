package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import Calc.*;
import java.math.BigDecimal;

public class ImageMath extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	public static final int MONOCHROME = 1;
	public static final int COLOR = 1 << 1;
	public static final int ALPHA = 1 << 2;
	public static final int HSV = 1 << 3;
	
	public int type;
	public String expressionM;
	public String expressionR;
	public String expressionG;
	public String expressionB;
	public String expressionA;
	public Data data;
	public CalcContext context;
	
	public ImageMath(int mode, String expression){
		type = mode;
		expressionM = expression;
		expressionR = "";
		expressionG = "";
		expressionB = "";
		expressionA = "";
		data = new Data();
		context = new CalcContext();
		Expression.setContext(context);
		data.setOut(debugConsole);
	}
	
	public ImageMath(int mode, String expression1, String expression2){
		type = mode;
		expressionM = expression1;
		expressionR = "";
		expressionG = "";
		expressionB = "";
		expressionA = expression2;
		data = new Data();
		context = new CalcContext();
		Expression.setContext(context);
		data.setOut(debugConsole);
	}
	
	public ImageMath(int mode, String expression1, String expression2, String expression3){
		type = mode;
		expressionM = "";
		expressionR = expression1;
		expressionG = expression2;
		expressionB = expression3;
		expressionA = "";
		data = new Data();
		context = new CalcContext();
		Expression.setContext(context);
		data.setOut(debugConsole);
	}
	
	public ImageMath(int mode, String expression1, String expression2, String expression3, String expression4){
		type = mode;
		expressionM = "";
		expressionR = expression1;
		expressionG = expression2;
		expressionB = expression3;
		expressionA = expression4;
		data = new Data();
		context = new CalcContext();
		Expression.setContext(context);
		data.setOut(debugConsole);
	}
	
	public String toString(){
		return "Image Math " + expressionR +"";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] red,green,blue,alpha,luma,y,cr,cb;
		double [][] hue,sat,val;
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		hue = toHueMatrix(image);
		sat = toSaturationMatrix(image);
		val = toValueMatrix(image);
		luma = toLumaMatrix(image);
		BufferedImage ycrcb = (new SwitchFilter(SwitchFilter.RGB_TO_YCRCB)).filter(image);
		y = toRedMatrix(ycrcb);
		cr = toGreenMatrix(ycrcb);
		cb = toBlueMatrix(ycrcb);
		
		int [][] newRed,newGreen,newBlue,newAlpha;
		double [][] newHue, newSat, newVal;
		newRed = new int[red.length][];
		newGreen = new int[red.length][];
		newBlue = new int[red.length][];
		newAlpha = new int[red.length][];
		newHue = new double[red.length][];
		newVal = new double[red.length][];
		newSat = new double[red.length][];
		data.addData("height",""+red[0].length);
		data.addData("width",""+red.length);
		for(int i = 0; i < red.length; i++){
			newRed[i] = new int[red[0].length];
			newGreen[i] = new int[red[0].length];
			newBlue[i] = new int[red[0].length];
			newAlpha[i] = new int[red[0].length];
			newHue[i] = new double[red[0].length];
			newSat[i] = new double[red[0].length];
			newVal[i] = new double[red[0].length];
			for(int j = 0; j < red[0].length; j++){
				data.addData("x",""+i);
				data.addData("y",""+j);
				data.addData("r",""+red[i][j]);
				data.addData("g",""+green[i][j]);
				data.addData("b",""+blue[i][j]);
				data.addData("a",""+alpha[i][j]);
				data.addData("h",""+hue[i][j]);
				data.addData("s",""+sat[i][j]);
				data.addData("v",""+val[i][j]);
				data.addData("l",""+luma[i][j]);
				data.addData("m",""+((red[i][j]+green[i][j]+blue[i][j])/3.0));
				data.addData("Yp",""+y[i][j]);
				data.addData("Cr",""+cr[i][j]);
				data.addData("Cb",""+cb[i][j]);
				
				if((type & MONOCHROME)>0){
					ReturnType monochrome = new Expression(expressionM,data).evaluate();
					if(monochrome.type()!=1){
						debugPrintln("Error on pixel at (" + i + "," + j + ") -- " + monochrome.print());
						newRed[i][j] = 0;
						newGreen[i][j] = 0;
						newBlue[i][j] = 0;
					}
					newRed[i][j] = fixRange(monochrome.number().intValue());
					newGreen[i][j] = fixRange(monochrome.number().intValue());
					newBlue[i][j] = fixRange(monochrome.number().intValue());
				} else if((type & COLOR)>0){
					ReturnType redVal = new Expression(expressionR,data).evaluate();
					if(redVal.type()!=1){
						debugPrintln("Error on red pixel at (" + i + "," + j + ") -- " + redVal.print());
						newRed[i][j] = 0;
					}
					newRed[i][j] = fixRange(redVal.number().intValue());
					ReturnType greenVal = new Expression(expressionG,data).evaluate();
					if(greenVal.type()!=1){
						debugPrintln("Error on green pixel at (" + i + "," + j + ") -- " + greenVal.print());
						newGreen[i][j] = 0;
					}
					newGreen[i][j] = fixRange(greenVal.number().intValue());
					ReturnType blueVal = new Expression(expressionB,data).evaluate();
					if(blueVal.type()!=1){
						debugPrintln("Error on blue pixel at (" + i + "," + j + ") -- " + blueVal.print());
						newBlue[i][j] = 0;
					}
					newBlue[i][j] = fixRange(blueVal.number().intValue());
				} else if((type & HSV)>0){
					ReturnType hVal = new Expression(expressionR,data).evaluate();
					if(hVal.type()!=1){
						debugPrintln("Error on hue pixel at (" + i + "," + j + ") -- " + hVal.print());
						newHue[i][j] = 0;
					}
					newHue[i][j] = fixHSVRange(hVal.number().doubleValue(),0.0, 360.0);
					ReturnType sVal = new Expression(expressionG,data).evaluate();
					if(sVal.type()!=1){
						debugPrintln("Error on saturation pixel at (" + i + "," + j + ") -- " + sVal.print());
						newSat[i][j] = 0;
					}
					newSat[i][j] = fixHSVRange(sVal.number().doubleValue(),0.0, 1.0);
					ReturnType vVal = new Expression(expressionB,data).evaluate();
					if(vVal.type()!=1){
						debugPrintln("Error on value pixel at (" + i + "," + j + ") -- " + vVal.print());
						newVal[i][j] = 0;
					}
					newVal[i][j] = fixHSVRange(vVal.number().doubleValue(),0.0, 1.0);
				}
				if((type & ALPHA)>0){
					ReturnType alphaVal = new Expression(expressionA,data).evaluate();
					if(alphaVal.type()!=1){
						debugPrintln("Error on alpha pixel at (" + i + "," + j + ") -- " + alphaVal.print());
						newAlpha[i][j] = 0;
					}
					newAlpha[i][j] = fixRange(alphaVal.number().intValue());
				}
			}
		}
		if((type & COLOR)> 0){
			if((type & ALPHA)>0){
				return toAlphaImage(newRed,newGreen,newBlue,newAlpha);
			} else {
				return toColorImage(newRed,newGreen,newBlue);
			}
		} else {
			if((type & ALPHA)>0){
				return toHSVAlphaImage(newHue,newSat,newVal,newAlpha);
			} else {
				return toHSVImage(newHue,newSat,newVal);
			}
		}
	}
	
	public static int fixRange(int in){
		if(in > 255){
			in = 255;
		} else if(in < 0){
			in = 0;
		}
		return in;
	}
	
	public static double fixHSVRange(double in, double min, double max){
		if(in >= max){
			in = max;
		} else if(in <= min){
			in = min;
		}
		return in;
	}
}

