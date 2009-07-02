/*
 * AbstractPlayerListener.java
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

package core;

import java.util.EventObject;

public abstract class AbstractPlayerListener implements PlayerListener {

	/* (non-Javadoc)
	 * @see core.PlayerListener#endReached(java.util.EventObject)
	 */
	public void endReached(EventObject e) {
	}

	/* (non-Javadoc)
	 * @see core.PlayerListener#errorOccurred(java.util.EventObject)
	 */
	public void errorOccurred(EventObject e) {
	}

	/* (non-Javadoc)
	 * @see core.PlayerListener#paused(java.util.EventObject)
	 */
	public void paused(EventObject e) {
	}

	/* (non-Javadoc)
	 * @see core.PlayerListener#playing(java.util.EventObject)
	 */
	public void playing(EventObject e) {
	}

	/* (non-Javadoc)
	 * @see core.PlayerListener#positionChanged(java.util.EventObject)
	 */
	public void positionChanged(EventObject e) {
	}

	/* (non-Javadoc)
	 * @see core.PlayerListener#stopped(java.util.EventObject)
	 */
	public void stopped(EventObject e) {
	}

	/* (non-Javadoc)
	 * @see core.PlayerListener#timeChanged(java.util.EventObject, long)
	 */
	public void timeChanged(EventObject e, long newTime) {
	}
}
