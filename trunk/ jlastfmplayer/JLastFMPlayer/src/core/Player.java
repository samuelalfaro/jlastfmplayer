/*
 * Player.java
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

import java.io.*;

public abstract class Player {

	public static Player getInstance(String path){
		if( path.indexOf("mplayer") >= 0 )
			return new MPlayerWrap(path);
		return null;
	}
	
	public abstract boolean isPlaying();
	
	public abstract void play(String stream_url) throws IOException;
	
	public abstract void play(String stream_url, boolean asynchronous) throws IOException;
	
	public abstract void pause();
	
	public abstract void mute();
	
	public abstract void setVolume(float value);

	public abstract void stop();
	
	public abstract void addListener(PlayerListener listener);
	
}