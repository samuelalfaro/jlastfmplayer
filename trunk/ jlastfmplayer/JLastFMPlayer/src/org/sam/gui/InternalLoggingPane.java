/*
 * InternalLoggingPane.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of JLastFMPlayer.
 * 
 * JLastFMPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JLastFMPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JLastFMPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.gui;

import java.awt.Container;

import javax.swing.*;

/**
 * A modal dialog based in <a target="_top" href="http://ostermiller.org/utils/PasswordDialog.html">Password Dialog - com.Ostermiller.util Java Utilities</a>.
 * 
 * <code><pre>
 * LoggingData logging = new InternalLoggingPane().showDialog();
 * if( logging != null ){
 * 	System.out.println("User:     " + logging.getUser());
 * 	System.out.println("Password: " + logging.getPassword());
 * 	System.out.println("Remember: " + logging.isRemember());
 * }else
 * 	System.out.println("User selected cancel");
 * </pre></code>
 * 
 * @author Samuel Alfaro
 */
public class InternalLoggingPane{

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 5626350056146664198L;
	
	private final Container glassPane;
	private final JComponent panel;
	private final LoggingManager manager;

	/**
	 * Create this dialog with the default title.
	 */
	public InternalLoggingPane() {
		this(null);
	}

	/**
	 * Create this dialog with the given parent and the default title.
	 * 
	 * @param parent
	 *            window from which this dialog is launched
	 */
	public InternalLoggingPane(JFrame parent) {
		this(parent, null);
	}

	/**
	 * Create this dialog with the given parent and title.
	 * 
	 * @param parent
	 *            window from which this dialog is launched
	 * @param title
	 *            the title for the dialog box window
	 */
	public InternalLoggingPane(JFrame parent, String title) {
		this(parent, title, false, false);
	}

	/**
	 * @param parent
	 * @param title
	 * @param showRemember
	 */
	public InternalLoggingPane(JFrame parent, String title, boolean showRemember, boolean showCancel) {
		this(parent, title, showRemember, showCancel, null);
	}
	
	/**
	 * @param parent
	 * @param title
	 * @param showRemember
	 * @param icon
	 */
	public InternalLoggingPane(JFrame parent, String title, boolean showRemember, boolean showCancel, Icon icon) {
		manager = new LoggingManager();
		glassPane = (Container) parent.getGlassPane();
		panel = new TranslucentRoundedPane();
		manager.initUI(glassPane, panel, icon, showRemember, showCancel);
	}

	/**
	 * showDialog(null)
	 * @see #showDialog(LoggingData)
	 */
	public LoggingData showDialog() {
		return showDialog(null);
	}
	
	/**
	 * Shows the dialog and returns LoggingData or null.
	 * @param loggingData
	 * 		initial values and object to store values.
	 * @return
	 * <ul>
	 * 	<li>null if cancel
	 * 	<li>new LoggingData whit strored valules if loggingData == null
	 * 	<li>loggingData whit strored valules if loggingData != null
	 * </ul>
	 */
	public LoggingData showDialog(LoggingData loggingData) {
		
		manager.setLoggingData(loggingData);
		glassPane.add(panel);
		glassPane.setFocusCycleRoot(true);
		glassPane.setVisible(true);

		LoggingData data = manager.getLoggingData();
		glassPane.remove(panel);
		return data;
	}
	
}
