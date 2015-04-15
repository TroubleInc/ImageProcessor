package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class GreyScale extends ImageFilter{
	public String name = "GreyScale";
	
	public String toString(){
		return "Grey Scale";
	}
	
	public BufferedImage filter(BufferedImage image){
		return toGreyImage(toGreyMatrix(image));
	}
}

