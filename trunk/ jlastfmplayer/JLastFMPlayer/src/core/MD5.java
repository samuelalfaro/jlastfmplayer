/*
 * MD5.java
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	private static final char[] hexChars =
		{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private static String toHexString(byte[] bytearray) {
		StringBuilder sb = new StringBuilder(); 
		for( byte b: bytearray ) {
			sb.append( hexChars[ b >> 4 & 0x0F ] );
			sb.append( hexChars[ b & 0x0F ] );
		}
		return sb.toString();
	}

	private static MessageDigest md = null;

	public static byte[] hash(byte[] dataToHash){
		if( md == null )
			try{
				md = MessageDigest.getInstance("MD5");
			}catch( NoSuchAlgorithmException ignorada ){
			}
		return md.digest(dataToHash); 
	}
	
	public static String hash(String stringToHash){
		return toHexString( hash(stringToHash.getBytes()) );
	}
}