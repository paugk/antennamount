/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package alma.control.gui.antennamount.utils;

import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * Some usefull conversions.
 *
 * @author ikriznar
 * 
 */
public final class ValueConverter
{
	
	/**
	 * The separators for the hours/nimutes/seconds
	 */
	public static final char[] HOURS_SEPARATORS = new char[] {':',':','.' };
	
	/**
	 * The separators for the degrees
	 */
	public static final char[] DEGREES_SEPARATORS = new char[] {0xb0, '\'', '.' };
	
	/**
	 * The separators for the radians
	 */
	public static final char[] RADIANS_SEPARATORS = new char[] {'.' };
	
	/**
	 * An enumerated representing the type of a number
	 * 
	 * @author acaproni
	 *
	 */
	public enum ValueType {
		RAD(2,3,RADIANS_SEPARATORS),  // radians
		DEG(7,2,DEGREES_SEPARATORS), // dddmmss
		HMS(6,2,HOURS_SEPARATORS), // hhmmss
		DMS(7,2,DEGREES_SEPARATORS); // dddmmss
		
		/**
		 * The length of the non decimal part of the string representing this type
		 * (+/- and separators excluded)
		 * 
		 */
		public final int length;
		
		/**
		 * The length of the non decimal part of the string representing this type
		 * (+/- and separators excluded)
		 * 
		 */
		public final int decimalLength;
		
		/**
		 * The spearators for the given type
		 */
		public final char[] separators;
		
		/**
		 * Constructor
		 * 
		 * @param len The size of the non decimal part of the string representing the number
		 * @param decLenThe size of the decimal part of the string representing the number
		 * @param seps The separators for the type
		 */
		private ValueType(int len, int decLen, char[] seps) {
			length=len;
			decimalLength=decLen;
			separators=seps;
		}
	}
	
	/**
	 *
	 */
	private ValueConverter() { }

	/**
	 * Converts value in format ddmmss.d in degrees in to radians.
	 *
	 * @param dms ddmmss.d degrees
	 *
	 * @return in radians
	 */
	public static final double dms2rad(double dms)
	{
		double f = 1.0;

		if (dms < 0.0) {
			dms = Math.abs(dms);
			f = -1.0;
		}

		double dd = Math.floor(dms / 10000.0);
		double mm = Math.floor((dms%10000.0) / 100.0);
		double ss = (dms%100.0) ;

		return Math.toRadians(f * (dd + (mm + ss / 60.0) / 60.0));
	}
	

	/**
	 * Converts value in rad to ddmmss.d format  in degrees.
	 *
	 * @param rad radians
	 *
	 * @return ddmmss.d in degrees
	 */
	public static final double rad2dms(double rad)
	{
		double f = 1.0;

		if (rad < 0.0) {
			rad = Math.abs(rad);
			f = -1.0;
		}

		double deg = Math.toDegrees(rad);

		double dd = Math.floor(deg);
		double mm = Math.floor((deg % 1.0 ) * 60.0);
		double ss = (((deg % 1.0 ) * 60.0 ) % 1.0) * 60.0;
		
		// Check if ss or dd are greater then 60
		int t=0;
		while (ss>=60.0) {
			ss-=60.0;
			t++;
		}
		mm+=t;
		t=0;
		while (mm>=60.0) {
			mm-=60.0;
			t++;
		}
		dd+=t;
		
		ss=Math.round(ss*100.0);

		return f * (dd * 10000.0 + mm * 100.0 + ss/100.0);
	}
	
	/**
	 * Converts value in format hhmmss.d in degrees in to radians.
	 *
	 * @param hms hhmmss.d degrees
	 *
	 * @return in radians
	 */
	public static final double hms2rad(double hms)
	{
		double f = 1.0;

		if (hms < 0.0) {
			hms = Math.abs(hms);
			f = -1.0;
		}

		double hh = Math.floor(hms / 10000.0);
		double mm = Math.floor((hms%10000.0) / 100.0);
		double ss = (hms%100.0) ;

		return (f * (hh + (mm + ss / 60.0) / 60.0)) / 12.0 * Math.PI;
	}

	/**
	 * Converts value in rad to hhmmss.d format  in degrees.
	 *
	 * @param rad radians
	 *
	 * @return hhmmss.d in degrees
	 */
	public static final double rad2hms(double rad)
	{
		double f = 1.0;

		if (rad < 0.0) {
			rad = Math.abs(rad);
			f = -1.0;
		}

		double hms = rad / Math.PI * 12.0;

		double hh = Math.floor(hms);
		double mm = Math.floor((hms % 1.0 ) * 60.0);
		double ss = (((hms % 1.0 ) * 60.0 ) % 1.0) * 60.0;
		
		// Check if ss or dd are greater then 60
		int t=0;
		while (ss>=60.0) {
			ss-=60.0;
			t++;
		}
		mm+=t;
		t=0;
		while (mm>=60.0) {
			mm-=60.0;
			t++;
		}
		hh+=t;

		return f * (hh * 10000.0 + mm * 100.0 + ss);
	}
	
	public static final double rad2deg(double rad) {
		return Math.toDegrees(rad);
	}
	
	public static final double deg2rad(double deg) {
		return Math.toRadians(deg);
	}

	/**
	 * The output is used for testing the conversions
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(" dms "+3600000.0+" -> rad "+dms2rad(3600000.0)+" test 6.28");
		System.out.println(" dms "+3595960.0+" -> rad "+dms2rad(3595960.0)+" test 6.28");
		System.out.println(" rad "+6.28+" -> dms "+rad2dms(6.28)+" test 3600000.0");
		System.out.println(" rad "+0.1+" -> dms "+rad2dms(0.1)+" test 3600000.0 deg "+Math.toDegrees(0.1));
		System.out.println(" hms "+240000.0+" -> rad "+hms2rad(240000.0)+" test 6.28");
		System.out.println(" hms "+239560+" -> rad "+hms2rad(235960.0)+" test 6.28");
		System.out.println(" rad "+6.28+" -> hms "+rad2hms(6.28)+" test 240000.0");
		System.out.println(" rad "+0.1+" -> hms "+rad2hms(0.1)+" test 240000.0 h "+(0.1/Math.PI*12.0));
		
		for (double d=-0.9; d<1.0; d+=0.1) {
			double temp =dms2rad(d);
			System.out.println("deg "+d+" = rad "+temp +" ==? "+rad2dms(temp)+" NEW "+ValueConverter.rad2dms(temp));
		}
		
		double temp=string2rad("12:30:30.00", ValueType.HMS);
		System.out.println("HMS string 12:30:30.00 -> rad "+temp+" -> HMS string "+rad2string(temp, ValueType.HMS));
		temp=string2rad("091:15:22.07", ValueType.DEG);
		System.out.println("DEG string 091:15:22.07 -> rad "+temp+" -> DEG string "+rad2string(temp, ValueType.DEG));
		temp=string2rad("-270:11:22.33", ValueType.DEG);
		System.out.println("DEG string -270:11:22.33 -> rad "+temp+" -> DEG string "+rad2string(temp, ValueType.DEG));
		
		temp=string2rad("+012:34:55.67", ValueType.DMS);
		System.out.println("DEG string +012:34:55.67 -> rad "+temp+" -> DMS string "+rad2string(temp, ValueType.DMS));
		
		temp=-123456.76891;
		System.out.println("RAD string -123456.76891 -> rad "+temp+" -> RAD string "+rad2string(temp, ValueType.RAD));
	}
	
	/**
	 * Convert a radians contained in a {@link ValueHolder} into a string 
	 * of the given type.
	 * <P>
	 * This method delegates to {@link ValueConverter#rad2string(double, ValueType)}.
	 * <BR>If the passed {@link ValueHolder} is <code>null</code> or it is <code>null</code> the
	 * double it contains, then an empty string is returned.
	 *  
	 * 
	 * @param vh The {@link ValueHolder} containing the radians to convert. <BR>
	 * 			It can be <code>null</code> as well the <code>Double</code> it contains
	 * @param type The type number to have in the returned <code>String</code>.
	 * @return A string representing the radians in the passed type.
	 */
	public static String rad2string(ValueHolder<Double> vh, ValueType type) {
		if (vh==null || vh.getValue()==null) {
			return "";
		}
		return rad2string(vh.getValue().doubleValue(), type);
	}
	
	/**
	 * Convert a radians to a string of the given type
	 * 
	 * @param r The radians to convert
	 * @param type The type of conversion
	 * @return A string representing the radians in the format
	 */
	public static String rad2string(double r, ValueType type) {
		if (type==null) {
			throw new IllegalArgumentException("The type can't be null");
		}
		switch (type) {
		case RAD: return formatDouble(r, type);
		case DEG: {
			double converted=rad2deg(r);
			return formatDouble(converted, type);
		}
		case HMS: {
			double converted=rad2hms(r);
			return formatDouble(converted, type);
		}
		case DMS: {
			double converted=rad2dms(r);
			return formatDouble(converted, type);
		}
		default: {
			throw new IllegalArgumentException("Unsupported conversion type: "+type.toString());
		}
		}
	}
	
	/**
	 * Return a string representing a double.
	 * <P>
	 * The double formatted by this method is one of that returned
	 * by the previous method converting a radians.
	 * It can be for example -1201233.44.
	 * <P>
	 * The string returned by the method, for the example reported upon,
	 * will be the same as the number ("-1201233.44") but there are cases
	 * when the double has less digits like for example 3211.00 and in that case
	 * the returned string is padded by 0: "-0003211.00" 
	 * <P>
	 * The passed type is used to determine the format the type of the
	 * returned string.
	 * For example the hms format has no +/- and the length is
	 * different from DMS and so on.
	 *  
	 * @param val The double to represent as String
	 * @param type The type of value to be represented in the returned string 
	 * @return a string representing the passed double.
	 */
	private static String formatDouble(double val, ValueType type) {
		boolean neg=false;
		if (val<0) {
			neg=true;
			val=-val;
		}
		String str= Double.toString(val);
		// remove the leading +/-
		if (str.startsWith("-") || str.startsWith("+")) {
			str=str.substring(1);
		}
		if (!str.contains(".")) {
			str=str+".00";
		}
		String[] parts=str.split("\\.");
		if (parts.length!=2) {
			// Impossible because there is only one . in the string
			throw new IllegalStateException("The string has a wrong format");
		}
		if (parts[1].length()>type.decimalLength) {
			// too many decimal digits
			parts[1]=parts[1].substring(0, type.decimalLength);
		} 
		while (parts[1].length()<type.decimalLength) {
			// too few decimal digits
				parts[1]=parts[1]+"0";
		}
		
		// Check the size of the left part of the string adding
		// 0 at the beginning
		while (parts[0].length()<type.length) {
			parts[0]="0"+parts[0];
		}
		
		String ret=addSeparators(parts[0], parts[1], type);
		if (neg) {
			ret="-"+ret;
		} else {
			ret = "+"+ret;
		}
		return ret;
	}
	
	/**
	 * Add the separators to the passed string
	 * 
	 * @param str The digits before the decimals (not including +/-)
	 * @param decStr The digits after the decimal dot
	 * @param type The type of value in the string to use the right set of separators
	 * 
	 * @return
	 */
	private static String addSeparators(String str, String decStr, ValueType type) {
		if (type==ValueType.RAD) {
			return str+RADIANS_SEPARATORS[0]+decStr;
		}
		if (str.length()!=type.length) {
			throw new IllegalArgumentException("Invalid length of string: "+str.length());
		}
		// Add the chars and the separator from the rightmost char
		StringBuilder temp=new StringBuilder(decStr);
		temp.insert(0, type.separators[2]);
		for (int t=str.length()-1; t>=0; t--) {
			temp.insert(0, str.charAt(t));
			if (str.length()-t==2) {
				temp.insert(0, type.separators[1]);
			}
			if (str.length()-t==4) {
				temp.insert(0, type.separators[0]);
			}
		}
		return temp.toString();
	}
	
	/**
	 * Parse the passed string and returns its value converted to radians.
	 *  
	 * @param str The string to parse (with separators)
	 * @param type The type of value in the string
	 * @return The value in the string converted to radians
	 */
	public static double string2rad(String str, ValueType type) {
		if (str==null || str.isEmpty()) {
			throw new IllegalArgumentException("Invalid string to parse");
		}
		if (type==null) {
			throw new IllegalArgumentException("Invalid null type for parsing");
		}
		if (type==ValueType.RAD) {
			return Double.parseDouble(str);
		}
		// Remove separators
		StringBuilder temp = new StringBuilder();
		if (!str.startsWith("+") && !str.startsWith("-")) {
			temp.append('+');
		}
		for (int t=0; t<str.length(); t++) {
			final char[] validChars= { '+', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
			char c=str.charAt(t);
			for (char cmp: validChars) {
				if (c==cmp) {
					temp.append(c);
					break;
				}
			}
		}
		// Check the size for correctness
		String[] parts = temp.toString().split("\\.");
		if (parts[0].length()!=type.length+1) {
			throw new IllegalStateException("Invalid format for "+str+" of type "+type+" (parts[0]="+parts[0]+", expected len="+type.length+")");
		}
		// Convert the double in the string into radians
		double d;
		try {
			d=Double.parseDouble(temp.toString());
		} catch (Exception e) {
			throw new IllegalStateException("Wrong string to parse "+str);
		}
		switch (type) {
		case DEG: return deg2rad(d);
		case HMS: return hms2rad(d);
		case DMS: return dms2rad(d);
		default: {
			throw new IllegalStateException("Invalid coversion requested at this point: "+type);
		}
		}
	}
	
}

/* __oOo__ */
