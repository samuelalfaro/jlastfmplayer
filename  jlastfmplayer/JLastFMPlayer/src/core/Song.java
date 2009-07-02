/*
 * Song.java
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class Song{

	private URL		location;
	private String	artist;
	private String	track;
	private String	album;
	private URL		albumcover;
	private int		duration;
	
	public Song(){
		this.location = null;
		this.artist = "";
		this.track = "";
		this.album = "";
		this.albumcover = null;
		this.duration = 0;
	}
	
	public Song(Song other){
		this.location	= other.location;
		this.artist		= other.artist;
		this.track		= other.track;
		this.album		= other.album;
		this.albumcover	= other.albumcover;
		this.duration	= other.duration;
	}
	
	public URL getLocation() {
		return location;
	}
	public void setLocation(URL location) {
		this.location = location;
	}
	public void setLocation(String location) {
		try{
			this.location = new URL(location);
		}catch( MalformedURLException e ){
			this.location = null;
		}
	}

	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public URL getAlbumcover() {
		return albumcover;
	}
	public void setAlbumcover(URL albumcover) {
		this.albumcover = albumcover;
	}
	public void setAlbumcover(String albumcover) {
		try{
			this.albumcover = new URL(albumcover);
		}catch( MalformedURLException e ){
			this.albumcover = null;
		}
	}
	
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	private static final String format =
		"Dirección:       %s%n"+
		"Artista:         %s%n"+
		"Tema:            %s%n"+
		"Album:           %s%n"+
		"Duración:        %5$tM:%5$tS%n";
	
	public String toString() {
		return String.format(format, location, artist, track, album, new Date(duration) );
	}
}
