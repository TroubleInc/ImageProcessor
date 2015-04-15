package Calc;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.StringBuilder;

public class CalcFile
{
	Data dat;
	public CalcFile(Data d){
		dat = d;
	}
	
	public ReturnType loadFile(String fileName){
	StringBuilder file = new StringBuilder();
	BufferedReader fileIn = null;
	ReturnType r = new ReturnType("Unidentified Problem Loading File",0.0,false,ReturnType.Type.text);
		try {
			fileIn  = new BufferedReader( new FileReader(fileName) );
			
			String oneLine;
			
			while( (oneLine = fileIn.readLine() ) != null ) {
				file.append(oneLine);
			}
			
			ReturnType Temp = new Expression(file.toString(),dat).evaluate();
			
			if(Temp.type()<3){
				r = new ReturnType("File Loaded Successfully",0.0,true,ReturnType.Type.text);
			}
		}
		catch (Exception e) {
			
			r = new ReturnType("File IO Error, Please check that you have the\n\tcorrect file and the permissions to access it.",0.0,true,ReturnType.Type.text);
		}
		finally {
			try {
				//if( fileOut != null ) {
				//	fileOut.close();
				//}
				if( fileIn != null ) {
					fileIn.close();
				}
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return r;

	}
}