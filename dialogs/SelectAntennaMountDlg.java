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
package alma.control.gui.antennamount.dialogs;

import alma.acs.container.ContainerServices;
import alma.control.gui.antennamount.AntennaRootPane;

import alma.common.gui.components.selector.ComponentSelector;
import alma.common.gui.components.selector.SelectorComponentEvent;
import alma.common.gui.components.selector.SelectorComponentListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The dialog to select and connect to a mount
 *
 * @author acaproni
 */
public class SelectAntennaMountDlg extends JDialog  implements ActionListener, SelectorComponentListener {
	
	
	// The wildcarded IDL (i.e. the type of the component) to select
	private static final String ANTENNA_IDL="*/Antenna:*";
	
	// The panel
	private AntennaRootPane panel;
	
	private JButton doneBtn = new JButton("Done");
	
	// The selector 
	private ComponentSelector selector;
	
	/**
	 * Constructor
	 *
	 */
	public SelectAntennaMountDlg(AntennaRootPane pnl, JFrame owner, ContainerServices cs) {
		super(owner);
		setTitle("Mount selector");
		if (pnl==null) {
			throw new IllegalArgumentException("Illegal null AntennaMountPanel in constructor");
		}
		if (cs==null) {
			throw new IllegalArgumentException("Illegal null ContainerServices in constructor");
		}
		panel=pnl;
		initialize(cs);
	}
	
	/**
	 * Initialize the GUI
	 *
	 */
	private void initialize(ContainerServices cs) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnl.add(doneBtn);
        doneBtn.addActionListener(this);
        getContentPane().add(pnl,BorderLayout.SOUTH);
        
        JPanel pnlSel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selector = new ComponentSelector(ANTENNA_IDL,cs,this);
        pnlSel.add(selector);
        getContentPane().add(pnlSel,BorderLayout.CENTER);
        pack();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==doneBtn) {
			doneBtn.removeActionListener(this);
			selector=null;
			setVisible(false);
			// If the dispose is not called, then the application doesn't exit
			// because the selector doesn't free some resources
			this.dispose();
		} else {
			System.err.println("Unknown source of event: "+e);
		}
	}
	
	/**
	 * @see SelectorComponentListener
	 */
	public void connectPerformed(SelectorComponentEvent selectorcomponentevent) {
		setVisible(false);
		selector=null;
		panel.connectPerformed(selectorcomponentevent);
		dispose();
	}
}
