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

import java.util.List;
import java.util.logging.Logger;

import org.omg.CORBA.Object;

import alma.acs.component.ComponentQueryDescriptor;

import alma.Control.Antenna;
import alma.Control.AntennaHelper;
import alma.Control.MountControllerHelper;
import alma.Control.Mount;
import alma.Control.MountAEM;
import alma.Control.MountAEMHelper;
import alma.Control.MountHelper;
import alma.Control.MountVertex;
import alma.Control.MountVertexLLama;
import alma.Control.MountVertexHelper;
import alma.Control.MountVertexLLamaHelper;
import alma.Control.MountACA;
import alma.Control.MountACAHelper;
import alma.Control.MountA7M;
import alma.Control.MountA7MHelper;
import alma.acs.component.ComponentDescriptor;
import alma.acs.container.ContainerServices;
import alma.acs.container.ContainerServices.ComponentListener;
import alma.acs.exceptions.AcsJException;
import alma.acs.logging.AcsLogLevel;

import alma.Control.AntModeControllerPackage.Status;
import alma.ControlGUIErrType.wrappers.AcsJErrorConnectingControllerEx;
import alma.ControlGUIErrType.wrappers.AcsJErrorGettingAntennaEx;
import alma.ControlGUIErrType.wrappers.AcsJErrorGettingMountEx;
import alma.ControlGUIErrType.wrappers.AcsJErrorInitingControllerEx;
import alma.ControlGUIErrType.wrappers.AcsJErrorReleasingComponentEx;
import alma.ControlGUIErrType.wrappers.AcsJGotANullCORBAReferenceErrorEx;
import alma.ControlGUIErrType.wrappers.AcsJInvalidControllerNameEx;
import alma.ControlGUIErrType.wrappers.AcsJInvalidMountNameEx;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.ControlGUIErrType.wrappers.AcsJWrongAntennaTypeEx;
import alma.JavaContainerError.wrappers.AcsJContainerServicesEx;

/**
 * A class that connects and holds the Mount and the MountController
 * 
 * @author acaproni
 *
 */
public class ACSComponentsManager implements ComponentListener {
	
	/**
	 * The IDL of the mount controller needed to get it as a dynamic component
	 */
	public static final String MOUNTCONTROLLER_IDL="IDL:alma/Control/MountController:1.0";
	
	// The types of the mounts
	public static enum AntennaType {
		UNKNOWN ("Unknown"),
		VERTEX("Vertex"),
		VERTEX_LLAMA("VertexLLama"),
		ALCATEL("AEM"),
		MELCO("ACA"),
        MELCOA7M("ACA 7m");
		
		/**
		 * Construct/or 
		 * 
		 * @param desc The description of the antenna type
		 */
		AntennaType(String desc) {
			description=desc;
		}
		
		/**
		 * The description of the antenna (to be shown in the GUI)
		 */
		public final String description;
		
		/**
		 * Override  toString method to return the user readable
		 * description of the antenna
		 * 
		 * @return The description of this antenna type
		 */
		@Override
		public String toString() {
			return description;
		}
		
		
	}
	
	/**
	 * The ContainerServices
	 */
	private ContainerServices contSvc;
	
	/**
	 * The logger
	 */
	private Logger logger;
	
	/**
	 * The Mount component
	 */
	private Mount mount;
	
	/**
	 * The name of the mount
	 */
	private String mountName;
	
	/**
	 * The type of the connected mount 
	 */
	private AntennaType mountType=AntennaType.UNKNOWN;
	
	// We instantiate a variable per each possible antenna 
	// type but only one of them will have a not null value
	//
	// While checking the type of the antenna we need to narrow
	// the mount to each possible IDL type
	// It is good if we store the type that narrowed well
	// to avoid repeating the operation several times
	
	/**
	 * The MountVertex
	 */
	private MountVertex vertex=null;
	/**
	 * The MountVertexLLama
	 */
	private MountVertexLLama vertexLLama=null;
	
	/**
	 * The MountAEM
	 */
    private MountAEM alcatel=null;
    
    /**
	 * The MountACA
	 */
    private MountACA aca=null;

         /**
         * The MountA7M
         */
        private MountA7M a7m=null;	
	/**
	 * The MountController component
	 */
	private alma.Control.MountController controller;
	
	/**
	 * The mount controller name
	 */
	private String controllerName;
	
	/**
	 * The name of the Antenna  (for example DV01) 
	 */
	private String antennaName;
	
	/**
	 * The name of the antenna component (for example CONTROL/DV01) 
	 */
	private String antennaComponentName;
	
	/**
	 * The Antenna component used to get the name of the NewMountController
	 * to connect to.
	 * It is not <code>null</code> ONLY when getting the name of the controller
	 */
	private Antenna antenna;
	
	public ACSComponentsManager(ContainerServices cs) {
		if (cs==null) {
			throw new IllegalArgumentException("Invalid null ContainerServices in constructor");
		}
		contSvc=cs;
		logger=contSvc.getLogger();
		if (logger==null) {
			throw new IllegalStateException("The logger from ContainerServices is null");
		}
	}
	
	
	/**
	 * Connects the Mount and the Controller for the antenna with the
	 * given name.
	 * 
	 * If the connection fails an exception is thrown.
	 * The method connects both the components or fails. 
	 * 
	 * @param antennaPath The name of the antenna the Mount and the MountController
	 *                    belong to.
	 *                    The string is like CONTROL/DV01 where the real
	 *                    name of the antenna is the last part of the parameter
	 * @throws AcsJException In case of error connecting the components
	 */
	public void connectComponents(String antennaPath) throws AcsJException {
		if (antennaPath==null || antennaPath.length()==0) {
			throw new IllegalArgumentException("The name of the antenna is invalid");
		}
		
		// Get the name of the MountComponent implement the untController from the Antenna
		antennaComponentName=antennaPath;
		getAntenna(antennaComponentName);
		try {
			logger.log(AcsLogLevel.DEBUG,"Getting the mount controller name from the antenna");
			controllerName=antenna.getMountControllerName();
			if (controllerName==null || controllerName.length()==0) {
				throw new Exception("Invalid NewMountController name received from "+antennaComponentName);
			}
		} catch (Throwable t) {
			AcsJInvalidControllerNameEx ex = new AcsJInvalidControllerNameEx(t);
			throw ex;
		}
		antenna=null;
		
		// Get the name of the antenna, for example ALMA001
		String[] temp = antennaComponentName.split("/");
		antennaName=temp[temp.length-1];
		
		// Get the MountController
		connectController(controllerName);
		if (controller.getStatus()!=Status.ALLOCATED) {
			initializeController(antennaName);
		}
		connectMount();
		// Check the type of the mount
		checkMountType();
		
		contSvc.registerComponentListener(this);
	}
	
	/**
	 * Get the antenna component of the given name
	 * 
	 * @param name The name of the Antenna component
	 * 
	 * @throws AcsJErrorGettingAntennaEx In case of error getting the antenna
	 */
	private void getAntenna(String name) throws AcsJErrorGettingAntennaEx {
		logger.log(AcsLogLevel.DEBUG,"Getting antenna "+name);
		Object obj = null;
		try { 
			obj=getCORBAComponent(name,false);
			antenna=AntennaHelper.narrow(obj);
			if (antenna==null) {
				throw new Exception("narrow returned null");
			}
		} catch (Throwable t) {
			AcsJErrorGettingAntennaEx ex = new AcsJErrorGettingAntennaEx(t);
			ex.setAntennaName(name);
			throw ex;
		}
	}
	
	/** 
	 * Initialize the controller in such a way it uses the 
	 * mount with the given name
	 * 
	 * @param antName The name of the antenna
	 * 
	 * @throws AcsJErrorInitingControllerEx In case of error initializing the Controler
	 */
	private void initializeController(String antName) throws AcsJErrorInitingControllerEx {
		if (controller==null) {
			throw new IllegalStateException("Try to initialize a null controller ");
		}
		logger.log(AcsLogLevel.DEBUG,"Initilizing the mount controller");
		// The mount controller need to e initialized
		try {
			controller.allocate(antName);
			logger.log(AcsLogLevel.DEBUG,"Mount controller for antenna "+antName+" initialized");
		} catch (Throwable t) {
			AcsJErrorInitingControllerEx ex = new AcsJErrorInitingControllerEx(t);
			ex.setAntennaName(antName);
			throw ex;
		}
		logger.log(AcsLogLevel.DEBUG,"Controller initialized for antenna "+antName);
	}

	/**
	 * Getter
	 *  
	 * @return The Mount
	 */
	public Mount getMount() {
		return mount;
	}

	/**
	 * Getter
	 *  
	 * @return The NewMountController
	 */
	public alma.Control.MountController getController() {
		return controller;
	}
	
	/**
	 * Connect the NewMountController dynamic component
	 * 
	 * @param name The name of the mount controller dynamic component
	 *             to connect
	 *             
	 * @throws AcsJErrorConnecttingControllerEx if the connection fails
	 * @throws AcsJMountGUIErrorEx If the controller name is wrong
	 */
	private void connectController(String name) 
	throws AcsJMountGUIErrorEx, AcsJErrorConnectingControllerEx {
		if (name==null || name.length()==0) {
			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx();
			ex.setContextDescription("Inavalid mount controller name "+name);
			throw ex;
		}
		logger.log(AcsLogLevel.DEBUG,"Connecting to MountController "+name);
		ComponentQueryDescriptor descriptor = new ComponentQueryDescriptor(name, MOUNTCONTROLLER_IDL);
	
		Object obj;
		
		try {
			obj=contSvc.getCollocatedComponent(descriptor, false, antennaComponentName);
			if (obj==null) {
				throw new Exception("getDynamicComponent returned a null CORBA Object");
			}
		} catch (Throwable t) {
			AcsJErrorConnectingControllerEx ex = new AcsJErrorConnectingControllerEx(t);
			ex.setControllername(name);
			throw ex;
		}
		// Narrow the CORBA obj to a Mount
		try {
			controller=MountControllerHelper.narrow(obj);
			if (controller==null) {
				throw new Exception("narrow returned a null controller");
			}
		} catch (Throwable t) {
			controller=null;
			AcsJErrorConnectingControllerEx ex = new AcsJErrorConnectingControllerEx(t);
			ex.setControllername(name);
			throw ex;
		}
		
		controllerName=controller.name();
		logger.log(AcsLogLevel.INFO,"MountController "+controllerName+" connected");
		if (!checkAllocatedAntenna()) {
			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx();
			ex.setContextDescription("The allocated antenna is not as expected");
			throw ex;
		}
	}
	
	/**
	 * Connect the mount of the NewMountController.
	 * Get the name of the mount out of the controller
	 * 
	 * This method must be called after connecting the NewMountController
	 * 
	 * @throws AcsJMountGUIErrorEx if the controller is still <code>null</code>
	 * @throws AcsJInvalidMountNameEx If the controller returned an invalid mount name 
	 * @throws AcsJErrorGettingMountEx In case of error getting the mount component
	 */
	private void connectMount() throws AcsJMountGUIErrorEx, AcsJInvalidMountNameEx, AcsJErrorGettingMountEx {
		if (controller==null) {
			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx();
			ex.setContextDescription("Controller not yet connected");
			throw ex;
		}
		// Get the name of the mount from the controller
		String name=controller.getMount();
		if (name==null || name.length()==0) {
			throw new AcsJInvalidMountNameEx();
		}
		logger.log(AcsLogLevel.DEBUG,"Connecting to mount "+mount);
		
		Object obj = null;
		try {
			obj=getCORBAComponent(name,true);
		} catch (Throwable t) {
			AcsJErrorGettingMountEx ex = new AcsJErrorGettingMountEx(t);
			ex.setMountName(name);
			throw ex;
		}
		// Narrow the CORBA obj to a Mount
		try {
			mount=MountHelper.narrow(obj);
			if (mount==null) {
				throw new Exception("narrow returned a null Mount");
			}
		} catch (Throwable t) {
			AcsJErrorGettingMountEx ex = new AcsJErrorGettingMountEx(t);
			ex.setMountName(name);
			mount=null;
			mountName=null;
			obj=null;
			throw ex;
		}
		mountName=mount.name();
		logger.log(AcsLogLevel.INFO,"ACS component "+mountName+" connected");
	}
	
	/**
	 * Get a CORBA component with the given name
	 * 
	 * @param compName The name of the ACS component to get
	 * @param noSticky true if the component must be no sticky
	 * @return A non null CORBA reference to the component
	 * 
	 * @throws AcsJContainerServicesEx when an error happens getting the component
	 * @throws AcsJException In case the returned component is <code>null</code>
	 */
	private Object getCORBAComponent(String compName, boolean noSticky) throws AcsJContainerServicesEx, AcsJGotANullCORBAReferenceErrorEx {
		logger.log(AcsLogLevel.DEBUG,"Getting "+compName);
		Object	obj;
		if (noSticky) {
			obj=contSvc.getComponent(compName);
		} else {
			obj=contSvc.getComponentNonSticky(compName);
		}
		if (obj==null) {
			throw new AcsJGotANullCORBAReferenceErrorEx();
		}
		logger.log(AcsLogLevel.DEBUG,"CORBA obj "+compName+" connected");
		return obj;
	}
	
	/**
	 * Release the components and all the resources.
	 * 
	 * This method is the last one to execute.
	 */
	public void close() {
		vertex=null;
		alcatel=null;
		aca=null;
                a7m=null; 

		mount=null;
		antenna=null; // Already released
		try {
			releaseComponent(mountName);
		} catch (AcsJException e) {
			logger.log(AcsLogLevel.ERROR,"Error releasing mount "+mountName,e);
		} catch (Throwable t) {
			logger.log(AcsLogLevel.ERROR,"Error releasing mount "+mountName,t);
		}
		controller=null;
		try {
			releaseComponent(controllerName);
		} catch (AcsJException e) {
			logger.log(AcsLogLevel.ERROR,"Error releasing controller "+controllerName,e);
		} catch (Throwable t) {
			logger.log(AcsLogLevel.ERROR,"Error releasing controller "+controllerName,t);
		}
		contSvc=null;
		mountType=AntennaType.UNKNOWN;
	}
	
	/**
	 * Release the component with the given name.
	 * In case of error, it logs a message without failing.
	 * 
	 * @param name The name of the component to release
	 * 
	 * @throws AcsJErrorReleasingComponentEx In case of an error releasing the component
	 */
	private void releaseComponent(String name) throws AcsJErrorReleasingComponentEx {
		if (name==null || name.length()==0) {
			// Nothing to do.
			//
			// It can happen that the close is called before getting
			// all the resources. In that case name can be null.
			return;
		}
		logger.log(AcsLogLevel.DEBUG,"Releasing "+name);
		try {
			contSvc.releaseComponent(name);
			logger.log(AcsLogLevel.DEBUG,name+" released");
		} catch (Throwable t) {
			AcsJErrorReleasingComponentEx ex =new AcsJErrorReleasingComponentEx(t);
			ex.setComponentName(name);
			throw ex;
		}
	}
	
	/**
	 * Check if the antenna allocated by this component is what we expect
	 * 
	 * TODO: implement this method
	 * 
	 * @return If the allocated antenna is right
	 */
	private boolean checkAllocatedAntenna() {
		return true;
	}
	
	/**
	 * Return the type of the mount
	 * 
	 * @return The type of mount
	 */
	public AntennaType getMountType() {
		return mountType;
	}
	
	/**
	 * Check the type of the mount by trying to narrow
	 * it to different IDL types.
	 */
	private void checkMountType() {
		vertex=null;
		alcatel=null;
        aca=null; //20090827MT
        a7m=null; //20090827MT
		try {
			vertex=MountVertexHelper.narrow(mount);
			mountType=AntennaType.VERTEX;
			return;
		} catch (Throwable t) {}
		try {
			vertexLLama = MountVertexLLamaHelper.narrow(mount);
			mountType=AntennaType.VERTEX_LLAMA;
			return;
		} catch (Exception ex) {}
		try {
			alcatel=MountAEMHelper.narrow(mount);
			mountType=AntennaType.ALCATEL;
			return;
		} catch (Throwable t) {}
		try {
		        aca=MountACAHelper.narrow(mount);
		        mountType=AntennaType.MELCO;
		        return;
                } catch (Throwable t) {}
                try {
                        a7m=MountA7MHelper.narrow(mount);
                        mountType=AntennaType.MELCOA7M;
                        return;
                } catch (Throwable t) {}


		mountType=AntennaType.UNKNOWN;
	}

	/**
	 * Return a MountVertex mount.
	 * 
	 * If the antenna is not a Vertex throws an exception
	 * 
	 * @return The mount component narrowed to a MountVertex
	 * @throws AcsJWrongAntennaTypeEx If the mount is not of the requested type
	 */
	public MountVertex getVertex() throws AcsJWrongAntennaTypeEx {
		if (mountType!=AntennaType.VERTEX) {
			AcsJWrongAntennaTypeEx ex=new AcsJWrongAntennaTypeEx();
			ex.setExpected(AntennaType.VERTEX.description);
			ex.setFound(mountType.description);
			throw ex;
		}
		return vertex;
	}
	/**
	 * Return a MountVertexLLama mount.
	 * 
	 * If the antenna is not a VertexLLama throws an exception
	 * 
	 * @return The mount component narrowed to a MountVertexLLama
	 * @throws AcsJWrongAntennaTypeEx If the mount is not of the requested type
	 */
	public MountVertexLLama getVertexLLama() throws AcsJWrongAntennaTypeEx {
		if (mountType!=AntennaType.VERTEX_LLAMA) {
			AcsJWrongAntennaTypeEx ex=new AcsJWrongAntennaTypeEx();
			ex.setExpected(AntennaType.VERTEX.description);
			ex.setFound(mountType.description);
			throw ex;
		}
		return vertexLLama;
	}
	
	/**
	 * Return a MountAlcatelProduction mount.
	 * 
	 * If the antenna is not a Alcatel throws an exception
	 * 
	 * @return The mount component narrowed to a Mount Alcatel
	 * @throws AcsJWrongAntennaTypeEx If the mount is not of the requested type
	 */
	public MountAEM getAlcatel() throws AcsJWrongAntennaTypeEx {
		if (mountType!=AntennaType.ALCATEL) {
			AcsJWrongAntennaTypeEx ex=new AcsJWrongAntennaTypeEx();
			ex.setExpected(AntennaType.ALCATEL.description);
			ex.setFound(mountType.description);
			throw ex;
		}
		return alcatel;
	}

        /**
         * Return a MountACA mount.
         * 
         * If the antenna is not a ACA throws an exception
         * 
         * @return The mount component narrowed to a Mount ACA
         * @throws AcsJWrongAntennaTypeEx If the mount is not of the requested type
         */
        public MountACA getACA() throws AcsJWrongAntennaTypeEx {
        	if (mountType!=AntennaType.MELCO) {
        		AcsJWrongAntennaTypeEx ex=new AcsJWrongAntennaTypeEx();
    			ex.setExpected(AntennaType.MELCO.description);
    			ex.setFound(mountType.description);
    			throw ex;
        	}
            return aca;
        }

        /**
         * Return a MountA7M mount.
         *
         * If the antenna is not a A7M throws an exception
         *
         * @return The mount component narrowed to a Mount A7M
         * @throws AcsJWrongAntennaTypeEx If the mount is not of the requested type
         */
        public MountA7M getA7M() throws AcsJWrongAntennaTypeEx {
                if (mountType!=AntennaType.MELCOA7M) {
                        AcsJWrongAntennaTypeEx ex=new AcsJWrongAntennaTypeEx();
                        ex.setExpected(AntennaType.MELCOA7M.description);
                        ex.setFound(mountType.description);
                        throw ex;
                }
            return a7m;
        }
	
	/**
	 * @return the description of the mount type of the 
	 *         connected component
	 */
	public String getTypeDescription() {
		return mountType.toString();
	}
	
	/**
	 * @see ComponentListener
	 */
	public boolean includeForeignComponents() {
		return false;
	}
	
	/**
	 * @see ComponentListener
	 */
	public void componentsAvailable(List<ComponentDescriptor> comps) {}
	
	/**
	 * @see ComponentListener
	 */
	public void componentsUnavailable(List<String> compNames) {
		// Called when at least one of the connected components becomes unavailable
	}
}
