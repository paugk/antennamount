/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2011
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
package alma.control.gui.antennamount.axis;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.AxisStatusDefinition;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.aem.MountAEM;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;
import alma.control.gui.antennamount.utils.bit.LongBit;

/**
 * The panel with the buttons to initialize the encoders
 * of the AEM.
 * <P>
 * {@link AemInitEncoderPanel#setComponents(MountController, Mount)} accepts only
 * a <code>null</code> mount or an AEM mount. 
 * 
 * @author acaproni
 *
 */
public class AemInitEncoderPanel extends JPanel implements ActionListener, Runnable {
	
	/**
	 * The state of the encoder
	 * 
	 * @author acaproni
	 *
	 */
	private enum EncoderState {
		INITED,
		NOT_INITED,
		UNKNOWN
	}
	
	/**
	 * The time between 2 refreshes of the state of the encoders
	 */
	private static final int REFRESH_TIME=1500;
	
	/**
	 * The thread to refresh the GUI
	 */
	private volatile Thread thread=null;
	
	/**
	 * Signal the thread to terminate
	 */
	private volatile boolean terminateThread=false;

	/**
	 * The button to init the azimuth encoder of the AEM
	 */
	private final JButton aemInitAzEncBtn=new JButton("Init AZ encoder");
	
	/**
	 * The button to init the elevation encoder of the AEM
	 */
	private final JButton aemInitElEncBtn=new JButton("Init EL encoder");
	
	/**
	 * The mount to read and set axis
	 */
	private Mount mount=null;
	
	/**
	 * The MountController.
	 * It is needed to call the stop()
	 */
	private MountController controller=null;
	
	/**
	 * The AntennaRootPane to add errors and status messages
	 */
	private final AntennaRootPane antennaRootP;
	
	/**
	 * The label with the actual state of the AZ encoder
	 * (initialized, not initialized)
	 */
	private final JLabel azStatusLbl = new JLabel();
	
	/**
	 * The label with the actual state of the EL encoder
	 * (initialized, not initialized)
	 */
	private final JLabel elStatusLbl = new JLabel();
	
	/**
	 * Constructor 
	 * 
	 * @param rootP The AntennaRootPane
	 */
	public AemInitEncoderPanel(AntennaRootPane rootP) {
		super();
		antennaRootP=rootP;
		initialize();
		setComponents(null, null);
		
		aemInitAzEncBtn.addActionListener(this);
		aemInitElEncBtn.addActionListener(this);
	}
	
	/**
	 * Initialize the panel with the buttons to init the
	 * absolute encoders of the AEM.
	 * <P>
	 * This panel is shown only if the connected antenna 
	 * is AEM
	 */
	private void initialize() {
		Box box = new Box(BoxLayout.Y_AXIS);
		
		JPanel azPnl = new  JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel azSetPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		azSetPnl.setBorder(BorderFactory.createTitledBorder("Set"));
		azSetPnl.add(aemInitAzEncBtn);
		JPanel azLblPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		azLblPnl.setBorder(BorderFactory.createTitledBorder("Actual state"));
		azLblPnl.add(azStatusLbl);
		azPnl.add(azSetPnl);
		azPnl.add(azLblPnl);
		
		JPanel elPnl = new  JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel elSetPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		elSetPnl.setBorder(BorderFactory.createTitledBorder("Set"));
		elSetPnl.add(aemInitElEncBtn);
		JPanel elLblPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		elLblPnl.setBorder(BorderFactory.createTitledBorder("Actual state"));
		elLblPnl.add(elStatusLbl);
		elPnl.add(elSetPnl);
		elPnl.add(elLblPnl);
		
		box.add(azPnl);
		box.add(elPnl);
		box.add(Box.createVerticalGlue());
		
		add(box);
	}
	
	/**
	 * Enable/disable the widgets
	 * 
	 * @param enable if true enable the widgets
	 */
	public void enableWidgets(final boolean enable) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				aemInitAzEncBtn.setEnabled(enable);
				aemInitElEncBtn.setEnabled(enable);
				if (!enable) {
					refreshEncState(azStatusLbl,EncoderState.UNKNOWN);
					refreshEncState(elStatusLbl,EncoderState.UNKNOWN);
				}
			}
		});
	}
	
	/**
	 * Set the mount and the mount controller
	 * 
	 * @param ctr The mount controller
	 * @param mnt The mount
	 */
	public synchronized void setComponents(MountController ctr, Mount mnt) {
		if (mnt!=null && mnt.getMountType()!=AntennaType.ALCATEL) {
			throw new IllegalArgumentException("Can't set a component of type "+mnt.getMountType());
		}
		mount=mnt;
		controller=ctr;
		if (mount!=null) {
			terminateThread=false;
			thread = new Thread(this,"AemInitEncoderPanel");
			thread.setDaemon(true);
			antennaRootP.getHeartbeatChecker().register(thread);
			thread.start();
		} else {
			terminateThread=true;
			if (thread!=null) {
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException ie) {}
				antennaRootP.getHeartbeatChecker().unregister(thread);
				thread=null;
			}
		}
		enableWidgets(mount!=null);
	}
	
	/**
	 * Release all the resources and terminate the thread
	 *
	 */
	public void close() {
		setComponents(null,null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==aemInitAzEncBtn) {
			synchronized (this) {
				if (mount!=null) {
					((MountAEM)mount).initAzEncoder();
				}
			}
		} if (e.getSource()==aemInitElEncBtn) {
			synchronized (this) {
				if (mount!=null) {
					((MountAEM)mount).initElEncoder();
				}
			}
		}
	}
	
	/**
	 * Refresh the state of an encoder
	 * 
	 * @param stateLbl The widget to update
	 * @param state The state to set
	 */
	private void refreshEncState(final JLabel stateLbl, final EncoderState state) {
		if (state==null) {
			throw new NullPointerException("Invalid null state");
		}
		if (stateLbl==null) {
			throw new NullPointerException("Invalid null label");
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch (state) {
				case UNKNOWN: {
					stateLbl.setText(AxisStatusDefinition.UNKNOWN.description);
					stateLbl.setIcon(AxisStatusDefinition.UNKNOWN.icon);
					break;
				}
				case INITED: {
					stateLbl.setText("Initialized");
					stateLbl.setIcon(AxisStatusDefinition.ENCODER.icon);
					break;
				}
				case NOT_INITED: {
					stateLbl.setText("NOT initialized");
					stateLbl.setIcon(AxisStatusDefinition.ERROR.icon);
					break;
				}
				}		
			}
		});
	}

	@Override
	public void run() {
		while (!terminateThread) {
			antennaRootP.getHeartbeatChecker().ping(thread);
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				continue;
			}
			synchronized (this) {
				if (mount==null) {
					continue;
				}
				ValueHolder<int[]> azStatus=mount.getAzStatus();
				Long longAzBits = new Long(azStatus.getValue()[4]);
				LongBit azBits= new LongBit(longAzBits);
				if (azBits.getBit(1)) {
					refreshEncState(azStatusLbl, EncoderState.NOT_INITED);
				} else {
					refreshEncState(azStatusLbl, EncoderState.INITED);
				}
				ValueHolder<int[]> elStatus=mount.getElStatus();
				Long longElBits = new Long(elStatus.getValue()[4]);
				LongBit elBits= new LongBit(longElBits);
				if (elBits.getBit(1)) {
					refreshEncState(elStatusLbl, EncoderState.NOT_INITED);
				} else {
					refreshEncState(elStatusLbl, EncoderState.INITED);
				}
			}
		}
	}
}
