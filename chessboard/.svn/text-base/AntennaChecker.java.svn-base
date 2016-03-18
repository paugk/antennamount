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
package alma.control.gui.antennamount.chessboard;

import java.util.HashMap;
import java.util.logging.Logger;

import org.omg.CORBA.Object;

import alma.Control.Antenna;
import alma.Control.AntennaHelper;
import alma.Control.AntennaState;
import alma.Control.AntennaStateEvent;
import alma.Control.AntennaSubstate;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.acs.container.ContainerServices;
import alma.acs.logging.AcsLogLevel;

/**
 * The class to get the states of the antennas.
 * It connects to all the antennas and read their status.
 * 
 * The state of each Antenna is stored in a HashMap having the name of the 
 * antenna as key.
 * When the user ask for the state of the antenna the object 
 * return the state if it is in the HashMap (and not too old),
 * otherwise the remote object is queried.
 * 
 * @author acaproni
 *
 */
public class AntennaChecker {
	// The situation of an antenna
	public class AntennaSituation {
		// The name of the ACS Antenna component
		public String componentName; 
		// A reference to ACS component
		public Antenna antenna;
		// The state of the antenna
		public AntennaStateEvent state;
		// When the state has been updated
		public long timestamp;
	}
	
	// The error state calculated by reading the state and substate reuned by
	// the master.
	// These will be used to show the cells with the right state/color in the 
 	// chessboard
	
	/**
	 * In the shutdown mode the antenna is not usable.
	 * The components do not connect to the antenna.
	 * 
	 * This is returned if the state of the antenna is
	 *  - Inaccessible
	 *  - Shutdown
	 */
	public static final int STATE_SHUTDOWN=0;
	
	/**
	 * The antenna is working properly.
	 * 
	 * This is returned if the state of the antenna is
	 * operationanl and the substate is no error
	 */
	public static final int STATE_NORMAL=1;
	
	/**
	 * Something in the antenna is not working properly
	 * 
	 * This is returned when the substate is NoError and the substate
	 * is degraded
	 */
	public static final int STATE_WARNING=2;
	
	/**
	 * There is an error in the antenna:
	 * 
	 * This is returned when the state substate is error and the state
	 * is operational or degraded 
	 */
	public static final int STATE_ERROR=3;
	
	// ContainerServices
	private ContainerServices contSvc;
	
	// The states of the antenna read from the Master
	//
	// It is refreshed only it is too old
	private HashMap<String, AntennaSituation> antennasStates=new HashMap<String, AntennaSituation>();
	private final int EVENTS_TIME=5000; // msec time to refresh
	
	// The logger
	private Logger logger;
	
	/**
	 * Constructor 
	 * 
	 * @param services ContainerServices
	 */
	public AntennaChecker(ContainerServices services) {
		if (services==null) {
			throw new IllegalArgumentException("Invalid null ContainerServices");
		}
		contSvc=services;
		logger=contSvc.getLogger();
	}
	
	/**
	 * Return the anme of an antenna from a String
	 * For example if the param is CONTRL/ALMA001 or ALMA001, the name of the
	 * antenna is ALMA001
	 * 
	 * @param name The name of the antenna
	 * @return A purified name of the antenna
	 */
	private String getAntennaName(String name) {
		if (name.startsWith("CONTROL/")) {
			String[] tmp = name.split("/");
			return tmp[tmp.length-1];
		} else {
			return name;
		}
	}
	
	/**
	 * Get the antenna component and update the AntennaSituation object.
	 * If the connection failed an error is logged and the reference set 
	 * to null;
	 * 
	 * @param situation The Hashmap entry for the antenna
	 * @param name The name of the antenna
	 */
	private void getAntenna(AntennaSituation situation, String name) {
		String antennaName=getAntennaName(name);
		
		// Get the component
		String antennaComponentName="CONTROL/"+antennaName;
		Object obj;
		logger.log(AcsLogLevel.DEBUG,"Getting "+antennaComponentName);
		try {
			obj=contSvc.getComponent(antennaComponentName);
		} catch (Throwable t) {
			situation.antenna=null;
			logger.log(AcsLogLevel.ERROR,"Error getting "+antennaComponentName+": "+t.getMessage());
			return;
		}
		try {
			situation.antenna=AntennaHelper.narrow(obj);
			situation.componentName=antennaComponentName;
		} catch (Throwable t) {
			situation.antenna=null;
			logger.log(AcsLogLevel.ERROR,"Error narrowing "+antennaComponentName+": "+t.getMessage());
			return;
		}
	}
	
	/**
	 * Read the state of the antenna from the component if the local copy
	 * is missing or too old
	 * 
	 * @param name The name of the antenna
	 * @return The state of the antenna
	 *         null if it is not possible to get the state of the antenna 
	 */
	private AntennaStateEvent getAntennaState(String name) {
		String antennaName= getAntennaName(name);
		AntennaSituation situation = get(name);
		if (situation==null) {
			// The antenna does not exist in the HashMap: create a new entry
			situation=new AntennaSituation();
			situation.componentName=null;
			situation.state=null;
			situation.timestamp=0;
			situation.antenna=null;
		}
		if (situation.antenna==null) {
			getAntenna(situation,name);
			if (situation.antenna==null) {
				// It can happen only if there was an error getting the component
				logger.log(AcsLogLevel.WARNING,"State info for "+name+" not found");
				put(antennaName,situation);
				return null;
			}
		}
		if (situation.state==null || situation.timestamp+EVENTS_TIME<System.currentTimeMillis()) {
			// Need to read the value from the component
			try {
				situation.state=situation.antenna.getAntennaState();
			} catch (Throwable t) {
				situation.state=null;
				situation.timestamp=0;
				put(antennaName,situation);
				logger.log(AcsLogLevel.ERROR,"Error getting the state of "+situation.componentName);
				return null;
			}
			situation.timestamp=System.currentTimeMillis();
		}
		put(antennaName,situation);
		return situation.state;
	}
	
	
	/**
	 * Return the state of the antenna calculated by its state and substate read 
	 * from the master.
	 * 
	 * Note that this state is not the state returned by the MASTER by 
	 * the calculated state represented by one of the STATE constants
	 * 
	 * @param name The name of the antenna
	 * @return The calculated state of the antenna
	 */
	public int getState(String name) {
		if (name==null || name.length()==0) {
			throw new IllegalArgumentException("Invalid antenna name");
		}
		AntennaStateEvent evt = getAntennaState(name);
		if (evt==null) {
			return STATE_SHUTDOWN;
		}
		
		AntennaState state = evt.newState;
		AntennaSubstate subState=evt.newSubstate;
		if (state==null || state==AntennaState.AntennaInaccessable || state==AntennaState.AntennaShutdown) {
			return STATE_SHUTDOWN;
		}
		if (subState==AntennaSubstate.AntennaError) {
			return STATE_ERROR;
		}
		if (state==AntennaState.AntennaOperational) {
			return STATE_NORMAL;
		}
		// state==AntennaState.AntennaDegraded
		return STATE_WARNING;
	}
	
	/**
	 * Release the master and all the resources
	 * 
	 * TODO There is a possible race here if the component is released
	 *      when the thread is reading values from it
	 */
	public void close() {
		for (String key: antennasStates.keySet()) {
			AntennaSituation sit=get(key);
			if (sit!=null && sit.antenna!=null && sit.componentName!=null) {
				try {
					contSvc.releaseComponent(sit.componentName);
				} catch (Throwable t) {
					logger.log(AcsLogLevel.ERROR,"Error releasing "+sit.componentName+": "+t.getMessage());
				}
			}
		}
		antennasStates.clear();
	}
	
	/**
	 * Put the description of the antenna state into the HashMap.
	 * It is here to avoid synchronizing in any place 
	 * 
	 * @param antennaName The name of the antenna
	 * @param situation The state of the antenna
	 */
	private void put(String antennaName, AntennaSituation situation) {
		if (antennaName==null || antennaName.length()==0) {
			throw new IllegalArgumentException("Invalid antenna name");
		}
		if (situation==null) {
			throw new IllegalArgumentException("Invalid null AntennaSituation");
		}
		String name=getAntennaName(antennaName);
		synchronized(antennasStates) {
			antennasStates.put(name,situation);
		}
	}
	
	/**
	 * Get the description of the state of the given antenna
	 * 
	 * @param antennaName The name of the antenna
	 * @return The state of the antenna
	 *         Can be null
	 */
	private AntennaSituation get(String antennaName) {
		if (antennaName==null || antennaName.length()==0) {
			throw new IllegalArgumentException("Invalid antenna name");
		}
		String name=getAntennaName(antennaName);
		synchronized(antennasStates) {
			return antennasStates.get(name);
		}
	}
}
