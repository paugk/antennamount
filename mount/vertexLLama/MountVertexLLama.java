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
package alma.control.gui.antennamount.mount.vertexLLama;

import org.omg.CORBA.LongHolder;

import alma.acs.container.ContainerServices;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ACSComponentsManager;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.MetrologyCommon;
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.mount.ISubreflector;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * Objects of this class holds a MountVertexLLama component
 * and execute the methods on the remote object 
 * 
 * @author acaproni
 *
 */
public final class MountVertexLLama extends Mount {
	
	// ACU error descriptions stuffs
	// To be replaced when the code generator will be used
	private static final String[] acuStates = {
/*		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_00,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_01,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_02,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_03,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_04,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_05,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_06,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_07,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_08,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_09,
		alma.Control.MountVertexLLamaPrototype.ACU_ERROR_DESC_0A */
	};
	
	
	/**
	 * The Vertex prototype mount
	 */
	private final alma.Control.MountVertexLLama vertex;
	
	/**
	 * The state of the mount
	 */
	private ValueHolder<int[]> status = new ValueHolder<int[]>();
	
	/**
	 * The status of EL
	 */
	private ValueHolder<int[]> azStatus = new ValueHolder<int[]>();
	
	/**
	 * The status of EL
	 */
	private ValueHolder<int[]> elStatus = new ValueHolder<int[]>();
	
	/**
	 * The power status
	 */
	private ValueHolder<int[]> powerStatus = new ValueHolder<int[]>();
	
	/**
	 * The shutter
	 */
	private final Shutter shutter;
	
	/**
	 * The subreflector
	 */
	private VertexSubreflector subreflector;
	
	/**
	 * The metrology
	 */
	private Metrology metrology;
	
	/** 
	 * Constructor
	 * 
	 * @param mount The remote component
	 * @param type The type of this mount component
	 * @param contSvcs The ContainerServices
	 * @param rootP The AntennaRootPane
	 */
	public MountVertexLLama(
			alma.Control.MountVertexLLama mnt, 
			ACSComponentsManager.AntennaType type, 
			ContainerServices contSvcs,
			AntennaRootPane rootP) {
		super(mnt,type,contSvcs,rootP);
		if (type!=ACSComponentsManager.AntennaType.VERTEX_LLAMA) {
			throw new IllegalStateException("The component is a VertexLLama but the passed type is "+type);
		}
		for (Integer t=0; t<acuStates.length; t++) {
			acuStateDescriptor.put(t, acuStates[t]);
		}
		vertex=mnt;
		shutter = new Shutter(vertex,listenersNotifier,logger);
		subreflector = new VertexSubreflector(mnt,antennaRootP,logger);
		metrology = new Metrology(vertex, logger,listenersNotifier);
		
		// Start the thread to update the state of the mount
		setName("MountVertexLLama");
		rootP.getHeartbeatChecker().register(this);
		start();
	}
		
	/**
	 * Update the status of the component. Called by the thread
	 * 
	 * @param errState The error state of execution
	 */
	public void update(UpdateError errState) throws Exception {
		antennaRootP.getHeartbeatChecker().ping(this);
		// Used to check if the component is slow answering
		long preReadTime;
		
		// System status
		LongHolder time=new LongHolder();
		
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=vertex.GET_SYSTEM_STATUS(time);
			status.setValue(vals, time.value);
		} catch (Throwable t) {
			status.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// EL status
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=vertex.GET_EL_STATUS(time);
			elStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			elStatus.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// AZ status
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=vertex.GET_AZ_STATUS(time);
			azStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			azStatus.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Shutter
		preReadTime = System.currentTimeMillis();
		try {
			Integer status=vertex.GET_SHUTTER(time);
			shutter.updateStatus(status, time.value);
		} catch (Throwable t) {
			shutter.updateStatus(null,0L);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Power status
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=vertex.GET_POWER_STATUS(time);
			powerStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			powerStatus.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}

		// Subreflector
		subreflector.refresh(errState);
		
		// Metrology
		metrology.refresh(errState);
	}
	
	public ValueHolder<int[]> getStatus() {
		return status;
	}
	
	public ValueHolder<int[]> getAzStatus() {
		return azStatus;
	}

	public ValueHolder<int[]> getElStatus() {
		return elStatus;
	}
	
	public ShutterCommon getShutter() {
		return shutter;
	}

	public ISubreflector getSubreflector() {
		return subreflector;
	}
	
	/**
	 * Return the metrology
	 */
	public IMetrology getMetrology() {
		return metrology;
	}
	
	/**
	 * @see Mount#getPowerStatus()
	 */
	public ValueHolder<int[]> getPowerStatus() {
		return powerStatus;
	}
}
