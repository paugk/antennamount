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
 * @version $Id: TextEditor.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.utils.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

/**
 * 
 * The editor for formatted fields like for example coordinates
 * The purpose of the editor is to help the user understanding the format 
 * of what he's writing adding special character at given positions.
 *
 * The input given by the user is divided in numeric fields separated
 * by char separators.
 * The number of numeric fields is equals to the number of separators plus 1.
 * 
 * All the numeric fields are positive number but the first one that can be signed.
 * 
 * This widget accepts numbers separated by tseparator defined by the user in the constructor.
 * Each column between two separators has a max and a min allowed value.
 * Besides max and min for columns, there is the max and min value for the whole number.
 * For example if we want to accept number between [-90 00' 00".00, 90 00' 00".00]
 * this are the min and max for the cols:
 * deg: [-90, 90], min: [00,59], sec: [00, 59], decimals: [00, 99]
 * Without specifying the max allowabl number, the text field would accept a number like
 * [90 59' 59" 99] that is out of range!
 * For this example minValue=-2700000.00 and maxValue=+2700000.00.
 * 
 */
public class TextEditor extends JTextField implements ActionListener {
	
//	 The separators between chars in the input string
	// These chars are added by the editor when the user is writing
	//
	// The number of separators determines the number of numeric fields:
	// fields number = char separators number +1
	private char[] separators=null;
	
	// The min/max values for each numeric fields
	private int[] mins=null;
	private int[] maxs=null;
	
	// The filter the precess the input in the text field
	private TextEditorFilter filter;
	
	// The max number of chars the user can enter in the text field
	// 
	// It is calculated from the number separators and the limits
	// of each field
	private int maxLength;
	
	// The number of chars of each field 
	private int[] lenghts;
	
	private PlainDocument document;
	
	/**
	 * Constructor
	 * 
	 * The constructor set the the parameters used by the object to check
	 * if the inserted value is right or must be rejected.
	 * For each field, its min and max must be given but that's not enough,
	 * the max and min possible value must also be specified.
	 * 
	 * @param seps The separators between numeric fields
	 * @param minFields  The min values for each numeric field
	 *             (the only negative value can be the first one)
	 * @param maxFields  The max values for each numeric field
	 *             (the only negative value can be the first one)
	 * @param minVal The minimum value returned by the text field without
	 *               separators (only the final dot for decimals is accepted)
	 * @param minVal The maximum value returned by the text field without
	 *               separators (only the final dot for decimals is accepted)
	 * 
	 *
	 */
	public TextEditor(char[] seps, int[] minFields, int[]maxFields, double minVal, double maxVal) {
		super();
		if (seps==null || seps.length==0) {
			throw new IllegalArgumentException("Invalid separators");
		}
		if (minFields==null || minFields.length==0) {
			throw new IllegalArgumentException("Invalid min values");
		}
		if (maxFields==null || maxFields.length==0) {
			throw new IllegalArgumentException("Invalid max values ");
		}
		if (minFields.length!=maxFields.length) {
			throw new IllegalArgumentException("Mins and Maxs have different sizes");
		}
		if (seps.length!=maxFields.length-1) {
			throw new IllegalArgumentException("Mins and Maxs size must be equals to the size of seps +1");
		}
		if (minVal>=maxVal) {
			throw new IllegalArgumentException("minVal >= maxVal not allowed");
		}
		separators=seps;
		mins=minFields;
		maxs=maxFields;
		maxLength=checkArgs(seps,minFields,maxFields);
		addActionListener(this);
		document = new PlainDocument(); 
		filter = new TextEditorFilter(this,maxLength,separators,maxs,mins,lenghts,maxVal, minVal, document);
		document.setDocumentFilter(filter);
		setDocument(document);
		setColumns(maxLength+1);
	}
	
	/**
	 * Check if the given arguments are valid.
	 * It also calculates the lenght of each fields, stored in
	 * lenghts;
	 * 
	 * @param seps The separators between numeric fields
	 * @param min The min values for each numeric field
	 * @param max The max values for each numeric field
	 * @return The number of columns needed to edit numbers
	 *         with the given format
	 */
	private int checkArgs(char[] seps, int[] min, int[]max) {
		int ret=seps.length;
		lenghts = new int[ret+1];
		for (int t=0; t<min.length; t++) {
			if (t>0 && (min[t]<0 || max[t]<0)) {
				throw new IllegalArgumentException("Max or Min <0 in position "+t);
			}
			if (min[t]>=max[t]) {
				throw new IllegalArgumentException("Min="+min[t]+" Max="+max[t]+" invalid in position "+t);
			}
			// Get the number of cols needed to represent the number
			int maxSize=Math.max(Math.abs(min[t]),Math.abs(max[t]));
			String str = ""+maxSize;
			ret+=str.length();
			lenghts[t]=str.length();
		}
		// If there is a negative number the + and - are allowed
		if (min[0]<0) {
			ret++;
		}
		return ret;
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		// Enter has been pressed
		// We catch the event but do nothing for the time being
	}
	
	/**
	 * Return the number inserted by the user delegating
	 * to the filter
	 * 
	 * @return The double inserted by the user
	 *         null if the format is wrong
	 *         
	 *  @see alma.control.gui.antennamount.utils.editorTextEditorFilter
	 */
	public Double getValue() {
		return filter.getNumber();
	}
	
	/**
	 * Check if the number in the text field is in the given range
	 * delegating to the filter
	 * 
	 * @return true if the number is in the given range
	 * 
	 * @see alma.control.gui.antennamount.utils.editorTextEditorFilter
	 */
	public boolean isInRange() {
		return filter.isInRange();
	}
}
