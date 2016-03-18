/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2009
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
package alma.control.gui.antennamount;

import java.util.HashMap;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.utils.GUIConstants;

/**
 * A class that checks if all the threads are running.
 * <P>
 * The class extends a {@link JLabel} to offer a feedback
 * to the user.
 * <P>
 * Each thread to be checked must 
 * <OL>
 * 	<LI>register itself
 * 	<LI>call the ping method before the timeout elapses
 *  <LI>unregister before terminating
 * </OL>
 * <P>
 * The threads must have a valid name.
 * <P>
 * Note that this method decide if a thread is dead if it is
 * not calling the ping method for a certain amount of time,
 * a <code>threshold</code>.  
 * 
 * @author acaproni
 *
 */
public class HeartbeatChecker extends JLabel implements Runnable {
	
	/**
	 * The icon shown when everything is ok
	 */
	private final ImageIcon onIcon= new ImageIcon(this.getClass().getResource(GUIConstants.resourceFolder+"heart.png"));
	
	/**
	 * The icon shown when some thread do not run
	 * but it is also used for blinking effect
	 */
	private final ImageIcon offIcon= new ImageIcon(this.getClass().getResource(GUIConstants.resourceFolder+"heart_grey.png"));
	
	/**
	 * The interval between 2 check of status thread and blink
	 * of the icon.
	 */
	private final int refreshInterval=750;
	
	/**
	 * The time to decide if a thread is dead
	 * i.e. we decide that a thread is dead if it
	 * did not call the ping method for more then
	 * threshold msec.
	 */
	private final int threshold=60000;
	
	/**
	 * Signal the thread to terminate
	 */
	private boolean terminateThread=false;
	
	/**
	 * The thread executed by this {@link Runnable}
	 */
	private final Thread thread;
	
	/**
	 * The map to store for each thread the last time
	 * it called ping 
	 */
	private final HashMap<String, Long> threadsPingTime = new HashMap<String, Long>();
	
	/**
	 * Constructor
	 */
	public HeartbeatChecker() {
		setIcon(offIcon);
		thread = new Thread(this,"HeartbeatThread");
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Stop the timer and set the icon to a not blinking state
	 */
	public void close() {
		terminateThread=true;
		thread.interrupt();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setIcon(offIcon);
			}
		});
	}
	
	/**
	 * Update the icon of the label
	 * 
	 * @param name The name of the thread that does not respond
	 * @see HeartbeatChecker#watchdog()
	 */
	private void updateIcon(final String name) {
		final int sz;
		synchronized (threadsPingTime) {
			sz=threadsPingTime.size();
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				if (name!=null) {
					setIcon(offIcon);
					setToolTipText("Thread "+name+"not responding!");
					return;
				}
				setToolTipText("Ok ("+sz+" threads)");
				if (getIcon()==onIcon) {
					setIcon(offIcon);	
				} else {
					setIcon(onIcon);
				}		
			}
		});
	}
	
	/**
	 * Add the thread to the list of threads to check
	 * <P>
	 * The thread is added with the current time.
	 * 
	 * @param t The thread to add for checking
	 */
	public void register(Thread t) {
		if (t==null) {
			throw new IllegalArgumentException("Can't check a null thread");
		}
		String name=t.getName();
		if (name==null || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid thread name. Please set a name to your thread");
		}
		synchronized (threadsPingTime) {
			if (threadsPingTime.containsKey(name)) {
				throw new IllegalArgumentException("Already checking "+name);
			}
			threadsPingTime.put(name, System.currentTimeMillis());
		}
	}
	
	/**
	 * Remove the thread to the list of threads to check.
	 * 
	 * @param t The thread to remove from checking
	 */
	public void unregister(Thread t) {
		if (t==null) {
			throw new IllegalArgumentException("Can't check a null thread");
		}
		String name=t.getName();
		if (name==null || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid thread name. Please set a name to your thread");
		}
		synchronized (threadsPingTime) {
			if (!threadsPingTime.containsKey(name)) {
				throw new IllegalArgumentException(name+" is not in the map");
			}
			threadsPingTime.remove(name);
		}
	}
	
	/**
	 * Signal that thread t is alive
	 * @param t
	 */
	public void ping(Thread t) {
		if (t==null) {
			throw new IllegalArgumentException("Can't check a null thread");
		}
		String name=t.getName();
		if (name==null || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid thread name. Please set a name to your thread");
		}
		synchronized (threadsPingTime) {
			if (!threadsPingTime.containsKey(name)) {
				throw new IllegalStateException("Register thread before calling ping");
			}
			threadsPingTime.put(name, System.currentTimeMillis());
		}
	}
	
	/**
	 * Check if all the registered threads are alive, i.e. 
	 * if they have called <code>ping</code> at list once.
	 * 
	 * @return the name of the thread that does not respond or
	 * 			<code>null</code> otherwise
	 */
	private String watchdog() {
		String ret=null;
		synchronized (threadsPingTime) {
			Set<String> keys=threadsPingTime.keySet();
			for (String key: keys) {
				if (System.currentTimeMillis()>threadsPingTime.get(key)+threshold) {
					if (ret==null) {
						ret=key+" ";
					} else {
						ret=ret+key+" ";
					}
				}
			}
			return null;
		}
	}
	
	@Override
	public void run() {
		while (!terminateThread) {
			try {
				Thread.sleep(refreshInterval);
			} catch (InterruptedException ie) {
				continue;
			}
			updateIcon(watchdog());
		}	
	}
}
