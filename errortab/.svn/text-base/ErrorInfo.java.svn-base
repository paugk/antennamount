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
package alma.control.gui.antennamount.errortab;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import alma.ACSErr.ErrorTrace;
import alma.Control.Common.Util;
import alma.acs.exceptions.AcsJException;

/**
 * A class containing all the relevant informations for an error
 * to be shown on the error tab.
 * 
 * An error is composed of 
 *   - a short message describing the error
 *   - a detailed message describing the error
 *   - a variable set of properties in the format <title, description>
 *   
 * There can be different instances of the same object when the same error
 * happens several times at different instants.
 * The GUI implements a guard i.e. instead of showing all the errors of the
 * same kind, it reports how many times such errors happened.
 * The method addInstance records that a new instance of the same object exists.
 * In that case, the time stamp is updated to be the time stamp of the last received 
 * error and the instance counter is increased.
 * 
 * @author acaproni
 *
 */
public class ErrorInfo {
	
	/**
	 * Additional info for an error.
	 * An additional information that can be defined for an
	 * error in the form <title, description>
	 * 
	 * @author acaproni
	 *
	 */
	public class AdditionalInfo {
		public final String title;
		public final String description;
		
		/**
		 * Constructor
		 * 
		 * @param title The not null and not empty title of the addition info
		 * @param desc The not null and not empty content of the additional info
		 */
		public AdditionalInfo(String title, String desc) {
			if (title==null || title.length()==0) {
				throw new IllegalArgumentException("Invalid title");
			}
			if (desc==null || desc.length()==0) {
				throw new IllegalArgumentException("Invalid description");
			}
			this.title=title;
			this.description=desc;
		}
	}
	
	// The format of the timestamp
	private static final String TIME_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
	
	// The short and detailed description of the error
	public final String shortDescription;
	public final String detailedDescription;
	
	// The timestamp for the error
	// It is generated when an object is built
	private long timestamp;
	private String timestampStr;
	
	// A vector of additional informations
	// At the beginning it is null
	private Vector<AdditionalInfo> additionalInfos=null;
	
	// A boolean to know if the error has been acknowledged by the user
	private boolean acknowledged=false;
	
	// The number of instances of the same error
	//
	// This is to implement the guard
	private long instances=0;
	
	/**
	 * Constructor with the descriptions
	 * 
	 * @param shortDescription A not null and not empty short sentence describing the error
	 * @param detailedDescription A not null and not empty detailed description of the error
	 */
	public ErrorInfo(String shortDescription, String detailedDescription) {
		if (shortDescription==null || shortDescription.length()==0) {
			throw new IllegalArgumentException("Invalid short description");
		}
		if (detailedDescription==null || detailedDescription.length()==0) {
			throw new IllegalArgumentException("Invalid detailed description");
		}
		this.shortDescription=shortDescription;
		this.detailedDescription=detailedDescription;
		
		updateTimestamps();
	}
	
	/**
	 * Constructor with the descriptions and an associated exception to insert as
	 * additional information.
	 * The exception can be null.
	 * 
	 * @param shortDescription A not null and not empty short sentence describing the error
	 * @param detailedDescription  A not null and not empty detailed description of the error
	 * @param exception The exception whose data will be added into the Additional info (can be null)
	 */
	public ErrorInfo(String shortDescription, String detailedDescription, AcsJException exception) {
		this(shortDescription,detailedDescription);
		
		if (exception==null) {
			return;
		} 
		
		// Add the exception in the additional infos
		if (exception.getShortDescription()!=null && !exception.getShortDescription().isEmpty()) {
			addAdditionalInfo(new AdditionalInfo("Exception",exception.getShortDescription()));
		}
		ErrorTrace errorTrace = exception.getErrorTrace();
		if (errorTrace!=null) {
			StringBuilder str = new StringBuilder("<CODE>");
			str.append(Util.getNiceErrorTraceHTMLString(errorTrace));
			str.append("</CODE>");
			addAdditionalInfo(new AdditionalInfo("At",str.toString()));
		}
	}
	
	/**
	 * Constructor with the descriptions and an associated exception to insert as
	 * additional information.
	 * The exception can be null.
	 * 
	 * @param shortDescription A not null and not empty short sentence describing the error
	 * @param detailedDescription  A not null and not empty detailed description of the error
	 * @param t The exception whose data will be added into the Additional info (can be null)
	 */
//	public ErrorInfo(String shortDescription, String detailedDescription, Throwable throwable) {
//		this(shortDescription,detailedDescription);
//		
//		if (throwable==null) {
//			return;
//		}
//		
//		// Add the exception in the additional infos
//		if (throwable.getMessage()!=null) {
//			if (throwable.getMessage().length()>0) {
//				addAdditionalInfo(new AdditionalInfo("Exception",throwable.getMessage()));
//			}
//		}
//		StackTraceElement[] stackTraces = throwable.getStackTrace();
//		if (stackTraces!=null) {
//			StringBuilder str = new StringBuilder("<CODE>");
//			for (StackTraceElement stackTrace: stackTraces) {
//				str.append(stackTrace.toString());
//				str.append("<BR>");
//			}
//			str.append("</CODE>");
//			addAdditionalInfo(new AdditionalInfo("At",str.toString()));
//		}
//		
//	}
	
	/**
	 * Add an additional information for the error
	 * 
	 * @param info The not null additional information to add
	 */
	public void addAdditionalInfo(AdditionalInfo info) {
		if (info==null) {
			throw new IllegalArgumentException("Invalid null additional information");
		}
		if (additionalInfos==null) {
			additionalInfos=new Vector<AdditionalInfo>();
		}
		additionalInfos.add(info);
	}
	
	/**
	 * Check if there are additional informations
	 * @return
	 */
	public boolean hasInfos() {
		return additionalInfos!=null;
	}
	
	/**
	 * Return the number of additional info for the error.
	 * 
	 * @return The number of additional info for this error
	 */
	public int getInfosSize() {
		if (!hasInfos()) {
			return 0;
		}
		return additionalInfos.size();
	}
	
	/**
	 * Return the additional infos for this error
	 * 
	 * @return A collection with the additional informations of this error
	 *         null if there are no additional info defined
	 */
	public Collection<AdditionalInfo> getAddInfos() {
		if (!hasInfos()) {
			return null;
		}
		
		return additionalInfos;
	}
	
	/**
	 * Acknowledge the error (i.e. the user saw the error)
	 */
	public void acknowledge() {
		acknowledged=true;
	}
	
	/**
	 * Return true if the user acknowledged the error (i.e. if the user
	 * has seen the error)
	 * 
	 * @return true if the user acknowledged the error
	 */
	public boolean hasBeenAcknowledged() {
		return acknowledged;
	}
	
	/**
	 * Override Object.equals().
	 * 2 ErrorInfo objects are equals if their short and detailed descriptions are
	 * equals.
	 * 
	 * @see Object.equals()
	 */
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		}
		if (!(obj instanceof ErrorInfo)) {
			return super.equals(obj);
		}
		ErrorInfo ei = (ErrorInfo)obj;
		return shortDescription.equals(ei.shortDescription) && detailedDescription.equals(ei.detailedDescription);
	}
	
	/**
	 * Update the time stamp of the error.
	 */
	private void updateTimestamps() {
		Date date = new Date();
		timestamp=date.getTime();
		StringBuffer dateSB = new StringBuffer();
		FieldPosition pos = new FieldPosition(0);
		dateFormat.format(date,dateSB,pos);
		timestampStr=dateSB.toString();
	}
	
	public void addInstance() {
		instances++;
		updateTimestamps();
	}

	/**
	 * 
	 * @return The timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * 
	 * @return A string with the time stamp
	 */
	public String getTimestampStr() {
		return timestampStr;
	}

	/**
	 * 
	 * @return The number of errors of the same type
	 */
	public long getInstances() {
		return instances;
	}
	
}
