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
package alma.control.gui.antennamount.statuspanel;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;
import alma.control.gui.antennamount.metrology.MetrologyPanel;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.subreflectorpanel.SubreflectorPanel;
import alma.control.gui.antennamount.tracking.CommonStatusTable;
import alma.control.gui.antennamount.utils.ValueDisplayer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * The panel for the antenna status i.e with this panel the user 
 * can't apply changes to the mount.
 * 
 * @author acaproni
 *
 */
public class AntennaTrackingPanel extends JPanel {
	
    /**
     *  The label for the type of the antenna (AEC/VA)
     */
    private JLabel antTypeLbl = new JLabel(ValueDisplayer.NOT_AVAILABLE);
    
    // The status of the antenna
    // The bytes returned by the method
    //
    // TODO: show the bits near to a more meaningful label
    private JTextField statusTF = new JTextField("N/A",40);
    
    /**
     *  The table with the detailed status of the antenna
     */
    private CommonStatusTable statusTable;
    
    /**
     *  The subreflector panel
     */
    private SubreflectorPanel subreflectorPnl;
    
    /**
     * The metrology panel
     */
    private MetrologyPanel metrologyPnl;
    
    /**
     * The mount
     */
    private Mount mount=null;
    
    /**
     *  The pane with the tabs
     */
    private ErrorTabbedPane mountTabbedPane;
    
    /**
     * Constructor
     *
     * @param rootP The AntennaRootPane containing this panel  
     */
    public AntennaTrackingPanel(AntennaRootPane rootP) {
    	super();
    	initialize(rootP);
    }
    
    /**
     * Init the GUI
     *
     */
    private void initialize(AntennaRootPane rootP) {
    	setLayout(new BorderLayout());
    	
   		mountTabbedPane=rootP.getErrorTabbedPane();
		mountTabbedPane.setName("mountStatusTabbedPane");
    	
    	JPanel detailsPnl = new JPanel();
    	detailsPnl.setLayout(new BoxLayout(detailsPnl,BoxLayout.Y_AXIS));
    	statusTable= new CommonStatusTable(rootP);
    	JScrollPane detailsScrollPanel = new JScrollPane(
    			statusTable,
    			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
    			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    	detailsPnl.add(btnPnl);
    	detailsPnl.add(detailsScrollPanel);
    	statusTable.setTabTitleSetter(mountTabbedPane, detailsPnl);
    	mountTabbedPane.addTab("Status",detailsPnl);
    	
    	subreflectorPnl = new SubreflectorPanel(rootP,false);
    	JScrollPane subreflectorPanel = new JScrollPane(
    			subreflectorPnl,
    			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
    			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	subreflectorPnl.setTabTitleSetter(mountTabbedPane, subreflectorPanel);
    	mountTabbedPane.addTab("AlcatelProtoSubreflector", subreflectorPanel);
    	
    	// Add the Metrology panel
		metrologyPnl = new MetrologyPanel(rootP, false);
		JScrollPane metrologyPanel = new JScrollPane(metrologyPnl,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mountTabbedPane.addTab("Metrology", metrologyPanel);
    	
    	add(mountTabbedPane,BorderLayout.CENTER);
    }

	 /** 
	  * Enable or disable the widgets 
	  * 
	  * @param b If <code>true</code> enable the widgets
	  **/
    public void enableWidgets(final boolean b) {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusTF.setEnabled(b);
		    	antTypeLbl.setEnabled(b);
			}
		});
    }
    
    /**
	 * Release all the resources and close the panel
	 *
	 */
	public void close() {
		setComponents(null,null);
		statusTable.close();
	}
	
	/**
	 * Set the mount and the mount controller
	 * 
	 * @param mnt The mount component (can be null)
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		mount=mnt;
		statusTable.setComponents(ctr, mnt);
		subreflectorPnl.setComponents(null, mnt);
		metrologyPnl.setComponents(mnt);
	}
	
}
