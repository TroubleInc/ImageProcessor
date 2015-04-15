/**
*	Variable.java
*	Stores and handles Variables.
*/

package Calc;

import java.math.BigDecimal;

public class Variable{
	private String sig;
	private BigDecimal val;
	
	public Variable(String s,BigDecimal v)
	{
		sig = s;
		val = v;
	}
	
	public String print()
	{
		return sig + "=" + val;
	}
	
	public String signature()
	{
		return sig;
	}
	
	public BigDecimal value()
	{
		return val;
	}
	
	public void setValue(BigDecimal v)
	{
		val = v;
	}
	
		public boolean equals(Object o)
	{
		if(o instanceof Variable){
			return ((Variable) o).signature().equals(sig);
		}
		return false;
	}
}