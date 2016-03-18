package alma.control.gui.antennamount.chessboard;

import alma.Control.Common.Util;
import alma.acs.exceptions.AcsJException;
import alma.common.gui.chessboard.ChessboardDetailsPluginFactory;
import alma.common.gui.chessboard.ChessboardPlugin;
import alma.common.gui.chessboard.ChessboardStatusProvider;

public class DartbordPlugin extends ChessboardPlugin {

	@Override
	protected ChessboardDetailsPluginFactory getDetailsProvider() {
		detailsProvider = new DartboardDetailsProvider();
		return detailsProvider;
	}

	@Override
	protected ChessboardStatusProvider getStatusProvider() {
		// TODO Auto-generated method stub
		try {
			statusProvider = new AntennaPresentationModel(pluginContainerServices);
		} catch(Throwable t) {
			if (t instanceof AcsJException) {
				Util.getNiceErrorTraceString(((AcsJException)t).getErrorTrace());
			} else {
				System.err.println("Exception caught: "+t.getMessage());
				t.printStackTrace(System.err);
			}
			// TODO - Alessandro to do something here
		}
		return statusProvider;
	}
}
