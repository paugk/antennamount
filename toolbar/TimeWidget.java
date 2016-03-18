package alma.control.gui.antennamount.toolbar;

import java.awt.Color;

import javax.swing.BorderFactory;

import alma.common.gui.components.astrotime.AstroTimeDisplay;

public class TimeWidget extends AstroTimeDisplay implements  IToolbarWidget {
	
	/**
	 * Constructor.
	 * 
	 * @param value The value (read from the component) to show in the widget 
	 * @param description The description of the info shown by the widget
	 * @param hasIcon true if an icon must be shown on the left side of the widget
	 * @param rootP The AntennaRootPane
	 */
	public TimeWidget() {
		super();
		setBorder(BorderFactory.createLineBorder(Color.black));
		refresh();
	}

	/**
	 * 
	 * @see IToolbarWidget
	 */
	public void refreshIcon() {} // Does nothing

	/**
	 * @see IToolbarWidget
	 */
	public void enableWidget(boolean enabled) {	} // Does nothing
}
