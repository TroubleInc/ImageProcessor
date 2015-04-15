/*to fix*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class ComponentPosterize extends ImageFilter
{
	public int numComponents;
	public int threshold;
	public boolean cartoonize;

	public ComponentPosterize(int thres, boolean cartoon)
	{
		this.threshold = thres;
		this.cartoonize = cartoon;
	}
	
	public String toString(){
		return "Component Posterize threshold:" + threshold + ((cartoonize)?" Cartoonify":"");
	}

	public BufferedImage filter(BufferedImage image)
	{
		BufferedImage edgeImage = new InvertFilter().filter(new BWThreshold(this.threshold).filter(new RobertEdgeDetect().filter(image)));

		int[][] greyImage = toGreyMatrix(edgeImage);
		int[][] redArray = toRedMatrix(image);
		int[][] greenArray = toGreenMatrix(image);
		int[][] blueArray = toBlueMatrix(image);
		int i = 0;

		for (int j = 0; j < greyImage.length; j++) {
			for (int k = 0; k < greyImage[0].length; k++) {
				if (greyImage[j][k] == 0) {
					int m = 0;
					if (j > 0) {
						if ((k > 0) && 
							(greyImage[(j - 1)][(k - 1)] == 0)) {
							m++;
						}

						if (greyImage[(j - 1)][k] == 0) {
							m++;
						}
						if ((k < greyImage[0].length - 1) && 
							(greyImage[(j - 1)][(k + 1)] == 0)) {
							m++;
						}
					}

					if ((k > 0) && 
						(greyImage[j][(k - 1)] == 0)) {
						m++;
					}

					if ((k < greyImage[0].length - 1) && 
						(greyImage[j][(k + 1)] == 0)) {
						m++;
					}

					if (j < greyImage.length - 1) {
						if ((k > 0) && 
							(greyImage[(j + 1)][(k - 1)] == 0)) {
							m++;
						}

						if (greyImage[(j + 1)][k] == 0) {
							m++;
						}
						if ((k < greyImage[0].length - 1) && 
							(greyImage[(j + 1)][(k + 1)] == 0)) {
							m++;
						}
					}

					if (m < 2) {
						greyImage[j][k] = (char)255;
					}
					if ((m > 6) && (this.cartoonize)) {
						greyImage[j][k] = (char)255;
					}
				}

			}

		}

		for (int j = 0; j < greyImage.length; j++) {
			for (int k = 0; k < greyImage[j].length; k++) {
				if (greyImage[j][k] > 0) {
					int m = 0;
					if ((j > 0) && 
						(greyImage[(j - 1)][k] > 0)) {
						greyImage[j][k] = greyImage[(j - 1)][k];
						m = 1;
					}

					if ((k > 0) && 
						(greyImage[j][(k - 1)] > 0)) {
						greyImage[j][k] = greyImage[j][(k - 1)];
						m = 1;
					}

					if (m == 0) {
						i++;
						greyImage[j][k] = i;
					}
				}
			}
		}

		int[] compEquivTable = new int[i + 1];
		compEquivTable[0] = 0;
		for (int k = 1; k < compEquivTable.length; k++) {
			compEquivTable[k] = (i + 100);
		}

		for (int k = 0; k < greyImage.length; k++) {
			for (int m = 0; m < greyImage[k].length; m++) {
				if (greyImage[k][m] > 0) {
					if ((k > 0) && (greyImage[(k - 1)][m] > 0) && (greyImage[k][m] != greyImage[(k - 1)][m])) {
						compEquivTable[greyImage[(k - 1)][m]] = min(greyImage[k][m], greyImage[(k - 1)][m], compEquivTable[greyImage[k][m]], compEquivTable[greyImage[(k - 1)][m]]);

						compEquivTable[greyImage[k][m]] = min(greyImage[k][m], greyImage[(k - 1)][m], compEquivTable[greyImage[k][m]], compEquivTable[greyImage[(k - 1)][m]]);

						greyImage[k][m] = min(greyImage[k][m], greyImage[(k - 1)][m], compEquivTable[greyImage[k][m]], compEquivTable[greyImage[(k - 1)][m]]);
					}

					if ((m > 0) && (greyImage[k][(m - 1)] > 0) && (greyImage[k][m] != greyImage[k][(m - 1)])) {
						compEquivTable[greyImage[k][(m - 1)]] = min(greyImage[k][m], greyImage[k][(m - 1)], compEquivTable[greyImage[k][m]], compEquivTable[greyImage[k][(m - 1)]]);

						compEquivTable[greyImage[k][m]] = min(greyImage[k][m], greyImage[k][(m - 1)], compEquivTable[greyImage[k][m]], compEquivTable[greyImage[k][(m - 1)]]);

						greyImage[k][m] = min(greyImage[k][m], greyImage[k][(m - 1)], compEquivTable[greyImage[k][m]], compEquivTable[greyImage[k][(m - 1)]]);
					}
				}
			}

		}

		int k = 0;
		for (int m = 0; m < compEquivTable.length; m++) {
			if ((compEquivTable[m] >= compEquivTable.length) || 
				(compEquivTable[compEquivTable[m]] >= compEquivTable[m])) continue;
			k++;
			compEquivTable[m] = compEquivTable[compEquivTable[m]];
			m--;
		}

		this.numComponents = 0;
		for (int m = 1; m < compEquivTable.length; m++) {
			if ((compEquivTable[m] == m) || (compEquivTable[m] == i + 100)) {
				compEquivTable[m] = this.numComponents;
				this.numComponents += 1;
			} else {
				compEquivTable[m] = compEquivTable[compEquivTable[m]];
			}
		}
		
		int[] arrayOfInt1 = new int[this.numComponents + 1];
		int[] arrayOfInt2 = new int[this.numComponents + 1];
		int[] arrayOfInt3 = new int[this.numComponents + 1];
		int[] arrayOfInt4 = new int[this.numComponents + 1];

		for (int m = 0; m < greyImage.length; m++) {
			for (int n = 0; n < greyImage[m].length; n++) {
				if (greyImage[m][n] > 0) {
					if (compEquivTable[greyImage[m][n]] != i) {
						arrayOfInt1[compEquivTable[greyImage[m][n]]] += redArray[m][n];
						arrayOfInt2[compEquivTable[greyImage[m][n]]] += greenArray[m][n];
						arrayOfInt3[compEquivTable[greyImage[m][n]]] += blueArray[m][n];
						arrayOfInt4[compEquivTable[greyImage[m][n]]] += 1;
					} else {
						arrayOfInt1[greyImage[m][n]] += redArray[m][n];
						arrayOfInt2[greyImage[m][n]] += greenArray[m][n];
						arrayOfInt3[greyImage[m][n]] += blueArray[m][n];
						arrayOfInt4[greyImage[m][n]] += 1;
					}
				}
			}
		}

		for (int m = 0; m <= this.numComponents; m++) {
			arrayOfInt1[m] = (int)(1.0D * arrayOfInt1[m] / arrayOfInt4[m]);
			arrayOfInt2[m] = (int)(1.0D * arrayOfInt2[m] / arrayOfInt4[m]);
			arrayOfInt3[m] = (int)(1.0D * arrayOfInt3[m] / arrayOfInt4[m]);
		}

		int[][] localObject2 = toGreyMatrix(edgeImage);
		int n = 1;
		boolean bool = this.cartoonize;
		while (bool) {
			bool = false;
			for (int i1 = 0; i1 < greyImage.length; i1++) {
				for (int i2 = 0; i2 < greyImage[0].length; i2++) {
					if (greyImage[i1][i2] == 0) {
						localObject2[i1][i2] = 0;
						if ((i1 > 0) && 
							(greyImage[(i1 - 1)][i2] != 0)) {
							localObject2[i1][i2] = greyImage[(i1 - 1)][i2];
						}

						if ((i2 > 0) && 
							(greyImage[i1][(i2 - 1)] != 0)) {
							localObject2[i1][i2] = greyImage[i1][(i2 - 1)];
						}

						if ((i2 < greyImage[0].length - 1) && 
							(greyImage[i1][(i2 + 1)] != 0)) {
							localObject2[i1][i2] = greyImage[i1][(i2 + 1)];
						}

						if ((i1 < greyImage.length - 1) && 
							(greyImage[(i1 + 1)][i2] != 0)) {
							localObject2[i1][i2] = greyImage[(i1 + 1)][i2];
						}

						if (localObject2[i1][i2] == 0)
							bool = true;
					}
					else {
						localObject2[i1][i2] = greyImage[i1][i2];
					}
				}
			}
			int [][] localObject3 = greyImage;
			greyImage = localObject2;
			localObject2 = localObject3;
		}
		resetImage();
		int[][] localObject3 = toGreyMatrix(edgeImage);
		resetImage();
		int[][] arrayOfInt9 = toGreyMatrix(edgeImage);
		resetImage();
		int[][] arrayOfInt10 = toGreyMatrix(edgeImage);
		for (int i3 = 0; i3 < greyImage.length; i3++) {
			for (int i4 = 0; i4 < greyImage[i3].length; i4++) {
				if (greyImage[i3][i4] > 0) {
					if (compEquivTable[greyImage[i3][i4]] != i) {
						localObject3[i3][i4] = arrayOfInt1[compEquivTable[greyImage[i3][i4]]];
						arrayOfInt9[i3][i4] = arrayOfInt2[compEquivTable[greyImage[i3][i4]]];
						arrayOfInt10[i3][i4] = arrayOfInt3[compEquivTable[greyImage[i3][i4]]];
					} else {
						localObject3[i3][i4] = arrayOfInt1[greyImage[i3][i4]];
						arrayOfInt9[i3][i4] = arrayOfInt2[greyImage[i3][i4]];
						arrayOfInt10[i3][i4] = arrayOfInt3[greyImage[i3][i4]];
					}
				} else {
					localObject3[i3][i4] = redArray[i3][i4];
					arrayOfInt9[i3][i4] = greenArray[i3][i4];
					arrayOfInt10[i3][i4] = blueArray[i3][i4];
				}
			}
		}

		return (BufferedImage)(BufferedImage)(BufferedImage)toColorImage(localObject3, arrayOfInt9, arrayOfInt10);
	}

	public int min(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		return Math.min(paramInt1, Math.min(paramInt2, Math.min(paramInt3, paramInt4)));
	}
}
