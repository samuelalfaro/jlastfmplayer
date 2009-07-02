/*
 * Icons.java
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

import java.awt.Image;
import java.awt.Toolkit;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public final class Icons{
	
	private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("resources/icons/theme");
	
	private static final int SMALL_WIDTH  = Integer.parseInt(RESOURCE_BUNDLE.getString("Icon.smallWidth"));
	private static final int SMALL_HEIGHT = Integer.parseInt(RESOURCE_BUNDLE.getString("Icon.smallHeight"));
	
	private static ImageIcon getSmallIcon(String key){
		try{
			Image i = Toolkit.getDefaultToolkit().getImage(CLASS_LOADER.getResource(RESOURCE_BUNDLE.getString(key)));
			return new ImageIcon(i.getScaledInstance(SMALL_WIDTH, SMALL_HEIGHT, Image.SCALE_SMOOTH));
		}catch( MissingResourceException e ){
			e.printStackTrace();
			return null;
		}
	}

	private static ImageIcon getIcon(String key){
		try{
			return new ImageIcon(CLASS_LOADER.getResource(RESOURCE_BUNDLE.getString(key)));
		}catch( MissingResourceException e ){
			e.printStackTrace();
			return null;
		}
	}	
	
	private Icons(){}
	
	public static final ImageIcon smallRadioIcon	= getSmallIcon("Icon.favoicon");

	public static final ImageIcon logginIcon	= getIcon("Icon.loggin");
	
	public static final ImageIcon smallStopIcon		= getSmallIcon("Icon.stop");
	public static final ImageIcon smallPlayIcon		= getSmallIcon("Icon.play");
	public static final ImageIcon smallPauseIcon	= getSmallIcon("Icon.pause");
	public static final ImageIcon smallSkipIcon		= getSmallIcon("Icon.skip");
	public static final ImageIcon smallRecIcon		= getSmallIcon("Icon.record");
	public static final ImageIcon smallMuteIcon		= getSmallIcon("Icon.mute");
	public static final ImageIcon smallNoSoundIcon	= getSmallIcon("Icon.enableSound");

	public static final ImageIcon smallLoveIcon		= getSmallIcon("Icon.love");
	public static final ImageIcon smallBanIcon		= getSmallIcon("Icon.ban");

	public static final ImageIcon smallNeighboursIcon	= getSmallIcon("Icon.neighbours");
	public static final ImageIcon smallTagsIcon			= getSmallIcon("Icon.tags");
	public static final ImageIcon smallArtistIcon		= getSmallIcon("Icon.artist");

	public static final ImageIcon banIcon		= getIcon("Icon.ban");
	public static final ImageIcon tagsIcon		= getIcon("Icon.tags");
	public static final ImageIcon artistIcon	= getIcon("Icon.artist");

	public static final ImageIcon noCoverIcon	= getIcon("Icon.nocover");
}
