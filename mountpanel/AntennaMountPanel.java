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

package alma.control.gui.antennamount.mountpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.controlspanel.ControlsPanel;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;
import alma.control.gui.antennamount.pointingpanel.Pointing;
import alma.control.gui.antennamount.subreflectorpanel.SubreflectorPanel;
import alma.control.gui.antennamount.tracking.CommonStatusTable;

import alma.control.gui.antennamount.metrology.MetrologyPanel;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;

/**
 * The mount GUI for an antenna
 * 
 * @author acaproni
 */
public class AntennaMountPanel extends JPanel implements ActionListener
{
	
    /**
     * Creates a new instance of this class.
     * 
     * @param rootP The AntennaRootPane containing this panel  
     * 
     */
    public AntennaMountPanel(AntennaRootPane rootP) {
        super();
        if (rootP==null) {
        	throw new IllegalArgumentException("Invalid null errorTabbedPane");
        }
        initialize(rootP);
    }
    
    /**
     * Build the tabbed panel
     * 
     * @param @param rootP The AntennaRootPane
     * @return the tabbed panel
     */
    private JTabbedPane getMainTabbedPane(AntennaRootPane rootP) {
    	if (mountTabbedPane == null) {
    		mountTabbedPane=rootP.getErrorTabbedPane();
			mountTabbedPane.setMinimumSize(new java.awt.Dimension(640, 480));
			mountTabbedPane.setName("mountTabbedPane");

			// Add the pointing panel
			pointing = new Pointing(rootP);
			JScrollPane pointingScrollPanel = new JScrollPane(pointing,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			mountTabbedPane.addTab("Pointing", pointingScrollPanel);
			
			// SubreflectorPanel
			subreflectorPnl = new SubreflectorPanel(rootP,true);
			JScrollPane subreflectorPanel = new JScrollPane(subreflectorPnl,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			subreflectorPnl.setTabTitleSetter(mountTabbedPane, subreflectorPanel);
			mountTabbedPane.addTab("Subreflector", subreflectorPanel);
			
			// ControlsPanel
			controlsPnl = new ControlsPanel(rootP);
			JScrollPane controlsPanel = new JScrollPane(controlsPnl,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			mountTabbedPane.addTab("Controls", controlsPanel);

			// Add the status table
			JPanel detailsPnl = new JPanel();
			detailsPnl.setLayout(new BoxLayout(detailsPnl, BoxLayout.Y_AXIS));
			statusTable = new CommonStatusTable(rootP);
			JScrollPane detailsScrollPanel = new JScrollPane(statusTable,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			statusTable.setTabTitleSetter(mountTabbedPane, detailsPnl);
			
			// Add the Metrology panel
			metrologyPnl = new MetrologyPanel(rootP, true);
			JScrollPane metrologyPanel = new JScrollPane(metrologyPnl,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			mountTabbedPane.addTab("Metrology", metrologyPanel);
			
			// Add the clear fault button
			JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			btnPnl.add(clearFaultBtn);
			clearFaultBtn.setEnabled(false);
			clearFaultBtn.addActionListener(this);
			detailsPnl.add(btnPnl);
			detailsPnl.add(detailsScrollPanel);

			mountTabbedPane.addTab("Status", detailsPnl);
    	}
    	return mountTabbedPane;

	}
    
    /**
     * @see AntennaRootPane
     */
    public void setComponents(MountController ctr, Mount mnt) {
    	mount=mnt;
    	controller=ctr;
        pointing.setComponents(ctr, mnt);
		statusTable.setComponents(ctr, mnt);
		subreflectorPnl.setComponents(ctr, mnt);
		controlsPnl.setComponents(ctr, mnt);
		metrologyPnl.setComponents(mnt);
        enableWidgets(controller!=null);
    }

    /**
     * initialize the panel
     * 
     * @param errorTabP The error panel to show in the error tab 
     */
    private void initialize(AntennaRootPane rootP) {
    	setLayout(new BorderLayout());
    	
        add(getMainTabbedPane(rootP), BorderLayout.CENTER);
		enableWidgets(false);
	}
    
    /** 
     * Enable or disable the widgets 
     * 
     * @param b If <code>true</code> enable the widgets
     */
    public void enableWidgets(final boolean b) {
        pointing.enableWidgets(b);
        controlsPnl.enableWidgets(b);
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
        		clearFaultBtn.setEnabled(b);
        	}
        });
    }

    /**
     * The pane with the tabs
     */
    private ErrorTabbedPane mountTabbedPane;
    
    /**
     * The mount 
     */
    private Mount mount=null;
    
    /**
     * The MountController
     */
    private MountController controller=null;
    
    /**
     * The subreflector panel
     */
    private SubreflectorPanel subreflectorPnl;
    
    /**
     * The metrology panel
     */
    private MetrologyPanel metrologyPnl;
    
    /**
     * The controls panel
     */
    private ControlsPanel controlsPnl;
    
    /**
     * The pointing swing component
     */
    private Pointing pointing;
    
    
    /**
     * The table with the detailed status of the antenna
     */
    private CommonStatusTable statusTable;
    
    /**
     * The button to clear the fault
     */
    private JButton clearFaultBtn = new JButton("Clear fault");

	/**
	 * Release all the resources and close the panel
	 *
	 */
	public void close() {
		mount=null;
        pointing.close();
        controlsPnl.close();
		enableWidgets(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==clearFaultBtn) {
			if (mount!=null) {
				mount.clearFault();
			}
		} else {
			System.out.println("Unknown event received: "+e);
		}
	}
	

}
