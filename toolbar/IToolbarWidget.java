package alma.control.gui.antennamount.toolbar;

public interface IToolbarWidget {

	/**
	 * Refresh the value and icon displayed by the widget.
	 */
	public void refresh();
	
	/**
	 * Refresh the icon in the label
	 */
	public void refreshIcon();
	
	/**
	 * Enable/disable the widget
	 * 
	 * @param enabled If true, enable the widget
	 */
	public void enableWidget(boolean enabled);
}
