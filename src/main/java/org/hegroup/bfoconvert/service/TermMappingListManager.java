/**
 * Service class to parse term mapping file
 */
package org.hegroup.bfoconvert.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxiang
 *
 */
public class TermMappingListManager {
	private static final Logger logger = LoggerFactory.getLogger(TermMappingListManager.class);


	private String mappingFilePath;
	private HashMap<String, String> hashMapTermMapping = new HashMap<String, String>();
	
	/**
	 * @return the mappingFilePath
	 */
	public String getMappingFilePath() {
		return mappingFilePath;
	}


	/**
	 * @param mappingFilePath the mappingFilePath to set
	 */
	public void setMappingFilePath(String mappingFilePath) {
		this.mappingFilePath = mappingFilePath;
	}


	/**
	 * @return the hashMapTermMapping
	 */
	public HashMap<String, String> getHashMapTermMapping() {
		return hashMapTermMapping;
	}


	/**
	 * @param hashMapTermMapping the hashMapTermMapping to set
	 */
	public void setHashMapTermMapping(HashMap<String, String> hashMapTermMapping) {
		this.hashMapTermMapping = hashMapTermMapping;
	}

	
	
	/**
	 * Parse the term mapping file (tab delimited term pairs).
	 * @return if the mapping file is successfully parsed.
	 */
	public boolean parseMappingFile() {
		boolean returnValue = true;
		
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(mappingFilePath));
			String strRead;
			bufferedReader.readLine();
			while ((strRead=bufferedReader.readLine())!=null){
				String[] arraySplit = strRead.split("\t");
				hashMapTermMapping.put(arraySplit[0], arraySplit[1]);
				
//				logger.debug(arraySplit[0] + "\t" + arraySplit[1]);
			}
			bufferedReader.close();
		
		} catch (FileNotFoundException e) {
			returnValue=false;
			logger.error(e.getMessage());
		} catch (IOException e) {
			returnValue=false;
			logger.error(e.getMessage());
		}
		
		return returnValue;
		
	}
	
	public boolean parseMappingInput( HashMap<String, String> hashMap ) {
		boolean returnValue = true;
		
		hashMapTermMapping = hashMap;
	
		return returnValue;
	}
	
}
