/*
 * PlayerListener.java
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

import java.util.EventListener;
import java.util.EventObject;

public interface PlayerListener extends EventListener {

	void playing(EventObject e);

	void paused(EventObject e);

	void stopped(EventObject e);

	void endReached(EventObject e);

	void timeChanged(EventObject e, long newTime);

	void positionChanged(EventObject e);

	void errorOccurred(EventObject e);

}
