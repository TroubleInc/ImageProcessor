/*to fix*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class LinearZoom extends ImageFilter
{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public double zoomX;
	public double zoomY;
	public int xSize = -1;
	public int ySize = -1;
	int[][] red;
	int[][] green;
	int[][] blue;
	int[][] alpha;

	public LinearZoom(double paramDouble) {
		this.zoomX = paramDouble;
		this.zoomY = paramDouble;
	}
	
	public LinearZoom(double zoomX, double zoomY){
		this.zoomX = zoomX;
		this.zoomY = zoomY;
	}
	
	public LinearZoom(int x, int y){
		xSize = x;
		ySize = y;
	}
	
	public String toString(){
		return "Linear Zoom " + zoomX + "x and " + zoomY + "y";
	}

	public BufferedImage filter(BufferedImage paramBufferedImage) {
		this.red = toRedMatrix(paramBufferedImage);
		this.green = toGreenMatrix(paramBufferedImage);
		this.blue = toBlueMatrix(paramBufferedImage);
		this.alpha = toAlphaMatrix(paramBufferedImage);
		
		if(xSize > 0){
			zoomX = 1.0 * xSize / red.length;
			zoomY = 1.0 * ySize / red[0].length;
		}
		
		ImageFilter.resetImage();
		int[][] newRed = new int[(int)(this.red.length * this.zoomX)][];
		int[][] newGreen = new int[(int)(this.green.length * this.zoomX)][];
		int[][] newBlue = new int[(int)(this.blue.length * this.zoomX)][];
		int[][] newAlpha = new int[(int)(this.alpha.length * this.zoomX)][];
		for (int i = 0; i < newRed.length; i++) {
			newRed[i] = new int[(int)(this.red[0].length * this.zoomY)];
			newGreen[i] = new int[(int)(this.red[0].length * this.zoomY)];
			newBlue[i] = new int[(int)(this.red[0].length * this.zoomY)];
			newAlpha[i] = new int[(int)(this.red[0].length * this.zoomY)];
			for (int j = 0; j < newRed[0].length; j++) {
				int [] color = bilinearInterpolation(i/zoomX,j/zoomY,(int)(i/zoomX),(int)(i/zoomX)+1,(int)(j/zoomY),(int)(j/zoomY)+1,
					color((int)(i/zoomX),(int)(j/zoomY)),color((int)(i/zoomX),(int)(j/zoomY)+1),color((int)(i/zoomX)+1,(int)(j/zoomY)),color((int)(i/zoomX)+1,(int)(j/zoomY)+1));
				
				newRed[i][j] = color[0];
				newGreen[i][j] = color[1];
				newBlue[i][j] = color[2];
				newAlpha[i][j] = color[3];
			}
		}
		return toAlphaImage(newRed, newGreen, newBlue, newAlpha);
	}

	public double dist(int[] color1, int[] color2){
		int i = color1[0] - color2[0];
		int j = color1[1] - color2[1];
		int k = color1[2] - color2[2];
		int l = color1[3] - color2[3];
		return i * i + j * j + k * k + l * l;
	}

	public int[] lin(double dist1, int[] color1, double dist2, int[] color2, double dist3, int[] color3, double dist4, int[] color4) {
		double d = Math.abs(dist1) + Math.abs(dist2) + Math.abs(dist3) + Math.abs(dist4);
		if (d < 0.001D) {
			return new int[] { 0, 0, 0, 0 };
		}
		dist1 /= d;
		dist2 /= d;
		dist3 /= d;
		dist4 /= d;
		int[] arrayOfInt = new int[4];
		for (int i = 0; i < 4; i++) {
			arrayOfInt[i] = cleanColor((int)(dist1 * color1[i] + dist2 * color2[i] + dist3 * color3[i] + dist4 * color4[i]));
		}
		return arrayOfInt;
	}
	
	public int[] bilinearInterpolation(double x, double y, double x1, double x2, double y1, double y2, int[] color11, int[] color12, int [] color21, int[] color22){
		int[] newColor = new int[4];
		
		for(int i = 0; i < 4; i++){
			newColor[i] = (int)(1/((x2-x1)*(y2-y1)) * (color11[i]*(x2-x)*(y2-y) 
				+ color21[i]*(x-x1)*(y2-y) + color12[i]*(x2-x)*(y-y1) + color22[i]*(x-x1)*(y-y1)));
		}
		return newColor;
	}

	public int[] color(int x, int y) {
		if (x < 0)
			x = 0;
		else if (x >= this.red.length) {
			x = this.red.length - 1;
		}
		if (y < 0)
			y = 0;
		else if (y >= this.red[0].length) {
			y = this.red[0].length - 1;
		}
		return new int[] { this.red[x][y], this.green[x][y], this.blue[x][y], this.alpha[x][y] };
	}
	
	public int cleanColor(int color){
		if(color < 0){
			color = 0;
		} else if( color > 255){
			color = 255;
		}
		return color;
	}
}

