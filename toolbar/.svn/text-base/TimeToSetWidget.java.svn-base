package alma.control.gui.antennamount.toolbar;

import java.util.Calendar;
import java.util.Date;

import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * <code>TimeToSetWidget</code> shows the number of seconds before
 * a tracked planet sets.
 * <P>
 * This widget is visible only if:
 * <UL>
 * 	<LI>the telescope is tracking
 * 	<LI>the planet sooner or later will set (circumpolar sources for example never set)
 * </UL>
 * 
 * @author acaproni
 *
 */
public class TimeToSetWidget extends ToolbarWidget {
	
	/**
	 * The timeToSet read from the <code>MountController</code>
	 */
	public ValueHolder<Double> timeToSet=null;
	
	/**
	 * The boolean read from the <code>MountController</code> to know if the
	 * telescope is tracking
	 */
	public ValueHolder<Boolean> onSource=null;

	/**
	 * Constructor
	 * 
	 * @param description The description
	 * @param hasIcon true if an icon appears in the widget
	 * @param rootP The AntennaRootPane
	 */
	public TimeToSetWidget(String description, boolean hasIcon, AntennaRootPane rootP) {
		super(description,hasIcon, rootP);
		refresh();
		refreshIcon();
	}
	
	/**
	 * Set the <code>timeToSet</code> and the <code>OnSource</code> from the <code>MountController</code>
	 * 
	 * @param timeToSet The <code>timeToSet</code> property of <code>MountController</code>
	 * @param onSource The <code>OnSource</code> property of <code>MountController</code>
	 */
	public void setValues(ValueHolder<Double> timeToSet, ValueHolder<Boolean> onSource) {
		this.timeToSet=timeToSet;
		this.onSource=onSource;
	}
	
	@Override
	public void refresh() {
		if (onSource==null || timeToSet==null) {
			setVisible(false);
			return;
		}
		Boolean onS=onSource.getValue();
		Double time=timeToSet.getValue();
		if (onS==null || time==null || onS==Boolean.FALSE) {
			setVisible(false);
			return;
		} 
		setVisible(true);
		String str;
		if (time>=1E30) {
			str="Does not set";
		} else if (time<0) {
			str="Not tracking a source";
		} else {
			long seconds = time.longValue();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(seconds*1000);
			str=String.format("Source sets in %1$02d:%2$02d:%3$02d", 
					cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND));
		}
		setLabelText(str);
	}

	@Override
	public void refreshIcon() {
	}
	
	/**
	 * Set the value of the label in the EDT
	 * 
	 * @param str The string of the label
	 */
	private void setLabelText(final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				label.setText(str);
			}
		});		
	}

}
