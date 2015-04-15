/*to fix*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class ImageInterleave extends ImageFilter
{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Image Interleave";
	public BufferedImage interleaveImage;
	public int vertSlice;
	public int horizSlice;

	public ImageInterleave(BufferedImage paramBufferedImage, int vertSlice, int horizSlice)
	{
		this.interleaveImage = paramBufferedImage;
		this.vertSlice = vertSlice;
		this.horizSlice = horizSlice;
	}
	
	public String toString(){
		return "Interleave Images" + vertSlice + "x" + horizSlice;
	}

	public BufferedImage filter(BufferedImage image) {
		int[][] redNew = toRedMatrix(image);
		int[][] greenNew = toGreenMatrix(image);
		int[][] blueNew = toBlueMatrix(image);
		int[][] alphaNew = toAlphaMatrix(image);
		int[][] redThis = toRedMatrix(this.interleaveImage);
		int[][] greenThis = toGreenMatrix(this.interleaveImage);
		int[][] blueThis = toBlueMatrix(this.interleaveImage);
		int[][] alphaThis = toAlphaMatrix(this.interleaveImage);

		if ((redNew.length < redThis.length) && (redNew[0].length < redThis[0].length)) {
			int[][] temp = redNew;
			redNew = redThis;
			redThis = temp;
			
			temp = greenNew;
			greenNew = greenThis;
			greenThis = temp;
			
			temp = blueNew;
			blueNew = blueThis;
			blueThis = temp;
			
			temp = alphaNew;
			alphaNew = alphaThis;
			alphaThis = temp;
		}

		for (int i = 0; (i < redNew.length) && (i < redThis.length); i++) {
			for (int j = 0; (j < redNew[0].length) && (j < redThis[0].length); j++) {
				if((((horizSlice > 0)?(j/horizSlice):(1))+((vertSlice > 0)?(i/vertSlice):(1)))%2 == 1){
					redNew[i][j] = redThis[i][j];
					greenNew[i][j] = greenThis[i][j];
					blueNew[i][j] = blueThis[i][j];
					alphaNew[i][j] = alphaThis[i][j];
				} else {

				}
			}
		}

		return toAlphaImage(redNew, greenNew, blueNew, alphaNew);
	}
}

