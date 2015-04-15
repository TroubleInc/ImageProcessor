package ImageProcess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import ImageProcess.Filter.*;

public class ImageHolder{
	private BufferedImage image;
	
	public ImageHolder(BufferedImage image){
		this.image = image;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public void setImage(BufferedImage image){
		this.image = image;
	}
	
	public void setState(int state){
		return;
	}
	
	public void applyFilter(ImageFilter f){
		image = f.filter(image);
	}
	
	public ImageHolder clone(){
		return new ImageHolder(image);
	}
	
	public void applyFilterToColors(ImageFilter f){
		BufferedImage red = ImageFilter.toGreyImage(ImageFilter.toRedMatrix(image));
		BufferedImage green = ImageFilter.toGreyImage(ImageFilter.toGreenMatrix(image));
		BufferedImage blue = ImageFilter.toGreyImage(ImageFilter.toBlueMatrix(image));
		
		red = f.filter(red);
		green = f.filter(green);
		blue = f.filter(blue);
		
		setImage(ImageFilter.toColorImage(ImageFilter.toRedMatrix(red),ImageFilter.toGreenMatrix(green),ImageFilter.toBlueMatrix(blue)));
	}
	
	public void applyFilterToAlpha(ImageFilter f){
		BufferedImage red = ImageFilter.toGreyImage(ImageFilter.toRedMatrix(image));
		BufferedImage green = ImageFilter.toGreyImage(ImageFilter.toGreenMatrix(image));
		BufferedImage blue = ImageFilter.toGreyImage(ImageFilter.toBlueMatrix(image));
		BufferedImage alpha = ImageFilter.toGreyImage(ImageFilter.toAlphaMatrix(image));
		
		red = f.filter(red);
		green = f.filter(green);
		blue = f.filter(blue);
		alpha = f.filter(alpha);
		
		setImage(ImageFilter.toAlphaImage(ImageFilter.toRedMatrix(red),ImageFilter.toGreenMatrix(green),ImageFilter.toBlueMatrix(blue),ImageFilter.toGreyMatrix(alpha)));
	}

	public void applyFilterToColors(ImageFilter r, ImageFilter g, ImageFilter b){
		BufferedImage red = ImageFilter.toGreyImage(ImageFilter.toRedMatrix(image));
		BufferedImage green = ImageFilter.toGreyImage(ImageFilter.toGreenMatrix(image));
		BufferedImage blue = ImageFilter.toGreyImage(ImageFilter.toBlueMatrix(image));
		
		red = r.filter(red);
		green = g.filter(green);
		blue = b.filter(blue);
		
		setImage(ImageFilter.toColorImage(ImageFilter.toRedMatrix(red),ImageFilter.toGreenMatrix(green),ImageFilter.toBlueMatrix(blue)));
	}
}
