/*to fix decompile stuff*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class ComponentPosterizeFull extends ImageFilter
{
	public int numComponents;
	public int threshold;

	public ComponentPosterizeFull(int paramInt)
	{
		this.threshold = paramInt;
	}
	
	public String toString(){
		return "Smooth Component Posterize threshold:" + threshold;
	}

	public BufferedImage filter(BufferedImage paramBufferedImage)
	{
		BufferedImage localBufferedImage = new InvertFilter().filter(new BWThreshold(this.threshold).filter(new RobertEdgeDetect().filter(paramBufferedImage)));

		int[][] arrayOfInt5 = toGreyMatrix(localBufferedImage);
		int[][] arrayOfInt6 = toRedMatrix(paramBufferedImage);
		int[][] arrayOfInt7 = toGreenMatrix(paramBufferedImage);
		int[][] arrayOfInt8 = toBlueMatrix(paramBufferedImage);
		int i = 0;

		for (int j = 0; j < arrayOfInt5.length; j++) {
			for (int k = 0; k < arrayOfInt5[0].length; k++) {
				if (arrayOfInt5[j][k] == 0) {
					int m = 0;
					if (j > 0) {
						if ((k > 0) && 
							(arrayOfInt5[(j - 1)][(k - 1)] == 0)) {
							m++;
						}

						if (arrayOfInt5[(j - 1)][k] == 0) {
							m++;
						}
						if ((k < arrayOfInt5[0].length - 1) && 
							(arrayOfInt5[(j - 1)][(k + 1)] == 0)) {
							m++;
						}
					}

					if ((k > 0) && 
						(arrayOfInt5[j][(k - 1)] == 0)) {
						m++;
					}

					if ((k < arrayOfInt5[0].length - 1) && 
						(arrayOfInt5[j][(k + 1)] == 0)) {
						m++;
					}

					if (j < arrayOfInt5.length - 1) {
						if ((k > 0) && 
							(arrayOfInt5[(j + 1)][(k - 1)] == 0)) {
							m++;
						}

						if (arrayOfInt5[(j + 1)][k] == 0) {
							m++;
						}
						if ((k < arrayOfInt5[0].length - 1) && 
							(arrayOfInt5[(j + 1)][(k + 1)] == 0)) {
							m++;
						}
					}

					if (m < 2) {
						arrayOfInt5[j][k] = (char)255;
					}
				}

			}

		}

		for (int j = 0; j < arrayOfInt5.length; j++) {
			for (int k = 0; k < arrayOfInt5[j].length; k++) {
				if (arrayOfInt5[j][k] > 0) {
					int m = 0;
					if ((j > 0) && 
						(arrayOfInt5[(j - 1)][k] > 0)) {
						arrayOfInt5[j][k] = arrayOfInt5[(j - 1)][k];
						m = 1;
					}

					if ((k > 0) && 
						(arrayOfInt5[j][(k - 1)] > 0)) {
						arrayOfInt5[j][k] = arrayOfInt5[j][(k - 1)];
						m = 1;
					}

					if (m == 0) {
						i++;
						arrayOfInt5[j][k] = i;
					}
				}
			}
		}

		int[] arrayOfInt9 = new int[i + 1];
		arrayOfInt9[0] = 0;
		for (int k = 1; k < arrayOfInt9.length; k++) {
			arrayOfInt9[k] = (i + 100);
		}

		for (int k = 0; k < arrayOfInt5.length; k++) {
			for (int m = 0; m < arrayOfInt5[k].length; m++) {
				if (arrayOfInt5[k][m] > 0) {
					if ((k > 0) && (arrayOfInt5[(k - 1)][m] > 0) && (arrayOfInt5[k][m] != arrayOfInt5[(k - 1)][m])) {
						arrayOfInt9[arrayOfInt5[(k - 1)][m]] = min(arrayOfInt5[k][m], arrayOfInt5[(k - 1)][m], arrayOfInt9[arrayOfInt5[k][m]], arrayOfInt9[arrayOfInt5[(k - 1)][m]]);

						arrayOfInt9[arrayOfInt5[k][m]] = min(arrayOfInt5[k][m], arrayOfInt5[(k - 1)][m], arrayOfInt9[arrayOfInt5[k][m]], arrayOfInt9[arrayOfInt5[(k - 1)][m]]);

						arrayOfInt5[k][m] = min(arrayOfInt5[k][m], arrayOfInt5[(k - 1)][m], arrayOfInt9[arrayOfInt5[k][m]], arrayOfInt9[arrayOfInt5[(k - 1)][m]]);
					}

					if ((m > 0) && (arrayOfInt5[k][(m - 1)] > 0) && (arrayOfInt5[k][m] != arrayOfInt5[k][(m - 1)])) {
						arrayOfInt9[arrayOfInt5[k][(m - 1)]] = min(arrayOfInt5[k][m], arrayOfInt5[k][(m - 1)], arrayOfInt9[arrayOfInt5[k][m]], arrayOfInt9[arrayOfInt5[k][(m - 1)]]);

						arrayOfInt9[arrayOfInt5[k][m]] = min(arrayOfInt5[k][m], arrayOfInt5[k][(m - 1)], arrayOfInt9[arrayOfInt5[k][m]], arrayOfInt9[arrayOfInt5[k][(m - 1)]]);

						arrayOfInt5[k][m] = min(arrayOfInt5[k][m], arrayOfInt5[k][(m - 1)], arrayOfInt9[arrayOfInt5[k][m]], arrayOfInt9[arrayOfInt5[k][(m - 1)]]);
					}
				}
			}

		}

		int k = 0;
		for (int m = 0; m < arrayOfInt9.length; m++) {
			if ((arrayOfInt9[m] >= arrayOfInt9.length) || 
				(arrayOfInt9[arrayOfInt9[m]] >= arrayOfInt9[m])) continue;
			k++;
			arrayOfInt9[m] = arrayOfInt9[arrayOfInt9[m]];
			m--;
		}

		this.numComponents = 0;
		for (int m = 1; m < arrayOfInt9.length; m++) {
			if ((arrayOfInt9[m] == m) || (arrayOfInt9[m] == i + 100)) {
				arrayOfInt9[m] = this.numComponents;
				this.numComponents += 1;
			} else {
				arrayOfInt9[m] = arrayOfInt9[arrayOfInt9[m]];
			}
		}
		int[] arrayOfInt1 = new int[this.numComponents + 1];
		int[] arrayOfInt2 = new int[this.numComponents + 1];
		int[] arrayOfInt3 = new int[this.numComponents + 1];
		int[] arrayOfInt4 = new int[this.numComponents + 1];

		for (int m = 0; m < arrayOfInt6.length; m++) {
			for (int n = 0; n < arrayOfInt6[m].length; n++) {
				int i1 = 0;
				if ((m > 0) && (n > 0) && (i1 == 0) && 
					(arrayOfInt5[(m - 1)][(n - 1)] > 0)) {
					arrayOfInt1[arrayOfInt9[arrayOfInt5[(m - 1)][(n - 1)]]] += arrayOfInt6[m][n];
					arrayOfInt2[arrayOfInt9[arrayOfInt5[(m - 1)][(n - 1)]]] += arrayOfInt7[m][n];
					arrayOfInt3[arrayOfInt9[arrayOfInt5[(m - 1)][(n - 1)]]] += arrayOfInt8[m][n];
					arrayOfInt4[arrayOfInt9[arrayOfInt5[(m - 1)][(n - 1)]]] += 1;
					i1 = 1;
				}

				if ((n > 0) && (m < arrayOfInt6.length - 1) && (i1 == 0) && 
					(arrayOfInt5[m][(n - 1)] > 0)) {
					arrayOfInt1[arrayOfInt9[arrayOfInt5[m][(n - 1)]]] += arrayOfInt6[m][n];
					arrayOfInt2[arrayOfInt9[arrayOfInt5[m][(n - 1)]]] += arrayOfInt7[m][n];
					arrayOfInt3[arrayOfInt9[arrayOfInt5[m][(n - 1)]]] += arrayOfInt8[m][n];
					arrayOfInt4[arrayOfInt9[arrayOfInt5[m][(n - 1)]]] += 1;
					i1 = 1;
				}

				if ((m > 0) && (n < arrayOfInt6[0].length - 1) && (i1 == 0) && 
					(arrayOfInt5[(m - 1)][n] > 0)) {
					arrayOfInt1[arrayOfInt9[arrayOfInt5[(m - 1)][n]]] += arrayOfInt6[m][n];
					arrayOfInt2[arrayOfInt9[arrayOfInt5[(m - 1)][n]]] += arrayOfInt7[m][n];
					arrayOfInt3[arrayOfInt9[arrayOfInt5[(m - 1)][n]]] += arrayOfInt8[m][n];
					arrayOfInt4[arrayOfInt9[arrayOfInt5[(m - 1)][n]]] += 1;
					i1 = 1;
				}

				if ((m >= arrayOfInt6.length - 1) || (n >= arrayOfInt6[0].length - 1) || (i1 != 0) || 
					(arrayOfInt5[m][n] <= 0)) continue;
				arrayOfInt1[arrayOfInt9[arrayOfInt5[m][n]]] += arrayOfInt6[m][n];
				arrayOfInt2[arrayOfInt9[arrayOfInt5[m][n]]] += arrayOfInt7[m][n];
				arrayOfInt3[arrayOfInt9[arrayOfInt5[m][n]]] += arrayOfInt8[m][n];
				arrayOfInt4[arrayOfInt9[arrayOfInt5[m][n]]] += 1;
				i1 = 1;
			}

		}

		for (int m = 0; m <= this.numComponents; m++) {
			arrayOfInt1[m] = (int)(1.0D * arrayOfInt1[m] / arrayOfInt4[m]);
			arrayOfInt2[m] = (int)(1.0D * arrayOfInt2[m] / arrayOfInt4[m]);
			arrayOfInt3[m] = (int)(1.0D * arrayOfInt3[m] / arrayOfInt4[m]);;
		}
		resetImage();
		int[][] arrayOfInt10 = toGreyMatrix(paramBufferedImage);
		resetImage();
		int[][] arrayOfInt11 = toGreyMatrix(paramBufferedImage);
		resetImage();
		int[][] arrayOfInt12 = toGreyMatrix(paramBufferedImage);
		for (int i2 = 0; i2 < arrayOfInt5.length; i2++) {
			for (int i3 = 0; i3 < arrayOfInt5[i2].length; i3++) {
				int i4 = 0;
				if ((i2 > 0) && (i3 > 0) && (i4 == 0) && 
					(arrayOfInt5[(i2 - 1)][(i3 - 1)] > 0)) {
					arrayOfInt10[i2][i3] = arrayOfInt1[arrayOfInt9[arrayOfInt5[(i2 - 1)][(i3 - 1)]]];
					arrayOfInt11[i2][i3] = arrayOfInt2[arrayOfInt9[arrayOfInt5[(i2 - 1)][(i3 - 1)]]];
					arrayOfInt12[i2][i3] = arrayOfInt3[arrayOfInt9[arrayOfInt5[(i2 - 1)][(i3 - 1)]]];
					i4 = 1;
				}

				if ((i3 > 0) && (i2 < arrayOfInt6.length - 1) && (i4 == 0) && 
					(arrayOfInt5[i2][(i3 - 1)] > 0)) {
					arrayOfInt10[i2][i3] = arrayOfInt1[arrayOfInt9[arrayOfInt5[i2][(i3 - 1)]]];
					arrayOfInt11[i2][i3] = arrayOfInt2[arrayOfInt9[arrayOfInt5[i2][(i3 - 1)]]];
					arrayOfInt12[i2][i3] = arrayOfInt3[arrayOfInt9[arrayOfInt5[i2][(i3 - 1)]]];
					i4 = 1;
				}

				if ((i2 > 0) && (i3 < arrayOfInt6[0].length - 1) && (i4 == 0) && 
					(arrayOfInt5[(i2 - 1)][i3] > 0)) {
					arrayOfInt10[i2][i3] = arrayOfInt1[arrayOfInt9[arrayOfInt5[(i2 - 1)][i3]]];
					arrayOfInt11[i2][i3] = arrayOfInt2[arrayOfInt9[arrayOfInt5[(i2 - 1)][i3]]];
					arrayOfInt12[i2][i3] = arrayOfInt3[arrayOfInt9[arrayOfInt5[(i2 - 1)][i3]]];
					i4 = 1;
				}

				if ((i2 < arrayOfInt6.length - 1) && (i3 < arrayOfInt6[0].length - 1) && (i4 == 0) && 
					(arrayOfInt5[i2][i3] > 0)) {
					arrayOfInt10[i2][i3] = arrayOfInt1[arrayOfInt9[arrayOfInt5[i2][i3]]];
					arrayOfInt11[i2][i3] = arrayOfInt2[arrayOfInt9[arrayOfInt5[i2][i3]]];
					arrayOfInt12[i2][i3] = arrayOfInt3[arrayOfInt9[arrayOfInt5[i2][i3]]];
					i4 = 1;
				}

				if (i4 == 0) {
					arrayOfInt10[i2][i3] = arrayOfInt6[i2][i3];
					arrayOfInt11[i2][i3] = arrayOfInt7[i2][i3];
					arrayOfInt12[i2][i3] = arrayOfInt8[i2][i3];
				}
			}
		}

		return toColorImage(arrayOfInt10, arrayOfInt11, arrayOfInt12);
	}

	public int min(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		return Math.min(paramInt1, Math.min(paramInt2, Math.min(paramInt3, paramInt4)));
	}
}

/* Location:					 Z:\home\will\Documents\ImageProcessjarring\diff\
 * Qualified Name:		 ImageProcess.ComponentPosterizeFull
 * JD-Core Version:		0.6.0
 */
