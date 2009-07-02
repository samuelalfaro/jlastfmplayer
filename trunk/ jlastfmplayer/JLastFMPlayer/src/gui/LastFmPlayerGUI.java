/*
 * LastFmPlayerGUI.java
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
package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.sam.gui.*;

import core.*;

@SuppressWarnings("serial")
public class LastFmPlayerGUI{
    
	private class SongGUIUpdater implements Runnable{
		public final Song song;
		
		SongGUIUpdater(Song song){
			this.song = song;
		}
		
		public void run() {
			if( song == null ){
				artist.setText("");
				track.setText("");
				album.setText("");
				
				timeProgress.setValue(0);
				timeProgress.setString("--:-- / --:--");
				timeProgress.setIndeterminate(true);
				
				pauseToggleAction.setEnabled(false);
				skipToggleAction.setEnabled(false);
				recToggleAction.setEnabled(false);
				
				loveSongToggleAction.setEnabled(false);
				banSongToggleAction.setEnabled(false);
				
				tuningNeighboursAction.setEnabled(false);
				tuningGlobalTagsAction.setEnabled(false);
				tuningSimilarArtistsAction.setEnabled(false);
				
				muteToggleAction.setEnabled(false);
				volumeSlider.setEnabled(false);
				
				album_cover.setIcon(null);
			}else{
				artist.setText( song.getArtist() );
				artist.setCaretPosition(0);
				track.setText( song.getTrack() );
				track.setCaretPosition(0);
				album.setText( song.getAlbum() );
				album.setCaretPosition(0);
	
				timeProgress.setMinimum(0);
				timeProgress.setMaximum(song.getDuration());
				timeProgress.setIndeterminate(false);
				
	    		pauseToggleAction.setEnabled(true);
	    		skipToggleAction.setEnabled(true);
	    		recToggleAction.setEnabled(true);
	    		
	    		loveSongToggleAction.setEnabled(true);
	    		banSongToggleAction.setEnabled(true);
	    		
				tuningNeighboursAction.setEnabled(true);
				tuningGlobalTagsAction.setEnabled(true);
				tuningSimilarArtistsAction.setEnabled(true);
				
				muteToggleAction.setEnabled(true);
				volumeSlider.setEnabled(true);

				ImageIcon icon = null;
				if( song.getAlbumcover() != null ){
					try{
						ImageProducer imageProducer = (ImageProducer) song.getAlbumcover().getContent();
						icon = new ImageIcon( Toolkit.getDefaultToolkit().createImage(imageProducer) );
					}catch( IOException errorDescargandoImagen ){
					}
				}
				album_cover.setIcon(icon != null ? icon : Icons.noCoverIcon);
			}
		}
	}
	
	private static class SongDownloaderWorker extends SwingWorker<Void,Void>{
		
		private static AudioScrobblerSession session = null;
		
		public static void setSession(AudioScrobblerSession session){
			if(SongDownloaderWorker.session == null)
				SongDownloaderWorker.session = session;
		}
		
		private static File tmpDir = null;
		
		public static void setTmpDir(File tmpDir){
			if(SongDownloaderWorker.tmpDir == null)
				SongDownloaderWorker.tmpDir = tmpDir;
		}

		private static Iterator<Song> playList;
		
		public static void cancelPlayList(){
			playList = null;
		}

		private static boolean downloadNext = true;

		public static void enableNextDownload(){
			downloadNext = true;
		}

		public static void disableNextDownload(){
			downloadNext = false;
		}
		
		private static SongDownloader currentDownloader;
		
		public static synchronized Song getSong(long minBuffer) throws IOException{
			if(currentDownloader == null)
				new SongDownloaderWorker();
			return currentDownloader.getLocalSong(minBuffer);
		}

		public static synchronized void cancelDownload(Song song){
			if(currentDownloader != null && song == currentDownloader.getLocalSong()){
				downloadNext = false;
				currentDownloader.cancelDownload();
				currentDownloader = null;
			}
		}
		
		final SongDownloader downloader;
		
		private SongDownloaderWorker() throws IOException{
			if(SongDownloaderWorker.downloadNext)
				SongDownloaderWorker.downloadNext = false;
			if(playList == null || !playList.hasNext())
				playList = session.getPlayList().iterator();
			currentDownloader = downloader = new SongDownloader(tmpDir,playList.next());
			execute();
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			downloader.download();
			return null;
		}
		
		@Override
		protected void done() {
			if( downloadNext ){
				if( downloader == currentDownloader )
					try{
						new SongDownloaderWorker();
					}catch( IOException ignorada ){
					}
			}else
				currentDownloader = null;
		}
    };
	
	private class PlayerWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			playingSong = SongDownloaderWorker.getSong(256000);
			player.play(playingSong.getLocation().toString());
			return null;
		}
	};
	
	private boolean exit   = false;
	private boolean paused = false;
	private boolean muted = false;
    
	private AudioScrobblerSession session;
	private Player player;
	private Song playingSong;
	private File saveDir;
	private File saveTo;
	
	private final JLabel album_cover = new JLabel(){
		public void paintComponent(Graphics g){
			if(this.getIcon() != null){
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.drawImage(((ImageIcon)this.getIcon()).getImage(), 0, 0, this.getPreferredSize().width, this.getPreferredSize().height, this);
			}
		}
	};

	private final JTextField artist = new JTextField();
	private final JTextField track  = new JTextField();
	private final JTextField album  = new JTextField();
	private final JProgressBar timeProgress = new JProgressBar();
	
	private final JFrame frame = new JFrame(Messages.get("Frame.title"));
	private final JFileChooser chooser = new JFileChooser();
	
	private final Action stopToggleAction = new AbstractAction("",Icons.smallStopIcon) {
     	public void actionPerformed(ActionEvent ae) {
        }
    };
    
    private final Action pauseToggleAction = new AbstractAction("",Icons.smallPauseIcon) {
     	public void actionPerformed(ActionEvent ae) {
    		paused = !paused;
    		this.putValue(Action.SMALL_ICON, paused ? Icons.smallPlayIcon: Icons.smallPauseIcon);
    		this.putValue(Action.SHORT_DESCRIPTION, paused ? Messages.get("Action.play.shortDescription"): Messages.get("Action.pause.shortDescription"));
            player.pause();
            if(paused)
            	SongDownloaderWorker.disableNextDownload();
            else
            	SongDownloaderWorker.enableNextDownload();
        }
    };
    
    private final Action skipToggleAction = new AbstractAction("",Icons.smallSkipIcon) {
		public void actionPerformed(ActionEvent ae) {
			if( playingSong == null)
				return;
			if( paused ){
				pauseToggleAction.putValue(Action.SMALL_ICON, Icons.smallPauseIcon);
				pauseToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.pause.shortDescription"));
				paused = false;
			}
			SongDownloaderWorker.cancelDownload(playingSong);
			player.stop();
		}
	};
	
	private final Action recToggleAction = new AbstractAction("",Icons.smallRecIcon) {
		public void actionPerformed(ActionEvent ae) {
			chooser.setCurrentDirectory(saveDir);
			chooser.setSelectedFile(new File(SongDownloader.generateFileName(playingSong)));
			int returnVal = chooser.showSaveDialog(null);
			if( returnVal == JFileChooser.APPROVE_OPTION ){
				if( SongDownloader.isValidFileName(chooser.getSelectedFile().getAbsolutePath()) ){
					try{
						saveTo = chooser.getSelectedFile().getCanonicalFile();
					}catch( IOException ignorada ){
					}
					if(saveTo.exists() && JOptionPane.showConfirmDialog(
								frame,
								Messages.get("Dialog.overwrite.message"),
						        Messages.get("Dialog.overwrite.title"),
						        JOptionPane.YES_NO_OPTION,
						        JOptionPane.WARNING_MESSAGE) != 0)
						saveTo = null;
				}
			}
			if(saveTo != null){
				this.setEnabled(false);
				skipToggleAction.setEnabled(false);
				banSongToggleAction.setEnabled(false);
			}
//			System.out.println(saveTo);
		}
	};
	
    private final Action muteToggleAction = new AbstractAction("",Icons.smallMuteIcon) {
     	public void actionPerformed(ActionEvent ae) {
    		muted = !muted;
    		this.putValue(Action.SMALL_ICON, muted ? Icons.smallNoSoundIcon: Icons.smallMuteIcon);
    		this.putValue(Action.SHORT_DESCRIPTION, muted ? Messages.get("Action.enableSound.shortDescription"): Messages.get("Action.mute.shortDescription"));
            player.mute();
            volumeSlider.setEnabled(!muted);
        }
    };
	
    private final GradientSlider volumeSlider = new GradientSlider();

    private final Action loveSongToggleAction = new AbstractAction("",Icons.smallLoveIcon) {
    	public void actionPerformed(ActionEvent ae) {
    		if(playingSong != null)
    			try{
    				boolean ok = session.loveTrack(playingSong.getArtist(), playingSong.getTrack());
    				if(ok){
    					this.setEnabled(false);
    					banSongToggleAction.setEnabled(false);
    				}
    			}catch( IOException e ){
    				e.printStackTrace();
    			}
    	}
    };
    
    private final Action banSongToggleAction = new AbstractAction("",Icons.smallBanIcon) {
     	public void actionPerformed(final ActionEvent ae) {
     		if(playingSong != null){
     			try{
     				if( InternalOptionPane.showConfirmDialog(
     						frame,
     						String.format(Messages.get("Dialog.ban.message"), playingSong.getArtist(), playingSong.getTrack()),
     						Messages.get("Dialog.ban.title"),
     						JOptionPane.YES_NO_OPTION,
     						JOptionPane.QUESTION_MESSAGE,
     						Icons.banIcon) == 0 ){
// 						boolean ok = 
     					session.banTrack(playingSong.getArtist(), playingSong.getTrack());
//     					System.out.println(ok);
     					skipToggleAction.actionPerformed(ae);
     				}
     			}catch( IOException e ){
     				e.printStackTrace();
     			}
     		}
        }
    };
    
    private final Action tuningNeighboursAction = new AbstractAction("",Icons.smallNeighboursIcon) {
        public void actionPerformed(ActionEvent ae) {
        	try{
				session.tuningNeighbours();
				SongDownloaderWorker.cancelPlayList();
				skipToggleAction.actionPerformed(ae);
			}catch( IOException e ){
				e.printStackTrace();
			}
        }
    };
   
    private final Action tuningGlobalTagsAction = new AbstractAction("",Icons.smallTagsIcon) {
    	public void actionPerformed(final ActionEvent ae) {
    		String entrada = (String) InternalOptionPane.showInputDialog(
    				frame,
    				Messages.get("Label.tag"),
    				Messages.get("Dialog.globalTagsRadio.title"),
    				JOptionPane.QUESTION_MESSAGE,
    				Icons.tagsIcon,
    				null,null);
    		if (entrada != null && entrada.length() > 0){
    			try{
    				if( session.tuningGlobalTags(entrada) ){
    					SongDownloaderWorker.cancelPlayList();
    					skipToggleAction.actionPerformed(ae);
    				}else
    					InternalOptionPane.showMessageDialog(
    							frame,
    							Messages.get("Dialog.radioNoFound.message")+entrada,
    							Messages.get("Dialog.radioNoFound.title"),
    							JOptionPane.INFORMATION_MESSAGE);
    			}catch( IOException e ){
    				e.printStackTrace();
    			}
    		}
    	}
    };
    
    private final Action tuningSimilarArtistsAction = new AbstractAction("",Icons.smallArtistIcon) {
    	public void actionPerformed(final ActionEvent ae) {
    		String entrada = (String) InternalOptionPane.showInputDialog(
    				frame,
    				Messages.get("Label.artist"),
    				Messages.get("Dialog.similarArtistRadio.title"),
    				JOptionPane.QUESTION_MESSAGE,
    				Icons.artistIcon,
    				null,null);
    		if (entrada != null && entrada.length() > 0){
    			try{
    				if (session.tuningSimilarArtists(entrada) ){
    					SongDownloaderWorker.cancelPlayList();
    					skipToggleAction.actionPerformed(ae);
    				}else
    					InternalOptionPane.showMessageDialog(
    							frame,
    							Messages.get("Dialog.radioNoFound.message")+entrada,
    							Messages.get("Dialog.radioNoFound.title"),
    							JOptionPane.INFORMATION_MESSAGE);
    			}catch( IOException e ){
    				e.printStackTrace();
    			}
    		}
    	}
    };
    
    private final PlayerListener playerListener = new AbstractPlayerListener(){

		public void endReached(EventObject e) {
			if(saveTo != null){
				new File(playingSong.getLocation().getFile()).renameTo(saveTo);
				saveTo = null;
			}
			if(!exit){
				SwingUtilities.invokeLater(new SongGUIUpdater(null));
				new PlayerWorker().execute();
			}
		}

		public void playing(EventObject e) {
			SwingUtilities.invokeLater(new SongGUIUpdater(playingSong));
		}

		public void timeChanged(EventObject e, final long newTime) {
			SwingUtilities.invokeLater( new Runnable(){
				public void run() {
					timeProgress.setValue((int)newTime);
					timeProgress.setString(String.format("%1$tM:%1$tS / %2$tM:%2$tS", new Date(newTime), new Date(playingSong.getDuration())));
				}
			});
		}
	};
	
	private final WindowAdapter closer = new WindowAdapter() {
    	public void windowClosing(WindowEvent e) {
        	exit = true;
    		if(player!= null)
        		player.stop();
    		SongDownloaderWorker.disableNextDownload();
			System.exit(0);
        }
    };
    
	private static Properties loadProperties(String filename){
		Properties properties = new Properties();
		try{
			FileInputStream in = new FileInputStream(filename);
			properties.load(in);
			in.close();
		}catch(FileNotFoundException ignorada){
		}catch(IOException ignorada){
		}
		return properties;
	}
	
	private static void loggin(JFrame parent, AudioScrobblerSession session, Properties defaultProperties) throws IOException {
		String user = defaultProperties.getProperty("USER");
		String md5pass = defaultProperties.getProperty("MD5PASS");

		InternalLoggingPane loggingPane = null;
		LoggingData loggingData = null;
		while( true ){
			if( user == null || user.length() == 0 || md5pass == null || md5pass.length() == 0 ){
				if( loggingPane == null ){
					loggingPane = new InternalLoggingPane(parent, null, true, false, Icons.logginIcon); 
					loggingData = new LoggingData();
				}
				LoggingData response = null;
				do{
					loggingData.setUser(user);
					loggingData.setPassword(null);
					loggingData.setRemember(false);
					response = loggingPane.showDialog(loggingData);
				}while(response == null);
				loggingData = response;
				user = loggingData.getUser();
				md5pass = MD5.hash(loggingData.getPassword());
			}
			
			if( session.logging(user, md5pass) ){
				if(loggingData != null && loggingData.isRemember() ){
					defaultProperties.setProperty("USER",user);
					defaultProperties.setProperty("MD5PASS",md5pass);
				}
				break;
			}

			InternalOptionPane.showMessageDialog(
					parent,
					"Vuelva a introducir su usuario y contraseña",
					"Error loggin",
					JOptionPane.WARNING_MESSAGE
			);
			md5pass = null;
		}
	}
    
	private LastFmPlayerGUI(){
    	try{
			Properties defaultProperties = loadProperties("lastfm.properties");
			int propertiesHash = defaultProperties.hashCode();
    		
    		showGUI(defaultProperties.getProperty("LOOK_AND_FEEL"));

			session = new AudioScrobblerSession();
			loggin(frame, session, defaultProperties);
			
			File playerFile = null;
			String playerPath = defaultProperties.getProperty("PLAYER_PATH");
			if(playerPath != null && playerPath.length() > 0)
				playerFile = new File(playerPath);
			if(playerFile == null || !playerFile.exists()){
				JFileChooser  chooser = new JFileChooser();
				chooser.setDialogTitle("Seleccione la ruta del reproductor:");
				if(chooser.showDialog(null,"Aceptar") == JFileChooser.APPROVE_OPTION){
					playerFile = chooser.getSelectedFile();
					defaultProperties.setProperty("PLAYER_PATH",playerFile.getCanonicalPath());
				}
			}

			player = Player.getInstance(playerFile.getCanonicalPath());
			player.setVolume(50);
    		player.addListener(playerListener);
			
			if(defaultProperties.hashCode() != propertiesHash)
				defaultProperties.store(new FileOutputStream("lastfm.properties"), null);
    		
    		SongDownloaderWorker.setSession(session);
    		
			File tmpDir = new File("./cache");
			if(!tmpDir.exists() || !tmpDir.isDirectory())
				tmpDir.mkdirs();
			else{
				for(File f:tmpDir.listFiles())
					if(!f.isDirectory())
						f.delete();
			}
				
			SongDownloaderWorker.setTmpDir(tmpDir);
			
			saveDir = new File("./saves");
			if(!saveDir.exists() || !saveDir.isDirectory())
				saveDir.mkdirs();
    		
    		new PlayerWorker().execute();
    	}catch( IOException e ){
    		e.printStackTrace();
    		System.exit(-1);
    	}
	}
    
	private void showGUI(String lookAndFeel){
		
		if(lookAndFeel != null){
			try{
				UIManager.setLookAndFeel(lookAndFeel);
			}catch( ClassNotFoundException igonarada ){
			}catch( InstantiationException igonarada ){
			}catch( IllegalAccessException igonarada ){
			}catch( UnsupportedLookAndFeelException igonarada ){
			}
		}

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		album_cover.setPreferredSize(new Dimension(112,112));
		gbc.insets.set(0,0,5,10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridheight = 4;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		panel.add(album_cover,gbc);
		
		gbc.insets.set(0,0,0,0);;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		panel.add(new JLabel(Messages.get("Label.artist")),gbc);

		artist.setEditable(false);
		artist.setOpaque(false);
		artist.setBorder(null);
		gbc.insets.set(0,10,0,0);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panel.add(artist,gbc);

		gbc.insets.set(0,0,0,0);;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		panel.add(new JLabel(Messages.get("Label.track")),gbc);

		track.setEditable(false);
		track.setOpaque(false);
		track.setBorder(null);
		gbc.insets.set(0,10,0,0);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panel.add(track,gbc);
		
		gbc.insets.set(0,0,0,0);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		panel.add(new JLabel(Messages.get("Label.album")),gbc);

		album.setEditable(false);
		album.setOpaque(false);
		album.setBorder(null);
		gbc.insets.set(0,10,0,0);
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panel.add(album,gbc);
		
		timeProgress.setStringPainted(true);
		timeProgress.setString("--:-- / --:--");
		gbc.insets.set(0,0,5,1);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		timeProgress.setPreferredSize(new Dimension(0,15));
		panel.add(timeProgress,gbc);
		
		JToolBar botones = new JToolBar();
		botones.setFloatable(false);

		botones.setBorderPainted(false);
		botones.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		stopToggleAction.setEnabled(false);
		stopToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.stop.shortDescription"));
		botones.add(stopToggleAction);
		
		pauseToggleAction.setEnabled(false);
		pauseToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.pause.shortDescription"));
		botones.add(pauseToggleAction);
		
		skipToggleAction.setEnabled(false);
		skipToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.skip.shortDescription"));
		botones.add(skipToggleAction);
		
		recToggleAction.setEnabled(false);
		recToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.rec.shortDescription"));
		botones.add(recToggleAction);
		
		botones.addSeparator();
		
		loveSongToggleAction.setEnabled(false);
		loveSongToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.love.shortDescription"));
		botones.add(loveSongToggleAction);
		
		banSongToggleAction.setEnabled(false);
		banSongToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.ban.shortDescription"));
		botones.add(banSongToggleAction);
		
		botones.addSeparator();
		
		tuningNeighboursAction.setEnabled(false);
		tuningNeighboursAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.tuningNeighbours.shortDescription"));
		botones.add(tuningNeighboursAction);
		
		tuningGlobalTagsAction.setEnabled(false);
		tuningGlobalTagsAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.tuningGlobalTags.shortDescription"));
		botones.add(tuningGlobalTagsAction);
		
		tuningSimilarArtistsAction.setEnabled(false);
		tuningSimilarArtistsAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.tuningSimilarArtists.shortDescription"));
		botones.add(tuningSimilarArtistsAction);
		
		botones.addSeparator();
		muteToggleAction.putValue(Action.SHORT_DESCRIPTION, Messages.get("Action.mute.shortDescription"));
		muteToggleAction.setEnabled(false);
		botones.add(muteToggleAction);
		
		volumeSlider.addPropertyChangeListener("Value",new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				player.setVolume((Float)evt.getNewValue());
			}
		});
		volumeSlider.setEnabled(false);
		botones.add(volumeSlider);
		
		gbc.insets.set(0,0,0,0);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(botones,gbc);
		
		frame.setIconImage(Icons.smallRadioIcon.getImage());
		frame.setContentPane(panel);
		frame.setGlassPane(new ModalBlurGlassPane());
		frame.addWindowListener(closer);
		frame.pack();
		
		artist.setPreferredSize(new Dimension(0,artist.getSize().height));
		track.setPreferredSize(new Dimension(0,track.getSize().height));
		album.setPreferredSize(new Dimension(0,album.getSize().height));
		
		Dimension minimumSize = frame.getSize();
		Rectangle  maximumBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		frame.setBounds(
				maximumBounds.x + maximumBounds.width  - minimumSize.width,
				maximumBounds.y + maximumBounds.height - minimumSize.height,
				minimumSize.width,
				minimumSize.height);
		frame.setMinimumSize(minimumSize);
		
		frame.setVisible(true);
	}
	
	public static void main(String... args){
		new LastFmPlayerGUI();
	}
}
