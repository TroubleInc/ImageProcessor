/*
*	CalcContext.java
*	Handles the accuracy of calculations, this is the class that actually does the math on the numbers, 
*	created by the Calc class initially, handled by the expression class, will by accessible by a menu to set accuracy mode.
*/

package Calc;

import java.math.RoundingMode;
import java.math.MathContext;
import java.math.BigDecimal;

public class CalcContext
{
	public enum DecimalMode {Int,Small,Medium,Large,Unlimited,Mod}
	private DecimalMode mode;
	private BigDecimal modBase;
	private MathContext mContext;

	public CalcContext()
	{
		mode = DecimalMode.Medium;
		mContext = new MathContext(MathContext.DECIMAL64.getPrecision(),RoundingMode.HALF_EVEN);
		
	}
	
	public DecimalMode mode(){
		return mode;
	}
	
	public void changeMode(DecimalMode m)
	{
		if(mode.equals(m)){
			return;
		}

		mode = m;

		if(mode.equals(DecimalMode.Int)){
			mContext = new MathContext(MathContext.UNLIMITED.getPrecision(),RoundingMode.DOWN);
		} else if(mode.equals(DecimalMode.Small)){
			mContext = new MathContext(MathContext.DECIMAL32.getPrecision(),RoundingMode.HALF_EVEN);
		} else if(mode.equals(DecimalMode.Medium)){
			mContext = new MathContext(MathContext.DECIMAL64.getPrecision(),RoundingMode.HALF_EVEN);
		} else if(mode.equals(DecimalMode.Large)){
			mContext = new MathContext(MathContext.DECIMAL128.getPrecision(),RoundingMode.HALF_EVEN);
		} else if(mode.equals(DecimalMode.Unlimited)){
			mContext = new MathContext(MathContext.UNLIMITED.getPrecision(),RoundingMode.HALF_EVEN);
		} else if(mode.equals(DecimalMode.Mod)){
			mContext = new MathContext(MathContext.UNLIMITED.getPrecision(),RoundingMode.DOWN);
		}
	}

	public void changeMode(DecimalMode m, BigDecimal mBase)
	{
		changeMode(m);
		modBase = mBase;
	}

	public void roundType(RoundingMode r)
	{
		mContext = new MathContext(mContext.getPrecision(),r);
	}
	
	public BigDecimal parseNumber(String s) throws Exception
	{
		//System.out.println(s);
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return new BigDecimal(s,mContext);
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = new BigDecimal(s,mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return new BigDecimal(s,mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}
	
	public BigDecimal cast(BigDecimal a){
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return a.add(new BigDecimal("0"),mContext);
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = a.add(new BigDecimal("0"),mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}
	
	public BigDecimal add(BigDecimal a, BigDecimal b)
	{
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return a.add(b,mContext);
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = a.add(b,mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.add(b,mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}

	public BigDecimal subtract(BigDecimal a, BigDecimal b)
	{
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return a.subtract(b,mContext);
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = a.subtract(b,mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.subtract(b,mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}

	public BigDecimal multiply(BigDecimal a, BigDecimal b)
	{
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return a.multiply(b,mContext);
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = a.multiply(b,mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.multiply(b,mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}

	public BigDecimal divide(BigDecimal a, BigDecimal b)
	{
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return a.divide(b,mContext);
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = a.divide(b,mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.divide(b,mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}

	public BigDecimal mod(BigDecimal a, BigDecimal b)
	{
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			BigDecimal temp = a.remainder(b,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(b,mContext);
			}
			return temp;
		} else if(mode.equals(DecimalMode.Mod)){
			BigDecimal temp = a.remainder(b,mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.remainder(b,mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}
	
	public BigDecimal power(BigDecimal a, BigDecimal b)
	{
		if(!mode.equals(DecimalMode.Mod) && !mode.equals(DecimalMode.Int)){
			return a.pow(b.intValue(),mContext);
		} else if(mode.equals(DecimalMode.Mod)){  //needs to be optimized to handle large numbers (binary exponention)
			BigDecimal temp = a.pow(b.intValue(),mContext).remainder(modBase,mContext);
			while(temp.compareTo(new BigDecimal("0.0"))<0){
				temp = temp.add(modBase,mContext);
			}
			return temp;
		} else {
			return a.pow(b.intValue(),mContext).divideToIntegralValue(new BigDecimal("1"),mContext);
		}
	}
		
	public double greaterThan(BigDecimal a, BigDecimal b)
	{
		if(a.compareTo(b) > 0){
			return 1.0;
		} else {
			return 0.0;
		}
	}

	
	public boolean isZero(BigDecimal a)
	{
		return a.compareTo(new BigDecimal("0.0")) == 0;
	}
}
