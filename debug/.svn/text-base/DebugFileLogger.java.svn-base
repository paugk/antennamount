/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2010
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
package alma.control.gui.antennamount.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import alma.acs.util.IsoDateFormat;
import alma.control.gui.antennamount.errortab.ErrorInfo;

/**
 * A class to log messages on a set of files while the panel runs 
 * in debug mode.
 * <P>
 * Objects of this class write log messages in the current folder.
 * The name of each file is composed of string with the following format:<BR>
 * {@link DebugFileLogger#fileName}_time_N.log<BR>
 * where:
 * <UL>
 * 	<LI>time: the time when this object has been created; it is unique for each session
 * 		and allow to run several instances of the panel in debug mode without mixing
 * 		the log files
 *  <LI>N: a progressive number for each generated file
 * </UL>
 * <P>
 * If there is an error creating the log file, the messages are redirected
 * to stdout (<code>System.out</code>) and no further attempt to create a log file 
 * will be made.
 * <P>
 * To reduce the I/O, the length of the file is checked every {@link DebugFileLogger#FILE_LENGTH_CHECK_RATE}
 * insertions of log messages; if the size of the current file is
 * greater the {@link DebugFileLogger#MAX_LOG_FILE_SIZE} then a new file is created. 
 * 
 * @author acaproni
 *
 */
public class DebugFileLogger {
	
	/**
	 * The prefix of the name of each generated file
	 */
	private final String fileName="mountPanelDebug";
	
	/**
	 * The progressive number in the file name
	 */
	private int fileNumber=0;
	
	/**
	 * The max dimension of each file of log in bytes.
	 */
	private final long MAX_LOG_FILE_SIZE=1024*1024*1024; // 1 Gb
	
	/**
	 * The writer to write log messages into.
	 */
	private PrintStream logFile=null;
	
	/**
	 * The file used for output.
	 * <P>
	 * If <code>null</code> then the output is performed on <code>System.out</code>
	 * because of an error creating the new file.
	 */
	private File outF; 
	
	/**
	 * The length of the file is checked when the number of inserted log
	 * messages is greater then <code>FILE_LENGTH_CHECK_RATE</code>.
	 * 
	 * @see DebugFileLogger#insertionsCounter
	 */
	private static final int FILE_LENGTH_CHECK_RATE=500;
	
	/**
	 * Count how many log messages have been inserted since the last
	 * checking of the length of the file
	 * 
	 * @see DebugFileLogger#FILE_LENGTH_CHECK_RATE
	 */
	private int insertionsCounter=0;
	
	/** 
	 * Constructor
	 */
	public DebugFileLogger() {
		getNewLogfile();
		printLogHeader();
	}
	
	/**
	 * Create a new log file.
	 * <P>
	 * If an error happen while creating a log file, then
	 * System.out is used and no further attempt to create a log
	 * file will be made.
	 * 
	 * @return The newly created file for output or 
	 * 			<code>System.out</code> in case of error
	 */
	private synchronized PrintStream getNewLogfile() {
		if (logFile!=System.out && logFile!=null) {
			logFile.flush();
			logFile.close();
			logFile=null;
			outF=null;
		}
		// Generate the file name
		StringBuilder newFilename = new StringBuilder(fileName);
		newFilename.append('_');
		newFilename.append(IsoDateFormat.formatCurrentDate());
		newFilename.append('_');
		newFilename.append(fileNumber++);
		newFilename.append(".log");
		outF = new File(newFilename.toString());
		OutputStream oStream;
		try {
			oStream = new FileOutputStream(outF);
		} catch (Throwable t) {
			// we don't want that this class propagate an error
			// so we simply log a message in the stderr and return null
			System.err.print("Error creating a file of log with name "+newFilename.toString());
			System.err.println(": "+t.getMessage());
			outF=null;
			logFile=System.out;
			return logFile;
		}
		logFile = new PrintStream(oStream,true);
		return logFile;
	}
	
	private void printLogHeader() {
		logFile.print("Mount panel debug session started at ");
		logFile.println(IsoDateFormat.formatCurrentDate());
		logFile.print(dumpMemory());
		Runtime runTime = Runtime.getRuntime();
		logFile.println("Available processors: "+runTime.availableProcessors());
		logFile.println("*******************************************\n");
	}
	
	/**
	 * Check if the length of the output file and if it is the case
	 * create a new file.
	 * 
	 */
	private synchronized void checkFileSize() {
		if (outF==null) {
			// The process is writing in System.out
			return;
		}
		if (outF.length()>MAX_LOG_FILE_SIZE) {
			getNewLogfile();
		}
	}

	/**
	 * Dump the status of the memory
	 * 
	 * @return A string describing the state of the memory
	 */
	private String dumpMemory() {
		Runtime runTime = Runtime.getRuntime();
		StringBuilder str = new StringBuilder("Max aumount of memory for the JVM (Kb):  ");
		str.append(runTime.maxMemory()/1024);
		str.append('\n');
		str.append("Total aumount of memory in the JVM (Kb): ");
		str.append(runTime.totalMemory()/1024);
		str.append('\n');
		str.append("Free memory at startup (Kb):             ");
		str.append(runTime.freeMemory()/1024);
		str.append('\n');
		return str.toString();
	}
	
	/**
	 * Write a log message in the file.
	 * <P>
	 * The message is prepended by the timestamp.
	 */
	public synchronized void log(String msg) {
		if (msg==null || msg.isEmpty()) {
			return;
		}
		logFile.print(IsoDateFormat.formatCurrentDate());
		logFile.print(": ");
		logFile.print(msg);
		if (!msg.endsWith("\n")) {
			logFile.println();
		}
		if (++insertionsCounter>FILE_LENGTH_CHECK_RATE) {
			checkFileSize();
			insertionsCounter=0;
		}
	}
	
	/**
	 * Write an {@link ErrorInfo} in the file.
	 * 
	 * @param errorInfo The error to write in the file
	 */
	public synchronized void log(ErrorInfo errorInfo) {
		if (errorInfo==null) {
			return;
		}
		StringBuilder ret = new StringBuilder();
		ret.append("ERROR: "+errorInfo.shortDescription);
		ret.append("\n\t");
		ret.append(errorInfo.detailedDescription);
		if (errorInfo.getInstances()>0) {
			ret.append("\n\tDetected ");
			ret.append(errorInfo.getInstances());
			ret.append("instances of the same error.");
		}
		this.log(ret.toString());
	}
}
