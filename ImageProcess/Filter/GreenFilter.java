package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class GreenFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Green Channel";
	}
	
	public BufferedImage filter(BufferedImage image){
		return toGreyImage(toGreenMatrix(image));
	}
}

