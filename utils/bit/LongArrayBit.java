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
 * @version $Id: LongArrayBit.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.utils.bit;

/**
 * Helper class to easily get the bi representation out of an array
 * of long. 
 *
 */
public class LongArrayBit {
	// The long to translate in their bits representation
	private Long[] values=null;
	
	// Bits of the values
	private LongBit[] longsBits=null;

	/**
	 * Constructor
	 * 
	 * @param size The number of long in the sequence
	 */
	public LongArrayBit(Long[] words) {
		if (words==null || words.length==0) {
			throw new IllegalArgumentException("Invalid aray of long");
		}
		setArray(words);
	}
	
	private void buildRepresentation(Long[] longs) {
		longsBits=new LongBit[longs.length];
		for (int t=0; t<longs.length; t++) {
			longsBits[t]=new LongBit(longs[t]);
		}
	}
	
	public void setArray(Long[] longs) {
		if (longs==null) {
			throw new IllegalArgumentException("Illegal null array of long");
		}
		values=longs;
		buildRepresentation(longs);
	}
	
	/**
	 * Return one bit of one long in the array
	 * 
	 * @param word The index of the word
	 * @param bit The index of the bit
	 * @return true if the bit is set
	 *         false otherwise
	 */
	public boolean getBit(int word, int bit) {
		if (word<0 || word>=values.length) {
			throw new IllegalArgumentException("Invalid word index: "+word);
		}
		if (bit<0 || bit>=Long.SIZE) {
			throw new IllegalArgumentException("Invalid bit index: "+bit);
		}
		return longsBits[word].getBit(bit);
	}
	
	/**
	 * Gte the bit representation of a given long in the array
	 * 
	 * @param word The index of the long in the array
	 * @return The bit representation of the item in the given position
	 */
	public LongBit getBits(int word) {
		if (word<0 || word>=values.length) {
			throw new IllegalArgumentException("Invalid word index: "+word);
		}
		return longsBits[word];
	}
	
	/**
	 * Print the bits of this array
	 */
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int t=0; t<longsBits.length; t++) {
			ret.append("Byte ");
			ret.append(t);
			ret.append(": [");
			ret.append(longsBits[t]);
			ret.append("]\n");
		}
		return ret.toString();
	}
}
