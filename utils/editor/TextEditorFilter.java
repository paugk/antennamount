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
 * @author  acaproni   
 * @version $Id: TextEditorFilter.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.utils.editor;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * 
 * The filter for the TextEditor
 * 
 * FUNCTIONING.
 * The separators are inserted automatically by the class.
 * The user only inserts numbers and the separators will appear
 * in the right position while he/she types.
 * The check for the correctness of the user input is done by removing
 * the separators and checking the cleaned string against a regular
 * expression (regExp) built in the constructor.
 * Each time the user inserts something in the text field:
 * 1 checks if the input is valid (if it is not, the insertion aborted)
 * 2 format the string (i.e. add separators)
 * 3 replace the content of the text field with the formatted string
 *
 */
public class TextEditorFilter extends DocumentFilter {
	// The separators between chars in the input string
	// These chars are added by the editor when the user is writing
	//
	// The number of separators determines the number of numeric fields:
	// fields number = char separators number +1
	private char[] separators=null;
	
	// The min/max values for each numeric fields
	private int[] mins=null;
	private int[] maxs=null;
	
	// The  min and max of the final value
	private double minVal;
	private double maxVal;
	
    // The number of chars of each field 
	private int[] lenghts;
	
	// The position of each separator (without takning into account 
	// the trailing + or -)
	private int[] sepsPositions;
	
	// The number of chars in the editor
	private int numberOfColumns;
	
	// The max number of digits
	private int digits;
	
	// The regular expression to check if the inserted string is valid
	private String regExp;
	
	// The document
	private PlainDocument document;
	
	// The editor that uses this filter
	private TextEditor editor=null;
	
	/**
	 * Constructor 
	 * 
	 * @param cols The max number of chars in the document
	 * @param seps The separators
	 * @param max The max number of chars per field
	 * @param min The min number of chars per field
	 * @param lens The length of each numeric field
	 * @param minValue The max accepted value
	 * @param manValue The max accepted value
	 * @param doc The document
	 * 
	 * @see alma.control.gui.antennamount.utils.editor.TextEditor
	 */
	public TextEditorFilter(TextEditor editor, int cols, char[] seps, int max[], int[] min, int[] lens, double maxValue, double minValue, PlainDocument doc) {
		if (seps==null || seps.length==0) {
			throw new IllegalArgumentException("You do not need this widget without separators!");
		}
		if (editor==null) {
			throw new IllegalArgumentException("Invalid null editor in constructor!");
		}
		this.editor=editor;
		numberOfColumns=cols;
		separators=seps;
		maxs=max;
		mins=min;
		minVal=minValue;
		maxVal=maxValue;
		lenghts=lens;
		document=doc;
		// Calc the position of each separator (+ and - ignored)
		sepsPositions=new int[seps.length];
		for (int t=0; t<seps.length; t++) {
			if (lens[t]<=0) {
				throw new IllegalArgumentException("Invalid len ("+lens[t]+") in position "+t);
			}
			if (t==0) {
				sepsPositions[t]=lens[t]-1;
			} else {
				sepsPositions[t]=sepsPositions[t-1]+lens[t];
			}
		}
		// Calc the max number of digits the user can insert
		digits=0;
		for (int t: lenghts) {
			digits+=t;
		}
		// Calc the regular expression to check the correctness of user input
		StringBuilder tempRegExp = new StringBuilder("^[+-]?\\d{0,");
		tempRegExp.append(digits);
		tempRegExp.append("}?");
		regExp=tempRegExp.toString();
	}
	
	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) {
	}

	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) {
		int caretPos=editor.getCaretPosition();
		String str=null;
		try {
			str = document.getText(0,document.getLength());
		} catch (Exception e) {
			System.err.println("1 ===>>> "+e.getMessage());
			return;
		}
		String removing =str.substring(offset,offset+length);
		StringBuilder builder = new StringBuilder(str.substring(0,offset));
		builder.append(str.substring(offset+length));
		if (checkInputString(builder.toString())) {
			// Check if the user is removing +, - or a separator
			String formattedStr=formatString(builder.toString());
			try {
				super.replace(fb,0,document.getLength(),formattedStr,null);
				// The new position of the caret depends if the user is using delete
				// (offset==caretPos) or backspace
				// If using delete then the position of the caret does not change
				int newCaretPos=(offset==caretPos)?caretPos:caretPos-length;
				editor.setCaretPosition(newCaretPos);
			} catch (Exception e) {
				System.err.println("3 ===>>> "+e.getMessage());
				return;
			}
		}
	}
	
	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) {
		int caretPos=editor.getCaretPosition();
		int len =document.getLength(); 
		StringBuilder builder=new StringBuilder();
		String str=null;
		try {
			str = document.getText(0,len);
		} catch (Exception e) {
			System.err.println("4 ===>>> "+e.getMessage());
			return;
		}
		builder.append(str.substring(0,offset));
		builder.append(text);
		builder.append(str.substring(offset+length));
		if (checkInputString(builder.toString())) {
			String formattedStr=formatString(builder.toString());
			try {
				super.replace(fb,0,document.getLength(),formattedStr,attrs);
				int lenAfter=document.getLength();
				if (caretPos==len) {
					return;
				} else if (lenAfter-len>0) { 
					editor.setCaretPosition(caretPos+(lenAfter-len));
				} else {
					editor.setCaretPosition(caretPos+text.length());
				}
			} catch (Exception e) {
				System.err.println("5 ===>>> "+e.getMessage());
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Check if the input string contains valid characters
	 * i.e. digits, +/-...
	 * 
	 * @param str The string to ccheck
	 * @return true if the string is valid
	 */
	private boolean checkInputString(String str) {
		// Remove separators from string
		String tmp=cleanInputString(str,false);
		// Check if the string is valid
		return Pattern.matches(regExp,tmp);
	}
	
	/**
	 * Return the number inserted in the editor removing all the occurrencies 
	 * of the separators from the string (i.e. the string is in the format
	 * nnnnnnnnnn depending on the lenghts of the numeric fields)
	 * 
	 * @param str The string to clean
	 * @param dot If it is true and the last separator is a dot (.)
	 *            it is considered a separator for decimal and is not removed
	 * @return The string without separators
	 */
	private String cleanInputString(String str, boolean dot) {
		if (str==null || str.length()==0) {
			return str;
		}
		String tmp = str;
		for (int t=0; t<separators.length; t++)  {
			char c=separators[t];
			if (t!=separators.length-1) {
				tmp=tmp.replace(""+c,"");
			} else {
				if (dot && c=='.') {
					continue;
				} else {
					tmp=tmp.replace(""+c,"");
				}
			}
		}
		return tmp;
	}
	
	private String cleanInputString(Integer[] fields, boolean dot) {
		if (fields==null || fields.length!=lenghts.length) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		for (int t=0; t<fields.length; t++) {
			if (dot && t==fields.length-1) {
				ret.append('.');
			}
			int pad=0; // Remember how many 0 we added
			int value=Math.abs(fields[t]);
			String num = Integer.valueOf(value).toString();
			while (num.length()+pad<lenghts[t]) {
				ret.append('0');
				pad++;
			}
			ret.append(num);
		}
		return ret.toString();
	}
	
	/**
	 * Format the passed string inserting the separators 
	 * in expected positions.
	 * 
	 * 
	 * @param str The string to format (with or without separators)
	 * @return The formatted string
	 */
	private String formatString(String str) {
		if (str==null) {
			throw new IllegalArgumentException("Invalid null string in formatString");
		}
		if (str.length()==0) {
			return str;
		}
		// Remove separators from the string
		String tmp=cleanInputString(str,false);
		StringBuilder builder = new StringBuilder();
		if (tmp.charAt(0)=='+' || tmp.charAt(0)=='-') {
			builder.append(tmp.charAt(0));
			tmp=tmp.replace(""+tmp.charAt(0),"");
		}
		// Scan all the chars in the cleaned string and add the separators
		// (at tis point the cleaned string only contains digits)
		for (int t=0; t<tmp.length(); t++) {
			builder.append(tmp.charAt(t));
			for (int j=0; j<sepsPositions.length; j++) {
				if (t==sepsPositions[j]) {
					builder.append(separators[j]);
				}
			}
		}
		return builder.toString();
	}
	
	/**
	 * Check if the number is negative, i.e. if the first char is a -
	 * 
	 * @return true if the number is negative
	 */
	private boolean isNegative() {
		String content=null;
		try {
			content = document.getText(0,document.getLength());
		} catch (Exception e) {
			return false;
		}
		return content.startsWith("-");
	}
	
	/**
	 * Return the value of each numeric field.
	 * If some fields have not been inserted, the corresponding value is null
	 * For example, if the user inserted 12:456, this method returns 12,456,null
	 * (the last null if a third field was defined but not yet inserted by the user)
	 * 
	 * @return The value of all the numeric fields
	 */
	private Integer[] getFieldsValues() {
		Integer[] ret = new Integer[separators.length+1];
		for (int t=0; t<ret.length; t++) {
			ret[t]=null;
		}
		String content=null;
		try {
			content = document.getText(0,document.getLength());
		} catch (Exception e) {
			System.err.println("Error getting text field content "+e.getMessage());
			return ret;
		}
		// Get the content of the field without separators
		String clean = cleanInputString(content,false);
		// Check if the document contains something
		if (clean.length()==0) {
			return ret;
		}
		if (clean.length()==1 && (clean.charAt(0)=='+' || clean.charAt(0)=='-')) {
			return ret;
		}
		// Start scanning the string
		int idx=0;
		StringBuilder tmp = new StringBuilder();
		for (int t=0; t<content.length(); t++) {
			if (idx<separators.length && content.charAt(t)!=separators[idx] || idx==separators.length ) {
				if (content.charAt(t)!='+') {
					tmp.append(content.charAt(t));
				}
				if (tmp.length()>0 && !(tmp.charAt(0)=='-' && tmp.length()==1)) {
					ret[idx]=Integer.parseInt(tmp.toString());
				}
			} 
			if (idx<separators.length && content.charAt(t)==separators[idx]) {
				tmp.delete(0,tmp.length());
				idx++;
			}
		}
		// Add a trailing '0' if the last char of the string is a dot
		// (i.e. if the user forgot to insert the decimals)
		if (ret[ret.length-1]==null && separators[separators.length-1]=='.') {
			ret[ret.length-1]=Integer.valueOf(0);
		}
		return ret;
	}
	
	/**
	 * Check if the number in the text field is in the given range
	 * i.e. if each numeric fields is between the max and min.
	 * 
	 * @return true if the all the numeric fields are in the given range
	 *         false otherwise (also if the user didn't insert all the digits)
	 */
	public boolean isInRange() {
		Integer[] fields=getFieldsValues();
		// Check if the user inserted all the fields
		boolean ret= true;
		for (int t=0; t<fields.length && ret; t++) {
			if (fields[t]==null) {
				fields[t]=Integer.valueOf(0);
			} 
			ret=ret && fields[t]>=mins[t] && fields[t]<=maxs[t];
			
		}
		// Check if the value is between the min and the max
		double val =getNumber().doubleValue();
		ret = ret && (val>=minVal && val<=maxVal);
		
		return ret;
	}
	
	/**
	 * Return the number inserted by the user
	 * Does not check if the number is in range
	 * 
	 * @return The number inserted by the user
	 *         null if the number is not complete (i.e. the user didn't insert
	 *              all the fields)
	 */
	public Double getNumber() {
		// Check if all the fields are present
		Integer[] fields=getFieldsValues();
		for (int t=0; t<fields.length; t++) {
			if (fields[t]==null) {
				// The user did not insert this field: we set its value to 0
				fields[t]=Integer.valueOf(0);	
			}
		}
		String content=null;
		try {
			content = document.getText(0,document.getLength());
		} catch (Exception e) {
			System.err.println("Error getting text field content "+e.getMessage());
			e.printStackTrace();
			return null;
		}
		String clean=cleanInputString(fields,true);
		Double ret = Double.parseDouble(clean);
		if (isNegative() && ret>0) {
			ret = -ret;
		}
		return ret;
	}
}
