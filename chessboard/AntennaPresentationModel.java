package alma.control.gui.antennamount.chessboard;

import alma.Control.gui.hardwaredevice.common.ControlChessboardPresentationModel;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.acs.logging.AcsLogLevel;
import alma.common.gui.chessboard.DefaultChessboardStatus;
import alma.exec.extension.subsystemplugin.PluginContainerServices;

public class AntennaPresentationModel extends
		ControlChessboardPresentationModel {
	
	// The objects that get antenna states from the master
	private AntennaChecker checker;
	
	public AntennaPresentationModel(PluginContainerServices svc) {
		super(svc);
		checker = new AntennaChecker(svc);
	}
	
	/**
	 * Release the resources
	 * 
	 * This is the last method called
	 */
	public void close() {
		checker.close();
		checker=null;
	}

	@Override
	protected DefaultChessboardStatus getStatusForCell(String antenna) {
		if (checker==null) {
			return DefaultChessboardStatus.ANTENNA_NOT_INSTALLED;
		}
		int state = checker.getState(antenna);
		switch (state) {
		case AntennaChecker.STATE_NORMAL: return DefaultChessboardStatus.NORMAL;
		case AntennaChecker.STATE_SHUTDOWN: return DefaultChessboardStatus.ANTENNA_OFFLINE;
		case AntennaChecker.STATE_ERROR: return DefaultChessboardStatus.SEVERE_ERROR;
		case AntennaChecker.STATE_WARNING: return DefaultChessboardStatus.WARNING;
		default: return DefaultChessboardStatus.ANTENNA_OFFLINE; // Not possible 
		}
	}

}
