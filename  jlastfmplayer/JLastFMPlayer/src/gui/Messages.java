/*
 * Messages.java
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

import java.util.*;

public final class Messages {
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("gui.translations.messages");
	
	private Messages() {
	}

	public static String get(String key) {
		try{
			return RESOURCE_BUNDLE.getString(key);
		}catch( MissingResourceException e ){
			return '!' + key + '!';
		}
	}
}
