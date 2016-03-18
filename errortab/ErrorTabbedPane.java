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
package alma.control.gui.antennamount.errortab;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import alma.control.gui.antennamount.utils.GUIConstants;
import alma.control.gui.antennamount.utils.ValueState;

/**
 * <code>ErrorTabbedPane</code> extends {@link JTabbedPane} with a set of specialized methods 
 * to get errors and show the error tab when there are new errors.
 * <P>
 * The label of the error tab changes when there are errors not acknowledged by the
 * user in the following way:
 *    * Blinking red: a new error has been inserted in the panel
 *    * Red: blinking red turns to red if the user do not look at the
 *           table during a defined interval after a new error has been
 *           inserted
 *    * Black when all the errors have been seen by the user (i.e. the user selects
 *            the tab)
 * <P>
 * In addition, <code>ErrorTabbedPane</code> offers methods to set the title of a tab depending if 
 * the component shown inside the tab reports warning and/or errors 
 * <P>
 * <B>Note</B>: (<EM>ALMA_7_0_0-B<?EM>) due to a bug in JDK, it is not possible to use HTM as tab titles
 * 			(http://bugs.sun.com/view_bug.do?bug_id=6670274).
 * 			HTML has been disabled and so the labels have no color!
 * <P>
 * TODO: Monitor java bug and re/enable label colors or redifen the
 * 		tab title components.
 * 
 * @author acaproni
 *
 */
public class ErrorTabbedPane extends JTabbedPane implements ActionListener, TabTitleSetter {
	
	/**
	 * The title of the error tab when flashing
	 * Used together with <code>redTitle</code> to implement flashing
	 */
	private static final String flashRedTitle="<HTML><BODY bgcolor=\"red\"><FONT color=\"white\"><B><EM>";
	
	/**
	 * The icon for blinking the error tab
	 */
	private final ImageIcon altErrorIcon = 
		new ImageIcon(this.getClass().getResource(GUIConstants.resourceFolder+"error_alt.gif"));
	
	/**
	 * The timer to flash the title of the tab when anew error arrives and the
	 * operator is not looking at the error tab
	 */
	private Timer flashTitleTimer;
	
	/**
	 *  The number of the current flash;
	 */
	private volatile int currentFlash;
	
	/**
	 *  The time between 2 flashes
	 */
	private static final int TITLE_FLASH_TIME=350;
	
	/** 
	 * The number of flashes
	 */
	private static final int NUMBER_OF_TITLE_FLASHES=10;
	
	/** 
	 * The error tab
	 */
	private final ErrorTab errorTab = new ErrorTab();
	
	/**
	 * The title of the error tab
	 */
	private static final String errorTabTitle="Error";
	
	/**
	 * Build a new ErrorTabbedPane.
	 * 
	 * This class is a singleton: uses getInstance() to create a new object
	 */
	public ErrorTabbedPane() {
		super();
		errorTab.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				// Turn the title to black
				tabTitleState(ValueState.NORMAL, errorTabTitle, errorTab, false);
		    }
		});
		// Set the timer to flash the title of the tab
		flashTitleTimer= new Timer(TITLE_FLASH_TIME,this);
		flashTitleTimer.stop();
	}
	
	/** 
	 * Add an error to the tab.
	 * <P>
	 * If it is the case, the title of the error tab begins flashing.
	 * 
	 * @param newError The new error to add to the tab
	 */
	public void addError(final ErrorInfo newError) {
		if (newError==null) {
			throw new IllegalArgumentException("Invalid null error");
		}
		
		errorTab.addError(newError);
		
		// Check if the error tab is already present
		if (indexOfComponent(errorTab)==-1) {
			addTab(errorTabTitle,errorTab);
			tabTitleState(ValueState.ERROR, errorTabTitle, errorTab, true);		
			return;
		} 
		// Check and eventually change the label of the tab
		if (getSelectedIndex()!=-1 && getSelectedIndex()==indexOfComponent(errorTab)) {
			tabTitleState(ValueState.NORMAL, errorTabTitle, errorTab, false);
		} else {
			tabTitleState(ValueState.ERROR, errorTabTitle, errorTab, true);
		}
	}
	
	/**
	 * Add the the component in a new tab with the passed title
	 * 
	 * @param title The title of the tab
	 * @param comp The component to add
	 */
	private void addTheTab(final String title, final Component comp) {
		if (comp==null) {
			throw new IllegalArgumentException("The component can't be null");
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int i = indexOfComponent(errorTab);
				if (i!=-1) {
					insertTab(title, null, comp, null, i);
				} else {
					addTab(title,null,comp);
				}
			}
		});
	}

	/** 
	 * Add a tab to this tabbed pane
	 * <P>
	 * This method ensures that the error tab is always the rightmost tab in the panel.
	 * It distinguishes if the operation is executed inside or outside
	 * of the AWT event dispatcher thread.
	 * The real operation is delegated to <code>addTheTab</code>
	 * 
	 * @return <code>true</code> if the tab has been successfully added, <code>false</code> otherwise
	 * 
	 * @see javax.swing.JTabbedPane
	 * 
	 */
	@Override
	public void addTab(final String newTitle, final Component theComponent) {
		if (newTitle==null || newTitle.isEmpty()) {
			throw new IllegalArgumentException("Invalid tab title");
		}
		if (theComponent.getName()==null || theComponent.getName().isEmpty()) {
			theComponent.setName(newTitle);
		}
		// Check if the component is already present
		for (int i=0; i<getTabCount(); i++) {
			Component c= getTabComponentAt(i);
			if (c!=null) {
				if (c.getName().equals(theComponent.getName())) {
					throw new IllegalArgumentException("Component already displayed: "+c.getName());
				}
			} 
		}
		if (EventQueue.isDispatchThread()) {
			addTheTab(newTitle, theComponent);
			return;
		}
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					addTheTab(newTitle, theComponent);
				}
			});
		} catch (Exception e) {
			System.err.println("Exception caught while adding "+theComponent+" with a TAB having title "+newTitle+": "+e.getMessage());
		}
	}
	
	/**
	 * @see import java.awt.event.ActionListener;
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==flashTitleTimer) {
			if (++currentFlash==NUMBER_OF_TITLE_FLASHES) {
				tabTitleState(ValueState.ERROR, errorTabTitle, errorTab, false);
				return;
			}
			int index = indexOfComponent(errorTab);
			if (currentFlash%2==0) {
				//setTitleAt(index, ValueState.ERROR.htmlHeader+errorTabTitle);
				setIconAt(index, altErrorIcon);
			} else {
				//setTitleAt(index, flashRedTitle+errorTabTitle);
				setIconAt(index, ValueState.ERROR.icon);
			}
		}
	}

	/**
	 * Set the title of the tab depending on its state.
	 * <P>
	 * <b>Note</b>: the only tab allowed to flash is the error tab.
	 * 
	 * @see alma.control.gui.antennamount.errortab.TabTitleSetter#tabTitleState(alma.control.gui.antennamount.errortab.TabTitleSetter.TitleState, java.lang.String, java.awt.Component)
	 */
	public void tabTitleState(
			final ValueState state, 
			final String title,
			final Component component, 
			final boolean flash) {
		if (state==null) {
			throw new IllegalArgumentException("The state of the tab can't be null");
		}
		if (component==null) {
			throw new IllegalArgumentException("The component can't be null");
		}
		if (flash && component!=errorTab) {
			throw new IllegalArgumentException("The only component allowed to flash is the error tab!");

		}
		// The following only tests if the tab is displayed
		int idx = indexOfComponent(component);
		if (idx==-1) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int i = indexOfComponent(component);
				if (getIconAt(i)!=state.icon) {
					setIconAt(i, state.icon);
				}
				//String newTitle = state.htmlHeader+title;
				String newTitle = title;
				if (!newTitle.equals(getTitleAt(i))) {
					setTitleAt(i, newTitle);
				}
				if (component==errorTab) {
					if (flash) {
						currentFlash=0;
						flashTitleTimer.restart();
					} else {
						flashTitleTimer.stop();
					}
				}
			}
		});
	}
}
