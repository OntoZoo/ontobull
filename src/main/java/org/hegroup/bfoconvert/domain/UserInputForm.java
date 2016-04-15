package org.hegroup.bfoconvert.domain;
import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.validation.constraints.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;


public class UserInputForm {
	@Autowired
	private ServletContext context;
	
	private String globalMessage;
	
	@NotNull
	private MultipartFile uploadFile;
	
	private MultipartFile uploadMap;
	private Boolean customMap = false;
	private HashMap<String, String> hashMap = new HashMap<String, String>();

	private MultipartFile uploadClean;
	private Boolean customClean = false;
	private HashMap<String, String> hashAddNamespace = new HashMap<String, String>();
	private HashMap<String, String> hashRemoveNamespace = new HashMap<String, String>();
	private HashMap<String, String> hashReplaceNamespace = new HashMap<String, String>();

	private String workDir;
	private String fileName;
	
	public String getGlobalMessage() {
		return globalMessage;
	}

	public void setGlobalMessage(String globalMessage) {
		this.globalMessage = globalMessage;
	}

	public MultipartFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}
	
	public MultipartFile getUploadMap() {
		return uploadMap;
	}

	public void setUploadMap(MultipartFile uploadMap) {
		this.uploadMap = uploadMap;
	}

	public String getWorkDir() {
		return workDir;
	}

	public void setWorkDir(String workDir) {
		if (!workDir.endsWith(File.separator)) workDir += File.separator;
		this.workDir = workDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @return the uploadClean
	 */
	public MultipartFile getUploadClean() {
		return uploadClean;
	}

	/**
	 * @param uploadClean the uploadClean to set
	 */
	public void setUploadClean(MultipartFile uploadClean) {
		this.uploadClean = uploadClean;
	}

	/**
	 * @return the customClean
	 */
	public Boolean getCustomClean() {
		return customClean;
	}

	/**
	 * @param customClean the customClean to set
	 */
	public void setCustomClean(Boolean customClean) {
		this.customClean = customClean;
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
	 * @return the customMap
	 */
	public Boolean getCustomMap() {
		return customMap;
	}

	/**
	 * @param customMap the customMap to set
	 */
	public void setCustomMap(Boolean customMap) {
		this.customMap = customMap;
	}

	/**
	 * @return the hashMap
	 */
	public HashMap<String, String> getHashMap() {
		return hashMap;
	}

	/**
	 * @param hashMap the hashMap to set
	 */
	public void setHashMap(HashMap<String, String> hashMap) {
		this.hashMap = hashMap;
	}
	
	
	
}
