/*
 * ModalBlurGlassPane.java
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
/**
 * 
 */
package org.sam.gui;

import java.awt.*;

import javax.swing.SwingUtilities;

/**
 * @author Samuel Alfaro <samuelalfaro at gmail.com>
 *
 */
public class ModalBlurGlassPane extends BlurGlassPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7730729404904555941L;

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible ){
			getFocusTraversalPolicy().getDefaultComponent(this).requestFocus();
			startModal();
		}else
			stopModal();
	}

	private synchronized void startModal() {
		try{
			if( SwingUtilities.isEventDispatchThread() ){
				EventQueue theQueue = getToolkit().getSystemEventQueue();
				while( isVisible() ){
					AWTEvent event = theQueue.getNextEvent();
					Object source = event.getSource();
					if( event instanceof ActiveEvent ){
						((ActiveEvent) event).dispatch();
					}else if( source instanceof Component ){
						((Component) source).dispatchEvent(event);
					}else if( source instanceof MenuComponent ){
						((MenuComponent) source).dispatchEvent(event);
					}else{
						System.err.println("Unable to dispatch: " + event);
					}
				}
			}else{
				while( isVisible() ){
					wait();
				}
			}
		}catch( InterruptedException ignored ){
		}
	}

	private synchronized void stopModal() {
		notifyAll();
	}
}
