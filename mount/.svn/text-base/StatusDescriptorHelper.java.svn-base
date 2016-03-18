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
package alma.control.gui.antennamount.mount;

import java.util.HashMap;

/**
 * An helper class to get the description of an item out of its IDL code.
 * This class will be replaced by another one automatically generated.
 * 
 * @author acaproni
 *
 */
public final class StatusDescriptorHelper<K> extends HashMap<K, String> {
	
	/**
	 * Empty constructor
	 */
	public StatusDescriptorHelper() {
		loadDescriptions();
	}
	
	/**
	 * Add the descriptions to the hashmap
	 */
	private void loadDescriptions() {
		// Load the couples <key, value> into the map
		
		// Check if a key is already present before adding.. if containsKey()....
	}
	
	/**
	 * Override HashMap.put to add a code with its description.
	 * The code and its description can't be null.
	 * The description can't be an empty string.
	 * 
	 * @param code The code
	 * @param description The description of the code
	 * @return The previous value associated with key, or null if there was no mapping for key. 
	 * @see java.util.HashMap
	 */
	public String put(K code, String description) {
		if (code==null || description==null) {
			throw new IllegalArgumentException("The code and its description can't be null");
		}
		if (description.length()==0) {
			throw new IllegalArgumentException("Invalid empty descritpion for "+code.toString());
		}
		return super.put(code, description);
	}
	
	/**
	 * Return a String with the description of the given code
	 * 
	 * @param key The not null code
	 * @return The description of the given code
	 * @throws  IllegalArgumentException If the code is null
	 * @see java.util.HashMap
	 */
	public String getDescription(K key) {
		if (key==null) {
			throw new IllegalArgumentException("Invalid null code");
		}
		String ret = get(key);
		if (ret==null) {
			return "N/A";
		}
		return ret;
	}
}
