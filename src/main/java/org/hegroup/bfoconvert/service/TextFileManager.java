/**
 * Service class to handle text files
 * 
 */
package org.hegroup.bfoconvert.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxiang
 *
 */
public class TextFileManager {
	private static final Logger logger = LoggerFactory.getLogger(TextFileManager.class);
	

	private String inputFile;
	private String outputFile;
	private HashMap<String, String> hashMapTermMapping;
	
	/**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * @return the hashMapTerm
	 */
	public HashMap<String, String> getHashMapTermMapping() {
		return hashMapTermMapping;
	}

	/**
	 * @param hashMapTerm the hashMapTerm to set
	 */
	public void setHashMapTermMapping(HashMap<String, String> hashMapTermMapping) {
		this.hashMapTermMapping = hashMapTermMapping;
	}

	/**
	 * Do one to one term replacement for one text file
	 * @return if conversion is successful or not
	 */
	
	public boolean doTermConversion() {
		BufferedReader bufferedReader;
		ArrayList<String> arrayInput = new ArrayList<String>();
		try {
			bufferedReader = new BufferedReader(new FileReader(inputFile));
			String strRead;
			while ((strRead=bufferedReader.readLine())!=null){
				strRead += "\r\n";
	        	for(String termOld : hashMapTermMapping.keySet()) {
	        		strRead = strRead.replace(termOld + " ", hashMapTermMapping.get(termOld).trim() + " ");
	        		strRead = strRead.replace(termOld + "\r\n", hashMapTermMapping.get(termOld).trim() + "\r\n");
	        		strRead = strRead.replace(termOld + "\"", hashMapTermMapping.get(termOld).trim() + "\"");
	        	}
        		arrayInput.add(strRead);
				
	        	//logger.debug(strRead);
			}
			bufferedReader.close();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile,false));
			
			
			for (String strLineOut : arrayInput) {
//	        	logger.debug(strLineOut);
				out.write(strLineOut);
			}
            out.close();
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
    

		return true;
	}
	
	
}
