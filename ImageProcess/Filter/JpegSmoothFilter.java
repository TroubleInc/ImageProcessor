package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class JpegSmoothFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Jpeg Smoother";
	public int compressionLevel;
	public double propNoise;
	public int[][] red, green, blue, alpha;
	
	public JpegSmoothFilter(int compLevel){
		compressionLevel = compLevel;
		propNoise = (100-compLevel)/100.0;
	}
	
	
	public String toString(){
		return "Jpeg Smoother";
	}
	
	public BufferedImage filter(BufferedImage image){
		red = toRedMatrix(image);
		green = toGreenMatrix(image);
		blue = toBlueMatrix(image);
		alpha = toAlphaMatrix(image);
		
		smoothVarianceByBlock();
		
		blockEdgeSmooth();
		
		//return (new GaussianSharpen(5,.1)).filter(toAlphaImage(red,green,blue,alpha));
		
		return toAlphaImage(red,green,blue,alpha);
	}
	
	public void blockEdgeSmooth(){
		for(int i = 0; i < red.length; i ++){
			for(int j = 0; j < red[0].length; j++){
				if(i%8 == 0 && i > 0 && i < red.length-2){
					red[i][j] = (int)((red[i-2][j] + 2 * red[i-1][j] + 3 * red[i][j] + 2 * red[i+1][j])/8);
					red[i-1][j] = (int)((2 * red[i-2][j] + 3 * red[i-1][j] + 2 * red[i][j] + red[i+1][j])/8);
					green[i][j] = (int)((green[i-2][j] + 2 * green[i-1][j] + 3 * green[i][j] + 2 * green[i+1][j])/8);
					green[i-1][j] = (int)((2 * green[i-2][j] + 3 * green[i-1][j] + 2 * green[i][j] + green[i+1][j])/8);
					blue[i][j] = (int)((blue[i-2][j] + 2 * blue[i-1][j] + 3 * blue[i][j] + 2 * blue[i+1][j])/8);
					blue[i-1][j] = (int)((2 * blue[i-2][j] + 3 * blue[i-1][j] + 2 * blue[i][j] + blue[i+1][j])/8);
				}
				if(j%8 == 0 && j > 0 && i < red[0].length-2){
					red[i][j] = (int)((red[i][j-2] + 2 * red[i][j-1] + 3 * red[i][j] + 2 * red[i][j+1])/8);
					red[i][j-1] = (int)((2 * red[i][j-2] + 3 * red[i][j-1] + 2 * red[i][j] + red[i][j+1])/8);
					green[i][j] = (int)((green[i][j-2] + 2 * green[i][j-1] + 3 * green[i][j] + 2 * green[i][j+1])/8);
					green[i][j-1] = (int)((2 * green[i][j-2] + 3 * green[i][j-1] + 2 * green[i][j] + green[i][j+1])/8);
					blue[i][j] = (int)((blue[i][j-2] + 2 * blue[i][j-1] + 3 * blue[i][j] + 2 * blue[i][j+1])/8);
					blue[i][j-1] = (int)((2 * blue[i][j-2] + 3 * blue[i][j-1] + 2 * blue[i][j] + blue[i][j+1])/8);
				}
			}
		}
	}
	
	public double blockVariance(int x, int y){
		int reds = 0;
		int redsq = 0;
		int greens = 0;
		int greensq = 0;
		int blues = 0;
		int bluesq = 0;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				reds += red[x+i][y+j];
				redsq += (red[x+i][y+j]) * (red[x+i][y+j]);
				greens += green[x+i][y+j];
				greensq += (green[x+i][y+j]) * (green[x+i][y+j]);
				blues += blue[x+i][y+j];
				bluesq += (blue[x+i][y+j]) * (blue[x+i][y+j]);
			}
		}
		
		return redsq/64.0 - reds*reds/512.0 + greensq/64.0 - greens * greens / 512.0 + bluesq/64.0 - blues * blues / 512.0;
		
	}
	
	public void smoothVarianceByBlock(){
		for(int i = 0; i < red.length-7; i+=8){
			for(int j = 0; j < red[0].length-7; j+=8){
				writeBlock(i,j,smooth(transcribeBlock(i,j),propNoise*2,Math.sqrt(Math.sqrt(256*256 - blockVariance(i,j)))));
			}
		}
	}
	
	public int [][][] transcribeBlock(int x, int y){
		int [][][] imageBlock = new int[3][8][8];
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				imageBlock[0][i][j] = red[x+i][y+j];
				imageBlock[1][i][j] = green[x+i][y+j];
				imageBlock[2][i][j] = blue[x+i][y+j];
			}
		}
		
		return imageBlock;
	}
	
	public void writeBlock(int x, int y, int[][][] imageBlock){
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				red[x+i][y+j] = imageBlock[0][i][j];
				green[x+i][y+j] = imageBlock[1][i][j];
				blue[x+i][y+j] = imageBlock[2][i][j];
			}
		}
	}
	
	public int[][][] smooth(int [][][] imageBlock, double proportion, double gaussianVariance){
		
		int[][][] out = new int[3][8][8];
		for(int c = 0; c < 3; c++){
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					int val = imageBlock[c][i][j];
					if(i > 0){
						val += proportion * Math.exp(-1 * ((imageBlock[c][i][j] - imageBlock[c][i-1][j])/gaussianVariance) * ((imageBlock[c][i][j] - imageBlock[c][i-1][j])/gaussianVariance))*(imageBlock[c][i-1][j] - imageBlock[c][i][j]);
					}
					if(j > 0){
						val += proportion * Math.exp(-1 * ((imageBlock[c][i][j] - imageBlock[c][i][j-1])/gaussianVariance) * ((imageBlock[c][i][j] - imageBlock[c][i][j-1])/gaussianVariance))*(imageBlock[c][i][j-1] - imageBlock[c][i][j]);
					}
					if(i < imageBlock[c].length - 1){
						val += proportion * Math.exp(-1 * ((imageBlock[c][i][j] - imageBlock[c][i+1][j])/gaussianVariance) * ((imageBlock[c][i][j] - imageBlock[c][i+1][j])/gaussianVariance))*(imageBlock[c][i+1][j] - imageBlock[c][i][j]);
					}
					if(j < imageBlock[c][i].length - 1){
						val += proportion * Math.exp(-1 * ((imageBlock[c][i][j] - imageBlock[c][i][j+1])/gaussianVariance) * ((imageBlock[c][i][j] - imageBlock[c][i][j+1])/gaussianVariance))*(imageBlock[c][i][j+1] - imageBlock[c][i][j]);
					}
					if(val > 255){
						val = 255;
					}
					if(val < 0){
						val = 0;
					}
					out[c][i][j] = val;
				}
			}
		}
		return out;
	}
}

