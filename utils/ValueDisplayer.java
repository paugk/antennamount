/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2007
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

/** 
 * @author  caproni   
 * @version $Id: ValueDisplayer.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.utils;

import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.ValueConverter.ValueType;

/**
 * Format a value (contained in a ValueHolder) into a HTML String.
 * <P>
 * The values is checked to determine if it is valid an up to date.
 * The returned value is then composed of the string to pass plus a 
 * {@link ValueState}. 
 *
 */
public class ValueDisplayer {
	
	/**
	 * The value returned by the <code>getString</code> methods is composed of
	 * a state plus the string to display.
	 * <P>
	 * The color to display the string comes from the state i.e. <code>str</code>
	 * is not HTML.
	 * 
	 * @author acaproni
	 *
	 */
	public static class DisplayStruct {
		/**
		 * The state of the value
		 */
		public ValueState state;
		
		/**
		 * The string to show in the GUI.
		 * <P>
		 * It is a plain string (not a HTML).
		 */
		public String str;
	}
	
	// The normal colors used to show values in the cells
	public static final String normalColor = "<HTML><FONT color=\"black\">";
	public static final String warningColor = "<HTML><FONT color=#FF9F5F>";
	public static final String errorColor="<HTML><FONT color=\"red\">";
	public static final String closeHTML ="</FONT></HTML>";
	
	/**
	 * The string shown when the Mount is not connected
	 * or the value in the holder is null
	 */
	public static final String NOT_AVAILABLE="N/A";
	public static final String RED_NOT_AVAILABLE=errorColor+NOT_AVAILABLE+closeHTML;
	
	/**
	 * The string shown when the bit is set
	 */
	public final static String Set="SET";
	
	/**
	 * The string shown when the bit is unset
	 */
	public final static String Unset="Unset";
	
	/**
	 * Set a String with the content and the colors  depending on the content
	 * of the ValueHolder
	 * <P>
	 * The number to display needs to be converted using the right converter
	 * in a human readable format. If the converter is null, no conversion
	 * is applied.
	 *  <P>
	 *  Apart of checking if the value is up to date and valid, this method converts
	 *  the value by depending on passe {@link ValueType}.
	 *  
	 * @param val The value to display (if <code>null</code>, NOT_AVAILABLE is displayed)
	 *            It is used to check if the value is correct ot not
	 * @param The converterType to show the number in a human readable format
	 *        (can be <code>null</code>)
	 *        
	 * @return The formatted string
	 */
	public static synchronized DisplayStruct getString(ValueHolder<Double> val, ValueType converterType) {
		DisplayStruct ret = new DisplayStruct();
		if (val==null || val.getValue()==null) {
			ret.str=NOT_AVAILABLE;
			ret.state=ValueState.ERROR;
			return ret;
		} 
		String str;
		if (converterType==null) {
			str=val.getValue().toString();
		} else {
			str=ValueConverter.rad2string(val.getValue().doubleValue(), converterType);
		}
		str=ValueDisplayer.checkDecimal(str);
		return getString(str,val);
	}
	
	/**
	 * This method format the string with a state depending on the status
	 * of the value holder.
	 * <P>
	 * In practice, the content of the value holder is not used, apart of checking
	 * if the value is valid and up to date.
	 *  
	 * @param str The string to print
	 * @param val The value used to decorate the string 
	 * @return The coloured string
	 */
	public static synchronized DisplayStruct getString(String str, ValueHolder<?> val) {
		DisplayStruct ret = new DisplayStruct();
		if (str==null || val==null || val.getValue()==null) {
			ret.str=NOT_AVAILABLE;
			ret.state=ValueState.ERROR;
			return ret;
		}
		ret.str=str;
		if (!val.isUpToDate()) {
			ret.state=ValueState.ERROR;
		} else if (!val.isValid()) {
			ret.state=ValueState.WARNING;
		} else {
			ret.state=ValueState.NORMAL;
		}
		return ret;
	}
	
	private static synchronized String checkDecimal(String str) {
		str=str.trim();
		int pos = str.lastIndexOf('.');
		if (pos<0) {
			return str;
		}
		String[] strs = str.split("\\.");
		StringBuilder ret = new StringBuilder();
		for (int t=0; t<strs.length-1; t++) {
			ret.append(strs[t]);
		}
		ret.append('.');
		int decimals=0;
		char c;
		for (int t=0; t<strs[strs.length-1].length()-1; t++) {
			c=strs[strs.length-1].charAt(t);
			if (c>+'0' && c<='9' && decimals<2) {
				decimals++;
				ret.append(c);
			}
		}
		for (int t=decimals; t<2; t++) {
			ret.append('0');
		}
		if (strs[strs.length-1].charAt(strs[strs.length-1].length()-1)<'0' ||strs[strs.length-1].charAt(strs[strs.length-1].length()-1)>'9') {
			ret.append(strs[strs.length-1].charAt(strs[strs.length-1].length()-1));
		}
		return ret.toString();
	}
	
	/**
	 * Format a string representing the value of a status bit, depending one the
	 * state of the {@link ValueHolder}.
	 * If the bit is set, then the bit represents an error state and the string is colored
	 * with <code>ValueDisplayer.errorColor</code>.
	 * <P>
	 * The value returned by this method is ERROR if the bit is <code>true</code> (SET)
	 * In case of WARNING or NORMAL, it returns the max between the previous state
	 * and the state of the value. 
	 * This is done to facilitate the reporting of errors in the titles of the
	 * tabbed pane.
	 * 
	 * @param bit The bit to set the string to
	 * @param actualState The actual error state
	 * @param str The string to set depending on the value of the bit and the <code>ValueHolder</code>
	 * @param val The {@link ValueHolder} used to get the boolean <code>bit</code>
	 * @return The new error state after updating the value
	 * 
	 * @see #formatStatusBitInverse(boolean, ValueState, StringBuilder, ValueHolder)
	 */
	public static ValueState formatStatusBit(boolean bit, ValueState actualState, StringBuilder str, ValueHolder val) {
		if (str.length()>0) {
			str.delete(0, str.length());
		}
		if (bit) {
			str.append(ValueDisplayer.errorColor);
			str.append(Set);
			str.append(ValueDisplayer.closeHTML);
			return ValueState.ERROR;
		} else {
			ValueDisplayer.DisplayStruct dStruct=ValueDisplayer.getString(Unset, val);
			str.append(dStruct.state.format(dStruct.str));
			return ValueState.max(actualState, dStruct.state);
		}
	}
	
	/**
	 * Format a string representing the value of a status bit, depending one the
	 * state of the {@link ValueHolder}.
	 * If the bit is NOT set, then the bit represents an error state and the string is colored
	 * with <code>ValueDisplayer.errorColor</code>.
	 * <P>
	 * It is the same of {@link #formatStatusBit(boolean, ValueState, StringBuilder, ValueHolder)}
	 * but with the inverse logic i.e. there is an error if the bit is NOT set.
	 * <P>
	 * The value returned by this method is ERROR if the bit is <code>false</code> (UNSET)
	 * In case of WARNING or NORMAL, it returns the max between the previous state
	 * and the state of the value. 
	 * This is done to facilitate the reporting of errors in the titles of the
	 * tabbed pane.
	 * 
	 * @param bit The bit to set the string to
	 * @param actualState The actual error state
	 * @param str The string to set depending on the value of the bit and the <code>ValueHolder</code>
	 * @param val The {@link ValueHolder} used to get the boolean <code>bit</code>
	 * @return The new error state after updating the value
	 * 
	 * @see #formatStatusBit(boolean, ValueState, StringBuilder, ValueHolder)
	 */
	public static ValueState formatStatusBitInverse(boolean bit, ValueState actualState, StringBuilder str, ValueHolder val) {
		if (str.length()>0) {
			str.delete(0, str.length());
		}
		if (bit) {
			// This is not an error
			ValueDisplayer.DisplayStruct dStruct=ValueDisplayer.getString(Set, val);
			str.append(dStruct.state.format(dStruct.str));
			return ValueState.max(actualState, dStruct.state);
		} else {
			str.append(ValueDisplayer.errorColor);
			str.append(Unset);
			str.append(ValueDisplayer.closeHTML);
			return ValueState.ERROR;
		}
	}
	
	/**
	 * Format a string representing the value of a bit, depending one the
	 * state of the {@link ValueHolder}.
	 * This bit does not represent an error state so the string is not colored.
	 * <P>
	 * The value returned by this method is the state of the bit in case of ERROR.
	 * In case of WARNING or NORMAL, it returns the max between the previous state
	 * and the state of the value. 
	 * This is done to facilitate the reporting of errors in the titles of the
	 * tabbed pane.
	 * 
	 * @param bit The bit to set the string to
	 * @param actualState The actual error state
	 * @param str The string to set depending on the value of the bit and the <code>ValueHolder</code>
	 * @param val The {@link ValueHolder} used to get the boolean <code>bit</code>
	 * @return The new error state after updating the value
	 */
	public static ValueState formatBit(boolean bit, ValueState actualState, StringBuilder str, ValueHolder val) {
		if (str.length()>0) {
			str.delete(0, str.length());
		}
		ValueDisplayer.DisplayStruct dStruct;
		if (bit) {
			dStruct=ValueDisplayer.getString(Set, val);
			str.append(Set);
		} else {
			dStruct=ValueDisplayer.getString(Unset, val);
			str.append(Unset);
		}
		return ValueState.max(actualState, dStruct.state);
	}
}
