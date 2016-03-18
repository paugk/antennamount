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
package alma.control.gui.antennamount;

import java.util.Vector;

import javax.swing.ImageIcon;

import alma.control.gui.antennamount.mount.MountConnectionListener;

/**
 * This shows the right icons and tooltips listening events
 * from the notifiers.
 * 
 * 
 * @author acaproni
 *
 */
public class VisibleConnectionStatus implements MountConnectionListener {

	/**
	 * The possible states of the connection.
	 * 
	 * @author acaproni
	 *
	 */
	public enum ConnectionStatus {
		// Both components are connected
		CONNECTED("/alma/control/gui/antennamount/resources/console-connected.png"),
		
		// Both components disconnected
		DISCONNECTED("/alma/control/gui/antennamount/resources/console-disconnected.png"),
		
		// At least one component is connected But not all the components are connected
		// This might happen during initialization for example
		PARTIALLY_CONNECTED("/alma/control/gui/antennamount/resources/console-connecting.png");
		
		// The icon representing the status of the connection
		public final ImageIcon icon;
		
		/**
		 * Constructor
		 * 
		 * @param imgPath The path to the icon representing the status of the connection
		 */
		private ConnectionStatus(String imgPath) {
			icon=new ImageIcon(this.getClass().getResource(imgPath));
		}
	}
	
	// The icon to show in case of delay getting the state of the connection
	private ImageIcon delay=new ImageIcon(this.getClass().getResource("/alma/control/gui/antennamount/resources/console-delay.png"));
	
	/**
	 *  The icon to show in case of a transient error (reuse the same icon shown by ConnectionStatus.PARTIALLY_CONNECTED)
	 */
	private ImageIcon transientError = ConnectionStatus.PARTIALLY_CONNECTED.icon;
	
	// The connected components, like the Mount and the MountController, 
	// identified by their names
	// Every time a component connects and is not yet in the list, it is added
	// to the Vector.
	// Whenever a component disconnects, its name ir removed from the vector.
	private Vector<String> components = new Vector<String>();
	
	// The names of all the components that connects at a certain point
	// we need that to know if some component that should be connected
	// has disappeared.
	private Vector<String> compHistory = new Vector<String>();
	
	/**
	 * The status line where the icons will be shown
	 */
	private StatusLine statusLine;
	
	/**
	 * Constructor
	 * 
	 * @param line The status line where the icons will be shown 
	 * @param compLostAction The listener for the component lost event
	 */
	public VisibleConnectionStatus(StatusLine line) {
		if (line==null) {
			throw new IllegalArgumentException("Invalid null StatusLine in constructor");
		}
		statusLine=line;
		statusLine.setStatusIcon(ConnectionStatus.DISCONNECTED.icon,"Disconnected");
	}
	
	/**
	 * @see MountConnectionListenr
	 *
	 */
	public void componentUnreliable(String name) {
		synchronized (components) {
			components.remove(name);
		}
		statusLine.setStatusIcon(checkConnection().icon,builtTooltip());
	}
	
	 /**
	  * @see MountConnectionListenr
	  *
	  */
	public void componentTransientError() {
		statusLine.setStatusIcon(transientError,"Transient error");
	}
	
	 /**
	  * @see MountConnectionListenr
	  *
	  */
	public void componentResponseTimeSlow() {
		statusLine.setStatusIcon(delay,"Components response time slow");
	}
	
	/**
	 * @see MountConnectionListenr
	 *
	 */
	public void componentResponseTimeOk() {
		statusLine.setStatusIcon(checkConnection().icon,builtTooltip());
	}
	
	/**
	 * @see MountConnectionListenr
	 *
	 */
	public void componentConnected(String name) {
		System.out.println("Connected "+name);
		synchronized (components) {
			if (!components.contains(name)) {
				components.add(name);
			}
		}
		synchronized (compHistory) {
			if (!compHistory.contains(name)) {
				compHistory.add(name);
			}
		}
		
		statusLine.setStatusIcon(checkConnection().icon,builtTooltip());
	}
	
	/**
	 * Check if all the components are connected or disconnected
	 * 
	 * @return @return The situation of the connection of the components
	 *         (connected, disconnected, partially connected)
	 */
	private ConnectionStatus checkConnection() {
		if (compHistory.size()==components.size() && components.size()>0) {
			return ConnectionStatus.CONNECTED;
		}
		if (compHistory.size()>=components.size() && components.size()==0) {
			return ConnectionStatus.DISCONNECTED;
		}
		if (compHistory.size()>components.size() && components.size()>0) {
			return ConnectionStatus.PARTIALLY_CONNECTED;
		}
		throw new IllegalStateException(
				"Invalid: components.size="+components.size()+", history.size="+compHistory.size());
	}
	
	/**
	 * Build the tool tip knowing the connected and disconnected components
	 * 
	 * 
	 */
	private String builtTooltip() {
		ConnectionStatus status=checkConnection();
		if (status==ConnectionStatus.CONNECTED) {
			return "Connected";
		} 
		if (status==ConnectionStatus.DISCONNECTED) {
			return "Disconnected";
		}
		StringBuilder buf = new StringBuilder("<HTML>");
		boolean first=true;
		synchronized (compHistory) {
			for (String historyCmp: compHistory) {
				if (!first) {
					buf.append("<BR>");
				} else {
					first=false;
				}
				synchronized (components) {
					if (components.indexOf(historyCmp)!=-1) {
						// The component is in the history
						// and in the list of running components
						synchronized (buf) {
							buf.append(historyCmp+" Ok");
						}
					} else {
						// The component is only in the history
						synchronized (buf) {
							buf.append(historyCmp+" NOP");
						}
					}
				}
			}
		}
		return buf.toString();
	}
	
	/**
	 * Dump the status of the object
	 */
	private void dump() {
		synchronized (compHistory) {
			System.out.println("History components: ");
			for (String c: compHistory) {
				System.out.println(c);
			}
		}
		synchronized (components) {
			System.out.println("Running components: ");
			for (String c: components) {
				System.out.println(c);
			}
			System.out.println("Status= "+checkConnection()+", tootip="+builtTooltip());
		}
	}
	
	/**
	 * @see MountConnectionListener
	 */
	public void componentDown(String name) {
		synchronized (components) {
			components.remove(name);
		}
		statusLine.setStatusIcon(checkConnection().icon,builtTooltip());
	}

	/**
	 * Executed whan a component is released
	 * 
	 * @see MountConnectionListener
	 */
	@Override
	public void componentDisconnected(String name) {
		System.out.println("Disconnected "+name);
		synchronized (components) {
			components.remove(name);
		}
		synchronized (compHistory) {
			compHistory.remove(name);
		}
		statusLine.setStatusIcon(checkConnection().icon,builtTooltip());
	}
}
 