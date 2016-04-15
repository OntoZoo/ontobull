/**
 * Service class to load terms which will be split into two
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
 * @author zxiang
 *
 */
public class TermSeparationListManager {
	private static final Logger logger = LoggerFactory.getLogger(TermSeparationListManager.class);


	private String separationFilePath;
	private HashMap<String, HashMap<String, String>> hashMapTermSeparation = 
			new HashMap<String, HashMap<String, String>>();
	
	/**
	 * @return the separationFilePath
	 */
	public String getSeparationFilePath() {
		return separationFilePath;
	}


	/**
	 * @param separationFilePath the separationFilePath to set
	 */
	public void setSeparationFilePath(String separationFilePath) {
		this.separationFilePath = separationFilePath;
	}


	/**
	 * @return the hashMapTermSeparation
	 */
	public HashMap<String, HashMap<String, String>> getHashMapTermSeparation() {
		return hashMapTermSeparation;
	}


	/**
	 * @param hashMapTermSeparation the hashMapTermSeparation to set
	 */
	public void setHashMapTermSeparation(
			HashMap<String, HashMap<String, String>> hashMapTermSeparation) {
		this.hashMapTermSeparation = hashMapTermSeparation;
	}

	
	/**
	 * Parse the term mapping file (tab delimited term pairs).
	 * @return if the mapping file is successfully parsed.
	 */
	public boolean parseSeparationFile() {
		boolean returnValue = true;
		
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(separationFilePath));
			String strRead;
			while ((strRead=bufferedReader.readLine())!=null){
				String[] arraySplit = strRead.split("\t");
				HashMap<String, String> hashMapNewTerm = new HashMap<String, String>();
				hashMapNewTerm.put("occurrent", arraySplit[1]);
				hashMapNewTerm.put("continuant", arraySplit[2]);
				
				hashMapTermSeparation.put(arraySplit[0], hashMapNewTerm);
				
				logger.debug("Term to be separated: {}\t{}\t{}", arraySplit[0], arraySplit[1], arraySplit[2]);
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

}
