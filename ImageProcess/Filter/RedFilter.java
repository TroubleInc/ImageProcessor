package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class RedFilter extends ImageFilter{
	public boolean conservative = true;
	public boolean sizeConservative = true;
	public String name = "default";
	
	public String toString(){
		return "Red Channel";
	}
	
	public BufferedImage filter(BufferedImage image){
		return toGreyImage(toRedMatrix(image));
	}
}

