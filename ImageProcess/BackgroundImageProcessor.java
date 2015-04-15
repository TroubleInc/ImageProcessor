package ImageProcess;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import ImageProcess.Filter.*;

public class BackgroundImageProcessor extends SwingWorker<ImageHolder,Object>{
	ImageFilter iF;
	ImageHolder iH;
	boolean toColors;
	public Processor processor;
	public JTextArea debug;
	
	public BackgroundImageProcessor(ImageFilter f,ImageHolder h, boolean colors, Processor proc, JTextArea text){
		iF = f;
		iH = h;
		toColors = colors;
		processor = proc;
		debug = text;
	}
	
	public ImageHolder doInBackground(){
		try{
			ImageHolder iH2 = iH.clone();
			if(!toColors){
				iH2.applyFilter(iF);
			} else {
				iH2.applyFilterToColors(iF);
			}
			return iH2;
		} catch (Exception excep){
			debug.append("* * * * Unhandled Exception * * * *\n");
			debug.append(excep.toString() + "\n");
			debug.append(excep.getStackTrace()[0].toString() +"\n");
			debug.append(excep.getStackTrace()[1].toString() +"\n");
			debug.append(excep.getStackTrace()[2].toString() +"\n");
			debug.append("...\n");
			debug.append("Failed " + iF.toString() + "\n");
			return iH;
		}
	}
	
	public void done(){
		try{
			processor.setImageHolder(get());
			debug.append("Completed " + iF.toString()+"\n");
			processor.workingText.setVisible(false);
			processor.currentTool.enable();
		} catch (InterruptedException iE){} catch (ExecutionException eE){}
	}
}

