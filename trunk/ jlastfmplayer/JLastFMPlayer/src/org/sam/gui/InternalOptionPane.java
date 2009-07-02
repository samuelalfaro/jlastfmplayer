/*
 * InternalOptionPane.java
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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class InternalOptionPane extends TranslucentRoundedPane {

	/**
	 * Shows a question-message dialog requesting input from the user parented
	 * to <code>parentComponent</code>. The dialog is displayed on top of the
	 * <code>Component</code>'s frame, and is usually positioned below the
	 * <code>Component</code>.
	 * 
	 * @param parentComponent
	 *            the parent <code>Component</code> for the dialog
	 * @param message
	 *            the <code>Object</code> to display
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static String showInputDialog(JFrame parentComponent, Object message) throws HeadlessException {
		return showInputDialog(
				parentComponent,
				message, 
				UIManager.getString(
						"OptionPane.inputDialogTitle",
						parentComponent != null? parentComponent.getLocale() : Locale.getDefault()
				),
				JOptionPane.QUESTION_MESSAGE
		);
	}

	/**
	 * Shows a question-message dialog requesting input from the user and
	 * parented to <code>parentComponent</code>. The input value will be
	 * initialized to <code>initialSelectionValue</code>. The dialog is
	 * displayed on top of the <code>Component</code>'s frame, and is usually
	 * positioned below the <code>Component</code>.
	 * 
	 * @param parentComponent
	 *            the parent <code>Component</code> for the dialog
	 * @param message
	 *            the <code>Object</code> to display
	 * @param initialSelectionValue
	 *            the value used to initialize the input field
	 * @since 1.4
	 */
	public static String showInputDialog(JFrame parentComponent, Object message, Object initialSelectionValue) {
		return (String) showInputDialog(
				parentComponent, 
				message,
				UIManager.getString(
						"OptionPane.inputDialogTitle",
						parentComponent != null? parentComponent.getLocale() : Locale.getDefault()
				),
				JOptionPane.QUESTION_MESSAGE,
				null, null, initialSelectionValue
		);
	}

	/**
	 * Shows a dialog requesting input from the user parented to
	 * <code>parentComponent</code> with the dialog having the title
	 * <code>title</code> and message type <code>messageType</code>.
	 * 
	 * @param parentComponent
	 *            the parent <code>Component</code> for the dialog
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the <code>String</code> to display in the dialog title bar
	 * @param messageType
	 *            the type of message that is to be displayed:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static String showInputDialog(JFrame parentComponent, Object message, String title, int messageType)
			throws HeadlessException {
		return (String) showInputDialog(
				parentComponent, 
				message, 
				title,
				messageType,
				null, null, null
		);
	}

	/**
	 * Prompts the user for input in a blocking dialog where the initial
	 * selection, possible selections, and all other options can be specified.
	 * The user will able to choose from <code>selectionValues</code>, where
	 * <code>null</code> implies the user can input whatever they wish, usually
	 * by means of a <code>JTextField</code>. <code>initialSelectionValue</code>
	 * is the initial value to prompt the user with. It is up to the UI to
	 * decide how best to represent the <code>selectionValues</code>, but
	 * usually a <code>JComboBox</code>, <code>JList</code>, or
	 * <code>JTextField</code> will be used.
	 * 
	 * @param parentComponent
	 *            the parent <code>Component</code> for the dialog
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the <code>String</code> to display in the dialog title bar
	 * @param messageType
	 *            the type of message to be displayed:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @param icon
	 *            the <code>Icon</code> image to display
	 * @param selectionValues
	 *            an array of <code>Object</code>s that gives the possible
	 *            selections
	 * @param initialSelectionValue
	 *            the value used to initialize the input field
	 * @return user's input, or <code>null</code> meaning the user canceled the
	 *         input
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	@SuppressWarnings("cast")
	public static Object showInputDialog(JFrame parentComponent, Object message, String title, int messageType,
			Icon icon, Object[] selectionValues, Object initialSelectionValue) throws HeadlessException {
		//TODO chequear
		InternalOptionPane pane = new InternalOptionPane(title, icon, message, messageType, selectionValues, initialSelectionValue, JOptionPane.OK_CANCEL_OPTION, true);
		
		Container glass = (Container)parentComponent.getGlassPane(); 
		glass.add(pane);
		glass.setFocusCycleRoot(true);
		glass.setVisible(true);
//		pane.inputComponent.requestFocus();
	
		Object selectedValue = pane.getValue();
		if( selectedValue == JOptionPane.UNINITIALIZED_VALUE || selectedValue == (Integer)JOptionPane.CANCEL_OPTION ){
			return null;
		}
		return selectedValue;
	}

	/**
	 * Brings up an information-message dialog titled "Message".
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static void showMessageDialog(JFrame parentComponent, Object message) throws HeadlessException {
		showMessageDialog(
				parentComponent, 
				message,
				UIManager.getString("OptionPane.inputDialogTitle", parentComponent==null?
						Locale.getDefault() : parentComponent.getLocale()),
				JOptionPane.INFORMATION_MESSAGE
		);
	}

	/**
	 * Brings up a dialog that displays a message using a default icon
	 * determined by the <code>messageType</code> parameter.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the title string for the dialog
	 * @param messageType
	 *            the type of message to be displayed:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static void showMessageDialog(JFrame parentComponent, Object message, String title, int messageType)
			throws HeadlessException {
		showMessageDialog(
				parentComponent,
				message,
				title,
				messageType,
				null
		);
	}

	/**
	 * Brings up a dialog displaying a message, specifying all parameters.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the title string for the dialog
	 * @param messageType
	 *            the type of message to be displayed:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @param icon
	 *            an icon to display in the dialog that helps the user identify
	 *            the kind of message that is being displayed
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static void showMessageDialog(JFrame parentComponent, Object message, String title, int messageType,
			Icon icon) throws HeadlessException {
		showOptionDialog(
				parentComponent,
				message,
				title,
				JOptionPane.DEFAULT_OPTION,
				messageType,
				icon,
				null, null
		);
	}

	/**
	 * Brings up a dialog with the options <i>Yes</i>, <i>No</i> and
	 * <i>Cancel</i>; with the title, <b>Select an Option</b>.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @return an integer indicating the option selected by the user
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static int showConfirmDialog(JFrame parentComponent, Object message) throws HeadlessException {
		return showConfirmDialog(
				parentComponent,
				message,
				UIManager.getString("OptionPane.titleText"),
				JOptionPane.YES_NO_CANCEL_OPTION
		);
	}

	/**
	 * Brings up a dialog where the number of choices is determined by the
	 * <code>optionType</code> parameter.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the title string for the dialog
	 * @param optionType
	 *            an int designating the options available on the dialog:
	 *            <code>YES_NO_OPTION</code>, <code>YES_NO_CANCEL_OPTION</code>,
	 *            or <code>OK_CANCEL_OPTION</code>
	 * @return an int indicating the option selected by the user
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static int showConfirmDialog(JFrame parentComponent, Object message, String title, int optionType)
			throws HeadlessException {
		return showConfirmDialog(
				parentComponent,
				message,
				title,
				optionType,
				JOptionPane.QUESTION_MESSAGE
		);
	}

	/**
	 * Brings up a dialog where the number of choices is determined by the
	 * <code>optionType</code> parameter, where the <code>messageType</code>
	 * parameter determines the icon to display. The <code>messageType</code>
	 * parameter is primarily used to supply a default icon from the Look and
	 * Feel.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used.
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the title string for the dialog
	 * @param optionType
	 *            an integer designating the options available on the dialog:
	 *            <code>YES_NO_OPTION</code>, <code>YES_NO_CANCEL_OPTION</code>,
	 *            or <code>OK_CANCEL_OPTION</code>
	 * @param messageType
	 *            an integer designating the kind of message this is; primarily
	 *            used to determine the icon from the pluggable Look and Feel:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @return an integer indicating the option selected by the user
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static int showConfirmDialog(JFrame parentComponent, Object message, String title, int optionType,
			int messageType) throws HeadlessException {
		return showConfirmDialog(
				parentComponent,
				message,
				title,
				optionType,
				messageType,
				null
		);
	}

	/**
	 * Brings up a dialog with a specified icon, where the number of choices is
	 * determined by the <code>optionType</code> parameter. The
	 * <code>messageType</code> parameter is primarily used to supply a default
	 * icon from the look and feel.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the Object to display
	 * @param title
	 *            the title string for the dialog
	 * @param optionType
	 *            an int designating the options available on the dialog:
	 *            <code>YES_NO_OPTION</code>, <code>YES_NO_CANCEL_OPTION</code>,
	 *            or <code>OK_CANCEL_OPTION</code>
	 * @param messageType
	 *            an int designating the kind of message this is, primarily used
	 *            to determine the icon from the pluggable Look and Feel:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @param icon
	 *            the icon to display in the dialog
	 * @return an int indicating the option selected by the user
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static int showConfirmDialog(JFrame parentComponent, Object message, String title, int optionType,
			int messageType, Icon icon) throws HeadlessException {
		return showOptionDialog(
				parentComponent,
				message,
				title,
				optionType,
				messageType,
				icon,
				null, null
		);
	}

	/**
	 * Brings up a dialog with a specified icon, where the initial choice is
	 * determined by the <code>initialValue</code> parameter and the number of
	 * choices is determined by the <code>optionType</code> parameter.
	 * <p>
	 * If <code>optionType</code> is <code>YES_NO_OPTION</code>, or
	 * <code>YES_NO_CANCEL_OPTION</code> and the <code>options</code> parameter
	 * is <code>null</code>, then the options are supplied by the look and feel.
	 * <p>
	 * The <code>messageType</code> parameter is primarily used to supply a
	 * default icon from the look and feel.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is
	 *            displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a
	 *            default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the title string for the dialog
	 * @param optionType
	 *            an integer designating the options available on the dialog:
	 *            <code>DEFAULT_OPTION</code>, <code>YES_NO_OPTION</code>,
	 *            <code>YES_NO_CANCEL_OPTION</code>, or
	 *            <code>OK_CANCEL_OPTION</code>
	 * @param messageType
	 *            an integer designating the kind of message this is, primarily
	 *            used to determine the icon from the pluggable Look and Feel:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
	 *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @param icon
	 *            the icon to display in the dialog
	 * @param options
	 *            an array of objects indicating the possible choices the user
	 *            can make; if the objects are components, they are rendered
	 *            properly; non-<code>String</code> objects are rendered using
	 *            their <code>toString</code> methods; if this parameter is
	 *            <code>null</code>, the options are determined by the Look and
	 *            Feel
	 * @param initialValue
	 *            the object that represents the default selection for the
	 *            dialog; only meaningful if <code>options</code> is used; can
	 *            be <code>null</code>
	 * @return an integer indicating the option chosen by the user, or
	 *         <code>CLOSED_OPTION</code> if the user closed the dialog
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static int showOptionDialog(JFrame parentComponent, Object message, String title, int optionType,
			int messageType, Icon icon, Object[] options, Object initialValue) throws HeadlessException {
		
		final InternalOptionPane pane = new InternalOptionPane(title, icon, message, messageType, options, initialValue, optionType, false);
		
		Container glass = (Container)parentComponent.getGlassPane(); 
		glass.add(pane);
		glass.setFocusCycleRoot(true);
		glass.setVisible(true);

		//TODO completar
//		pane.optionsPane.getComponent(0).requestFocus();
//		pane.setInitialValue(initialValue);
		
		Object selectedValue = pane.getValue();

		if( selectedValue == null )
			return JOptionPane.CLOSED_OPTION;
		if( options == null ){
			if( selectedValue instanceof Integer )
				return ((Integer) selectedValue).intValue();
			return JOptionPane.CLOSED_OPTION;
		}
		for( int i = 0, len = options.length; i < len; i++ ){
			if( options[i].equals(selectedValue) )
				return i;
		}
		return JOptionPane.CLOSED_OPTION;
	}

	private Object value;
	private JComponent inputComponent;
	private final JPanel optionsPane;

	private InternalOptionPane(String title, Icon icon, Object message, int messageType,
			Object[] options, Object initialValue, int optionType, boolean input) {
		if(input){
			optionsPane = createOptionsPane(optionType);
			intiUI(
					createTitleLabel(title),
					createContentPane(icon, message, messageType, options, initialValue),
					optionsPane
			);
		}else{
			optionsPane = options == null ? createOptionsPane(optionType) : createOptionsPane(options);
			intiUI(
					createTitleLabel(title),
					createContentPane(icon, message, messageType),
					optionsPane
			);
		}
		if(initialValue != null)
			value = initialValue;
		else
			value = JOptionPane.UNINITIALIZED_VALUE;
	}

	private static Icon getIcon(int messageType){
		switch(messageType){
		case JOptionPane.ERROR_MESSAGE:
			return (Icon)UIManager.get("OptionPane.errorIcon");
		case JOptionPane.INFORMATION_MESSAGE:
			return (Icon)UIManager.get("OptionPane.informationIcon");
		case JOptionPane.WARNING_MESSAGE:
			return (Icon)UIManager.get("OptionPane.warningIcon");
		case JOptionPane.QUESTION_MESSAGE:
			return (Icon)UIManager.get("OptionPane.questionIcon");
		case JOptionPane.PLAIN_MESSAGE:
		default:
		}
		return null;
	}
	
	private JLabel createTitleLabel(String title){
		if(title == null)
			return null;
		JLabel titleLabel = new ShadowLabel(title);
		titleLabel.setForeground(((Color)UIManager.get("Panel.background")).brighter());
		Font font = titleLabel.getFont();
		titleLabel.setFont(font.deriveFont(Font.BOLD).deriveFont(font.getSize() * 1.4f));
		titleLabel.setVerticalAlignment(SwingConstants.TOP);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		return titleLabel;
	}
	
	private static String toHTMLmultilineString(String text){
		if (text.contains("<html>"))
			return text;
		StringTokenizer st = new StringTokenizer(text, "\n");
		if( st.countTokens() == 1)
			return text;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		while(st.hasMoreElements()){
			sb.append(st.nextToken());
			sb.append("<br>");
		}
		sb.append("</html>");
		return sb.toString();
	}

	private JPanel createContentPane(Icon icon, Object message, int messageType){
		JPanel content = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		if(icon == null)
			icon = getIcon(messageType);
		if(icon != null){
			JLabel iconLabel = new JLabel(icon,SwingConstants.CENTER);
			iconLabel.setVerticalAlignment(SwingConstants.CENTER);
			iconLabel.setFocusable(false);
			boolean isLeftToRight = ComponentOrientation.getOrientation(getLocale()).isLeftToRight();
			gbc.insets.set(0, isLeftToRight?0:10, 0, isLeftToRight?10:0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridwidth = 1;
			gbc.gridheight = GridBagConstraints.REMAINDER;
			gbc.weightx = 0.0;
			gbc.weighty = 1.0;
			content.add(iconLabel, gbc);
		}
		if(message != null){
			Component cMessage;
			if(message instanceof Component)
				cMessage = (Component)message;
			else
				cMessage = new JLabel(toHTMLmultilineString(message.toString()));
			cMessage.setFocusable(false);
			gbc.insets.set(0,0,0,0);
			gbc.gridx = GridBagConstraints.RELATIVE;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridheight = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			content.add(cMessage, gbc);
		}
		return content;
	}
	
	private JPanel createContentPane(Icon icon, Object message, int messageType, Object[] options, Object initialValue){
		JPanel content = createContentPane(icon, message, messageType);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets.set(0,0,0,0);
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		if(options != null){
			JList list = new JList(options);
			list.setVisibleRowCount(Math.min(5, options.length ));
			if(initialValue != null)
				list.setSelectedValue(initialValue, true);
			list.registerKeyboardAction(
					new EnterListAction(),
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
					JComponent.WHEN_FOCUSED);
			inputComponent = list;
			content.add(new JScrollPane(inputComponent),gbc);
		}else{
			JTextField text = new JTextField();
			if(initialValue != null)
				text.setText(initialValue.toString());
			text.registerKeyboardAction(
					new EnterTextFieldAction(),
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
					JComponent.WHEN_FOCUSED);
			inputComponent = text;
			content.add(inputComponent,gbc);
		}
		return content;
	}
	
	private class EnterListAction extends AbstractAction{
		public void actionPerformed(ActionEvent e) {
			value = ((JList)inputComponent).getSelectedValue();
			hideParent();
		}
	}

	private class EnterTextFieldAction extends AbstractAction{
		public void actionPerformed(ActionEvent e) {
			value = ((JTextField)inputComponent).getText();
			hideParent();
		}
	}
	
	private class YesAction extends AbstractAction{
		YesAction(String s){
			super(s);
		}
		
		public void actionPerformed(ActionEvent e) {
			value = JOptionPane.YES_OPTION;
			hideParent();
		}
	}

	private class NoAction extends AbstractAction{
		NoAction(String s){
			super(s);
		}
		
		public void actionPerformed(ActionEvent e) {
			value = JOptionPane.NO_OPTION;
			hideParent();
		}
	}

	private class OkAction extends AbstractAction{
		OkAction(String s){
			super(s);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(inputComponent != null){
				if(inputComponent instanceof JTextField)
					value = ((JTextField)inputComponent).getText();
				else
					value = ((JList)inputComponent).getSelectedValue();
			}else
				value = JOptionPane.OK_OPTION;
			hideParent();
		}
	}

	private class CancelAction extends AbstractAction{
		CancelAction(String s){
			super(s);
		}
		
		public void actionPerformed(ActionEvent e) {
			value = JOptionPane.CANCEL_OPTION;
			hideParent();
		}
	}

	private class OptionAction extends AbstractAction{
		final Object option;
		OptionAction(Object option){
			super(option.toString());
			this.option = option;
		}
		
		public void actionPerformed(ActionEvent e) {
			value = option;
			hideParent();
		}
	}

	private void hideParent(){
		getParent().setVisible(false);
		getParent().remove(this);
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
	
	private JPanel createOptionsPane(int optionType){
		ResourceBundle basic = ResourceBundle.getBundle("com.sun.swing.internal.plaf.basic.resources.basic");
		JPanel buttons = new JPanel();
		
		switch(optionType){
		case JOptionPane.YES_NO_OPTION:
			buttons.add(new JButton(new YesAction(basic.getString("OptionPane.yesButtonText"))));
			buttons.add(new JButton(new NoAction(basic.getString("OptionPane.noButtonText"))));
			break;
		case JOptionPane.YES_NO_CANCEL_OPTION:
			buttons.add(new JButton(new YesAction(basic.getString("OptionPane.yesButtonText"))));
			buttons.add(new JButton(new NoAction(basic.getString("OptionPane.noButtonText"))));
			buttons.add(new JButton(new CancelAction(basic.getString("OptionPane.cancelButtonText"))));
			break;
		case JOptionPane.OK_CANCEL_OPTION:
			buttons.add(new JButton(new OkAction(basic.getString("OptionPane.okButtonText"))));
			buttons.add(new JButton(new CancelAction(basic.getString("OptionPane.cancelButtonText"))));
			break;
		case JOptionPane.DEFAULT_OPTION:
		default:
			buttons.add(new JButton(new OkAction(basic.getString("OptionPane.okButtonText"))));
		}
		for(Component c:buttons.getComponents()){
			JButton button = (JButton)c; 
			registerEnterAction(button);
			button.setOpaque(false);
		}
		return buttons;
	}

	private JPanel createOptionsPane(Object[] options){
		JPanel buttons = new JPanel();
		for(Object option: options){
			if(option instanceof Component)
				buttons.add((Component)option);
			else
				buttons.add(new JButton(new OptionAction(option)));
		}
		for(Component c:buttons.getComponents()){
			if(c instanceof JButton)
				registerEnterAction((JButton)c);
			if(c instanceof JComponent)
				((JComponent)c).setOpaque(false);
		}
		return buttons;
	}
	
	private void intiUI(JComponent title, JPanel contentPane, JPanel buttonsPane) {

		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		if( title != null ){
			title.setOpaque(false);
			title.setFocusable(false);
			this.add(title, BorderLayout.NORTH);
		}
		if( contentPane != null ){
			contentPane.setOpaque(false);
			contentPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			this.add(contentPane, BorderLayout.CENTER);
		}
		if( buttonsPane != null ){
			buttonsPane.setOpaque(false);
			this.add(buttonsPane, BorderLayout.SOUTH);
		}
	}

	private Object getValue() {
		return value;
	}
}