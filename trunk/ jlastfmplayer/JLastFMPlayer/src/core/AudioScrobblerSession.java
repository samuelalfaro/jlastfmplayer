/*
 * AudioScrobblerSession.java
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
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class AudioScrobblerSession{

	private String session;
	private String user;
	private String md5pass;
	
	public AudioScrobblerSession(){
		this.session = null;
		this.user = null;
		this.md5pass = null;
	}
	
	public boolean logging(String user, String md5pass) throws IOException{
		this.session = null;
		this.user = URLEncoder.encode(user,"UTF-8");
		this.md5pass = md5pass;
		StringBuilder sb;
		sb= new StringBuilder("http://ws.audioscrobbler.com/radio/handshake.php?version=1.1.1&username=");
		sb.append(this.user);
		sb.append("&passwordmd5=");
		sb.append(md5pass);
		sb.append("&debug=0");
		
		URL urlSesion= new URL(sb.toString());
		BufferedReader result = new BufferedReader(new InputStreamReader(urlSesion.openStream()));
		String line;
		if(result.ready()){
			line = result.readLine();
			if(line.startsWith("session="))
				session = line.substring("session=".length(), line.length());
		}
		result.close();
		return session != null && !session.equals("FAILED");
	}
	
	static class XMLHandlerTrackList extends DefaultHandler {
		
		private XMLReader parser;
		private DefaultHandler parent;
		private XMLHandlerTrack child;
		private Collection<Song> tackList;
		private Collection<Collection<Song>> destination;

		public XMLHandlerTrackList (XMLReader parser, DefaultHandler parent){
			this.parser = parser;
			this.parent = parent;
			this.child  = new XMLHandlerTrack(parser, this);
		}
		
		public void setDestinantion(Collection<Collection<Song>> destination){
			this.destination = destination;
		}
		
		public void startElement( String namespaceURI, String localName, String qName, Attributes attr ) 
		throws SAXException {
			if (localName.equals("trackList")){
				tackList = new ArrayList<Song>(5); 
			}else if (localName.equals("track")){
				child.setDestinantion(tackList);
				parser.setContentHandler(child);
				child.startElement(namespaceURI, localName, qName, attr);
			}
		}
		
		public void endElement (String namespaceURI, String localName, String rawName)
		throws SAXException{
			if (localName.equals("trackList")){
				destination.add(tackList);
				parser.setContentHandler( parent );
			}
		}
	}
	
	static class XMLHandlerTrack extends DefaultHandler {
		
		private XMLReader parser;
		private DefaultHandler parent;
		private String value;
		private Song   song;
		private Collection<Song> destination;

		public XMLHandlerTrack (XMLReader parser, DefaultHandler parent){
			this.parser = parser;
			this.parent = parent;
		}
		
		public void setDestinantion(Collection<Song> destination){
			this.destination = destination;
		}
		
		public void startElement( String namespaceURI, String localName, String qName, Attributes attr ) 
		throws SAXException {
			if (localName.equals("track")){
				song = new Song();
			}
		}
		
		public void characters (char ch[], int start, int length)
		throws SAXException{
			value = new String(ch,start,length).trim();
		}

		public void endElement (String namespaceURI, String localName, String rawName)
		throws SAXException{
			if (localName.equals("location")){
				song.setLocation(value);
			} else if (localName.equals("title")){
				song.setTrack(value);
			} else if (localName.equals("album")){
				song.setAlbum(value);
			} else if (localName.equals("creator")){
				song.setArtist(value);
			} else if (localName.equals("duration")){
				song.setDuration(Integer.parseInt(value));
			} else if (localName.equals("image")){
				song.setAlbumcover(value);
			} else if (localName.equals("track")){
				destination.add(song);
				parser.setContentHandler( parent );
			}
		}
	}
	
	public Collection<Song> getPlayList() throws IOException{
		StringBuilder sb;
		sb= new StringBuilder("http://ws.audioscrobbler.com/radio/xspf.php?sk=");
		sb.append(session);
		sb.append("&discovery=0&desktop=1.5");
		URL urlList= new URL(sb.toString());
		
		try{
			XMLReader parser = XMLReaderFactory.createXMLReader();
			XMLHandlerTrackList contentHandler = new XMLHandlerTrackList(parser,null);
			Collection<Collection<Song>> collections = new ArrayList<Collection<Song>>();
			contentHandler.setDestinantion(collections);
			parser.setContentHandler(contentHandler);
			parser.parse(new InputSource(urlList.openStream()));
			
			if(collections.size() == 0)
				return null;
			if(collections.size() == 1)
				return collections.iterator().next();
			Collection<Song> collection = new ArrayList<Song>();
			for(Collection<Song> c:collections)
				collection.addAll(c);
			return collection;
		}catch( SAXException e ){
			e.printStackTrace();
			return null;
		}
	}
	
	private static boolean sendXMLCommand(String command) throws IOException{

		String host = "ws.audioscrobbler.com";
		String path = "/1.0/rw/xmlrpc.php";
		
		Socket sock = new Socket( InetAddress.getByName(host), 80 );
		BufferedWriter  wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

		//Send header
		wr.write("POST " + path + " HTTP/1.0\r\n");
		wr.write("Host: " + host+ "\r\n");
		wr.write("Accept-Language: es, en\r\n");
		wr.write("content-type: text/xml\r\n");
		wr.write("content-length: " + command.length() + "\r\n");
		wr.write("\r\n");

		wr.write(command);
		wr.flush();

		// Response
		BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));

		String line;
		// Skip HTTP header 
		do{
			line = rd.readLine();
		}while(line != null && line.length() > 0);
		// Find OK in XML Response
		do{
			line = rd.readLine();
		}while(line != null && !line.contains("<string>OK</string>"));
		
		return line != null;
	}
	
	private static String utf8Encode(String str) throws UnsupportedEncodingException{
		return new String(str.getBytes("UTF-8"), "UTF-8");
	}
	
	private static final String loveBanMethodFormat = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
		"<methodCall>\r\n" +
			"<methodName>%s</methodName>\r\n" +
			"<params>\r\n" +
				"<param><value><string>%s</string></value></param>\r\n" +
				"<param><value><string>%d</string></value></param>\r\n" +
				"<param><value><string>%s</string></value></param>\r\n" +
				"<param><value><string>%s</string></value></param>\r\n" +
				"<param><value><string>%s</string></value></param>\r\n" +
			"</params>\r\n" +
		"</methodCall>\r\n";
	
	public boolean loveTrack(String artist, String track) throws IOException{
		long timeInSeg = System.currentTimeMillis()/1000;
		String command = String.format(loveBanMethodFormat, 
				"loveTrack",
				user,
				timeInSeg,
				MD5.hash(md5pass + timeInSeg),
				utf8Encode(artist),
				utf8Encode(track)
		);
		return sendXMLCommand(command);
	}

	public boolean banTrack(String artist, String track) throws IOException{
		long timeInSeg = System.currentTimeMillis()/1000;
		String command = String.format(loveBanMethodFormat, 
				"banTrack",
				user,
				timeInSeg,
				MD5.hash(md5pass + timeInSeg),
				utf8Encode(artist),
				utf8Encode(track)
		);
		return sendXMLCommand(command);
	}
	
	private boolean tuning(URL urlTuning) throws IOException{
		boolean ok = false;
		BufferedReader result = new BufferedReader(new InputStreamReader(urlTuning.openStream(),"UTF-8"));
		while(result.ready()){
			String line = result.readLine();
			if(line.startsWith("response="))
				ok = line.substring("response=".length(), line.length()).equalsIgnoreCase("OK");
		}
		result.close();
		return ok;
	}
	
	private static String tuningNeighboursFormat =
		"http://ws.audioscrobbler.com/radio/adjust.php?session=%s" +
		"&url=lastfm://user/%s/neighbours&debug=0";
	
	public boolean tuningNeighbours() throws IOException{
		return tuning(new URL(String.format(tuningNeighboursFormat, session, user )));
	}

	private static String tuningGlobalTagsFormat =
		"http://ws.audioscrobbler.com/radio/adjust.php?session=%s" +
		"&url=lastfm://globaltags/%s&debug=0";
	
	public boolean tuningGlobalTags(String tag) throws IOException{
		return tuning(new URL(String.format(tuningGlobalTagsFormat, session, URLEncoder.encode(tag,"UTF-8") )));
	}

	private static String tuningSimilarArtistsFormat =
		"http://ws.audioscrobbler.com/radio/adjust.php?session=%s" +
		"&url=lastfm://artist/%s/similarartists&debug=0";
	
	public boolean tuningSimilarArtists(String artist) throws IOException{
		return tuning(new URL(String.format(tuningSimilarArtistsFormat, session, URLEncoder.encode(artist,"UTF-8") )));
	}
}
