/*to fix*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class NearestNeighborZoom extends ImageFilter
{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public double zoom;
	int[][] red;
	int[][] green;
	int[][] blue;
	int[][] alpha;

	public NearestNeighborZoom(double paramDouble)
	{
		this.zoom = paramDouble;
	}
	
	public String toString(){
		return "Nearest Neighbor Zoom " + zoom + "x";
	}

	public BufferedImage filter(BufferedImage paramBufferedImage) {
		this.red = toRedMatrix(paramBufferedImage);
		this.green = toGreenMatrix(paramBufferedImage);
		this.blue = toBlueMatrix(paramBufferedImage);
		this.alpha = toAlphaMatrix(paramBufferedImage);
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
				int[] color = nN(Math.hypot(j / this.zoom - (int)(j / this.zoom), i / this.zoom - (int)(i / this.zoom)) + 0.001D, 
					color((int)(i / this.zoom), (int)(j / this.zoom)), 
					Math.hypot(j / this.zoom - (int)(j / this.zoom) - 1.0D, i / this.zoom - (int)(i / this.zoom)) + 0.001D, 
					color((int)(i / this.zoom), (int)(j / this.zoom) + 1), 
					Math.hypot(j / this.zoom - (int)(j / this.zoom), i / this.zoom - (int)(i / this.zoom) - 1.0D) + 0.001D, 
					color((int)(i / this.zoom) + 1, (int)(j / this.zoom)), 
					Math.hypot(j / this.zoom - (int)(j / this.zoom) - 1.0D, i / this.zoom - (int)(i / this.zoom) - 1.0D) + 0.001D, 
					color((int)(i / this.zoom) + 1, (int)(j / this.zoom) + 1));

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

	public int[] nN(double dist1, int[] color1, double dist2, int[] color2, double dist3, int[] color3, double dist4, int[] color4) {
		double d = dist1;
		int[] arrayOfInt2 = color1;
		if (d > dist2) {
			d = dist2;
			arrayOfInt2 = color2;
		}
		if (d > dist3) {
			d = dist3;
			arrayOfInt2 = color3;
		}
		if (d > dist4) {
			arrayOfInt2 = color4;
		}
		return arrayOfInt2;
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
}
