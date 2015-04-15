/**
*	Function.java
*	stores and handles functions.  the substitutions are handled by the Data class, works correctly now, 
*	but needs was to do symbolic operations on functions such as differentiate 
*/

package Calc;

public class Function{
	private String sig;
	private String exp;
	private String[] vars;
	
	public Function(String s, String[] v, String e)
	{
		sig = s;
		vars = v;
		exp = e;
		
	}
	
	public String getSignature()
	{
		return sig;
	}
	
	public String[] getVars()
	{
		return vars;
	}
	
	public ReturnType evaluate(Data d)
	{
		return new Expression(exp,d).evaluate();
	}
	
	public String print()
	{
		String s = sig + "(";
		s = s + vars[0];
		
		for(int i = 1; i < vars.length; i++){
			s = s + vars[i] + ",";
		}
		
		return s + ")=" + exp;
	}
	
	public String signature()
	{
		return sig;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Function){
			return ((Function)o).getSignature().equals(sig) && vars.length == ((Function) o).getVars().length;
		}
		return false;
	}
	
	public String expression()
	{
		return exp;
	}
	
	public void setFunc(String[] v, String e)
	{
		vars = v;
		exp = e;
	}
}