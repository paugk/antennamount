/**
 * 
 */
package alma.control.gui.antennamount.metrology;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

/**
 * The panel to set and read the metrology mode for each type of 
 * antenna.
 * <P>
 * This is accomplished by adding and removing components depending on the
 * type of the connected antenna.
 * 
 * @author acaproni
 *
 */
public class MetrologyModePnl extends JPanel implements ActionListener {
	
	/**
	 * The widget for each bit of the metrology model
	 */
	private ModeSwitchWidget[] modeWidgets;
	
	/**
	 * The button to apply the changes
	 */
	protected final JButton applyBtn = new JButton("Apply");
	
	/**
	 * The button to reset the metrology (only shown for AEM antennae)
	 */
	protected final JButton resetBtn = new JButton("Reset");
	
	/**
	 * <code>true</code> if the user can apply changes
	 */
	protected final boolean interactive;
	
	/**
	 * The metrology
	 */
	protected final IMetrology metrology;
	
	/**
	 * The layout for the panels.
	 * <P>
	 * It contains
	 * <UL>
	 * 	<LI>a panel with the label shown when the mount is not connected
	 * 	<LI>one panel for each type of antenna
	 * </UL>
	 * The layout associate a name to each panel that is the same of the name of the antenna.
	 * For tha panel with the label the name is "Label"
	 */
	private final CardLayout layout = new CardLayout();
	
	/**
	 * Constructor
	 * 
	 * @param interactive <code>true</code> if the user can apply changes
	 * @param metrology The metrology to apply the metrology mode
	 */
	public MetrologyModePnl(boolean interactive, IMetrology metrology, AntennaType type) {
		if (metrology==null) {
			throw new IllegalArgumentException("The metrology can't be null");
		}
		if (type==null) {
			throw new IllegalArgumentException("The antenna type can't be null");
		}
		this.interactive=interactive;
		this.metrology=metrology;
		buildWidgets(type);
		initialize(type);
	}
	
	/**
	 * Build the widgets depending on the type of the connected antenna
	 * 
	 * @param type The type of the connected antenna
	 */
	private void buildWidgets(AntennaType type) {
		switch (type) {
		case ALCATEL: {
			modeWidgets= new ModeSwitchWidget[]{
					new ModeSwitchWidget("Standard pointing model enabled",0,0,interactive),
					new ModeSwitchWidget("Tiltmeter compensation enabled",0,1,interactive),
					new ModeSwitchWidget("Temperature compensation enabled",0,2,interactive),
					new ModeSwitchWidget("Metrology correction enabled",0,3,interactive),
					new ModeSwitchWidget("Automatic sub-reflector position correction enabled",0,5,interactive),
					new ModeSwitchWidget("Encoder mount displacement sensor correction enabled",0,6,interactive)
				};
			break;
		}
		case MELCO: {
			modeWidgets= new ModeSwitchWidget[]{
					new ModeSwitchWidget("Bearing",0,0,interactive),
					new ModeSwitchWidget("Base",0,1,interactive),
					new ModeSwitchWidget("Yoke",0,2,interactive),
					new ModeSwitchWidget("Reflector",0,3,interactive),
					new ModeSwitchWidget("Reflector-EL",0,4,interactive),
					new ModeSwitchWidget("Reflector thermal",0,5,interactive),
					new ModeSwitchWidget("Tiltmeter",0,6,interactive),
					new ModeSwitchWidget("Acceleration cancel",0,7,interactive),
					new ModeSwitchWidget("Base AZ",1,0,interactive),
					new ModeSwitchWidget("Yoke AZ",1,1,interactive),
				};
			break;
		}
		case VERTEX: {
			modeWidgets= new ModeSwitchWidget[]{
					new ModeSwitchWidget("Standard pointing model enabled",0,0,interactive),
					new ModeSwitchWidget("Tiltmeter compensation enabled",0,1,interactive),
					new ModeSwitchWidget("Automatic sub-ref. pos. correction enabled",0,5,interactive),
					new ModeSwitchWidget("Correction on encoder mount displacement sensors correction enabled",0,6,interactive),
				};
			break;
		}
		case MELCOA7M: {
			modeWidgets= new ModeSwitchWidget[]{
					new ModeSwitchWidget("Bearing",0,0,interactive),
					new ModeSwitchWidget("Base",0,1,interactive),
					new ModeSwitchWidget("Yoke",0,2,interactive),
					new ModeSwitchWidget("Reflector",0,3,interactive),
					new ModeSwitchWidget("Reflector-EL",0,4,interactive),
					new ModeSwitchWidget("Reflector thermal",0,5,interactive),
					new ModeSwitchWidget("Tiltmeter",0,6,interactive),
					new ModeSwitchWidget("Acceleration cancel",0,7,interactive),
					new ModeSwitchWidget("Base AZ",1,0,interactive),
					new ModeSwitchWidget("Yoke AZ",1,1,interactive),
				};
			break;
		}
		}
	}
	
	/**
	 * Initialize the panel by adding the widgets
	 */
	protected void initialize(AntennaType type) {
		setBorder(BorderFactory.createTitledBorder("Metrology mode"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (ModeSwitchWidget wdgt: modeWidgets) {
			add(wdgt);
		}
		if (interactive) {
			JPanel btnPnl = new JPanel(new BorderLayout());
			btnPnl.add(applyBtn,BorderLayout.WEST);
			applyBtn.addActionListener(this);
			if (type==AntennaType.ALCATEL) {
				btnPnl.add(resetBtn,BorderLayout.EAST);
				resetBtn.addActionListener(this);
			}
			add(btnPnl);
			
		}
	}
	
	/**
	 * Refresh the value of the mode bits
	 * 
	 * @param val The new value (can be <code>null</code>)
	 */
	public void refresh(ValueHolder<LongArrayBit> val) {
		for (ModeSwitchWidget wdgt: modeWidgets) {
			if (val==null ||val.getValue()==null) {
				wdgt.setValue(null);
				continue;
			} 
			LongArrayBit lab = val.getValue();
			wdgt.setValue(lab.getBit(wdgt.byteNum, wdgt.bitNum));
		}
	}
	
	/**
	 * build the bytes of the metrology mode
	 * from the widgets
	 * 
	 * @param mode The bytes of the metrology mode
	 * @param reset <code>true</code> if a reset is also requested 
	 * 				(used only by AEM and always set to <code>false</code>
	 * 				for other antenna types) 
	 */
	private void bitsFromWidgets(int[] mode, boolean reset) {
		for (int t=0; t<mode.length; t++) {
			mode[t]=0;
		}
		for (ModeSwitchWidget mWdgt: modeWidgets) {
			if (mWdgt.getUserDesiredValue()) {
				mode[mWdgt.byteNum]=mode[mWdgt.byteNum]+(int)Math.pow(2,mWdgt.bitNum);
			}
		}
		if (reset) {
			mode[0]=mode[0]+(int)Math.pow(2,7);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==applyBtn) {
			int[] mode = new int[4];
			for (int t=0; t<mode.length; t++) {
				mode[t]=0;
			}
			for (ModeSwitchWidget mWdgt: modeWidgets) {
				if (mWdgt.getUserDesiredValue()) {
					mode[mWdgt.byteNum]=mode[mWdgt.byteNum]+(int)Math.pow(2,mWdgt.bitNum);
				}
			}
			try {
				metrology.setMetrMode(mode);
			} catch (Throwable t) {
				// An error setting the metrology
			}
		}  else if (e.getSource()==resetBtn) {
			int[] mode = new int[4];
			for (int t=0; t<mode.length; t++) {
				mode[t]=0;
			}
			for (ModeSwitchWidget mWdgt: modeWidgets) {
				if (mWdgt.getValue()) {
					mode[mWdgt.byteNum]=mode[mWdgt.byteNum]+(int)Math.pow(2,mWdgt.bitNum);
				}
			}
			mode[0]=mode[0]+(int)Math.pow(2,7);
			try {
				metrology.setMetrMode(mode);
			} catch (Throwable t) {
				// An error setting the metrology
			}
		} else {
			System.err.println("Unknown source of event: "+e.getSource());
		}	
	}
	
}
