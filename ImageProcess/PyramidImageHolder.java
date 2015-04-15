package ImageProcess;

import java.awt.image.BufferedImage;
import ImageProcess.Filter.*;

public class PyramidImageHolder extends ImageHolder{
	private BufferedImage[] image;
	private int levelToDisplay;
	private int levels;
	
	public PyramidImageHolder(BufferedImage image, int levels){
		super(image);
		this.levels = levels;
		setImage(image);
	}
	
	public PyramidImageHolder clone(){
		return new PyramidImageHolder(image[0],levels);
	}
	
	public BufferedImage getImage(){
		return image[levelToDisplay];
	}
	
	public void setImage(BufferedImage image){
		this.image = new BufferedImage[levels];
		ImageFilter shrink = new MeanShrink();
		this.image[0] = image;
		for(int i = 1; i < levels; i++){
			this.image[i] = shrink.filter(this.image[i-1]);
		}
	}
	
	public void setState(int state){
		if(state >= levels){
			state = levels - 1;
		} else if(state < 0){
			state = 0;
		}
		levelToDisplay = state;
	}
	
	public void applyFilter(ImageFilter f){
		for(int i = 0; i < levels; i++){
			this.image[i] = f.filter(this.image[i]);
		}
	}
	
	public void applyFilterToColors(ImageFilter f){
		for(int i = 0; i < levels; i++){
			BufferedImage red = ImageFilter.toGreyImage(ImageFilter.toRedMatrix(image[i]));
			BufferedImage green = ImageFilter.toGreyImage(ImageFilter.toGreenMatrix(image[i]));
			BufferedImage blue = ImageFilter.toGreyImage(ImageFilter.toBlueMatrix(image[i]));
			
			red = f.filter(red);
			green = f.filter(green);
			blue = f.filter(blue);
			
			image[i] = ImageFilter.toColorImage(ImageFilter.toRedMatrix(red),ImageFilter.toGreenMatrix(green),ImageFilter.toBlueMatrix(blue));
		}
	}

	public void applyFilterToColors(ImageFilter r, ImageFilter g, ImageFilter b){
		for(int i = 0; i < levels; i++){
			BufferedImage red = ImageFilter.toGreyImage(ImageFilter.toRedMatrix(image[i]));
			BufferedImage green = ImageFilter.toGreyImage(ImageFilter.toGreenMatrix(image[i]));
			BufferedImage blue = ImageFilter.toGreyImage(ImageFilter.toBlueMatrix(image[i]));
			
			red = r.filter(red);
			green = g.filter(green);
			blue = b.filter(blue);
		
			image[i] = ImageFilter.toColorImage(ImageFilter.toRedMatrix(red),ImageFilter.toGreenMatrix(green),ImageFilter.toBlueMatrix(blue));
		}
	}
}
