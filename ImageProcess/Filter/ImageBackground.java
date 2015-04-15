package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class ImageBackground extends ImageFilter {
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Background Image";
	public BufferedImage backgroundImage;

	public ImageBackground(BufferedImage paramBufferedImage)
	{
		this.backgroundImage = paramBufferedImage;
	}
	
	public String toString(){
		return "Background Image";
	}

	public BufferedImage filter(BufferedImage image) {
		int[][] redNew = toRedMatrix(image);
		int[][] greenNew = toGreenMatrix(image);
		int[][] blueNew = toBlueMatrix(image);
		int[][] alphaNew = toAlphaMatrix(image);
		int[][] redThis = toRedMatrix(backgroundImage);
		int[][] greenThis = toGreenMatrix(backgroundImage);
		int[][] blueThis = toBlueMatrix(backgroundImage);

		for (int i = 0; (i < redNew.length) && (i < redThis.length); i++) {
			for (int j = 0; (j < redNew[0].length) && (j < redThis[0].length); j++) {
				double a = alphaNew[i][j]/255.0;
				redNew[i][j] = (int)(redNew[i][j] * a + (1-a)* redThis[i][j]);
				greenNew[i][j] = (int)(greenNew[i][j] * a + (1-a)* greenThis[i][j]);
				blueNew[i][j] = (int)(blueNew[i][j] * a + (1-a)* blueThis[i][j]);
			}
		}

		return toColorImage(redNew, greenNew, blueNew);
	}
}

