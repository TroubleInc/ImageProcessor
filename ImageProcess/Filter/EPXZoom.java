/*to fix*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class EPXZoom extends ImageFilter
{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public int zoom = 2;
	int[][] red;
	int[][] green;
	int[][] blue;
	
	public String toString(){
		return "EPX Zoom";
	}

	public BufferedImage filter(BufferedImage paramBufferedImage)
	{
		this.red = toRedMatrix(paramBufferedImage);
		this.green = toGreenMatrix(paramBufferedImage);
		this.blue = toBlueMatrix(paramBufferedImage);
		int[][] arrayOfInt1 = new int[this.red.length * this.zoom][this.red[0].length * this.zoom];
		int[][] arrayOfInt2 = new int[this.red.length * this.zoom][this.red[0].length * this.zoom];
		int[][] arrayOfInt3 = new int[this.red.length * this.zoom][this.red[0].length * this.zoom];
		for (int i = 0; i < this.red.length; i++) {
			for (int j = 0; j < this.red[0].length; j++) {
				arrayOfInt1[(2 * i)][(2 * j)] = this.red[i][j];
				arrayOfInt1[(2 * i + 1)][(2 * j)] = this.red[i][j];
				arrayOfInt2[(2 * i)][(2 * j)] = this.green[i][j];
				arrayOfInt2[(2 * i + 1)][(2 * j)] = this.green[i][j];
				arrayOfInt3[(2 * i)][(2 * j)] = this.blue[i][j];
				arrayOfInt3[(2 * i + 1)][(2 * j)] = this.blue[i][j];
				arrayOfInt1[(2 * i)][(2 * j + 1)] = this.red[i][j];
				arrayOfInt1[(2 * i + 1)][(2 * j + 1)] = this.red[i][j];
				arrayOfInt2[(2 * i)][(2 * j + 1)] = this.green[i][j];
				arrayOfInt2[(2 * i + 1)][(2 * j + 1)] = this.green[i][j];
				arrayOfInt3[(2 * i)][(2 * j + 1)] = this.blue[i][j];
				arrayOfInt3[(2 * i + 1)][(2 * j + 1)] = this.blue[i][j];

				int[] arrayOfInt4 = color(i - 1, j);
				int[] arrayOfInt5 = color(i, j + 1);
				int[] arrayOfInt6 = color(i, j - 1);
				int[] arrayOfInt7 = color(i + 1, j);

				if ((equal(arrayOfInt6, arrayOfInt4)) && (!equal(arrayOfInt6, arrayOfInt7)) && (!equal(arrayOfInt4, arrayOfInt5))) {
					arrayOfInt1[(2 * i)][(2 * j)] = arrayOfInt4[0];
					arrayOfInt2[(2 * i)][(2 * j)] = arrayOfInt4[1];
					arrayOfInt3[(2 * i)][(2 * j)] = arrayOfInt4[2];
				}
				if ((equal(arrayOfInt4, arrayOfInt5)) && (!equal(arrayOfInt4, arrayOfInt6)) && (!equal(arrayOfInt5, arrayOfInt7))) {
					arrayOfInt1[(2 * i)][(2 * j + 1)] = arrayOfInt5[0];
					arrayOfInt2[(2 * i)][(2 * j + 1)] = arrayOfInt5[1];
					arrayOfInt3[(2 * i)][(2 * j + 1)] = arrayOfInt5[2];
				}
				if ((equal(arrayOfInt7, arrayOfInt6)) && (!equal(arrayOfInt7, arrayOfInt5)) && (!equal(arrayOfInt6, arrayOfInt4))) {
					arrayOfInt1[(2 * i + 1)][(2 * j)] = arrayOfInt6[0];
					arrayOfInt2[(2 * i + 1)][(2 * j)] = arrayOfInt6[1];
					arrayOfInt3[(2 * i + 1)][(2 * j)] = arrayOfInt6[2];
				}
				if ((equal(arrayOfInt5, arrayOfInt7)) && (!equal(arrayOfInt5, arrayOfInt4)) && (!equal(arrayOfInt7, arrayOfInt6))) {
					arrayOfInt1[(2 * i + 1)][(2 * j + 1)] = arrayOfInt7[0];
					arrayOfInt2[(2 * i + 1)][(2 * j + 1)] = arrayOfInt7[1];
					arrayOfInt3[(2 * i + 1)][(2 * j + 1)] = arrayOfInt7[2];
				}
			}
		}
		return toColorImage(arrayOfInt1, arrayOfInt2, arrayOfInt3);
	}

	public int[] color(int paramInt1, int paramInt2)
	{
		if (paramInt1 < 0)
			paramInt1 = 0;
		else if (paramInt1 >= this.red.length) {
			paramInt1 = this.red.length - 1;
		}
		if (paramInt2 < 0)
			paramInt2 = 0;
		else if (paramInt2 >= this.red[0].length) {
			paramInt2 = this.red[0].length - 1;
		}
		return new int[] { this.red[paramInt1][paramInt2], this.green[paramInt1][paramInt2], this.blue[paramInt1][paramInt2] };
	}

	public boolean equal(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
		return Math.abs(paramArrayOfInt1[0] - paramArrayOfInt2[0]) + Math.abs(paramArrayOfInt1[1] - paramArrayOfInt2[1]) + Math.abs(paramArrayOfInt1[2] - paramArrayOfInt2[2]) < 10;
	}
}

/* Location:					 Z:\home\will\Documents\ImageProcessjarring\diff\
 * Qualified Name:		 ImageProcess.EPXZoom
 * JD-Core Version:		0.6.0
 */
