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
package alma.control.gui.antennamount.pointingpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.utils.ValueConverter;
import alma.control.gui.antennamount.utils.editor.TextEditor;

/**
 * The pointing panel showing the widgets for the standard and the detailed view.
 * <P>
 * When the user writes the name of a planet in the combo box, or select a planet
 * from the list, the other widgets are grayed.
 * In fact the policy while pointing in RA/DEC is to point to a source if its name
 * is written in the combo box, otherwise use the other fields.
 * <P>
 * In the previous version, there were two classes one for the standard panel (<code>StandardPanel</code>) 
 * and this one that extended <code>StandardPanel</code> by adding several widgets.
 * The hierarchy has been refactored by using only one class that shows the required widgets depending on
 * the selected mode. 
 * It is the <code>buildPanel</code> that hides/shows the widgets depending on the selected mode.
 * 
 * @author acaproni
 *
 */
public class ExpertPanel extends JPanel implements ActionListener {
	
	/**
	 * The editor of the combo box needed to know when its
	 * text field is empty or not.
	 * 
	 * @author acaproni
	 *
	 */
	public class SourceObjComboBoxEditor implements ComboBoxEditor, DocumentListener {
		
		/**
		 * The editor
		 */
		private final JTextField editor = new JTextField();
		
		/**
		 * Constructor
		 */
		public SourceObjComboBoxEditor() {
			editor.getDocument().addDocumentListener(this);
		}

		/**
		 * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
		 */
		@Override
		public void addActionListener(ActionListener l) {
			editor.addActionListener(l);
			
		}

		/**
		 * @see javax.swing.ComboBoxEditor#getEditorComponent()
		 */
		@Override
		public Component getEditorComponent() {
			return editor;
		}

		/**
		 * @see javax.swing.ComboBoxEditor#getItem()
		 */
		@Override
		public Object getItem() {
			if (editor.getDocument().getLength()==0) {
				return "";
			} else {
				try {
					return editor.getDocument().getText(0,editor.getDocument().getLength());
				} catch (BadLocationException ex) {
					return "";
				}
			}
		}

		/**
		 * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
		 */
		@Override
		public void removeActionListener(ActionListener l) {
			editor.removeActionListener(l);
		}

		/**
		 * @see javax.swing.ComboBoxEditor#selectAll()
		 */
		@Override
		public void selectAll() {
			editor.selectAll();
		}

		/**
		 * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
		 */
		@Override
		public void setItem(Object anObject) {
			if (anObject==null) {
				editor.setText("");
			} else {
				editor.setText(anObject.toString());
			}
		}

		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
		 */
		@Override
		public void changedUpdate(DocumentEvent e) {
			ratioWidgets();
		}

		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
		 */
		@Override
		public void insertUpdate(DocumentEvent e) {
			ratioWidgets();
		}

		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
		 */
		@Override
		public void removeUpdate(DocumentEvent e) {
			ratioWidgets();
		}
		
	}
	
	// The widgets to point
	private JTextField epoch;
    private JTextField pmRA;
    private JTextField pmDec;
    private JTextField parallax;
    
    /** 
     * The parallax label
     */
    private JLabel parallaxLbl;
    
    /** 
     * The proper motion RA label
     */
    private JLabel pmRaLbl;
    
    /** 
     * The proper motion DEC label
     */
    private JLabel pmDecLbl;

    /** 
     * The epoch label
     */
    private JLabel epochLbl;
    
    /**
     * The mode actually shown by the panel.
     * <P>
     * If <code>true</code> then the widget of the detailed
     * view are visible otherwise they are hidden. 
     */
    private boolean expertMode;
    
    /**
     * The buttons to point in AZ/EL
     */
    private JButton applyAzEl = new JButton("Apply");
    
    /**
     * The buttons to point in Ra/Dec
     */
    private JButton applyRADec = new JButton("Apply");
	
    // The widgets to insert Az/El, RA/Dec, epoch, equinoxTE, parallax, proper motion
    private TextEditor azTE;
    private TextEditor elTE;
    private TextEditor raTE;
    private TextEditor decTE;
    private JTextField equinoxTE;
    
    /** 
     * The Azimuth label
     */
    private JLabel azLbl;
    
    /** 
     * The Elevation label
     */
    private JLabel elLbl;
    
    /** 
     * The Right Ascension label
     */
    private JLabel raLbl;
    
    /** 
     * The Declination label
     */
    private JLabel decLbl;
    
    /** 
     * The Equinox label
     */
    private JLabel equinoxLbl;
    
    /** 
     * The label
     */
    private JLabel sourceObjLbl;
    
    /**
     * The mount controller
     */
    private MountController controller;
    
    /**
     * The antenna root pane used to write status messages
     */
    private AntennaRootPane antennaRoot;
    
    /**
     * The source objects for the <code>sourceObjectsCB</code>.
     */
    private static final String[] initalSourceObjects = {
    	"", // The first item is always the empty string
    	"Sun",
    	"Moon",
    	"Mercury",
    	"Venus",
    	"Mars",
    	"Jupiter",
    	"Saturn",
    	"Uranus",
    	"Neptune",
    	"Pluto"
    };
    
    /** The editable combo box of source objects
     * 
     * The first item (index 0) is empty and means no source object selected
     */
    private JComboBox sourceObjectsCB;
    
    /**
     * The model of the combo box to allow adding items to the list
     */
    private DefaultComboBoxModel sourceObjectsCBModel = new DefaultComboBoxModel(initalSourceObjects);
    
    /**
     * The editor of the combo box
     */
    private SourceObjComboBoxEditor sourceObjsEditor =new SourceObjComboBoxEditor();
    
    /**
     * Constructor
     * 
     * @param rootP The AntennaRootPane to write status line messages
     * @param mode if <code>true</code> the panel shows the widgets for the detailed view
     */
    public ExpertPanel(AntennaRootPane rootP, boolean mode) {
    	antennaRoot=rootP;
    	buildWidgets();
    	setExpertMode(mode,null);
    }
    
    /**
	 * Build the widgets displayed in the panel
	 * and connect the listeners.
	 * 
	 * 
	 */
    private void buildWidgets() {
    	parallaxLbl=new JLabel("<HTML><BODY><B>Parallax [rad]");
        pmRaLbl=new JLabel("<HTML><BODY><B>Prop. M. RA [rad/sec]");
        pmDecLbl=new JLabel("<HTML><BODY><B>Prop. M. Dec [rad/sec]");
        epochLbl=new JLabel("<HTML><BODY><B>JEpoch");
        azLbl=new JLabel("<HTML><BODY><B>Azimuth");
        elLbl=new JLabel("<HTML><BODY><B>Elevation");
        raLbl=new JLabel("<HTML><BODY><B>Right Ascension");
        decLbl=new JLabel("<HTML><BODY><B>Declination");
        equinoxLbl=new JLabel("<HTML><BODY><B>Equinox");
        sourceObjLbl=new JLabel("<HTML><BODY><B>source obj");
        
     // AZ/EL

		// The separators are the same for Az and El
		char[] seps = new char[] {0xb0, '\'', '.' }; 
		
		// Set min and max for Az
		int[] minsAz = new int[] { -270,0,0,0 };
		int[] maxsAz = new int[] { +270,59,59,99 };
		
		// Set min and max for El
		int[] minsEl = new int[] { 2,0,0,0 };
		int[] maxsEl = new int[] { 88,59,59,99 };
		
		// Builds the widgets
		azTE = new TextEditor(seps,minsAz,maxsAz,-2700000.00,2700000.00);
		azTE.setColumns(14);
		elTE = new TextEditor(seps,minsEl,maxsEl,20000.00,885359.99);
		elTE.setColumns(14);
		
		// DA/DEC
		
		// The separators for RA and dec
		char[] decSeps = new char[] {0xb0, '\'', '.' };
		char[] raSeps=new char[] {':',':','.' };
		
		// Set min and max for RA
		int[] minsRA = new int[] { 0,0,0,0 };
		int[] maxsRA = new int[] { 23,59,59,99 };
		
		// Set min and max for Dec
		int[] minsDec = new int[] { -90,0,0,0 };
		int[] maxsDec = new int[] { 90,59,59,99 };
		
		// Builds the widgets
		raTE = new TextEditor(raSeps,minsRA,maxsRA,0.0,235959.99);
		raTE.setColumns(14);
		decTE = new TextEditor(decSeps,minsDec,maxsDec,-900000.00,900000.00);
		decTE.setColumns(14);
	    
		//Equinox
	    equinoxTE= new JTextField("2000.0",6);
	    equinoxTE.setColumns(14);

	    // Source object
	    sourceObjectsCB = new JComboBox();
    	sourceObjectsCB.setMaximumRowCount(6);
    	sourceObjectsCB.setEditable(true);
		sourceObjectsCB.setEditor(sourceObjsEditor);
		sourceObjectsCB.setModel(sourceObjectsCBModel);
		
    	// Set the listeners
		applyAzEl.addActionListener(this);
		applyRADec.addActionListener(this);
        
    	
    	// Epoch
	    epoch= new JTextField("2000.0",6);
	    epoch.setColumns(14);
	    
	    // Proper motions
	    pmRA = new JTextField("0.0",14);
	    pmDec = new JTextField("0.0",14);
	    
	    // Parallax
	    parallax = new JTextField("0.0",14);
    }
    
    /**
	 * Build the panel adding the widgets for the expert (detailed) or the standard mode.
	 * <P>
	 * This method is executed while changing the operating mode between expert and
	 * standard.
	 * 
	 * 
	 * @param expert If <code>true</code> add all the widgets of the expert panel
	 */
	private void buildPanel(boolean expert) {
		removeAll();
		if (expert) {
			setLayout(new GridLayout(9,3,5,3));
		} else {
			setLayout(new GridLayout(7,3,5,3));
		}
		
		// 1: Az/EL labels
		JPanel azimuthPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
		azimuthPnl.add(azLbl);
		add(azimuthPnl);
		JPanel elPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		elPnl.add(elLbl);
		add(elPnl);
		add(new JLabel(""));
		
		// 2: RA/Dec/Apply
		add(azTE);
		add(elTE);
		add(applyAzEl);
		
		// 3: An empty line
		add(new JLabel(""));
		add(new JLabel(""));
		add(new JLabel(""));
		
		// 4: Equinox, Epoch and source object labels
		JPanel equinoxPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
		equinoxPnl.add(equinoxLbl);
		add(equinoxPnl);
		JPanel epochPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
		epochPnl.add(epochLbl);
		add(epochPnl);
		JPanel sourcObjPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
		sourcObjPnl.add(sourceObjLbl);
		add(sourcObjPnl);
		
		// 5: Equinox, epoch and source object
		add(equinoxTE);
		add(epoch);
		add(sourceObjectsCB);
		
		if (expert) {
			// 6: PMs and parallax labels
			JPanel pmRaPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
			pmRaPnl.add(pmRaLbl);
			add(pmRaPnl);
			JPanel pmDecPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
			pmDecPnl.add(pmDecLbl);
			add(pmDecPnl);
			JPanel parallaxPnl= new JPanel(new FlowLayout(FlowLayout.CENTER));
			parallaxPnl.add(parallaxLbl);
			add(parallaxPnl);
			
			// 7: Proper motion in RA and Dec and the parallax
			add(pmRA);
			add(pmDec);
			add(parallax);
		}
		
		// 8: RA/DEC labels
		JPanel raPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		raPnl.add(raLbl);
		add(raPnl);
		JPanel decPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		decPnl.add(decLbl);
		add(decPnl);
		add(new JLabel(""));
		
		// 9: RA/DEC/Apply
		add(raTE);
		add(decTE);
		add(applyRADec);
	}

	/** 
     * Enable or disable the widgets 
     * 
     * @param enabled if <code>true</code> enable the widgets
     */
    public void enableWidgets(final boolean enabled) {
    	// The epoch is disabled because it is not yet supported
    	// by the component
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			Object item = sourceObjectsCB.getSelectedItem();
    			boolean hasSrcObj = item!=null && !item.toString().isEmpty();
    			epoch.setEnabled(false); 
    	        pmRA.setEnabled(enabled && !hasSrcObj);
    	        pmDec.setEnabled(enabled && !hasSrcObj);
    	        parallax.setEnabled(enabled && !hasSrcObj);
    	        sourceObjectsCB.setEnabled(enabled); 
    	        azTE.setEnabled(enabled);
    	        elTE.setEnabled(enabled);
    	        raTE.setEnabled(enabled);
    	        decTE.setEnabled(enabled);
    	        applyAzEl.setEnabled(enabled);
    	        applyRADec.setEnabled(enabled);
    	        equinoxTE.setEnabled(enabled);
    	        
    	        ratioWidgets();
    		}
    	});
        
    }
    
    /** 
	 * Override objstar in the standard panel by reading proper motions and parallax
	 * 
	 * The objstar is executed with pmRA=pmDec=parallax=0 because
	 * the standard panel does not allow the user to set such parameters as it is done 
	 * by the expert panel  
	 *
	 */
	protected void objstar() {
		if (controller==null) {
			throw new IllegalStateException("Calling objstar without a controller");
		}
		Double ra = raTE.getValue();
		if (ra==null || !raTE.isInRange()) {
			antennaRoot.addStatusMessage("Right ascencion is wrong: "+raTE.getText(), true);
			raTE.setForeground(Color.RED);
			return;
		} else {
			raTE.setForeground(Color.BLACK);
		}
		Double dec = decTE.getValue();
		if (dec==null || !decTE.isInRange()) {
			antennaRoot.addStatusMessage("Declination is wrong: "+decTE.getText(), true);
			decTE.setForeground(Color.RED);
			return;
		} else {
			decTE.setForeground(Color.BLACK);
		}
		Double equinoxVal=null;
		try {
			equinoxVal= Double.parseDouble(equinoxTE.getText());
			equinoxTE.setForeground(Color.BLACK);
		} catch (Exception e) {
			antennaRoot.addStatusMessage("Equinox is wrong: "+equinoxTE.getText(), true);
			equinoxTE.setForeground(Color.RED);
			return;
		}	
		Double pmRAVal=0.0;
		Double pmDecVal=0.0;
		Double parallaxVal=0.0;
		if (expertMode) {
			try {
				pmRAVal= Double.parseDouble(pmRA.getText());
				pmRA.setForeground(Color.BLACK);
			} catch (Exception e) {
				antennaRoot.addStatusMessage("RA proper motion is wrong: "+pmRA.getText(), true);
				pmRA.setForeground(Color.RED);
				return;
			}
			try {
				pmDecVal= Double.parseDouble(pmDec.getText());
				pmDec.setForeground(Color.BLACK);
			} catch (Exception e) {
				antennaRoot.addStatusMessage("Dec proper motion is wrong: "+pmDec.getText(), true);
				pmDec.setForeground(Color.RED);
				return;
			}
			try {
				parallaxVal= Double.parseDouble(parallax.getText());
				parallax.setForeground(Color.BLACK);
			} catch (Exception e) {
				antennaRoot.addStatusMessage("Parallax is wrong: "+parallax.getText(), true);
				parallax.setForeground(Color.RED);
				return;
			}
		} 
		
		controller.objstar(ValueConverter.hms2rad(ra),ValueConverter.dms2rad(dec),equinoxVal,pmRAVal,pmDecVal,parallaxVal);
	}
    
    /**
	 * Clear the fields for the other coordinate system.
	 * 
	 * Override the method in StandardPanel to clear additional
	 * fields
	 * 
	 * @param equatorial If false the AZ/EL are cleared
	 *                   if true, RA/DEC are cleared
	 *                   
	 * @see StandardPanel
	 */
	protected void clearFields(final boolean equatorial) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (equatorial) {
					raTE.setText("");
					decTE.setText("");
					epoch.setText("");
					pmRA.setText("");
					pmDec.setText("");
					parallax.setText("");
					sourceObjectsCB.setSelectedIndex(0);
				} else {
					azTE.setText("");
					elTE.setText("");
				} 		
			}
		});
		
	}
	
	/**
	 * When a planet is selected in the combo box, all other widgets are
	 * grayed.
	 */
	protected void ratioWidgets() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean enable=sourceObjectsCB.getSelectedItem()==null || sourceObjectsCB.getEditor().getItem().toString().isEmpty();
				equinoxTE.setEditable(enable);
				epoch.setEditable(false); // Not yet implemented
				pmRA.setEditable(enable);
				pmDec.setEditable(enable);
				parallax.setEditable(enable);
				raTE.setEditable(enable);
				decTE.setEditable(enable);
				equinoxTE.setEditable(enable);
				raTE.setEditable(enable);
				decTE.setEditable(enable);
			}
		});
	}
	
	/**
	 * Switch between the expert (detailed) and the standard mode.
	 * <P>
	 * While in expert mode, the pointing panel shows more widgets.
	 * 
	 * @param expert If <code>true</code> switch to the expert mode.
	 */
	public void setExpertMode(final boolean expert, final JToggleButton modeBtn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				expertMode=expert;
				buildPanel(expert);
				if (modeBtn!=null) {
					modeBtn.setEnabled(true);
				}
			}
		});
	}
	
	/**
	 * @see java.awt.event.ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==applyAzEl) {
			clearFields(true);
			objfix();
		} else if (e.getSource()==applyRADec) {
			clearFields(false);
			String source = sourceObjectsCB.getEditor().getItem().toString().trim();
			if (!source.isEmpty()) {
				controller.setPlanet(source);
				// Check if the combobox already contains this item
				for (int t=0; t<sourceObjectsCBModel.getSize(); t++) {
					if (source.compareToIgnoreCase(sourceObjectsCBModel.getElementAt(t).toString())==0) {
						// Item already in the combobox
						return;
					}
				}
				// Add the new source to the combobox
				sourceObjectsCBModel.addElement(source);
			} else {
				objstar();
			}
		} else {
			System.out.println("Unknown event received: "+e);
		}
	}
	
	/**
	 * Set the mount and mount controller components
	 * 
     * @param ctr The mount controller
     * @param mnt The mount
     */
    public void setComponents(MountController ctr, Mount mnt) {
    	controller=ctr;
    }
    
    /** 
	 * The user want to apply objfix
	 *
	 */
	private void objfix() {
		if (controller==null) {
			throw new IllegalStateException("Calling objfix without a controller");
		}
		Double az = azTE.getValue();
		if (az==null || !azTE.isInRange()) {
			antennaRoot.addStatusMessage("Azimut is wrong: "+azTE.getText(), true);
			azTE.setForeground(Color.RED);
			return;
		} else {
			azTE.setForeground(Color.BLACK);
		}
		Double el = elTE.getValue();
		if (el==null || !elTE.isInRange()) {
			antennaRoot.addStatusMessage("Elevation is wrong: "+elTE.getText(), true);
			elTE.setForeground(Color.RED);
			return;
		} else {
			elTE.setForeground(Color.BLACK);
		}
		controller.objfix(ValueConverter.dms2rad(az),ValueConverter.dms2rad(el));
	}

}
