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

import alma.control.gui.antennamount.mount.MountCommom.UpdateError;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

/**
 * <code>ISubreflector</code> interface represents the subreflector returned by different types of antennas.
 * <P>
 * This interface decouples the real implementation of the subreflector in the different
 * antennas allowing to show/change the values in the subreflector panel.
 * 
 * @author acaproni
 *
 */
public interface ISubreflector {
	
	/**
	 * The X, Y and Z coordinates in meters
	 * 
	 * @author acaproni
	 *
	 */
	public class Coordinates {
		
		/**
		 * Constructor
		 * 
		 * @param x The value of coordinate X in meter
		 * @param y The value of coordinate Y in meter
		 * @param z The value of coordinate Z in meter
		 */
		public Coordinates (double x, double y, double z) {
			this.x=Double.valueOf(x);
			this.y=Double.valueOf(y);
			this.z=Double.valueOf(z);
		}
		
		/**
		 * Generate a human readable representation of this object
		 * 
		 * @return A string representation of this Coordinates
		 */
		public String toString() {
			return getClass().getName()+" [X, Y, Z]=["+x+", "+y+", "+z+"]";
		}
		
		/**
		 * Compare this Coordinates to that passed as parameter.
		 * The 2 instances are equal if the are the same instance 
		 * (same reference) or if the x,y,z coordinates are equal.
		 * 
		 * @param c The Coordinates to compare with this object
		 * @return true if this the two Coordinates are equal
		 */
		@Override
		public boolean equals(Object c) {
			if (c==null) {
				return false;
			}
			if (c==this) {
				return true;
			}
			if (!(c instanceof ISubreflector.Coordinates)) {
				return super.equals(c);
			}
			ISubreflector.Coordinates temp=(ISubreflector.Coordinates)c;
			return this.x.equals(temp.x) && this.y.equals(temp.y) && this.z.equals(temp.z);
		}
		
		/**
		 * X coordinate in meter
		 */
		public final Double x;
		
		/**
		 * Y coordinate in meter
		 */
		public final Double y;
		
		/**
		 * Z coordinate in meter
		 */
		public final Double z;
		
		/**
		 * 
		 * @return An array of double representing the X, Y, Z in mm 
		 * 		(instead of meters)
		 */
		public double[] toMMCoordinate() {
			double ret[] = new double[3];
			ret[0]=x*1000;
			ret[1]=y*1000;
			ret[2]=z*1000;
			return ret;
		}
		
		/**
		 * Build a <code>Coordinates</code> object from the coordinates
		 * represented as millimeters.
		 * 
		 * @param mmX X coordinate in mm
		 * @param mmY Y coordinate in mm
		 * @param mmZ Z coordinate in mm
		 * @return
		 */
		public static Coordinates fromMM(double mmX, double mmY, double mmZ) {
			return new Coordinates(mmX/1000, mmY/1000, mmZ/1000);
		}
	}
	
	/**
	 * Return the coordinates of the absolute position (m)
	 * 
	 * @return The absolute position
	 */
	public ValueHolder<Coordinates> getAbsPosition();
	
	/**
	 * Return the coordinates of the delta position (m)
	 * 
	 * @return The delta position
	 */
	public ValueHolder<Coordinates> getDeltaPosition();
	
	/**
	 * Return the bytes representing the status of the limit switches
	 * 
	 * @return The bytes showing the state of the limits switches 
	 */
	public ValueHolder<LongArrayBit> getLimits();
	
	/**
	 * Return the bytes representing the status of the subreflector
	 * 
	 * @return The bytes showing the state of the subreflector
	 */
	public ValueHolder<LongArrayBit> getStatus();
	
	/**
	 * Set the absolute position to the given coordinates (m)
	 * 
	 * @param pos The desired absolute position
	 */
	public void setAbsPosition(Coordinates pos);
	
	/**
	 * Set the delta position to the given coordinates (m)
	 * 
	 * @param pos The desired delta position
	 */
	public void setDeltaPosition(Coordinates delta);
	
	/**
	 * Reset the subreflector: set the subreflector to the absolute position
	 * and clear the delta
	 */
	public void zeroDelta();
	
	/**
	 * Initialize the subreflector
	 */
	public void init();
	
	/**
	 * Get the actual rotation (0.001 deg)
	 * 
	 * @return The actual rotation
	 */
	public ValueHolder<Coordinates> getRotation();
	
	/**
	 * Set the rotation (0.001 deg)
	 * 
	 * @param tip tip angle
	 * @param tilt tilt angle
	 * @param rotation rotation angle
	 */
	public void setRotation(int tip, int tilt, int rotation);
	
	/**
	 * Refresh the values from the component
	 * 
	 * @param errState The errors received from the component
	 */
	public void refresh(UpdateError errState);
}
