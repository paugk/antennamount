package alma.control.gui.antennamount.offset;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.mount.MountController;

/**
 * Panel for setting offsets.
 * 
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class OffsetPanel extends JPanel implements ActionListener {

	private static final String resetAzStr = "Reset Hr";
	private static final String resetRAStr = "Reset RA";
	private static final String resetElStr = "Reset Ve";
	private static final String resetDecStr = "Reset Dec";
	
	// The MountController
	private MountController controller=null;
	
	// control values, all in radians
	private boolean coordinatesEquatorial=true;
	private JToggleButton eqBtn = new JToggleButton("Equatorial");
	private JToggleButton hrBtn = new JToggleButton("Horizontal");
	private ButtonGroup btnGroup = new ButtonGroup();
	
	private OffsetButtonsPanel buttonsPnl = new OffsetButtonsPanel();
	
	// The buttons to reset offsets
	private JButton resetAzRABtn = new JButton(resetAzStr);
	private JButton resetElDecBtn = new JButton(resetElStr);
	private JButton resetBothBtn = new JButton("Reset both");
	
	public OffsetPanel() throws HeadlessException {
		super();
		initialize();
		enableWidgets(controller!=null);
	}

	private void initialize() {
		setLayout(new BorderLayout());
		// Add the buttons to reset offsets in the upper side of the panel
		JPanel pnl= new JPanel();
		pnl.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnl.add(resetAzRABtn);
		pnl.add(resetElDecBtn);
		pnl.add(resetBothBtn);
		resetBothBtn.setToolTipText("Reset offset in both axis");
		add(pnl,BorderLayout.NORTH);
		
		// Button Hr/Eq coordinates
		btnGroup.add(hrBtn);
		btnGroup.add(eqBtn);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		
		FlowLayout buttonLayout = new FlowLayout(FlowLayout.CENTER);
		JPanel buttonPnl = new JPanel(buttonLayout);
		buttonLayout.setHgap(20);
		buttonPnl.add(hrBtn);
		buttonPnl.add(eqBtn);
		hrBtn.addActionListener(this);
		eqBtn.addActionListener(this);
		hrBtn.doClick();
		
		mainPanel.add(buttonPnl);
		
		mainPanel.add(buttonsPnl);
		
		add (mainPanel,BorderLayout.CENTER);
		
		// Connect the buttons to th listener
		resetAzRABtn.addActionListener(this);
		resetBothBtn.addActionListener(this);
		resetElDecBtn.addActionListener(this);
	}

	/**
	 * @param ctrl The new MountController
	 */
	public void setController(MountController ctrl) {
		controller = ctrl;
		buttonsPnl.setController(ctrl);
		enableWidgets(ctrl!=null);
	}
	
	/**
	 * @return Returns the coordinatesEquatorial.
	 */
	public boolean isCoordinatesEquatorial() {
		return coordinatesEquatorial;
	}
	
	/**
	 * Enable disable widgets
	 */
	public void enableWidgets(final boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				eqBtn.setEnabled(enabled);
				hrBtn.setEnabled(enabled);
				buttonsPnl.enableWidgets(enabled);
				resetAzRABtn.setEnabled(enabled);
				resetBothBtn.setEnabled(enabled);
				resetElDecBtn.setEnabled(enabled);	
			}
		});
	}
	
	/**
	 * Release the resources and close the panel
	 *
	 */
	public void close() {
		setController(null);
	}
	
	/**
	 * @see ActionListener
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==hrBtn) {
			coordinatesEquatorial=false;
			buttonsPnl.setCoordinatesEquatorial(false);
			resetAzRABtn.setText(resetAzStr);
			resetElDecBtn.setText(resetElStr);
		} else if (e.getSource()==eqBtn) {
			buttonsPnl.setCoordinatesEquatorial(true);
			coordinatesEquatorial=true;
			resetAzRABtn.setText(resetRAStr);
			resetElDecBtn.setText(resetDecStr);
		} else if (e.getSource()==resetAzRABtn) {
			if (coordinatesEquatorial) {
				zeroRAOffset();
			} else {
				zeroAzOffset();
			}
		} else if (e.getSource()==resetElDecBtn) {
			if (coordinatesEquatorial) {
				zeroDecOffset();
			} else {
				zeroElOffset();
			}
		} else if (e.getSource()==resetBothBtn) {
			if (coordinatesEquatorial) {
				zeroRaDecOffsets();
			} else {
				zeroAzElOffsets();
			}
		} else {
			System.err.println("Unknown event from "+e);
		}
	}
	
	/**
	 * Set the azimuth offset to 0
	 *
	 */
	private void zeroAzOffset() {
		if (controller==null) {
			return;
		}
		Double elOffset=controller.getOffsetEl().getValue();
		controller.offsetAzEl(0,elOffset);
	}
	
	/**
	 * Set the elevation offset to 0
	 *
	 */
	private void zeroElOffset() {
		if (controller==null) {
			return;
		}
		Double azOffset=controller.getOffsetAz().getValue();
		controller.offsetAzEl(azOffset,0);
	}
	
	/**
	 * Set the RA offset to 0
	 *
	 */
	private void zeroRAOffset() {
		if (controller==null) {
			return;
		}
		Double decOffset=controller.getOffsetDec().getValue();
		controller.offsetEquatorial(0,decOffset);
	}
	
	/**
	 * Set the declination offset to 0
	 *
	 */
	private void zeroDecOffset() {
		if (controller==null) {
			return;
		}
		Double raOffset=controller.getOffsetRA().getValue();
		controller.offsetEquatorial(raOffset,0);
	}
	
	/**
	 * Set AZ/EL offsets to 0
	 *
	 */
	private void zeroAzElOffsets() {
		controller.offsetAzEl(0, 0);
	}
	
	/**
	 * Set RA/DEC offsets to 0
	 *
	 */
	private void zeroRaDecOffsets() {
		controller.offsetEquatorial(0, 0);
	}
	
}
