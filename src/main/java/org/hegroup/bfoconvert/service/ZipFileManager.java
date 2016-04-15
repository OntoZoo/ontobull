/**
 * Service class to handle zip files.
 */
package org.hegroup.bfoconvert.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxiang
 *
 */
public class ZipFileManager {
	private static final Logger logger = LoggerFactory.getLogger(ZipFile.class);

	private ArrayList<String> listFilePath = new ArrayList<String>();
	
	private String workDir;
	private String inputZipFilePath;
	private String outputZipFilePath;

	
	/**
	 * @return the listFilePath
	 */
	public ArrayList<String> getListFilePath() {
		return listFilePath;
	}



	/**
	 * @param listFilePath the listFilePath to set
	 */
	public void setListFilePath(ArrayList<String> listFilePath) {
		this.listFilePath = listFilePath;
	}



	/**
	 * @return the workDir
	 */
	public String getWorkDir() {
		return workDir;
	}



	/**
	 * @param workDir the workDir to set
	 */
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}



	/**
	 * @return the inputZipFilePath
	 */
	public String getInputZipFilePath() {
		return inputZipFilePath;
	}



	/**
	 * @param inputZipFilePath the inputZipFilePath to set
	 */
	public void setInputZipFilePath(String inputZipFilePath) {
		this.inputZipFilePath = inputZipFilePath;
	}



	/**
	 * @return the outputZipFilePath
	 */
	public String getOutputZipFilePath() {
		return outputZipFilePath;
	}



	/**
	 * @param outputZipFilePath the outputZipFilePath to set
	 */
	public void setOutputZipFilePath(String outputZipFilePath) {
		this.outputZipFilePath = outputZipFilePath;
	}



	/**
	 * Unzip uploaded file
	 */
	public boolean unZipIt() {
		if (!workDir.endsWith(File.separator)) workDir += File.separator;
		

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(workDir);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(inputZipFilePath));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = workDir + ze.getName();
				File newFile = new File(fileName);

				logger.debug("Unzip file: {}", fileName);

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder

				if (ze.isDirectory()) {
					new File(newFile.getParent()).mkdirs();
				} else {
					FileOutputStream fos = null;

					new File(newFile.getParent()).mkdirs();

					fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
					
					listFilePath.add(fileName);
				}

				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	

	public boolean writeZipFile() {
		if (!workDir.endsWith(File.separator)) workDir += File.separator;

		try {
			FileOutputStream fos = new FileOutputStream(outputZipFilePath);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String filePath : listFilePath) {
				logger.debug("Adding '{}'",  filePath);
				if (!addToZip(workDir, filePath, zos))
					return false;
			}

			logger.debug("Writing '{}'", outputZipFilePath);
			
			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		
		return true;
	}

	private boolean addToZip(String workDir, String filePath, ZipOutputStream zos) {
		File file = new File(filePath);
		if (!file.isDirectory()) { // we only zip files, not directories
			if (!workDir.endsWith(File.separator)) workDir += File.separator;
	
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				logger.debug("filePath: {}", filePath);
				logger.debug("workDir: {}", workDir);
		
				// we want the zipEntry's path to be a relative path that is relative
				// to the directory being zipped, so chop off the rest of the path
				String zipFilePath = filePath.substring(workDir.length(),
						filePath.length());
				ZipEntry zipEntry = new ZipEntry(zipFilePath);
				zos.putNextEntry(zipEntry);
		
				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
		//			logger.debug("write 1024 bytes");
					zos.write(bytes, 0, length);
				}
		
				zos.closeEntry();
				fis.close();
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
				return false;
			} catch (IOException e) {
				logger.error(e.getMessage());
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Try to open the uploaded file as a zip file.
	 * Reject if the file is not in zip format.
	 */
	public boolean testZipFile(final File file) {
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(file);
		} catch (ZipException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			if (zipfile != null)
				try {
					zipfile.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
		}
		return true;
	}
}
