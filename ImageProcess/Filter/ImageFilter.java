package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.JTextArea;

public class ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	public static JTextArea debugConsole;
	
	private static int imageHashcode = -1;
	private static int[][] red,green,blue,grey,alpha,luma;
	private static double[][] hue,sat,val;
	
	public static final int GREY_HIST = 0;
	public static final int RED_HIST = 1;
	public static final int BLUE_HIST = 2;
	public static final int GREEN_HIST = 3;
	public static final int HUE_HIST = 4;
	
	public BufferedImage filter(BufferedImage image){
		return image;
	}
	
	public String toString(){
		return "Default Image Filter";
	}
	
	public static void debugPrint(String s){
		debugConsole.append(s);
	}
	
	public static void debugPrintln(String s){
		debugConsole.append(s);
		debugConsole.append("\n");
	}
	
	public static void resetImage(){
		imageHashcode = -1;
	}
	
	public static void toAllMatrix(BufferedImage image){
		if(image.hashCode() == imageHashcode){
			return;
		}
		alpha = new int[image.getWidth()][];
		red = new int[image.getWidth()][];
		green = new int[image.getWidth()][];
		blue = new int[image.getWidth()][];
		grey = new int[image.getWidth()][];
		hue = new double[image.getWidth()][];
		sat = new double[image.getWidth()][];
		val = new double[image.getWidth()][];
		luma = new int[image.getWidth()][];
		int rgb;
		int mask = 255;
		for(int i = image.getWidth()-1; i >= 0; i--){
			alpha[i] = new int[image.getHeight()];
			red[i] = new int[image.getHeight()];
			green[i] = new int[image.getHeight()];
			blue[i] = new int[image.getHeight()];
			grey[i] = new int[image.getHeight()];
			hue[i] = new double[image.getHeight()];
			sat[i] = new double[image.getHeight()];
			val[i] = new double[image.getHeight()];
			luma[i] = new int[image.getHeight()];
			for(int j = image.getHeight()-1; j >= 0; j--){
				rgb = image.getRGB(i,j);
				blue[i][j] 	= rgb & mask;
				rgb = rgb>>8;
				green[i][j] = rgb & mask;
				rgb = rgb>>8;
				red[i][j]	= rgb & mask;
				rgb = rgb>>8;
				alpha[i][j]	= rgb & mask;
				grey[i][j] = (red[i][j] + green[i][j] + blue[i][j])/3;
				//double a,b,c;
				//a = .5 *(2*red[i][j] - green[i][j] - blue[i][j]) / 255.0;
				//b = Math.sqrt(3.0) / 2.0 * (green[i][j] - blue[i][j]) / 255.0;
				//c = Math.hypot(a,b);
				//hue[i][j] = Math.atan2(b,a);
				
				int max = ((red[i][j]>green[i][j])?((red[i][j]>blue[i][j])?(red[i][j]):(blue[i][j])):((green[i][j]>blue[i][j])?(green[i][j]):(blue[i][j])));
				int min = ((red[i][j]<green[i][j])?((red[i][j]<blue[i][j])?(red[i][j]):(blue[i][j])):((green[i][j]<blue[i][j])?(green[i][j]):(blue[i][j])));
				
				int c = max - min;
				
				if(c == 0){
					hue[i][j] = 0;
				} else if(max == red[i][j]){
					hue[i][j] = 60.0 * (green[i][j] - blue[i][j] +0.0)/c;
					if(hue[i][j] < 0){
						hue[i][j] += 360;
					}
				} else if(max == green[i][j]){
					hue[i][j] = 60.0 * ((blue[i][j] - red[i][j] +0.0)/c+2);
				} else if(max == blue[i][j]){
					hue[i][j] = 60.0 * ((red[i][j] - green[i][j] +0.0)/c+4);
				} else {
					hue[i][j] = 0;
				}
				
				val[i][j] = max/255.0;
				sat[i][j] = (val[i][j] == 0)?(0):((c/255.0) / val[i][j]);
				luma[i][j] = fixRange((int)((.3*red[i][j] + .59*green[i][j] + .11*blue[i][j])));
			}
		}
		imageHashcode = image.hashCode();
	}
	
	public static int[][] toGreyMatrix(BufferedImage image){
		toAllMatrix(image);
		return grey;
	}

	public static int[][] toAlphaMatrix(BufferedImage image){
		toAllMatrix(image);
		return alpha;
	}
	
	public static int[][] toRedMatrix(BufferedImage image){
		toAllMatrix(image);
		return red;
	}
	
	public static int[][] toGreenMatrix(BufferedImage image){
		toAllMatrix(image);
		return green;
	}
	
	public static int[][] toBlueMatrix(BufferedImage image){
		toAllMatrix(image);
		return blue;
	}
	
	public static double[][] toHueMatrix(BufferedImage image){
		toAllMatrix(image);
		return hue;
	}
	
	public static double[][] toSaturationMatrix(BufferedImage image){
		toAllMatrix(image);
		return sat;
	}
	
	public static double[][] toValueMatrix(BufferedImage image){
		toAllMatrix(image);
		return val;
	}
	
	public static int[][] toLumaMatrix(BufferedImage image){
		toAllMatrix(image);
		return luma;
	}
	
	public static BufferedImage toGreyImage(int [][] greyMatrix){
		BufferedImage image = new BufferedImage(greyMatrix.length,greyMatrix[0].length,BufferedImage.TYPE_INT_ARGB);
		for(int i = image.getWidth()-1; i >= 0; i--){
			for(int j = image.getHeight()-1; j >= 0; j--){
				image.setRGB(i,j,new Color(greyMatrix[i][j], greyMatrix[i][j], greyMatrix[i][j]).getRGB());
			}
		}
		return image;
	}
	
	public static BufferedImage toColorImage(int [][] redMatrix, int [][] greenMatrix, int [][] blueMatrix){
		BufferedImage image = new BufferedImage(redMatrix.length,redMatrix[0].length,BufferedImage.TYPE_INT_RGB);
		for(int i = image.getWidth()-1; i >= 0; i--){
			for(int j = image.getHeight()-1; j >= 0; j--){
				image.setRGB(i,j,new Color(redMatrix[i][j], greenMatrix[i][j], blueMatrix[i][j]).getRGB());
			}
		}
		return image;
	}
	
	public static BufferedImage toAlphaImage(int [][] redMatrix, int [][] greenMatrix, int [][] blueMatrix, int [][] alphaMatrix){
		BufferedImage image = new BufferedImage(redMatrix.length,redMatrix[0].length,BufferedImage.TYPE_INT_ARGB);
		for(int i = image.getWidth()-1; i >= 0; i--){
			for(int j = image.getHeight()-1; j >= 0; j--){
				image.setRGB(i,j,(alphaMatrix[i][j]<<24) + (redMatrix[i][j]<<16) + (greenMatrix[i][j]<<8) + (blueMatrix[i][j]));
			}
		}
		return image;
	}
	
	public static BufferedImage toAlphaImage(int [][] redMatrix, int [][] greenMatrix, int [][] blueMatrix, int [][] alphaMatrix, int width, int height){
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		for(int i = width-1; i >= 0; i--){
			for(int j = height-1; j >= 0; j--){
				int argb = 0;
				if(alphaMatrix != null){
					argb += alphaMatrix[i][j];
				} else {
					argb += 255;
				}
				argb = argb << 8;
				if(redMatrix != null){
					argb += redMatrix[i][j];
				}
				argb = argb << 8;
				if(greenMatrix != null){
					argb += greenMatrix[i][j];
				} else {
					argb += redMatrix[i][j];
				}
				argb = argb << 8;
				if(blueMatrix != null){
					argb += blueMatrix[i][j];
				} else {
					argb += redMatrix[i][j];
				}
				image.setRGB(i,j,argb);
			}
		}
		return image;
	}
	
	public static BufferedImage toHSVImage(double [][] hueMatrix, double [][] satMatrix, double [][] valMatrix){
		BufferedImage image = new BufferedImage(hueMatrix.length,hueMatrix[0].length,BufferedImage.TYPE_INT_RGB);
		for(int i = image.getWidth()-1; i >= 0; i--){
			for(int j = image.getHeight()-1; j >= 0; j--){
				double r,g,b;
				double c = valMatrix[i][j] * satMatrix[i][j];
				double hp = hueMatrix[i][j] / (60 );
				double x = c * ( 1 - Math.abs(hp%2 - 1));
				double m = valMatrix[i][j]-c;
				if(hp < -2){
					r = 0 + m;
					g = x + m;
					b = c + m;
				} else if (hp < -1){
					r = x + m;
					g = 0 + m;
					b = c + m;
				} else if (hp < 0){
					r = c + m;
					g = 0 + m;
					b = x + m;
				} else if (hp < 1){
					r = c + m;
					g = x + m;
					b = 0 + m;
				} else if (hp < 2){
					r = x + m;
					g = c + m;
					b = 0 + m;
				} else {
					r = 0 + m;
					g = c + m;
					b = x + m;
				}
				
				image.setRGB(i,j,((int)fixRange((int)(r*255))<<16) + ((int)fixRange((int)(g*255))<<8) + ((int)fixRange((int)(b*255))));
			}
		}
		return image;
	}
	
	public static BufferedImage toHSVAlphaImage(double [][] hueMatrix, double [][] satMatrix, double [][] valMatrix, int [][] alphaMatrix){
		BufferedImage image = new BufferedImage(hueMatrix.length,hueMatrix[0].length,BufferedImage.TYPE_INT_RGB);
		for(int i = image.getWidth()-1; i >= 0; i--){
			for(int j = image.getHeight()-1; j >= 0; j--){
				double r,g,b;
				double c = valMatrix[i][j] * satMatrix[i][j];
				double hp = hueMatrix[i][j] / ( 60 );
				if(hp < 0){
					hp += 6;
				}
				double x = c * ( 1 - Math.abs(hp%2 - 1));
				double m = valMatrix[i][j]-c;
				if(satMatrix[i][j] < 1.0/255){
					r = valMatrix[i][j];
					g = r;
					b = r;
				} else if (hp <= 1){
					r = c + m;
					g = x + m;
					b = 0 + m;
				} else if (hp <= 2){
					r = x + m;
					g = c + m;
					b = 0 + m;
				} else if (hp <= 3){
					r = 0 + m;
					g = c + m;
					b = x + m;
				} else if(hp <= 4){
					//debugPrintln(""+hueMatrix[i][j]);
					r = 0 + m;
					g = x + m;
					b = c + m;
				} else if (hp <= 5){
					r = x + m;
					g = 0 + m;
					b = c + m;
				} else if (hp <= 6){
					r = c + m;
					g = 0 + m;
					b = x + m;
				} else{
					r = 0;
					g = 0;
					b = 0;
				}
				
				
				image.setRGB(i,j,(alphaMatrix[i][j]<<24) + ((int)fixRange((int)(r*255))<<16) + ((int)fixRange((int)(g*255))<<8) + ((int)fixRange((int)(b*255))));
			}
		}
		return image;
	}
	
	public static BufferedImage toGreyImageScaled(int [][] greyMatrix){
		int min = greyMatrix[0][0];
		int max = greyMatrix[0][0];
		for(int i = greyMatrix.length - 1; i >= 0; i--){
			for(int j = greyMatrix[0].length -1; j >= 0; j--){
				if(greyMatrix[i][j] < min){
					min = greyMatrix[i][j];
				}
				if(greyMatrix[i][j] > max){
					max = greyMatrix[i][j];
				}
			}
		}
		for(int i = greyMatrix.length - 1; i >= 0; i--){
			for(int j = greyMatrix[0].length -1; j >= 0; j--){
				greyMatrix[i][j] = (int)((1.0 * greyMatrix[i][j] + min) /(max - min) * (255));
			}
		}
		return toGreyImage(greyMatrix);
	}
	
	public static int[] toHist(int[][]imageMatrix){
		int [] hist = new int[256];
		for(int i = 0; i < imageMatrix.length; i++){
			for(int j = 0; j < imageMatrix[0].length; j++){
				hist[imageMatrix[i][j]]++;
			}
		}
		return hist;
	}
	
	public static int[] toHueHist(double[][]imageMatrix, double [][]satMatrix){
		int [] hist = new int[360];
		double [] hueHist = new double[360];
		for(int i = 0; i < imageMatrix.length; i++){
			for(int j = 0; j < imageMatrix[0].length; j++){
				//if(satMatrix[i][j] >= 0.0 && imageMatrix[i][j] != 0.0){
					hueHist[(int)(imageMatrix[i][j]%360)]+=satMatrix[i][j];
				//}
			}
		}
		
		for(int i = 0; i < hueHist.length; i++){
			hist[i] = (int)hueHist[i];
		}
		return hist;
	}
	
	public static BufferedImage drawHist(int[] hist, boolean logStretch, int type){
		int size = hist.length;
		int multiplier;
		BufferedImage image;
		if(size > 300){
			image = new BufferedImage(size * 3 + 1,120,BufferedImage.TYPE_INT_ARGB);
			multiplier = 3;
		} else {
			image = new BufferedImage(size * 4 + 1,120,BufferedImage.TYPE_INT_ARGB);
			multiplier = 4;
		}
		Graphics g = image.getGraphics();
		if(logStretch){
			int max = (int)Math.log1p(max(hist));
			g.drawLine(0,100,size * multiplier + 1,100);
			for(int i = 0; i < size; i++){
				if(type == GREY_HIST){
					g.setColor(Color.gray);
				} else if(type == RED_HIST){
					g.setColor(Color.red);
				} else if(type == BLUE_HIST){
					g.setColor(Color.blue);
				} else if(type == GREEN_HIST){
					g.setColor(Color.green);
				} else if(type == HUE_HIST){
					g.setColor(Color.getHSBColor(i/360.0f,1.0f,1.0f));
				}
				g.fillRect(i*multiplier,(int)(100 - 100.0 * Math.log1p(hist[i])/max), multiplier,(int)(100*Math.log1p(hist[i])/max));
				g.setColor(Color.black);
				g.drawRect(i*multiplier,(int)(100 - 100.0 * Math.log1p(hist[i])/max), multiplier,(int)(100*Math.log1p(hist[i])/max));
				if(i % (12+multiplier) == 0){
					g.drawRect(i*multiplier,100,0,2);
				g.drawString(i+"",i*multiplier,112);
				}
			}
		} else {
			int max = max(hist);
			g.drawLine(0,100,size*multiplier + 1,100);
			for(int i = 0; i < size; i++){
				if(type == GREY_HIST){
					g.setColor(Color.gray);
				} else if(type == RED_HIST){
					g.setColor(Color.red);
				} else if(type == BLUE_HIST){
					g.setColor(Color.blue);
				} else if(type == GREEN_HIST){
					g.setColor(Color.green);
				} else if(type == HUE_HIST){
					g.setColor(Color.getHSBColor(i/360.0f,1.0f,1.0f));
				}
				g.fillRect(i*multiplier,(int)(100 - 100.0 * hist[i]/max), multiplier,(int)(100*hist[i]/max));
				g.setColor(Color.black);
				g.drawRect(i*multiplier,(int)(100 - 100.0 * hist[i]/max), multiplier,(int)(100*hist[i]/max));
				if(i % (12+multiplier) == 0){
					g.drawRect(i*multiplier,100,0,2);
					g.drawString(i+"",i*multiplier,112);
				}
			}
		}
		
		g.drawLine(0,99,size*multiplier+1,99);
		g.drawLine(0,100,size*multiplier+1,100);
		return image;
	}
	
	public static BufferedImage removeAlpha(BufferedImage image){
		toAllMatrix(image);
		return toColorImage(toRedMatrix(image),toGreenMatrix(image),toBlueMatrix(image));
	}
	
	public static int max(int [] list ){
		int max = 0;
		for(int i = 0; i < list.length; i++){
			if(list[i] > max){
				max = list[i];
			}
		} 
		return max;
	}
	
	public static int fixRange(int in){
		if(in > 255){
			in = 255;
		} else if(in < 0){
			in = 0;
		}
		return in;
	}
}

