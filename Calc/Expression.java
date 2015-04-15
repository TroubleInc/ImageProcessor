package Calc;

import java.math.BigDecimal;

public class Expression{
	//compile type variables
	public static final int ERROR 				= 1;
	public static final int LINE_BREAK			= 2;
	public static final int IF_STATEMENT		= 3;
	public static final int WHILE_STATEMENT		= 4;
	public static final int CONSTANT			= 5;
	public static final int DATA_ACCESS			= 6;
	public static final int PASSTHROUGH			= 7;
	public static final int DATA_WRITE			= 9;
	public static final int GREATER_THAN_EQUAL	= 10;
	public static final int LESS_THAN_EQUAL		= 11;
	public static final int GREATER_THAN		= 12;
	public static final int LESS_THAN			= 13;
	public static final int EQUAL_TEST			= 14;
	public static final int NOT_EQUAL			= 15;
	public static final int ADDITION			= 16;
	public static final int SUBTRACTION			= 17;
	public static final int MULTIPLICATION		= 18;
	public static final int DIVISION			= 19;
	public static final int NEGATIVE			= 20;
	public static final int MODULUS				= 21;
	public static final int POWERS				= 22;
	
	//string holds the expression
	private String expr;
	private Data data;
	static private CalcContext calc;
	private boolean compiled;
	private int expressionType;
	private Expression compSubExp1;
	private Expression compSubExp2;
	private Expression compSubExp3;
	private Expression compSubExp4;
	private ReturnType result;
	
	public Expression(String s,Data d)	{
		expr = s;
		data = d;
		
		compiled = false;
		
		//remove all the spaces
		expr = expr.replaceAll(" ","");
		expr = expr.replaceAll("\n","");
		expr = expr.replaceAll("\t","");
		
		//error checking, sees how the functions are handled.
		//System.err.println("\""+expr+"\"");
		
		//this code is now unnecessary.
		/*//close all the parentheses
		int i = 0;
		for(int j = 0; j < expr.length(); j++){
			if(expr.charAt(j) == ')'){
				i--;
			}
			
			if(expr.charAt(j) == '('){
				i++;
			}
		}
		for(int j = i; j>0; j--){
			expr = expr + ")";
		}*/
	}
	
	/**
	 * This method sets the context for all calculations in this 
	 * class, makes it possible to have infinite precision.
	 */
	static public void setContext(CalcContext c){
		calc = c;
	}
	
	static public CalcContext getContext(){
		return calc;
	}
	
	/**
	 * This is the method that takes an expression and actually 
	 * produces the answer. returns a ReturnType, which can hold 
	 * a string and a number, as well as a 'Type' which tells 
	 * whether it is a string, a number or an error.
	 */
	public ReturnType evaluate(){
		int pos = -1;
		
		if(compiled){
			return evaluateCompiled();
		}
		
		if(expr.length() == 0){
			return new ReturnType("ERROR: Bad Input",0.0,false);
		}

		//handle line breaks
		pos = posOfFirstLevel(";",'{','}');
		if (pos !=-1 && pos != expr.length()-1){
			new Expression(expr.substring(0,pos),data).evaluate();
			return new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
		} else if(pos == expr.length()-1) {
			return new Expression(expr.substring(0,pos),data).evaluate();
			
		}
		
		//handle brackets/if for while statements (for is a stub)
		pos = posOfFirstLevel("if",'{','}');
		if (pos != -1){
			if(pos>0){
				new Expression(expr.substring(0,pos),data).evaluate();
			}
			ReturnType r1 = new ReturnType("Null Result",0.0,false,ReturnType.Type.text);
			int pos2 = posOnLevel(expr.substring(pos+2,expr.length()),"{","{(",")}",0);
			int pos3 = posOnLevel(expr.substring(pos2+pos+2,expr.length()),"}","{(",")}",0);
			if(!calc.isZero(new Expression(expr.substring(pos+2,pos2+pos+2),data).evaluate().number())){
				r1 = new Expression(expr.substring(pos2+pos+2+1,pos2+pos+2+pos3),data).evaluate();
			}
			if(pos3+pos2+pos+2+1 < expr.length()){
				return new Expression(expr.substring(pos3+pos2+pos+2+1,expr.length()),data).evaluate();
			} else {
				return r1;
			}
		}
		pos = posOfFirstLevel("for",'{','}');
		if (pos !=-1){
			return new ReturnType("Error: 'For' Unimplemented, use 'While'",0.0,false);
		}
		pos = posOfFirstLevel("while",'{','}');
		if (pos > -1){
			if(pos>0){
				new Expression(expr.substring(0,pos),data).evaluate();
			}
			ReturnType r1 = new ReturnType("Null Result",0.0,false,ReturnType.Type.text);
			int pos2 = posOnLevel(expr.substring(pos+5,expr.length()),"{","{(",")}",0);
			int pos3 = posOnLevel(expr.substring(pos2+pos+5,expr.length()),"}","{(",")}",0);
			while(!calc.isZero(new Expression(expr.substring(pos+5,pos2+pos+5),data).evaluate().number())){
				r1 = new Expression(expr.substring(pos2+pos+5+1,pos2+pos+5+pos3),data).evaluate();
			}
			if(pos3+pos2+pos+2+1 < expr.length()){
				return new Expression(expr.substring(pos3+pos2+pos+5+1,expr.length()),data).evaluate();
			} else {
				return r1;
			}
		}
		
		//first base case, the empty string
		if(expr.equals("")){
			return new ReturnType("null",0,true);
		}
		
		//second base case, a number
		try{
			BigDecimal d = calc.parseNumber(expr);
			return new ReturnType(d.toString(),d,true);
		}
		catch(NumberFormatException e){}
		catch(Exception e){
			System.err.println(e);
		}
		
		ReturnType r = data.match(this);
		if(r.type() == 1 || r.type() == 2){
			return r;
		}
		
		//handle surrounding parentheses
		if(expr.charAt(0) == '('){
			if(allInParen()){
				if(expr.charAt(expr.length()-1) == ')'){
					r = new Expression(expr.substring(1,expr.length() - 1),data).evaluate();
					return new ReturnType("(" + r.string() + ")", r.number(), r.bool(), r.realType());
				}
				r = new Expression(expr.substring(1,expr.length()),data).evaluate();
				return new ReturnType("(" + r.string() + ")", r.number(), r.bool(), r.realType());
			}
		}
		
		//handle surrounding brackets
		if(expr.charAt(0) == '{'){
			if(allInBracket()){
				if(expr.charAt(expr.length()-1) == '}'){
					r = new Expression(expr.substring(1,expr.length() - 1),data).evaluate();
					return new ReturnType(r.string(), r.number(), r.bool(), r.realType());
				}
				r = new Expression(expr.substring(1,expr.length()),data).evaluate();
				return new ReturnType(r.string(), r.number(), r.bool(), r.realType());
			}
		}

		//handles definition
		if(!(posOfFirstLevel(":=","{(","})") ==-1)){
			pos = posOfFirstLevel(":=","{(","})");
			if(data.addData(expr.substring(0,pos),expr.substring(pos+2,expr.length()))){
				if(expr.charAt(pos-1) == ')'){
					return new ReturnType("Function " + expr.substring(0,pos) + " Added",0.0,false,ReturnType.Type.text);
				} else {
					return new ReturnType("Variable " + expr.substring(0,pos) + " Added",0.0,false,ReturnType.Type.text);
				}
			}else{
				return new ReturnType("ERROR: Data Adding Failure",0.0,false);
			}
		}
		
		//handles comparison >=
		if(!(posOfFirstLevel(">=",'(',')') ==-1)){
			pos = posOfFirstLevel(">=",'(',')');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+2,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				double ret;
				if(calc.greaterThan(r2.number(),r1.number()) == 0.0){
					ret = 1.0;
				} else {
					ret = 0.0;
				}
				return new ReturnType(r1.string() + ">=" + r2.string(),ret,true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}

		//handles comparison =
		if(!(posOfFirstLevel("<=",'(',')') ==-1)){
			pos = posOfFirstLevel("<=",'(',')');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+2,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				double ret;
				if(calc.greaterThan(r1.number(),r2.number()) == 0.0){
					ret = 1.0;
				} else {
					ret = 0.0;
				}
				return new ReturnType(r1.string() + "<=" + r2.string(),ret,true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}

		//handles comparison >
		if(!(posOfFirstLevel(">",'(',')') ==-1)){
			pos = posOfFirstLevel(">",'(',')');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				double ret;
				if(calc.greaterThan(r1.number(),r2.number()) == 1.0){
					ret = 1.0;
				} else {
					ret = 0.0;
				}
				return new ReturnType(r1.string() + ">" + r2.string(),ret,true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}

		//handles comparison <
		if(!(posOfFirstLevel("<",'(',')') ==-1)){
			pos = posOfFirstLevel("<",'(',')');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				double ret;
				if(calc.greaterThan(r2.number(),r1.number()) == 1.0){
					ret = 1.0;
				} else {
					ret = 0.0;
				}
				return new ReturnType(r1.string() + "<" + r2.string(),ret,true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}

		//handles comparison ==
		if(!(posOfFirstLevel("==",'(',')') ==-1)){
			pos = posOfFirstLevel("==",'(',')');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+2,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				double ret;
				if(calc.isZero(calc.subtract(r1.number(),r2.number()))){
					ret = 1.0;
				} else {
					ret = 0.0;
				}
				return new ReturnType(r1.string() + "<" + r2.string(),ret,true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}

		//handles addition
		if(containsOnFirstLevel('+')){
			pos = posOfFirstLevel('+');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				return new ReturnType(r1.string() + "+" + r2.string(),calc.add(r1.number(),r2.number()),true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}
		
		//handles negatives
		if(containsOnFirstLevel('-')){
			pos = posOfFirstLevel('-');
			if(pos == 0){
				ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
				return new ReturnType("-" + r2.string(),calc.subtract(new BigDecimal("0"),r2.number()),true);
			}
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null")){
					if(r2.string().equals("null")){
						return new ReturnType("ERROR: Bad Input",0.0,false);
					} else {
						//handles negative numbers
						return new ReturnType("-" + r2.string(),calc.subtract(new BigDecimal("0"),r2.number()),true);
					}
				}
				return new ReturnType(r1.string() + "-" + r2.string(),calc.subtract(r1.number(),r2.number()),true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}
		
		//handles multiplication
		if(containsOnFirstLevel('*')){
			pos = posOfFirstLevel('*');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				return new ReturnType(r1.string() + "*" + r2.string(),calc.multiply(r1.number(),r2.number()),true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}
		
		//handles division
		if(containsOnFirstLevel('/')){
			pos = posOfFirstLevel('/');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				return new ReturnType(r1.string() + "/(" + r2.string() + ")",calc.divide(r1.number(),r2.number()),true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}
		
		//handles mod
		if(containsOnFirstLevel('%')){
			pos = posOfFirstLevel('%');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				//if(r1.string().equals("null") || r2.string().equals("null") || calc.greaterThan(r1.number(),new BigDecimal(0.0)) == 1.0 ){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				return new ReturnType(r1.string() + "%" + r2.string(),calc.mod(r1.number(),r2.number()),true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}
		
		//handles powers
		if(containsOnFirstLevel('^')){
			pos = posOfFirstLevel('^');
			ReturnType r1 = new Expression(expr.substring(0,pos),data).evaluate();
			ReturnType r2 = new Expression(expr.substring(pos+1,expr.length()),data).evaluate();
			if(r1.bool() && r2.bool()){
				if(r1.string().equals("null") || r2.string().equals("null")){
					return new ReturnType("ERROR: Bad Input",0.0,false);
				}
				return new ReturnType("(" + r1.string() + ")^(" + r2.string() + ")",calc.power(r1.number() , r2.number()),true);
			} else {
				if(!r1.bool())	{return new ReturnType(r1.string(),0.0,false);}
				if(!r2.bool())	{return new ReturnType(r2.string(),0.0,false);}
			}
		}
		
		if(implicitMultiplicationFixer()){
			return this.evaluate();
		}
		
		return new ReturnType("ERROR: Bad Input",0.0,false);
	}
	
	/**
	 * Compiles to an internal representation
	 */
	public Expression compile(){
		int pos = -1;
		
		if(expr.length() == 0){
			result = new ReturnType("ERROR: Bad Input",0.0,false);
			compiled = true;
			expressionType = CONSTANT;
			return this;
		}

		//handle line breaks
		pos = posOfFirstLevel(";",'{','}');
		if (pos !=-1 && pos != expr.length()-1){
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = LINE_BREAK;
			return this;
		} else if(pos == expr.length()-1) {
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compiled = true;
			expressionType = PASSTHROUGH;
			return this;
		}
		
		//handle brackets/if for while statements (for is a stub)
		pos = posOfFirstLevel("if",'{','}');
		if (pos != -1){
			if(pos>0){
				compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			}
			ReturnType r1 = new ReturnType("Null Result",0.0,false,ReturnType.Type.text);
			int pos2 = posOnLevel(expr.substring(pos+2,expr.length()),"{","{(",")}",0);
			int pos3 = posOnLevel(expr.substring(pos2+pos+2,expr.length()),"}","{(",")}",0);
			
			compSubExp2 = new Expression(expr.substring(pos+2,pos2+pos+2),data).compile();
			compSubExp3 = new Expression(expr.substring(pos2+pos+2+1,pos2+pos+2+pos3),data).compile();
			if(pos3+pos2+pos+2+1 < expr.length()){
				compSubExp4 = new Expression(expr.substring(pos3+pos2+pos+2+1,expr.length()),data).compile();
			}
			compiled = true;
			expressionType = IF_STATEMENT;
			return this;
		}
		pos = posOfFirstLevel("for",'{','}');
		if (pos !=-1){
			compiled = true;
			expressionType = CONSTANT;
			result = new ReturnType("Error: 'For' Unimplemented, use 'While'",0.0,false);
			return this;
		}
		pos = posOfFirstLevel("while",'{','}');
		if (pos > -1){
			if(pos>0){
				compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			}
			int pos2 = posOnLevel(expr.substring(pos+5,expr.length()),"{","{(",")}",0);
			int pos3 = posOnLevel(expr.substring(pos2+pos+5,expr.length()),"}","{(",")}",0);
			compSubExp2 = new Expression(expr.substring(pos+5,pos2+pos+5),data).compile();
			compSubExp3 = new Expression(expr.substring(pos2+pos+5+1,pos2+pos+5+pos3),data).compile();
			if(pos3+pos2+pos+2+1 < expr.length()){
				compSubExp4 = new Expression(expr.substring(pos3+pos2+pos+5+1,expr.length()),data).compile();
			}
			compiled = true;
			expressionType = WHILE_STATEMENT;
			return this;
		}
		
		//first base case, the empty string
		if(expr.equals("")){
			result = new ReturnType("null",0,true);
			compiled = true;
			expressionType = CONSTANT;
			return this;
		}
		
		//second base case, a number
		try{
			BigDecimal d = calc.parseNumber(expr);
			result = new ReturnType(d.toString(),d,true);
			compiled = true;
			expressionType = CONSTANT;
			return this;
		}
		catch(NumberFormatException e){}
		catch(Exception e){
			System.err.println(e);
		}
		
		//fix this so it doesn't do matching
		boolean r = data.compileMatch(this);
		if(r){
			compiled = true;
			expressionType = DATA_ACCESS;
			return this;
		}
		
		//handle surrounding parentheses
		if(expr.charAt(0) == '('){
			if(allInParen()){
				if(expr.charAt(expr.length()-1) == ')'){
					compSubExp1 = new Expression(expr.substring(1,expr.length() - 1),data).compile();
					compiled = true;
					expressionType = PASSTHROUGH;
					return this;
				}
				compSubExp1 = new Expression(expr.substring(1,expr.length()),data).compile();
				compiled = true;
				expressionType = PASSTHROUGH;
				return this;
			}
		}
		
		//handle surrounding brackets
		if(expr.charAt(0) == '{'){
			if(allInBracket()){
				if(expr.charAt(expr.length()-1) == '}'){
					compSubExp1 = new Expression(expr.substring(1,expr.length() - 1),data).compile();
					compiled = true;
					expressionType = PASSTHROUGH;
					return this;
				}
				compSubExp1 = new Expression(expr.substring(1,expr.length()),data).compile();
				compiled = true;
				expressionType = PASSTHROUGH;
				return this;
			}
		}

		//handles definition
		if(!(posOfFirstLevel(":=","{(","})") ==-1)){
			compiled = false;
			return this;
		}
		
		//handles comparison >=
		if(!(posOfFirstLevel(">=",'(',')') ==-1)){
			pos = posOfFirstLevel(">=",'(',')');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+2,expr.length()),data).compile();
			compiled = true;
			expressionType = GREATER_THAN_EQUAL;
			return this;
		}

		//handles comparison <=
		if(!(posOfFirstLevel("<=",'(',')') ==-1)){
			pos = posOfFirstLevel("<=",'(',')');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+2,expr.length()),data).compile();
			compiled = true;
			expressionType = LESS_THAN_EQUAL;
			return this;
		}

		//handles comparison >
		if(!(posOfFirstLevel(">",'(',')') ==-1)){
			pos = posOfFirstLevel(">",'(',')');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = GREATER_THAN;
			return this;
		}

		//handles comparison <
		if(!(posOfFirstLevel("<",'(',')') ==-1)){
			pos = posOfFirstLevel("<",'(',')');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = LESS_THAN;
			return this;
		}

		//handles comparison ==
		if(!(posOfFirstLevel("==",'(',')') ==-1)){
			pos = posOfFirstLevel("==",'(',')');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+2,expr.length()),data).compile();
			compiled = true;
			expressionType = EQUAL_TEST;
			return this;
		}

		//handles addition
		if(containsOnFirstLevel('+')){
			pos = posOfFirstLevel('+');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = ADDITION;
			return this;
		}
		
		//handles negatives
		if(containsOnFirstLevel('-')){
			pos = posOfFirstLevel('-');
			if(pos == 0){
				compSubExp1 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
				compiled = true;
				expressionType = NEGATIVE;
				return this;
			}
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = SUBTRACTION;
			return this;
		}
		
		//handles multiplication
		if(containsOnFirstLevel('*')){
			pos = posOfFirstLevel('*');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = MULTIPLICATION;
			return this;
		}
		
		//handles division
		if(containsOnFirstLevel('/')){
			pos = posOfFirstLevel('/');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = DIVISION;
			return this;
		}
		
		//handles mod
		if(containsOnFirstLevel('%')){
			pos = posOfFirstLevel('%');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = MODULUS;
			return this;
		}
		
		//handles powers
		if(containsOnFirstLevel('^')){
			pos = posOfFirstLevel('^');
			compSubExp1 = new Expression(expr.substring(0,pos),data).compile();
			compSubExp2 = new Expression(expr.substring(pos+1,expr.length()),data).compile();
			compiled = true;
			expressionType = POWERS;
			return this;
		}
		
		if(implicitMultiplicationFixer()){
			return this.compile();
		}
		
		compiled = false;
		return this;
	}
	
	/**
	 * Evaluate Compiled expression
	 */
	public ReturnType evaluateCompiled(){
		return new ReturnType("ERROR: Bad Input",0.0,false);
	}
	
	/**
	 * Returns a String that is the unevaluated expression
	 */
	public String string(){
		return expr;
	}
	
	/**
	 * Same as string.
	 */
	public String toString(){
		return expr;
	}
	
	/**
	 * Finds symbols or strings on the first level 
	 * of the given symbols, usually parentheses.
	 */
	private int posOfFirstLevel(String a, char up, char down){
		return posOnLevel(expr,a,up+"",down+"",0);
	}
	
	private int posOfFirstLevel(String a, String up, String down){
		return posOnLevel(expr,a,up+"",down+"",0);
	}
	
	/**
	 * Finds a char on the first level of parentheses, 
	 * uses the other posOfFirstLevel.
	 */
	private int posOfFirstLevel(char a){
		return posOfFirstLevel(a+"",'(',')');
	}
	
	/**
	 * Returns a bool of whether posOfFirstLevel(char) will succeed.
	 */
	private boolean containsOnFirstLevel(char a){
		return (posOfFirstLevel(a) > -1);
	}
	
	/**
	 * Checks to see if this expression is all in parentheses
	 */
	private boolean allInParen(){
		int parenLevel = 1;
		for(int i = 1; i < expr.length(); i++){
			
			if(parenLevel == 0){
				return false;
			}
			
			if(expr.charAt(i) == '('){
				parenLevel++;
			}
			
			if(expr.charAt(i) == ')'){
				parenLevel--;
			}
		}
		return true;
	}

	/**
	 * Checks to see if this expression is all in brackets
	 */
	private boolean allInBracket(){
		int bracketLevel = 1;
		for(int i = 1; i < expr.length(); i++){
			
			if(bracketLevel == 0){
				return false;
			}
			
			if(expr.charAt(i) == '{'){
				bracketLevel++;
			}
			
			if(expr.charAt(i) == '}'){
				bracketLevel--;
			}
		}
		return true;
	}
	
	private int posOnLevel(String search, String find, String ups, String downs,int onlevel){
		int level = 0;
		for(int i = search.length() - 1; i >= 0; i--){
			String s = search.charAt(i) + "";
			if(search.substring(i,Math.min(i+find.length(),search.length())).equals(find)){
				if(level == onlevel){
					return i;
				} else if( downs.contains(s) && level +1 == onlevel){
					return i;
				} else if( ups.contains(s) && level - 1 == onlevel){
					return i;
				}
			}
			if(downs.contains(s)){
				level++;
			}
			
			if(ups.contains(s)){
				level--;
			}
		}
		return -1;
	}
	
	private boolean implicitMultiplicationFixer(){
		String operators = "+-=<>^*%/{}(";
		String allOps = "+-=<>^*%/{}()";
		for(int i = 0; i < expr.length(); i++){
			if(i > 0 && expr.charAt(i) == '(' && !operators.contains(expr.charAt(i-1)+"")){	//x(
				//System.err.println("Fixing Implicit Multiplication 1 in \"" + expr + "\"");
				expr = expr.substring(0,i) + "*" + expr.substring(i,expr.length());
				//System.err.println("\tto \"" + expr + "\"");
				return true;
			}
			if((i < expr.length() - 1 ) && expr.charAt(i) == ')' && !operators.contains(expr.charAt(i+1)+"")){	//)x
				//System.err.println("Fixing Implicit Multiplication 2 in \"" + expr + "\"");
				expr = expr.substring(0,i+1) + "*" + expr.substring(i+1,expr.length());
				//System.err.println("\tto \"" + expr + "\"");
				return true;
			}
			if((i < expr.length() - 1 ) && (inCharRange(expr.charAt(i),'0','9')) && !allOps.contains(expr.charAt(i+1)+"") 
					&& !inCharRange(expr.charAt(i+1),'0','9')){	//1x
				//System.err.println("Fixing Implicit Multiplication 3 in \"" + expr + "\"");
				expr = expr.substring(0,i+1) + "*" + expr.substring(i+1,expr.length());
				//System.err.println("\tto \"" + expr + "\"");
				return true;
			}
			if((i < expr.length() - 1 ) && (inCharRange(expr.charAt(i+1),'0','9')) && !allOps.contains(expr.charAt(i)+"") 
					&& !inCharRange(expr.charAt(i),'0','9')){	//1x
				//System.err.println("Fixing Implicit Multiplication 3 in \"" + expr + "\"");
				expr = expr.substring(0,i+1) + "*" + expr.substring(i+1,expr.length());
				//System.err.println("\tto \"" + expr + "\"");
				return true;
			}
		}
		//System.err.println("No Implicit on \"" + expr + "\"");
		return false;
	}
	
	private boolean inCharRange(char var, char begin, char end){
		return (var >= begin) && (var <= end);
	}
}
