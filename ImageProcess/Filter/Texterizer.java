package ImageProcess.Filter;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.Rectangle2D;

public class Texterizer extends ImageFilter{
	public boolean conservative = false;
	public boolean sizeConservative = true;
	public String name = "Texterizer";
	
	public String[] textToUse;
	public BufferedImage background;
	public boolean noOverlap = true;
	public boolean edgesFromImage = true;
	public int numOps = 1000000;
	public int numOpsNoChange = 1000000;
	public int varianceThreshold = 100;
	
	public double averageR;
	public double averageG;
	public double averageB;
	public double varianceR;
	public double varianceG;
	public double varianceB;
	public int[] textUsages;
	public int textSelected;
	public int radius = 5;
	public int intensity = 1;
	
	public Texterizer(String[] text, BufferedImage background, boolean edges){
		textToUse = text;
		this.background = background;
		edgesFromImage = edges;
	}
	
	public String toString(){
		return "Texterizer" + ((edgesFromImage)?" Extracting Edges":"");
	}
	
	public BufferedImage filter(BufferedImage image){
		if(0 == 0){
			return randomClimbSearch(image);
		}
		
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		for(int i = 0; i < red.length; i++){
			for ( int j = 0; j < red[0].length; j++){
				red[i][j] = 0;
				green[i][j] = 0;
				blue[i][j] = 0;
			}
		}
		BufferedImage current = toColorImage(red, green, blue);
		ImageDiff differ = new ImageDiff(image);
		ImageMask mask = new ImageMask(new double[][]{{0,1,0},{1,1,1},{0,1,0}},true);
		BufferedImage average = mask.filter(image);
		for(int i = current.getWidth()*current.getHeight()/50; i > 0 ; i--){
			BufferedImage diff = differ.filter(current);
			int[] brightestPoint = brightest(diff);
			for(int x = brightestPoint[0]-2; x <= brightestPoint[0]+2; x++ ){
				for(int y = brightestPoint[1]-2; y <= brightestPoint[1] + 2; y++){
					current.setRGB(x, y, average.getRGB(brightestPoint[0],brightestPoint[1]));
				}
			}
		}
		return current;
	}

	public static int[] brightest(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int max = 0;
		int[] coord = {0, 0};
		for(int i = 0; i < red.length; i++){
			for ( int j = 0; j < red[0].length; j++){
				if(red[i][j] + green[i][j] + blue[i][j] > max){
					max = red[i][j] + green[i][j] + blue[i][j];
					coord[0] = i;
					coord[1] = j;
				}
			}
		}
		return coord;
	}

	public boolean statsImageSection(int [][] red, int [][] green, int[][] blue, int x, int y, int width, int height){
		double rTotal = 0;
		double gTotal = 0;
		double bTotal = 0;
		double rSq = 0;
		double gSq = 0;
		double bSq = 0;
		int count = 0;
		
		if(x >= 0 && y >= 0){
			for(int i = x; i < x + width && i < red.length; i++){
				for(int j = y; j < y + height && j < red[0].length; j++){
					if(noOverlap && (red[i][j] < 0 || green[i][j] < 0 || blue[i][j] < 0 )){
						return false;
					}
					rTotal += red[i][j];
					rSq += red[i][j] * red[i][j];
					gTotal += green[i][j];
					gSq += green[i][j] * green[i][j];
					bTotal += blue[i][j];
					bSq += blue[i][j] * blue[i][j];
					count++;
				}
			}
		} else {
			debugPrintln("Strange Error: (" + x + "," + y + ")");
		}
		
		//debugPrintln("C" + count + " x" + x + "y" + y + "w" + red[0].length + "h" + red.length);
		averageR = rTotal / count;
		varianceR = rSq / count - averageR * averageR;
		averageG = gTotal / count;
		varianceG = gSq / count - averageG * averageG;
		averageB = bTotal / count;
		varianceB = bSq / count - averageB * averageB;
		return true;
	}
	
	public BufferedImage randomSearch(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		
		int countNoChanges = 0;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		BufferedImage current = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = current.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,image.getWidth(), image.getHeight());
		Font font;
		FontMetrics fm;
		int fontSize = 72;
		font = new Font("Arial",Font.BOLD,fontSize);
		textUsages = new int[textToUse.length];
		
		while(countNoChanges < numOps){
			if(Math.random() < 0.4){
				fontSize = (int)(fontSize * (Math.random() + 0.35) + 3);
			}
			if(fontSize < 4){
				fontSize = 6;
			}
			font = new Font("Arial",Font.BOLD,fontSize);
			fm = g2d.getFontMetrics(font);
			String text = selectText(); //textToUse[(int)(Math.random() * textToUse.length)];
			
			//debugPrintln(text);
			
			int height = fm.getAscent() * 37/40;
			int width = fm.stringWidth(text);
			
			//Rectangle2D.Float rect2D = (Rectangle2D.Float)fm.getStringBounds(text,g2d);
			
			//height = (int)(rect2D.y - rect2D.height);
			//width = (int)rect2D.width;
			
			int x,y;
			
			//debugPrintln(width + "x" + height);
			
			if(width <= imageWidth && height < imageHeight){
				x = (int)((imageWidth - width) * Math.random());
				y = (int)((imageHeight - height) * Math.random());
			} else {
				continue;
			}
			if(statsImageSection(red,green,blue,x,y,width,height) || !noOverlap){
				
				if(varianceR + varianceG + varianceB < varianceThreshold * 3 && averageR + averageG + averageB < 750){
					for(int i = x; i < x + width && i < red.length; i++){
						for(int j = y; j < y + height && j < red[0].length; j++){
							red[i][j] = -1;
							green[i][j] = -1;
							blue[i][j] = -1;
						}
					}
					textUsages[textSelected]++;
					g2d.setFont(font);
					g2d.setColor(new Color((int)averageR,(int)averageG,(int)averageB));
					g2d.drawString(text,x,y+height);
					fontSize++;
					countNoChanges = 0;
				} else {
					fontSize--;
					countNoChanges++;
				}
			} else {
				fontSize--;
				countNoChanges++;
			}
		}
		
		return current;
		
	}
	
	public String selectText(){
		int max = textUsages[0];
		int sum = 0;
		for(int i = 0; i < textUsages.length; i++){
			if(textUsages[i] > max){
				max = textUsages[i];
			}
			sum += textUsages[i];
		}
		
		int select = (int)(sum * Math.random());
		int index = 0;
		int stop = 0;
		
		while(stop < textUsages.length * 10){ 
			select = select + textUsages[index] - 2 * sum / textUsages.length;
			if(select <= 0){
				textSelected = index;
				return textToUse[index];
			}
			index = (index + 1) % textUsages.length;
			stop++;
		}
		//debugPrintln("Trouble: " + select + " m: " + max + " 1: " + textUsages[0] + " 2: " + textUsages[1]);
		//textSelected = (int)(Math.random() * textToUse.length);
		return textToUse[textSelected];
	}
	
	public Boolean tryTextRC(int[][] red, int[][] green, int[][] blue, int x, int y, int fontSize, String text, BufferedImage current){
		Graphics2D g2d = current.createGraphics();
		Font font = new Font("Arial",Font.BOLD,fontSize);
		FontMetrics fm = g2d.getFontMetrics(font);
		
		int height = fm.getAscent() * 37/40;
		int width = fm.stringWidth(text);
		
		if(width + x <= red.length && height + y < red[0].length && width > 2 && height > 2){
				
		} else{
			return false;
		}
		
		
		if(statsImageSection(red,green,blue,x,y,width,height)){
			if(varianceR + varianceG + varianceB < varianceThreshold * 3){
				if(averageR + averageG + averageB < 750){
					if(tryTextRC(red,green,blue,x+(int)(2*Math.random()-1), y+(int)(2*Math.random()-1), fontSize+(int)(1.4*Math.random() + .6), text,current)){
						
					} else {
						//TODO: make this only do pixels that are colored by the text ? is this a good idea?
						textUsages[textSelected]+=fontSize;
						g2d.setFont(font);
						g2d.setColor(new Color((int)averageR,(int)averageG,(int)averageB));
						g2d.drawString(text,x,y+height);
						int white = 255<<16 + 255<<8 + 255;
						for(int i = (x<=0)?1:x ; i <= x + width && i < red.length-1; i++){
							for(int j = (y<=0)?1:y ; j <= y + height + fm.getDescent() && j < red[0].length-1; j++){
								if(((current.getRGB(i,j)>>16)&255) < 250 ||((current.getRGB(i,j)>>8)&255) < 250 ||((current.getRGB(i,j))&255) < 250 ){
									red[i][j] 	= -1;
									green[i][j] = -1;
									blue[i][j] 	= -1;
									
									red[i-1][j-1] = -1;
									green[i-1][j-1] = -1;
									blue[i-1][j-1] = -1;
									
									red[i][j-1] = -1;
									green[i][j-1] = -1;
									blue[i][j-1] = -1;
									
									red[i-1][j] = -1;
									green[i-1][j] = -1;
									blue[i-1][j] = -1;
									
									red[i+1][j+1] = -1;
									green[i+1][j+1] = -1;
									blue[i+1][j+1] = -1;
									
									red[i+1][j] = -1;
									green[i+1][j] = -1;
									blue[i+1][j] = -1;
									
									red[i][j+1] = -1;
									green[i][j+1] = -1;
									blue[i][j+1] = -1;
									
									red[i+1][j-1] = -1;
									green[i+1][j-1] = -1;
									blue[i+1][j-1] = -1;
									
									red[i-1][j+1] = -1;
									green[i-1][j+1] = -1;
									blue[i-1][j+1] = -1;
								}
							}
						}
					}
					return true;
				} else {
					for(int i = x ; i < x + width && i < red.length; i++){
						for(int j = y ; j < y + height && j < red[0].length; j++){
							red[i][j] 	= -1;
							green[i][j] = -1;
							blue[i][j] 	= -1;
						}
					}
					return false;
				}
			}
		}
		return false;
	}
	
	public BufferedImage randomClimbSearch(BufferedImage image){
		int [][] red = toRedMatrix(image);
		int [][] green = toGreenMatrix(image);
		int [][] blue = toBlueMatrix(image);
		int count = 0;
		int countNoChanges = 0;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		BufferedImage current;
		Graphics2D g2d;
		if(edgesFromImage){
			ImageFilter.resetImage();
			current = (new GaussianEdge(1,10)).filter(image); 
			ImageFilter.resetImage();
			g2d = current.createGraphics();
		} else {
			current = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			g2d = current.createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0,0,image.getWidth(), image.getHeight());
		}
		int fontSize = image.getHeight()/10;
		textUsages = new int[textToUse.length];
		
		while(countNoChanges < numOpsNoChange && count < numOps){
			if(Math.random() < 0.4){
				fontSize = (int)(fontSize * (Math.random() + 0.35) + 3);
			}
			if(fontSize < 4){
				fontSize = 6;
			}
			
			//fontSize = 6;
			String text = selectText();
			
			int x,y;
			
			x = (int)((imageWidth) * Math.random());
			y = (int)((imageHeight) * Math.random());
			
			if(tryTextRC(red,green,blue,x,y,fontSize,text,current)){
				countNoChanges = 0;
				fontSize++;
			} else {
				countNoChanges++;
			}
			count++;
		}
		return current;
	}
	
}

