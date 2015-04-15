/**
*	this is the ReturnType it handles the returned values from expressions.
*/

package Calc;

import java.math.BigDecimal;

public class ReturnType{
	private String string;
	private BigDecimal number;
	private boolean bool;
	public enum Type {text,value,error}
	private Type kind;
	
	public ReturnType(String s, double n, boolean b)
	{
		string = s;
		number = new BigDecimal(n);
		bool = b;
		if(b==false){
			kind = Type.error;
		} else {
			kind = Type.value;
		}
	}
	
	public ReturnType(String s, double n, boolean b,Type t)
	{
		string = s;
		number = new BigDecimal(n);
		bool = b;
		kind = t;
	}
	
	public ReturnType(String s, BigDecimal n, boolean b)
	{
		string = s;
		number = n;
		bool = b;
		if(b==false){
			kind = Type.error;
		} else {
			kind = Type.value;
		}
	}
	
	public ReturnType(String s, BigDecimal n, boolean b, Type t)
	{
		string = s;
		number = n;
		bool = b;
		kind = t;
	}
	
	public String string()
	{
		return string;
	}
	
	public BigDecimal number()
	{
		return number;
	}
	
	public boolean bool()
	{
		return bool;
	}
	
	public int type()
	{
		if(kind==Type.value){	return 1;	}
		if(kind==Type.text){	return 2;	}
		if(kind==Type.error){	return 3;	}
		return 0;
	}
	
	public Type realType()
	{
		return kind;
	}
	
	public String print()
	{
		if(kind.equals(Type.value)){
			return Expression.getContext().cast(number).toString();
		} else {
			return string;
		}
	}
}
