package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class BestNeighborZoom extends ImageFilter
{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public double zoom;
	int[][] red;
	int[][] green;
	int[][] blue;
	int[][] alpha;

	public BestNeighborZoom(double zoomAmount){
		this.zoom = zoomAmount;
	}
	
	public String toString(){
		return "Best Neighbor Zoom " + zoom + "x";
	}
	
	public BufferedImage filter(BufferedImage image) {
		this.red = toRedMatrix(image);
		this.green = toGreenMatrix(image);
		this.blue = toBlueMatrix(image);
		this.alpha = toAlphaMatrix(image);
		int[][] newRed = new int[(int)(this.red.length * this.zoom)][];
		int[][] newGreen = new int[(int)(this.green.length * this.zoom)][];
		int[][] newBlue = new int[(int)(this.blue.length * this.zoom)][];
		int[][] newAlpha = new int[(int)(this.alpha.length * this.zoom)][];
		for (int i = 0; i < newRed.length; i++) {
			newRed[i] = new int[(int)(this.red[0].length * this.zoom)];
			newGreen[i] = new int[(int)(this.red[0].length * this.zoom)];
			newBlue[i] = new int[(int)(this.red[0].length * this.zoom)];
			newAlpha[i] = new int[(int)(this.red[0].length * this.zoom)];
			for (int j = 0; j < newRed[0].length; j++) {
				int [] color = bNInterpolation(i/zoom,j/zoom,(int)(i/zoom),(int)(i/zoom)+1,(int)(j/zoom),(int)(j/zoom)+1,
					color((int)(i/zoom),(int)(j/zoom)),color((int)(i/zoom),(int)(j/zoom)+1),color((int)(i/zoom)+1,(int)(j/zoom)),color((int)(i/zoom)+1,(int)(j/zoom)+1));
				
				newRed[i][j] = color[0];
				newGreen[i][j] = color[1];
				newBlue[i][j] = color[2];
				newAlpha[i][j] = color[3];
			}
		}
		return toAlphaImage(newRed, newGreen, newBlue, newAlpha);
	}

	public double dist(int[] color1, int[] color2){
		int r = color1[0] - color2[0];
		int g = color1[1] - color2[1];
		int b = color1[2] - color2[2];
		return r * r + g * g + b * b;
	}

	public int[] bNInterpolation(double x, double y, double x1, double x2, double y1, double y2, int[] color11, int[] color12, int [] color21, int[] color22){
		int newColor[] = bilinearInterpolation(x,y,x1,x2,y1,y2,color11,color12,color21,color22);
		double d = 256*256*256;
		int[] finalColor = color11;
		if (d > dist(newColor, color11) && ((x-x1 + y-y1) <= 1)) {
			d = dist(newColor, color11);
			finalColor = color11;
		}
		if (d > dist(newColor, color12) && ((x-x1 + y2-y) <= 1)) {
			d = dist(newColor, color12);
			finalColor = color12;
		}
		if (d > dist(newColor, color21) && ((x2-x + y-y1) <= 1)) {
			d = dist(newColor, color21);
			finalColor = color21;
		}
		if (d > dist(newColor, color22) && ((x2-x + y2-y) <= 1)) {
			d = dist(newColor, color22);
			finalColor = color22;
		}
		return finalColor;
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
		return new int[] { this.red[x][y], this.green[x][y], this.blue[x][y], this.alpha[x][y]};
	}
}
