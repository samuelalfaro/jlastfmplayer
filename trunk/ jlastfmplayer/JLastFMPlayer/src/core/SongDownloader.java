/*
 * SongDownloader.java
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
import java.util.Date;

public class SongDownloader {
	
	public static boolean isValidFileName(String name){
		if( name == null || name.length() == 0)
			return false ;
		File f = new File( name ) ;
		if( f.exists() )
			return true;
		try {
			if( f.createNewFile() ) {
				f.delete() ;
				return true;
			}
		} catch ( IOException ioe ) {
		}
		return false ;
	}
	
	public static String generateFileName(Song song){
		String fileName = null;
		if(song != null){
			boolean invalidArtistName = !isValidFileName(song.getArtist());
			boolean unknownTrac = song.getTrack() == null || song.getTrack().length() == 0;    

			if( !invalidArtistName && !unknownTrac )
				fileName = String.format("%s-%s.mp3",song.getArtist().toUpperCase(),song.getTrack());
			else if( !invalidArtistName )
				fileName = String.format("%s-%s.mp3",song.getArtist().toUpperCase(),"Unknown_Track");
			else if( !unknownTrac )
				fileName = String.format("%s-%s.mp3","UNKNOWN_ARTIST",song.getTrack());

		}
		// Probamos el nombre completo para el track
		// 101 Dalmatas.mp3               --> nombre incorrecto
		// Musica Dysney-101 Dalmatas.mp3 --> nombre correcto
		if( !isValidFileName(fileName) )
			fileName = String.format("lastFM_radio[%1$tY-%1$tm-%1$td]%1$tH-%1$tM-%1$tS.mp3",new Date());
		return fileName;
	}
	
	private final Song remoteSong;
	private final Song localSong;
	
	private File file;
	private boolean canceled;
	private boolean complete; 
	
	public SongDownloader(Song remoteSong) throws IOException{
		this(null, remoteSong);
	}
	
	public SongDownloader(File parent, Song remoteSong) throws IOException{
		this.remoteSong = remoteSong;
		this.localSong = new Song(remoteSong);
		localSong.setLocation("file://localhost/"+new File(parent, generateFileName(null)).getCanonicalPath());
		this.file = null;
		this.canceled = false;
		this.complete = false;
	}
	
	public Song getLocalSong(){
		return localSong;
	}
	
	public Song getLocalSong(long minBuffer){
		while(!complete && !canceled && (file == null || file.length() < minBuffer) )
			try{
				Thread.sleep(500);
			}catch( InterruptedException ignorada ){
			}
		return localSong;
	}
	
	public void download() throws IOException{
		if(file != null)
			return;
		this.file = new File(localSong.getLocation().getFile());
		this.file.deleteOnExit();
		
		InputStream  input  = remoteSong.getLocation().openStream();
		OutputStream output = new FileOutputStream(file);
		
		byte[] buffer = new byte[8192];
		int bytesRead;
		boolean eof = false;
		
		while(!canceled && !eof ){
			eof = (bytesRead = input.read(buffer)) < 0;
			if( !eof )
				output.write(buffer, 0, bytesRead);
		}
		output.close();
		if(eof)
			this.complete = true;
		synchronized(this){
			this.notifyAll();
		}
	}
	
	public void cancelDownload(){
		if(file != null && !canceled  && !complete ){
			canceled = true;
			synchronized(this){
				try{
					this.wait();
				}catch( InterruptedException ignorada ){
				}
			}
		}
	}

	public void waitForDownload(){
		if(!canceled && !complete)
			synchronized(this){
				try{
					this.wait();
				}catch( InterruptedException ignorada ){
				}
			}
	}
}