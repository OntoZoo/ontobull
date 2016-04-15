package org.hegroup.bfoconvert.validator;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import org.hegroup.bfoconvert.service.ZipFileManager;
import org.hegroup.bfoconvert.domain.UserInputForm;
 
public class UserInputValidator implements Validator{
	private static final Logger logger = LoggerFactory.getLogger(UserInputValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		//Validate the UserInputForm instances
		return UserInputForm.class.isAssignableFrom(clazz);
	}

	
	@Override
	public void validate(Object target, Errors errors) {
 
		UserInputForm userInputForm = (UserInputForm)target;
		
		
		MultipartFile uploadFile = userInputForm.getUploadFile();
		
		//fileURL or uploadFile is needed
		if(uploadFile.getSize()==0){
			errors.rejectValue("uploadFile", "UserInputForm.fileUpload.required");
			return;
		}
		
	
		if(uploadFile.getSize()>20848820) {
			errors.rejectValue("uploadFile", "UserInputForm.fileUpload.fileSizeExceeded");
			return;
		}
		
		String workDir = System.getProperty("java.io.tmpdir");
		String fileName = String.valueOf(System.nanoTime());
		String fileExt = "";
		if (!workDir.endsWith(File.separator)) workDir += File.separator;
		if (uploadFile.getOriginalFilename().toLowerCase().endsWith(".zip")) {
			fileExt=".zip";
		} else if (uploadFile.getOriginalFilename().toLowerCase().endsWith(".owl")) {
			fileExt=".owl";
		} else {
			errors.rejectValue("uploadFile", "UserInputForm.fileUpload.formatNotSupported");
			return;
		}

		logger.info( "upload file: {}", workDir + fileName + fileExt );
		if ( fileExt == ".zip" ) {
			try {
				File savedFile = new File(workDir + fileName + fileExt);
				
				uploadFile.transferTo(savedFile);

		
				//Do some file format checking here.
				ZipFileManager zipFileManager = new ZipFileManager();
				zipFileManager.testZipFile(savedFile);
				
				userInputForm.setWorkDir(workDir);
				userInputForm.setFileName(fileName);
			} catch (IllegalStateException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			} catch (IOException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			}
		} else if ( fileExt == ".owl" ) {
			try {
				File savedFile = new File(workDir + fileName + fileExt);
				uploadFile.transferTo(savedFile);
				
				userInputForm.setWorkDir(workDir);
				userInputForm.setFileName(fileName);
			} catch (IllegalStateException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			} catch (IOException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			}
		} else {
			try {
				File savedFile = new File( workDir + fileName + fileExt );
				
				uploadFile.transferTo( savedFile );
			} catch (IllegalStateException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			} catch (IOException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			}
		}
		
		MultipartFile mapFile = userInputForm.getUploadMap();
		if ( mapFile.getSize() != 0 ) {
			if ( mapFile.getSize() > 20848820 ) {
				errors.rejectValue( "uploadFile", "UserInputForm.fileUpload.fileSizeExceeded" );
				return;
			}
			logger.info( "upload mapping: {}", workDir + fileName + "_MAP.txt" );
			try {
				File savedFile = new File( workDir + fileName + "_MAP.txt" );
				
				mapFile.transferTo(savedFile);
				
				userInputForm.setCustomMap( true );
			} catch (IllegalStateException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			} catch (IOException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			}
		}
		
		MultipartFile cleanFile = userInputForm.getUploadClean();
		if ( cleanFile.getSize() != 0 ) {
			if ( cleanFile.getSize() > 20848820 ) {
				errors.rejectValue( "uploadFile", "UserInputForm.fileUpload.fileSizeExceeded" );
				return;
			}
			logger.info( "upload cleaning: {}", workDir + fileName + "_CLEAN.txt" );
			try {
				File savedFile = new File( workDir + fileName + "_CLEAN.txt" );
				
				cleanFile.transferTo(savedFile);
				
				userInputForm.setCustomClean( true );
			} catch (IllegalStateException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			} catch (IOException e) {
				logger.error(e.getMessage());
				errors.rejectValue("uploadFile", "UserInputForm.fileUpload.errorReading");
			}
		}
		
	}



}