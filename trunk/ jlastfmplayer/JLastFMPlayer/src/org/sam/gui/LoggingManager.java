/*
 * LoggingManager.java
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

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class LoggingManager {

	/**
	 * showDialog(null)
	 * @see #showDialog(Frame)
	 */
	public static LoggingData showDialog() {
		return showDialog(null);
	}

	/**
	 * Create this dialog with the given parent and the default title.
	 * 
	 * @param parent
	 *            window from which this dialog is launched
	 * @see #showDialog(Frame, String)
	 */
	public static LoggingData showDialog(Frame parent) {
		return showDialog(parent, null);
	}

	/**
	 * Create this dialog with the given parent and title.
	 * 
	 * @param parent
	 *            window from which this dialog is launched
	 * @param title
	 *            the title for the dialog box window
	 * @see #showDialog(Frame, String, boolean, boolean)
	 */
	public static LoggingData showDialog(Frame parent, String title) {
		return showDialog(parent, title, false, false);
	}

	/**
	 * @param parent
	 * @param title
	 * @param showRemember
	 * @see #showDialog(Frame, String, boolean, boolean, Icon)
	 */
	public static LoggingData showDialog(Frame parent, String title, boolean showRemember, boolean showCancel) {
		return showDialog(parent, title, showRemember, showCancel, null);
	}

	/**
	 * @param parent
	 * @param title
	 * @param showRemember
	 * @param icon
	 * @see #showDialog(Frame, String, boolean, boolean, Icon, LoggingData)
	 */
	public static LoggingData showDialog(Frame parent, String title, boolean showRemember, boolean showCancel, Icon icon) {
		return showDialog(parent, title, showRemember, showCancel, icon, null);
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
	public static LoggingData showDialog(Frame parent, String title, boolean showRemember, boolean showCancel, Icon icon, LoggingData loggingData) {
		// TODO set data null when dialog is colsed
		JDialog dialog = new JDialog(parent, title, true);
		LoggingManager manager = new LoggingManager();
		manager.initUI(dialog, (JComponent)dialog.getContentPane(), icon, showRemember, showCancel);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(parent);
		
		manager.setLoggingData(loggingData);
		dialog.setVisible(true);
		return manager.getLoggingData();
	}

	/**
	 * Where the user is typed.
	 */
	private JTextField user;

	/**
	 * Where the password is typed.
	 */
	private JPasswordField password;
	
	/**
	 * The remember CheckBox
	 */
	private JCheckBox rememberPass;

	/**
	 * The OK button.
	 */
	private JButton okButton;

	/**
	 * update this variable when the user makes an action
	 */
	private LoggingData data;

	/**
	 * 
	 */
	LoggingManager(){
	}
	
	private static void registerEnterAction(JButton button){
		button.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
				button.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false))
		);
		button.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
				button.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true))
		);
	}

	private void checkButtonsEnabled(){
		boolean enabled = user.getText().length() != 0 && password.getPassword().length != 0;
		if(okButton.isEnabled() != enabled){
			okButton.setEnabled(enabled);
			if(rememberPass != null)
				rememberPass.setEnabled(enabled);
		}	
	}

	@SuppressWarnings("serial")
	JComponent initUI(
			final Container parent, JComponent pane,
			final Icon icon, final boolean showRemember, final boolean showCancel) {

		Locale locale = parent != null ? parent.getLocale() : Locale.getDefault(); 
		ResourceBundle labels = ResourceBundle.getBundle("org.sam.gui.translations.messages", locale);
		ResourceBundle basic = ResourceBundle.getBundle("com.sun.swing.internal.plaf.basic.resources.basic", locale);
		
		CaretListener caretListener = new CaretListener(){
			public void caretUpdate(CaretEvent e) {
				checkButtonsEnabled();
			}
		};
		
		final Action cancelAction = new AbstractAction(basic.getString("OptionPane.cancelButtonText")){
			public void actionPerformed(ActionEvent e) {
				data = null;
				parent.setVisible(false);
			}
		};
		
		final Action okAction = new AbstractAction(basic.getString("OptionPane.okButtonText")){
			public void actionPerformed(ActionEvent e) {
				if(data == null)
					data = new LoggingData();
				data.setUser( user.getText() );
				data.setPassword( new String(password.getPassword()) );
				data.setRemember( rememberPass != null && rememberPass.isSelected() );
				parent.setVisible(false);
			}
		};
		
		pane.setLayout(new GridBagLayout());
		
		pane.getActionMap().put(
				"cancelAction",
				cancelAction
		);
		pane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
				"cancelAction"
		);
		
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 1.0;
		
		if(icon != null){
			gbc.gridx = 0;
			gbc.gridheight = 2;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets.set(0, 0, 0, 10);
			pane.add(new JLabel(icon), gbc);
			gbc.gridheight = 1;
		}
		
		gbc.gridx = icon == null ? 0 : 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets.set(0, 0, 10, 10);
		pane.add(new JLabel(labels.getString("Dialog.loggin.name")),gbc);

		user = new JTextField(15);
		user.addCaretListener(caretListener);
		user.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				user.transferFocus();
			}
		});

		gbc.gridx = icon == null ? 1 : 2;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.0;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets.set(0, 0, 10, 0);
		pane.add(user,gbc);

		gbc.gridx = icon == null ? 0 : 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets.set(0, 0, 0, 10);
		pane.add(new JLabel(labels.getString("Dialog.loggin.pass")),gbc);

		password = new JPasswordField(15);
		password.addCaretListener(caretListener);
		password.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if( password.getPassword().length == 0 || user.getText().length() == 0 || rememberPass != null )
					password.transferFocus();
				else
					okAction.actionPerformed(e);
			}
		});

		gbc.gridx = icon == null ? 1 : 2;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets.set(0, 0, 0, 0);
		pane.add(password,gbc);
		
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		buttonsPanel.setOpaque(false);

		if(showRemember){
			rememberPass = new JCheckBox(labels.getString("Dialog.loggin.remember"));
			rememberPass.setOpaque(false);

			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets.set(0, 0, 0, 10);
			buttonsPanel.add(rememberPass,gbc);
		}
		
		okButton = new JButton(okAction);
		registerEnterAction(okButton);
		
		gbc.gridx = showRemember ? 1:0;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets.set(0, 0, 0, 0);
		buttonsPanel.add(okButton,gbc);
		
		if(showCancel){
			JButton cancelButton = new JButton(cancelAction);
			registerEnterAction(cancelButton);
			gbc.gridx = showRemember ? 2:1;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets.set(0, 5, 0, 0);
			buttonsPanel.add(cancelButton,gbc);
		}
		
		gbc.gridx = icon == null ? 0 : 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets.set(10, 0, 0, 0);
		
		pane.add(buttonsPanel,gbc);
		
		return pane;
	}

	void setLoggingData(LoggingData loggingData){
		if(loggingData == null){
			user.setText(null);
			password.setText(null);
			if(rememberPass != null)
				rememberPass.setSelected(false);
		}else{
			user.setText(loggingData.getUser());
			if(loggingData.getUser() != null)
				user.setCaretPosition(loggingData.getUser().length());
			password.setText(loggingData.getPassword());
			if(loggingData.getPassword() != null)
				password.setCaretPosition(loggingData.getPassword().length());
			if(rememberPass != null)
				rememberPass.setSelected(loggingData.isRemember());
		}
		checkButtonsEnabled();
		data = loggingData;
	}
	
	LoggingData getLoggingData(){
		return data;
	}
}
