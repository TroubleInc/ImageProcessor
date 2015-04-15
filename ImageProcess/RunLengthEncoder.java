/*to fix*/

package ImageProcess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JTextArea;
import ImageProcess.Filter.*;

public class RunLengthEncoder{
	private JTextArea debug;

	public RunLengthEncoder(JTextArea debugConsole){
		debug = debugConsole;
	}
	
	public void debugPrint(String s){
		debug.append(s);
	}
	
	public void debugPrintln(String s){
		debug.append(s);
		debug.append("\n");
	}

	public void exportImage(File file, BufferedImage image) {
		FileOutputStream outfile = null;
		try{
			outfile = new FileOutputStream(file);
		} catch (IOException fileOpenExcept){
			fileOpenExcept.printStackTrace();
			return;
		}
		
		int [][] red = ImageFilter.toRedMatrix(image);
		int [][] green = ImageFilter.toGreenMatrix(image);
		int [][] blue = ImageFilter.toBlueMatrix(image);
		int [][] alpha = ImageFilter.toAlphaMatrix(image);
		
		int height = red.length;
		int width = red[0].length;
		
		try{
			outfile.write((int)'r');
			outfile.write((int)'l');
			outfile.write((int)'e');
			
			outfile.write(height);
			outfile.write(height>>8);
			outfile.write(width);
			outfile.write(width>>8);
			
			int count = 0;
			int cCur = 0;
			
			//red:
			for(int i = 0; i < height; i++){
				count = 1;
				cCur = red[i][0];
				for(int j = 1; j < width; j++){
					if(cCur != red[i][j] || count == 255){
						debugPrintln("R:" + cCur + "x" + count);
						outfile.write(cCur);
						outfile.write(count);
						count = 1;
						cCur = red[i][j];
					} else {
						count++;
					}
				}
				debugPrintln("R:" + cCur + "x" + count);
				outfile.write(cCur);
				outfile.write(count);
			}
			
			//green:
			for(int i = 0; i < height; i++){
				count = 1;
				cCur = green[i][0];
				for(int j = 1; j < width; j++){
					if(cCur != green[i][j] || count == 255){
						debugPrintln("G:" + cCur + "x" + count);
						outfile.write(cCur);
						outfile.write(count);
						count = 1;
						cCur = green[i][j];
					} else {
						count++;
					}
				}
				debugPrintln("G:" + cCur + "x" + count);
				outfile.write(cCur);
				outfile.write(count);
			}
			
			//blue:
			for(int i = 0; i < height; i++){
				count = 1;
				cCur = blue[i][0];
				for(int j = 1; j < width; j++){
					if(cCur != blue[i][j] || count == 255){
						debugPrintln("B:" + cCur + "x" + count);
						outfile.write(cCur);
						outfile.write(count);
						count = 1;
						cCur = blue[i][j];
					} else {
						count++;
					}
				}
				debugPrintln("B:" + cCur + "x" + count);
				outfile.write(cCur);
				outfile.write(count);
			}
			
			//alpha:
			for(int i = 0; i < height; i++){
				count = 1;
				cCur = alpha[i][0];
				for(int j = 1; j < width; j++){
					if(cCur != alpha[i][j] || count == 255){
						outfile.write(cCur);
						outfile.write(count);
						count = 1;
						cCur = alpha[i][j];
					} else {
						count++;
					}
				}
				outfile.write(cCur);
				outfile.write(count);
			}
			outfile.flush();
			outfile.close();
		} catch (IOException fileWriteException){
			fileWriteException.printStackTrace();
		}
	}
	
	public BufferedImage importImage(File file) {
		FileInputStream infile = null;
		try{
			infile = new FileInputStream(file);
		} catch (IOException fileOpenExcept){
			fileOpenExcept.printStackTrace();
			return null;
		}
		
		try{
			
			int readr, readl, reade;
			readr = readFile(infile);
			readl = readFile(infile);
			reade = readFile(infile);
			
			if(!(readr == ((int)'r') || readl == ((int)'l') || reade == ((int)'e'))){
				System.out.println("File Read Failed");
				return null;
			}
			
			int height = readFile(infile);
			height += (readFile(infile)<<8);
			int width = readFile(infile);
			width += (readFile(infile)<<8);
			
			debugPrintln("Loading Image " + width + "x" + height);
			
			if(width > 5000){
				width = 5000;
			}
			if(height > 5000){
				height = 5000;
			}
			
			int [][] red = new int[height][width];
			int [][] green = new int[height][width];
			int [][] blue = new int[height][width];
			int [][] alpha = new int[height][width];
					
			
			int count = 0;
			int cCur = 0;
			
			//red:
			for(int i = 0; i < height; i++){
				cCur = readFile(infile);
				count = readFile(infile);
				for(int j = 0; j < width; j++){
					if(count <= 0){
						cCur = readFile(infile);
						count = readFile(infile);
					}
					red[i][j] = cCur;
					count--;
				}
			}
			
			//green:
			for(int i = 0; i < height; i++){
				cCur = readFile(infile);
				count = readFile(infile);
				for(int j = 0; j < width; j++){
					if(count <= 0){
						cCur = readFile(infile);
						count = readFile(infile);
					}
					green[i][j] = cCur;
					count--;
				}
			}
			
			//blue:
			for(int i = 0; i < height; i++){
				cCur = readFile(infile);
				count = readFile(infile);
				for(int j = 0; j < width; j++){
					if(count <= 0){
						cCur = readFile(infile);
						count = readFile(infile);
					}
					blue[i][j] = cCur;
					count--;
				}
			}
			
			//alpha:
			for(int i = 0; i < height; i++){
				cCur = readFile(infile);
				count = readFile(infile);
				for(int j = 0; j < width; j++){
					if(count <= 0){
						cCur = readFile(infile);
						count = readFile(infile);
					}
					alpha[i][j] = cCur;
					count--;
				}
			}
			
			infile.close();
			
			return ImageFilter.toAlphaImage(red,green,blue,alpha);
		} catch (IOException excep){
			debugPrintln("* * * * Unhandled Exception * * * *");
			debugPrintln(excep.toString());
			debugPrintln(excep.getStackTrace()[0].toString());
			debugPrintln(excep.getStackTrace()[1].toString());
			debugPrintln(excep.getStackTrace()[2].toString());
			debugPrintln("...");
			return null;
		}
	}
	
	public int readFile(FileInputStream file) throws IOException {
		if(file.available()>0){
			int fromFile = file.read();
			debugPrintln("Reading " + fromFile + " or '" + ((char)fromFile) + "'");
			return fromFile;
		}
		return 0;
	}
	
}

