/**
 * 
 */
package alma.control.gui.antennamount.metrology;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.utils.GUIConstants;
import alma.control.gui.antennamount.utils.bit.LongBit;

/**
 * The widget for one bit of the metrology mode.
 * <P>
 * The widget:
 * <UL>
 * 	<LI>shows the actual state of the bit
 * 	<LI>allows to change the state by means of a checkbox
 * </UL>
 *  
 * @author acaproni
 *
 */
public class ModeSwitchWidget extends JPanel {
	
	/**
	 * The value of the bit
	 */
	private Boolean value=null;
	
	/**
	 * The bit index of the value byte of this metrology mode.
	 * <P>
	 * <code>bit</code> is an integer in [0..7]
	 */
	public final int bitNum;
	
	/**
	 * The byte index of the value byte of this metrology mode.
	 * <P>
	 * <code>bit</code> is an integer in [0..3]
	 */
	public final int byteNum;
	
	/**
	 * The label with the actual state.
	 * <P>
	 * The label is composed of the colored dot plus a description text 
	 */
	private final JLabel actualStateLbl = new JLabel();
	
	/**
	 * To change the state of this bit
	 */
	private final JCheckBox stateCB = new JCheckBox();
	
	/**
	 * The image shown when the bit is set
	 */
	private static final ImageIcon setImg=new ImageIcon(ModeSwitchWidget.class.getResource(GUIConstants.resourceFolder+"green.png")); 
	
	/**
	 * The image shown when the bit is unset or unknown
	 */
	private static final ImageIcon unsetImg=new ImageIcon(ModeSwitchWidget.class.getResource(GUIConstants.resourceFolder+"grey.png"));
	
	/**
	 * Constructor
	 * 
	 * @param description The description of the bit
	 * @param pos The index of the bit of the metrology mode byte
	 * @param byteNum The  number of the bit that owns this bit
	 * @param interactive <code>true</code> if the user can apply changes
	 */
	public ModeSwitchWidget(String description, int byteNum, int pos, boolean interactive) {
		if (pos<0 || pos>7) {
			throw new IllegalArgumentException("Invalid bit index "+pos+" not in [0..7]");
		}
		if (byteNum<0 || byteNum>3) {
			throw new IllegalArgumentException("Invalid byte index "+pos+" not in [0..3]");
		}
		if (description==null || description.isEmpty()) {
			throw new IllegalArgumentException("Invalid bit description");
		}
		this.byteNum=byteNum;
		bitNum=pos;
		initialize(description,interactive);
	}
	
	/**
	 * Init the panel
	 * 
	 * @param The description of the bit
	 * @param interactive <code>true</code> if the user can apply changes
	 */
	private void initialize(String desc,boolean interactive) {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		actualStateLbl.setText(desc);
		if (interactive) {
			add(stateCB);
		}
		add(actualStateLbl);
		refresh();
	}
	
	/**
	 * Set the value
	 * <P>
	 * The check box is set to be equal to the state (set/unset) of the metroogy bit
	 */
	public void setValue(Boolean val) {
		value=val;
		refresh();
	}
	
	/**
	 * Refresh the actual state of the widget
	 */
	public void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (value==null) {
					actualStateLbl.setIcon(unsetImg);
					return;
				} else {
					if (value) {
						actualStateLbl.setIcon(setImg);
					} else {
						actualStateLbl.setIcon(unsetImg);
					}
				}
			}
		});
	}

	/**
	 * @return the value of this bit read from the Mount
	 */
	public Boolean getValue() {
		return value;
	}
	
	/**
	 * Return he value that the user want to apply for this bit
	 * 
	 * @return the state of the CB
	 * @return
	 */
	public boolean getUserDesiredValue() {
		return stateCB.isSelected();
	}
}
