/*
This contains the data structures that hold the functions and the variables, has the match() function, which is the method which will handle the matching of a variable or a function
to the expression currently being evaluated.
*/

package Calc;

import javax.swing.JTextArea;
import java.math.BigDecimal;

public class Data{
	private Variable[] vars;
	private Function[] func;
	private int numVar;
	private int numFunc;
	private Data subData;
	static JTextArea out = null;
	
	/**
	 *Default Constructor, creates first level Data, 
	 * called only when Calc is Started.
	 */
	public Data()
	{
		//variable and function lists
		vars = new Variable[100];
		func = new Function[100];
		numVar = 0;
		numFunc = 0;
		subData = null;
	}
	
	/**
	 * Creates lower level Data, used to handle functions.
	 */
	public Data(Data d)
	{
		//variable and function lists
		vars = new Variable[100];
		func = new Function[100];
		numVar = 0;
		numFunc = 0;
		subData = d;
		
	}
	
	/**
	 * Sets the TextArea to output print() funcs to.
	 */
	static public void setOut(JTextArea p)
	{
		out = p;
	}
	
	/**
	 * Matches given expression to templates for funcs and variables
	 */
	public ReturnType match(Expression e)
	{
		String s = e.string();
		if(s.equals("help()")){
			out.append("    Internal Functions:\n");
			out.append("    printVars() - prints the vars in the current context\n");
			out.append("    printFuncs() - prints the funcs in the current context\n");
			out.append("    print(expr) - prints the results of the expression\n");
			out.append("    helpLanguage() - prints help on the language\n");
			out.append("    listModes() - lists the possible modes\n");
			out.append("    load <file_name> - load a file, defining all the variables and functions\n");
			out.append("    mode() - prints the current mode of the calculator\n");
			return new ReturnType("",0.0,false,ReturnType.Type.text);
		}
		if(s.equals("helpLanguage()")){
			out.append("    Operators:\n");
			out.append("    + - addition\n");
			out.append("    - - subtract, negation\n");
			out.append("    * - multiplication\n");
			out.append("    / - division\n");
			out.append("    % - modulo/remainder (15 % 10 -> 5)\n");
			out.append("    ^ - Exponentiation <exponent must be a whole number>\n");
			out.append("    <,>,<=,>=,== - comparison, 1 if true, 0 if false\n");
			out.append("    () - parentheses, for order of operations\n");
			out.append("    := - define variable or function\n");
			out.append("    ; - line split, expression split\n");
			out.append("    define<signature>=definition - define variable or function\n");
			out.append("    if(<expression){<expression>} - if statement\n");
			out.append("    while(<expression>){<expression>) - while statement\n");
			return new ReturnType("",0.0,false,ReturnType.Type.text);
		}
		if(s.equals("listModes()")){
			out.append("    modes:\n");
			out.append("    setModeInt() - Use Integer resolution (32bit, whole numbers)\n");
			out.append("    setModeSmall() - Use float resolution (32bit, floating point)\n");
			out.append("    setModeMedium() - Use double resolution (64 bit, floating point)\n");
			out.append("    setModeLarge() - Use double double resolution (128 bit, floating point)\n");
			out.append("    setModeUnlimited() - Use Unlimited floating point resolution\n");
			out.append("    setModeMod(<mod_base>) - Use a Mod space, supply mod base as argument\n");
			return new ReturnType("",0.0,false,ReturnType.Type.text);
		}
		//needs to be changed to dynamic
		if(s.equals("printVars()")){
			out.append("    Variables are\n");
			for(int i = 0; i<numVar ; i++){
				out.append("    " + vars[i].print()+"\n");
			}
			
			return new ReturnType("",0.0,false,ReturnType.Type.text);
		}
		//needs to be changed to dynamic
		if(s.equals("printFuncs()")){
			for(int i = 0; i<numFunc ; i++){
				out.append("    " + func[i].print() + "\n");
			}
			
			return new ReturnType("",0.0,false,ReturnType.Type.text);
		}
		//setModeFunctions
		if(s.equals("setModeInt()")){
			Expression.getContext().changeMode(CalcContext.DecimalMode.Int);
			return new ReturnType("Changed Mode to Int",0.0,false,ReturnType.Type.text);
		}
		if(s.equals("setModeSmall()")){
			Expression.getContext().changeMode(CalcContext.DecimalMode.Small);
			return new ReturnType("Changed Mode to Small",0.0,false,ReturnType.Type.text);
		}
		if(s.equals("setModeMedium()")){
			Expression.getContext().changeMode(CalcContext.DecimalMode.Medium);
			return new ReturnType("Changed Mode to Medium",0.0,false,ReturnType.Type.text);
		}
		if(s.equals("setModeLarge()")){
			Expression.getContext().changeMode(CalcContext.DecimalMode.Large);
			return new ReturnType("Changed Mode to Large",0.0,false,ReturnType.Type.text);
		}
		if(s.equals("setModeUnlimited()")){
			Expression.getContext().changeMode(CalcContext.DecimalMode.Unlimited);
			return new ReturnType("Changed Mode to Unlimited",0.0,false,ReturnType.Type.text);
		} else {
			int pos = s.indexOf("(");
			if(pos != -1 && s.substring(0,pos).equals("setModeMod") && allInParen(s.substring(pos +1))){
				ReturnType r = new Expression( s.substring(pos +1,s.length()-1),this).evaluate();
				Expression.getContext().changeMode(CalcContext.DecimalMode.Mod,r.number());
				return new ReturnType("Changed Mode to Mod, base " + r.number().toString(),0.0,false,ReturnType.Type.text);
			}
		}
		if(s.equals("mode()")){
			return new ReturnType("Current mode is " + Expression.getContext().mode(), 0.0, false, ReturnType.Type.text);
		}

		if(s.substring(0,Math.min(4,s.length())).equals("load")){
				CalcFile file = new CalcFile(this);
				return file.loadFile(s.substring(4,s.length()));
		}
		
		if(s.substring(0,Math.min(6,s.length())).equals("define")){
			for(int i = 6; i<s.length(); i++){
				if(s.charAt(i) =='('){
					int parenBegin = i;
					for(int j = i; j<s.length(); j++){
						if(s.charAt(j) == ')' && s.charAt(j+1) == '='){
							Function funcx = posOfFunc(s.substring(6,parenBegin));
							if(funcx==null){
								//change to adding funciton to dynamic
								func[numFunc] = new Function(s.substring(6,parenBegin),s.substring(parenBegin+1,j).split(","),s.substring(j+2));
								numFunc++;
								//System.out.println("added function " + s.substring(6,parenBegin));
								return new ReturnType(s.substring(6),0.0,true);
							} else {
								funcx.setFunc(s.substring(parenBegin+1,j).split(","),s.substring(j+2));
								//System.out.println("set function " + s.substring(6,parenBegin));
								return new ReturnType(s.substring(6),0.0,true);
							}
						}
					}
					
					return new ReturnType("ERROR: Failed to create Variable or Function", 0.0, false,ReturnType.Type.error);
				}
				
				//implementation of variable reading
				if(s.charAt(i)== '='){
					Variable varx = posOfVar(s.substring(6,i));
					BigDecimal num = new Expression(s.substring(i+1),this).evaluate().number();
					if(varx ==  null){
						vars[numVar] = new Variable(s.substring(6,i),num);
						numVar++;
						//System.out.println("added variable " +s.substring(6,i) + " = " + num); 
						return new ReturnType(s.substring(6),vars[numVar-1].value(),true);
					} else {
						varx.setValue(num);
						//System.out.println("set variable " +s.substring(6,i) + " = " + num); 
						return new ReturnType(s.substring(6),vars[numVar-1].value(),true);
					}
				}
			}
			
			return new ReturnType("ERROR: Failed to create Variable or Function", 0.0, false);
		}
		
		Variable posv = posOfVar(s);
		if(posv != null){
			//System.out.println("Variable " + posv.signature() + " is " + posv.value());
			return new ReturnType(posv.signature(),posv.value(),true);
		}
		
		if(s.contains("(")){
			int pos = s.indexOf("(");
			Function funcx = posOfFunc(s.substring(0,pos));
			if(funcx != null && allInParen(s.substring(pos +1))){
				if(s.charAt(s.length()-1) == ')'){s = s.substring( 0, s.length()-1);}
				String [] exp = splitOnParen(',',s.substring(pos+1));
				Data dat = new Data(this);
				if(funcx.getVars().length == exp.length){
					for(int i = 0; i < funcx.getVars().length; i++){
						new Expression("define" + funcx.getVars()[i] + "=" + exp[i],dat).evaluate();
					}
					return funcx.evaluate(dat);
				}
			}
			
			if(s.substring(0,pos).equals("print") && allInParen(s.substring(pos +1))){
				if(s.charAt(s.length()-1) == ')'){s = s.substring( 0, s.length()-1);}
				ReturnType r = new Expression( s.substring(pos +1),this).evaluate();
				out.append("\t"+r.print()+"\n");
				return r;
			}
		}
		
		if(subData!=null){
			//System.out.println("calling subdata on " +e);
			return subData.match(e);
		}
		//System.out.println("no match on " +e );
		return new ReturnType("ERROR: No Such Variable or Expression", 0.0, false,ReturnType.Type.error);
	}
	
	/**
	 *	Add Data from outside sources
	 */
	public boolean addData(String signature, String definition)
	{
		for(int i = 1; i<signature.length(); i++){
			if(signature.charAt(i) =='('){
				int parenBegin = i;
				for(int j = i; j<signature.length(); j++){
					if(signature.charAt(j) == ')'){
						Function funcx = posOfFunc(signature.substring(0,parenBegin));
						if(funcx==null){
							//change to adding function to dynamic
							func[numFunc] = new Function(signature.substring(0,parenBegin),signature.substring(parenBegin+1,j).split(","),definition);
							numFunc++;
							//System.out.println("Added function " + signature);
							return true;
						} else {
							funcx.setFunc(signature.substring(parenBegin+1,j).split(","),definition);
							numFunc++;
							//System.out.println("Set function " + signature);
							return true;
						}
					}
				}
				return false;
			}
		}
		Variable varx = posOfVar(signature);
		BigDecimal num = new Expression(definition,this).evaluate().number();
		if(varx ==  null){
			vars[numVar] = new Variable(signature,num);
			numVar++;
			//System.out.println("added variable " +signature + " = " + num); 
			return true;
		} else {
			varx.setValue(num);
			//System.out.println("set variable " + signature + " = " + num); 
			return true;
		}
		//unreachable
		//return false;
	}
	
	public boolean compileMatch(Expression e){
		return false;
	}
	
	/**
	 *	Returns the function list.
	 */
	public Function[] funcList()
	{
		return func;
	}
	
	/**
	 *Returns the Variable list.
	 */
	public Variable[] varList()
	{
		return vars;
	}
	
	/**
	 * Returns The number of functions in this Data.
	 */
	public int numFuncs()
	{
		return numFunc;
	}
	
	/**
	 * Returns the number of Variables in this Data.
	 */
	public int numVars()
	{
		return numVar;
	}
	
	/**
	 * Returns a pointer to a variable that matches the given signature. 
	 */
	private Variable posOfVar(String sig)
	{
		for(int i =0; i<numVar; i++){
			if(vars[i].signature().equals(sig)){
				return vars[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns a pointer to a function that matches the given signature. 
	 */
	private Function posOfFunc(String sig)
	{
		for(int i =0; i<numFunc; i++){
			if(func[i].signature().equals(sig)){
				return func[i];
			}
		}
		return null;
	}
	
	/**
	 * Same as in Expression. 
	 */
	private int posOnFirstLevel(char a, String s)
	{
		int parenLevel = 0;
		for(int i = 0; i < s.length(); i++){
			
			if(s.charAt(i) == a && parenLevel == 0){
				return i;
			}
			
			if(s.charAt(i) == '('){
				parenLevel++;
			}
			
			if(s.charAt(i) == ')'){
				parenLevel--;
			}
		}
		return -1;
	}
	
	/**
	 * Same as in Expression. 
	 */
	private boolean allInParen(String s)
	{
		int parenLevel = 1;
		for(int i = 1; i < s.length(); i++){
			
			if(parenLevel == 0){
				return false;
			}
			
			if(s.charAt(i) == '('){
				parenLevel++;
			}
			
			if(s.charAt(i) == ')'){
				parenLevel--;
			}
		}
		return true;
	}
	
	/**
	 * Splits on first level. 
	 */
	private String[] splitOnParen(char a, String s)
	{
		String [] ret = new String [1];
		ret[0] = s;
		int index = 0;
		int pos = posOnFirstLevel(a,s);
		while(pos != -1){
			String[] temp = new String [index+2];
			for(int i = 0; i<index; i++){
				temp[i] = ret[i];
			}
			temp[index] = ret[index].substring(0,pos);
			temp[index+1] = ret[index].substring(pos+1,ret[index].length());
			ret = temp;
			index++;
			pos = posOnFirstLevel(a,ret[index]);
		}
		return ret;
	}
	
}
