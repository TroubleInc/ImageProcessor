package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ComponentFilter extends ImageFilter{
	public int numComponents;
	public double averageSize;
	int [] colorsRed	= new int [] {0xff,0x00,0x00,0xff,0xff,0x00,0x80,0x00,0x00,0x8b,0x80,0x00,0xff,0xff};
	int [] colorsGreen	= new int [] {0x00,0xff,0x00,0xff,0x00,0xff,0x00,0x80,0x00,0x45,0x00,0x80,0x14,0x8c};
	int [] colorsBlue	= new int [] {0x00,0x00,0xff,0x00,0xff,0xff,0x00,0x00,0x80,0x13,0x80,0x80,0x93,0x00};
	
	public String toString(){
		return "Component Filter";
	}
	
	public BufferedImage filter(BufferedImage image){
		int [][] components = toGreyMatrix(image);
		int componentNum = 0;
		//label each "thing"
		for(int i = 0; i < components.length; i++){
			for(int j = 0; j < components[i].length; j++){
				if(components[i][j] > 0){
					boolean found = false;
					if(i > 0){
						if(components[i-1][j] > 0){
							components[i][j] = components[i-1][j];
							found = true;
						}
					}
					if(j > 0){
						if(components[i][j-1] > 0){
							components[i][j] = components[i][j-1];
							found = true;
						}
					}
					if(!found){
						componentNum++;
						components[i][j] = componentNum;
					}
				}
			}
		}
		//initialize the equivalence table
		int [] componentMatching = new int[componentNum + 1];
		componentMatching[0] = 0;
		for(int i = 1; i < componentMatching.length; i++){
			componentMatching[i] = componentNum + 100;
		}
		//make equivalencies
		for(int i = 0; i < components.length; i++){
			for(int j = 0; j < components[i].length; j++){
				if(components[i][j] > 0){
					if(i > 0 && components[i-1][j] > 0 && components[i][j] != components[i-1][j]){
						componentMatching[components[i-1][j]] = min(components[i][j],components[i-1][j],
							componentMatching[components[i][j]],componentMatching[components[i-1][j]]);
						componentMatching[components[i][j]] = min(components[i][j],components[i-1][j],
							componentMatching[components[i][j]],componentMatching[components[i-1][j]]);
						components[i][j] = min(components[i][j],components[i-1][j],
							componentMatching[components[i][j]],componentMatching[components[i-1][j]]);
					}
					if(j > 0 && components[i][j-1] > 0 && components[i][j] != components[i][j-1]){
						componentMatching[components[i][j-1]] = min(components[i][j],components[i][j-1],
							componentMatching[components[i][j]],componentMatching[components[i][j-1]]);
						componentMatching[components[i][j]] = min(components[i][j],components[i][j-1],
							componentMatching[components[i][j]],componentMatching[components[i][j-1]]);
						components[i][j] = min(components[i][j],components[i][j-1],
							componentMatching[components[i][j]],componentMatching[components[i][j-1]]);
					}
				}
			}
		}
		//clean equivalence table
		int errors = 0;
		for(int i = 0; i < componentMatching.length; i++){
			if(componentMatching[i] < componentMatching.length){
				if(componentMatching[componentMatching[i]] < componentMatching[i]){
					errors++;
					componentMatching[i] = componentMatching[componentMatching[i]];
					i--;
				}
			}
			//debugPrintln(i + " : " + componentMatching[i]);
		}
		//debugPrintln(errors + "/" + componentMatching.length);
		//count components and assign colors - completely unnecessary
		numComponents = 0;
		for(int i = 1; i < componentMatching.length; i++){
			if(componentMatching[i] == i || componentMatching[i] == componentNum + 100){
				componentMatching[i] = numComponents;
				numComponents++;
			} else {
				componentMatching[i] = componentMatching[componentMatching[i]];
			}
		}
		resetImage();
		int [][] red = toGreyMatrix(image);
		resetImage();
		int [][] green = toGreyMatrix(image);
		resetImage();
		int [][] blue = toGreyMatrix(image);
		averageSize = 0;
		for(int i = 0; i < components.length; i++){
			for(int j = 0; j < components[i].length; j++){
				if(components[i][j] > 0){
					averageSize += 1.0;
					if(componentMatching[components[i][j]] != componentNum){
						red[i][j] = colorsRed[componentMatching[components[i][j]]%colorsRed.length];
						green[i][j] = colorsGreen[componentMatching[components[i][j]]%colorsGreen.length];
						blue[i][j] = colorsBlue[componentMatching[components[i][j]]%colorsBlue.length];
					} else {
						red[i][j] = colorsRed[components[i][j]%colorsRed.length];
						green[i][j] = colorsGreen[components[i][j]%colorsGreen.length];
						blue[i][j] = colorsBlue[components[i][j]%colorsBlue.length];
					}
				}
			}
		}
		averageSize = averageSize / numComponents;
		debugPrintln("Number of components " + numComponents);
		debugPrintln("Average size " + averageSize);
		//debugPrintln(numComponents + " components of size " + averageSize);
		return toColorImage(red,green,blue);
	}
	
	public int min(int x1, int x2, int x3, int x4){
		return Math.min(x1,Math.min(x2,Math.min(x3,x4)));
	}
}

