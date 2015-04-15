package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class BlueFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Blue Channel";
	}
	
	public BufferedImage filter(BufferedImage image){
		return toGreyImage(toBlueMatrix(image));
	}
}

