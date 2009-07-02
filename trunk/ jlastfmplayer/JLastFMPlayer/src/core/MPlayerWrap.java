/*
 * MPlayerWrap.java
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
import java.util.*;

public class MPlayerWrap extends Player {

	private final String path;

	private final Object lock;
	private boolean playing;

	private Process mPlayerPrc;
	private BufferedReader mPlayerOut;
	private PrintStream mPlayerIn;

	private Collection<PlayerListener> listeners;

	protected MPlayerWrap(String path) {
		this.path = path;
		lock = new Object();
		playing = false;
	}

	@Override
	public boolean isPlaying() {
		return playing;
	}

	private void sendCommand(String command) {
		if( mPlayerIn != null ){
			mPlayerIn.print(command);
			mPlayerIn.flush();
		}
	}
	
	public void play(String stream_url) throws IOException {
		play(stream_url, false);
	}

	@Override
	public void play(String stream_url, boolean asynchronous) throws IOException {
		if( stream_url.startsWith("file://localhost/") )
			stream_url = stream_url.substring("file://localhost/".length());
		StringBuilder sb;
		if( mPlayerPrc == null){
			sb = new StringBuilder(path);
			sb.append(" -slave ");
			if(stream_url.indexOf(' ') > -1){
				sb.append('\"');
				sb.append(stream_url);
				sb.append('\"');
			}else
				sb.append(stream_url);

			System.out.println(sb.toString());
			mPlayerPrc = Runtime.getRuntime().exec(sb.toString());
			System.out.println(mPlayerPrc);
			mPlayerOut = new BufferedReader(new InputStreamReader(mPlayerPrc.getInputStream()));
			mPlayerIn = new PrintStream(mPlayerPrc.getOutputStream());

			final Thread stdoutThread = new Thread() {
				public void run() {
					try{
						String line;
						long previousTime, currentTime = 0;
						while( (line = mPlayerOut.readLine()) != null ){
							if( line.length() == 0 )
								continue;
							if( line.startsWith("A:") ){
								if(listeners != null){
									StringTokenizer sk = new StringTokenizer(line);
									sk.nextToken();
									previousTime = currentTime;
									currentTime = new Float(Float.valueOf(sk.nextToken()) * 1000).longValue();
									if( currentTime != previousTime )
										for( PlayerListener listener: listeners )
											// TODO mirar eventos
											listener.timeChanged(null, currentTime);
								}
							}else if( !playing && line.startsWith("Starting playback...") ){
								synchronized( lock ){
									playing = true;
									lock.notify();
								}
								if( listeners != null )
									for( PlayerListener listener: listeners )
										// TODO mirar eventos
										listener.playing(null);

							}else if( line.startsWith("Exiting...") ){
								playing = false;
								mPlayerOut.close();
								mPlayerIn.close();
								mPlayerPrc = null;
								if( listeners != null )
									for( PlayerListener listener: listeners )
										// TODO mirar eventos
										listener.endReached(null);
								break;
							}
							// Escape secuence in C = "\x1b[A\r\x1b[K"
							// in java = "\u001b[A\r\u001b[K" 2 lines
							else if( line.startsWith("\u001b[A") )
								continue;
							else if( line.startsWith("\u001b[K")){
								if( listeners != null  && line.contains("PAUSE") )
									for( PlayerListener listener: listeners )
										// TODO mirar eventos
										listener.paused(null);
							}else{
								// System.out.println(line);
							}
						}
					}catch( IOException ie ){
						System.err.println("IO exception on MPlayerOut: ");
						ie.printStackTrace();
					}
				}
			};
			stdoutThread.start();
		}else{
			sb = new StringBuilder("loadfile \"");
			sb.append(stream_url);
			sb.append("\n");
			sendCommand(sb.toString());
		}
//		System.out.println(sb.toString());
		playing = false;
		if( !asynchronous ){
			synchronized( lock ){
				try{
					lock.wait();
				}catch( InterruptedException ignorada ){
				}
			}
		}
	}

	@Override
	public void pause() {
		sendCommand("pause\n");
	}

	private transient boolean muted = false;

	@Override
	public void mute() {
		sendCommand(muted ? "mute 0\n" : "mute 1\n");
		muted = !muted;
	}

	@Override
	public void setVolume(float value) {
		if( value < 0 )
			value = 0;
		else if( value > 100 )
			value = 100;
		sendCommand(String.format("volume %.0f 1\n", value));
	}

	@Override
	public void stop() {
		sendCommand("quit\n");
		if( mPlayerPrc != null ){
			try{
				mPlayerPrc.waitFor();
			}catch( InterruptedException ignorada ){
			}
			mPlayerPrc = null;
		}
	}

	@Override
	public void addListener(PlayerListener listener) {
		if( listeners == null )
			listeners = new ArrayList<PlayerListener>(1);
		listeners.add(listener);
	}
}
