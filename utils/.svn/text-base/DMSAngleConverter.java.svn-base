package alma.control.gui.antennamount.utils;

import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * A class to convert a randians angle into degrees/minutes/seconds
 * 
 * @author acaproni
 *
 */
public class DMSAngleConverter implements AngleConverter {
	
	/**
	 * The separators
	 */
	static final char[] DEGREES_SEPARATORS = new char[] {0xb0, '\'', '.' };
	
	/**
	 * The string to format the d/m/s
	 */
	public final static String formatString = "%03d"+DEGREES_SEPARATORS[0]+"%02d"+DEGREES_SEPARATORS[1]+"%05.2f";
	
	/**
	 * A struct holding degrees, minutes and seconds
	 *
	 */
	private class DMS {
		public boolean neg; // true mins <0
		public double deg;
		public double min;
		public double sec;
		/**
		 * Constructor
		 * @param n true mins <0
		 * @param d Degrees
		 * @param m Minutes
		 * @param s Second
		 */
		public DMS(boolean n, double d, double m, double s) {
			neg=n;
			deg=d;
			min=m;
			sec=s;
		}
		
	}
	
	// The angle in radians (can be null)
	private ValueHolder<Double> value=null;
	
	public DMSAngleConverter(ValueHolder<Double> val) {
		if (val==null) {
			throw new IllegalArgumentException("Invalid null radians value");
		}
		value=val;
	}
	
	public String getString() {
		if (value==null || value.getValue()==null) {
			return "";
		}
		DMS dms = rad2dms();
		String str=String.format(DMSAngleConverter.formatString, (int)dms.deg, (int)dms.min, dms.sec);
		if (dms.neg) {
			return "-"+str;
		}
		return "+"+str;
	}
	
	/**
	 * Convert the radians in DMS
	 * 
	 * @return A double representing the angle in DMS
	 */
	private DMS rad2dms()
	{
		if (value==null || value.getValue()==null) {
			throw new IllegalStateException("The value is null: no radians angle to convert");
		}
		double f = 1.0;
		double rad=value.getValue();
		if (rad < 0.0) {
			rad = Math.abs(rad);
			f = -1.0;
		}

		double deg = Math.toDegrees(rad);

		double dd = Math.floor(deg);
		double mm = Math.floor((deg % 1.0 ) * 60.0);
		double ss = (((deg % 1.0 ) * 60.0 ) % 1.0) * 60.0;
		ss=Math.round(ss*100.0);
		ss=ss/100;
		
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
		
		return new DMS(f<0,dd,mm,ss);

	}

}
