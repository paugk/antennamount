/*
 * Created on Jun 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package alma.control.gui.antennamount;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

/**
 * @author ikriznar
 *
 */
public class StatusLine extends JPanel implements ActionListener {
	
	/**
	 * The label containing the massage
	 */
	private JLabel textLabel;
	
	/**
	 * The progress bar for long last operations
	 */
	private JProgressBar progressBar;
	
	/**
	 * The icon to show the status of the connection with the antenna
	 */
	private JLabel iconLabel=new JLabel();
	
	private final JLabel heartbeatLbl; 
	
	/**
	 * The timer used to delete the message in the status bar after some time
	 */
	private Timer timer=null;
	
	/**
	 * The number of milliseconds after which the timer send an event and the status message is cleared
	 */
	private static int TIMER_EVENTS_DELAY = 20000;
	
	/**
	 * 
	 */
	public StatusLine(JLabel heartbeatLbl) {
		if (heartbeatLbl==null) {
			throw new IllegalArgumentException("The heartbeat label can't be null");
		}
		this.heartbeatLbl=heartbeatLbl;
		initialize();
		timer = new Timer(TIMER_EVENTS_DELAY,this);
		timer.stop();
		timer.setRepeats(false);
	}
	
	private void initialize() {
		setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
		add(Box.createRigidArea(new Dimension(2, 0)));
		textLabel= new JLabel();
		textLabel.setBorder( new EtchedBorder());
		textLabel.setForeground(new Color(88,88,117));
		
		add(textLabel);
		add(Box.createRigidArea(new Dimension(3, 0)));

		
		progressBar= new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setPreferredSize(new Dimension(120,21));
		progressBar.setMinimumSize(new Dimension(120,21));
		add(progressBar);
		add(Box.createRigidArea(new Dimension(3, 0)));
		
		add(iconLabel);
		add(Box.createRigidArea(new Dimension(3, 0)));
		iconLabel.setPreferredSize(new Dimension(24,26));
		iconLabel.setMinimumSize(iconLabel.getMinimumSize());
		add(Box.createRigidArea(new Dimension(3, 0)));
		
		add(heartbeatLbl);
		heartbeatLbl.setMinimumSize(new Dimension(16,16));
		heartbeatLbl.setPreferredSize(new Dimension(24,26));
		add(Box.createRigidArea(new Dimension(2, 0)));
	}
	
	/**
	 * Set the message in the status bar
	 * 
	 * @param s The message
	 */
	public synchronized void setMessage(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textLabel.setText(s);
				textLabel.setToolTipText(s);
				timer.restart();
			}
		});
	}
	
	public String getMessage() {
		return textLabel.getText();
	}
	
	public int getProgress() {
		return progressBar.getValue();
	}
	
	public synchronized void startProgressAnimation() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setIndeterminate(true);
			}
		});
	}
	
	public synchronized void stopProgressAnimation() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setIndeterminate(false);
				progressBar.setValue(0);
			}
		});
	}
	
	/**
	 * Set the status icon 
	 * 
	 * @param icon The new icon to show in the label
	 * @param tootip The tooltip
	 */
	public void setStatusIcon(final ImageIcon icon, final String tootip) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				iconLabel.setIcon(icon);
				iconLabel.setToolTipText(tootip);
			}
		});
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource()==timer) {
			textLabel.setText(null);
			textLabel.setToolTipText(null);
			return;
		}
	}
}
