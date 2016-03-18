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
 * @version $Id: ValueHolder.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.mount;

/**
 * A class that encapsulate a value read throw a CORBA call
 * 
 * Together with the value itself, there are a few fields to
 * undertsand when the value has been read and if it is reliable
 * (for example if there was an error reading the value etc etc)
 */
public class ValueHolder<T> {
	// The time in millisec to consider the value as "old"
	// If the value has been generated more then VALIDITY_THRESHOLD
	// msec ago it is too old to be considered valid
	private static int VALIDITY_THRESHOLD = 10000;
	
	// The value read 
	protected T value=null;

	// The time (millisec) when the value has been read
	private long time;
	
	// The code of the error
	private long err;
	
	// The type of the error
	private long type;

	public ValueHolder() {
		value=null;
		err=0;
		type=0;
		time=0;
	}
	
	
	public synchronized long getErr() {
		return err;
	}

	public synchronized long getTime() {
		return time;
	}

	public synchronized long getType() {
		return type;
	}
	
	/**
	 * The value ecapsulated in this holder
	 * Can be null 
	 * 
	 * @return The value ecapsulated in this holder
	 */
	public synchronized T getValue() {
		return value;
	}
	
	/**
	 * @return true if the valus has been succesfully updated
	 */
	public synchronized boolean isUpToDate() {
		return type==0 && err==0 && time!=0;
	}
	
	/**
	 * Says if the object is valid, i.e. if it has been
	 * updated less the VALIDITY_THRESHOLD msec ago
	 * 
	 * @return true if the object is valid
	 */
	public synchronized boolean isValid() {
		long tm = System.currentTimeMillis();
		return (tm-time)<VALIDITY_THRESHOLD; 
	}
	
	/**
	 * Set the value of the object
	 * 
	 * @param obj The object to hold
	 * @param err The error go reading the value
	 * @param type The type of the error got reading the value
	 */
	public synchronized void setValue(T obj, int err, int type) {
		setValue(obj);
		this.err=err;
		this.type=type;
	}
	
	/**
	 * Set the value with no error
	 * 
	 * @param obj The object to hold
	 */
	public synchronized void setValue(T obj) {
		time = System.currentTimeMillis();
		err=0;
		type=0;
		value=obj;
	}

	/**
	 * Set the value of the object in case:
	 *   - no error
	 *   - the timestamp is produced from the source
	 *     i.e. not generated when setting the value
	 *     
	 * @param obj The new value for the holder
	 * @param timestamp The timestamp when the value has been generated
	 */
	public synchronized void setValue(T obj, long timestamp) {
		if (timestamp<=System.currentTimeMillis()) {
			time=timestamp;
		} else {
			time=System.currentTimeMillis();
		}
		err=0;
		type=0;
		value=obj;
	}
	
	/**
	 * Set an error for this object
	 * The value of the encapsulated object
	 * remain untouched
	 * 
	 * @param err
	 * @param type
	 */
	public synchronized void setError(int err, int type) {
		this.err=err;
		this.type=type;
	}
}
