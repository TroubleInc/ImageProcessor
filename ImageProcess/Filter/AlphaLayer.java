/*to fix*/

package ImageProcess.Filter;

import java.awt.image.BufferedImage;

public class AlphaLayer extends ImageFilter
{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "Image Diff";
	public BufferedImage alphaImage;

	public AlphaLayer(BufferedImage alphaImage){
		this.alphaImage = alphaImage;
	}
	
	public String toString(){
		return "Adding Alpha Layer";
	}

	public BufferedImage filter(BufferedImage image) {
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int [][] alpha = toAlphaMatrix(image);
		int [][] newAlpha = toGreyMatrix(alphaImage);
		
		for(int i = 0; i < red.length && i < newAlpha.length; i++){
			for(int j = 0; j < red[0].length && j < newAlpha[0].length; j++){
				alpha[i][j] = newAlpha[i][j];
			}
		}
		
		return toAlphaImage(red,green,blue,alpha);
	}
}

