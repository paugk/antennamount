package alma.control.gui.antennamount.utils;

import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * A class to convert a randians angle into hours/minutes/seconds
 * 
 * @author acaproni
 *
 */
public class HMSAngleConverter implements AngleConverter {
	
	static final char[] HOURS_SEPARATORS = new char[] {':',':','.' };
	
	// The angle in radians (can be null)
	private ValueHolder<Double> value=null;
		
	/**
	 * The string to format the d/m/s
	 */
	public final static String formatString = "%02d"+HOURS_SEPARATORS[0]+"%02d"+HOURS_SEPARATORS[1]+"%05.2f";
		
	public HMSAngleConverter(ValueHolder<Double> val) {
		if (val==null) {
			throw new IllegalArgumentException("Invalid null radians value");
		}
		value=val;
	}
	
	public String getString() {
		if (value==null || value.getValue()==null) {
			return "";
		}
		return rad2hms();
	}
	
	private String rad2hms() {
		double rad = value.getValue();
		boolean negative=false;
		if (rad<0) {
			negative=true;
			rad = Math.abs(rad);
		}
		
		double hr, min, sec;
		hr=min=sec=0.;
		
		// hr
		hr = Math.abs(Math.floor(rad / (Math.PI/12)));
		
		// Min
		double temp = rad/(15*Math.PI/10800)-(60*hr);
		min = (temp>0) ? Math.floor(temp) : Math.ceil(temp);
		
		// Sec
		sec= rad/(15*Math.PI/648000)-(min*60)-(hr*3600);
		sec = Math.floor(sec*100.0)/100.0;
		if (negative) {
			return "-"+String.format(formatString, (int)hr, (int)min, sec);
		} else {
			return "+"+String.format(formatString, (int)hr, (int)min, sec);
		}
	}
}
