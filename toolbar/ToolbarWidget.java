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
package alma.control.gui.antennamount.toolbar;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.utils.GUIConstants;

/**
 * The interface for the widgets in the toolbar.
 * 
 * @author acaproni
 *
 */
public abstract class ToolbarWidget extends JPanel implements IToolbarWidget {
	
	public enum WidgetIcon {
		OK("green.png"),
		NORMAL("blue.png"),
		WARNING("orange.png"),
		LIGHT_WARNING("yellow.png"),
		ERROR("red.png"),
		UNKNOWN("grey.png");
		
		/**
		 * Constructor
		 * 
		 * @param iconURL The path/URL of the icon for this state
		 */
		private WidgetIcon(String iconURL) {
			icon=new ImageIcon(this.getClass().getResource(GUIConstants.resourceFolder+iconURL));
		}
		
		/**
		 * The icon
		 */
		public final ImageIcon icon;
	}
	
	/**
	 * The description to show in the widget
	 */
	protected final String description;
	
	/**
	 * <code>true</code> if the widget shows an icon at the left side 
	 */
	protected final boolean hasIcon;
	
	/**
	 * The label showing the text and the icon in the widget
	 */
	protected final JLabel label = new JLabel();
	
	/**
	 * The AntennaRootPane to add errors and status messages
	 */
	protected AntennaRootPane antennaRootP;
	
	/**
	 * Constructor
	 * 
	 * @param description The description of the info shown by the widget
	 * @param hasIcon <code>true</code> if the widget shows an icon at the left side
	 * @param rootP The AntennaRootPane
	 */
	public ToolbarWidget(String description, boolean hasIcon, AntennaRootPane rootP) {
		if (description==null || description.length()==0) {
			throw new IllegalArgumentException("Invalid description");
		}
		if (rootP==null) {
			throw new IllegalArgumentException("Invalid null AntennaRootPane");
		}
		antennaRootP=rootP;
		this.description=description+": ";
		this.hasIcon=hasIcon;
		initialize();
	}
	
	/**
	 * Initialize the widget
	 */
	private void initialize() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		Font font = label.getFont();
		Font reducedFont = font.deriveFont(6);
		label.setFont(reducedFont);
		add(label);
	}
	
	/**
	 * Enable/disable the widget
	 * 
	 * @param enabled If true, enable the widget
	 */
	public void enableWidget(final boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				label.setEnabled(enabled);
			}
		});
	}
}
