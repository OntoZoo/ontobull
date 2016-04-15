/**
 * Project: BFOConvert
 * Package: org.hegroup.bfoconvert.service
 * File: NamespaceCleanupListManager.java
 *
 * Author: Edison Ong
 * Create: Jul 14, 2015
 * Version 0.1
 */
package org.hegroup.bfoconvert.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class NamespaceCleanupListManager {
	private static final Logger logger = LoggerFactory.getLogger(NamespaceCleanupListManager.class);
	
	private String cleanupFilePath;
	private HashMap<String, String> hashAddNamespace = new HashMap<String, String>();
	private HashMap<String, String> hashRemoveNamespace = new HashMap<String, String>();
	private HashMap<String, String> hashReplaceNamespace = new HashMap<String, String>();
	
	/**
	 * @return the hashReplaceNamespace
	 */
	public HashMap<String, String> getHashReplaceNamespace() {
		return hashReplaceNamespace;
	}
	/**
	 * @param hashReplaceNamespace the hashReplaceNamespace to set
	 */
	public void setHashReplaceNamespace(HashMap<String, String> hashReplaceNamespace) {
		this.hashReplaceNamespace = hashReplaceNamespace;
	}
	
	/**
	 * @return the cleanupFilePath
	 */
	public String getCleanupFilePath() {
		return cleanupFilePath;
	}
	/**
	 * @param cleanupFilePath the cleanupFilePath to set
	 */
	public void setCleanupFilePath(String cleanupFilePath) {
		this.cleanupFilePath = cleanupFilePath;
	}
	
	/**
	 * @return the hashAddNamespace
	 */
	public HashMap<String, String> getHashAddNamespace() {
		return hashAddNamespace;
	}
	/**
	 * @param hashAddNamespace the hashAddNamespace to set
	 */
	public void setHashAddNamespace(HashMap<String, String> hashAddNamespace) {
		this.hashAddNamespace = hashAddNamespace;
	}
	/**
	 * @return the hashRemoveNamespace
	 */
	public HashMap<String, String> getHashRemoveNamespace() {
		return hashRemoveNamespace;
	}
	/**
	 * @param hashRemoveNamespace the hashRemoveNamespace to set
	 */
	public void setHashRemoveNamespace(HashMap<String, String> hashRemoveNamespace) {
		this.hashRemoveNamespace = hashRemoveNamespace;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("resource")
	public boolean parseCleanupFile() {
		boolean returnValue = true;
		
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader( new FileReader( cleanupFilePath ) );
			String strRead;
			bufferedReader.readLine();
			while ( ( strRead = bufferedReader.readLine() ) != null ) {
				String[] arraySplit = strRead.split( "\t" );
				if ( arraySplit[0].equalsIgnoreCase( "add" ) ) {
					hashAddNamespace.put( arraySplit[2], arraySplit[1] );
				} else if ( arraySplit[0].equalsIgnoreCase( "remove" ) ) {
					hashRemoveNamespace.put( arraySplit[2], arraySplit[1] );
				} else if ( arraySplit[0].equalsIgnoreCase( "replace" ) ) {
					hashReplaceNamespace.put( arraySplit[2], arraySplit[1] );
				}
				//logger.debug( arraySplit[0] + "\t" + arraySplit[1] + "\t" + arraySplit[2] );
			}
		} catch (FileNotFoundException e) {
            returnValue=false;
            logger.error(e.getMessage());
        } catch (IOException e) {
            returnValue=false;
            logger.error(e.getMessage());
        }
		
		return returnValue;
	}
	
}
