/**
 * This Controller handles requests for the conversion module.
 */

package org.hegroup.bfoconvert.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.hegroup.bfoconvert.domain.UserInputForm;
import org.hegroup.bfoconvert.service.NamespaceCleanupListManager;
import org.hegroup.bfoconvert.service.OWLFileManager;
import org.hegroup.bfoconvert.service.TermMappingListManager;
import org.hegroup.bfoconvert.service.TermSeparationListManager;
import org.hegroup.bfoconvert.service.TextFileManager;
import org.hegroup.bfoconvert.service.ZipFileManager;
import org.hegroup.bfoconvert.validator.UserInputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConvertController {
	@Autowired
	private ServletContext context;
	
	private static final Logger logger = LoggerFactory.getLogger(ConvertController.class);
	
	@ModelAttribute("userInputForm")
	public UserInputForm getUserInputForm() {
		return new UserInputForm();
	}
	
	/**
	 * Attach the custom validator to the Spring context
	 */
	@InitBinder("userInputForm")
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new UserInputValidator());
	}
	
	/*
	 * Validate and process OntoCow data
	 */
	@RequestMapping(value = "/convert", method = RequestMethod.POST)
	public String convertDo(
			@Valid @ModelAttribute("userInputForm") UserInputForm userInputForm,
	        BindingResult result,
			Model model
	        ) {

		if (result.hasErrors()) {
			logger.error(result.getFieldError().toString());
			return "/index";
		} 
		
		String fileName = userInputForm.getFileName();
		String workDir = userInputForm.getWorkDir();
		
		String fileOriginalName = userInputForm.getUploadFile().getOriginalFilename();
		String fileExt = fileOriginalName.substring(fileOriginalName.lastIndexOf('.'));
		logger.info( "File Extension: " + fileExt );
		
		TermMappingListManager termMappingListManager = new TermMappingListManager();
		
		if ( !userInputForm.getCustomMap() ) {
			if ( userInputForm.getHashMap().isEmpty() ) {
				result.rejectValue("globalMessage", "TermMappingListManager.noMappingInput");
				return "/index";
			} else {
				if ( !termMappingListManager.parseMappingInput( userInputForm.getHashMap() ) ) {
					result.rejectValue("globalMessage", "TermMappingListManager.cannotParseMappingInput");
					return "/index";
				}
			}
		} else {
			logger.info( "Custom mapping file uploaded." );
			termMappingListManager.setMappingFilePath( workDir + fileName + "_MAP.txt" );
			
			if (!termMappingListManager.parseMappingFile()) {
				result.rejectValue("globalMessage", "TermMappingListManager.cannotParseMappingFile");
				return "/index";
			}
		}
		

		
		HashMap<String, String> hashMapTermMapping = termMappingListManager.getHashMapTermMapping();
		
		NamespaceCleanupListManager namespaceCleanupListManager = new NamespaceCleanupListManager();
		if ( !userInputForm.getCustomClean() ) {
			logger.info( "No cleanup uploaded, use input setting." );
			if ( !userInputForm.getHashAddNamespace().isEmpty() ) {
				namespaceCleanupListManager.setHashAddNamespace( userInputForm.getHashAddNamespace() );
			}
			if ( !userInputForm.getHashRemoveNamespace().isEmpty() ) {
				namespaceCleanupListManager.setHashRemoveNamespace( userInputForm.getHashRemoveNamespace() );
			}
			if ( !userInputForm.getHashReplaceNamespace().isEmpty() ) {
				namespaceCleanupListManager.setHashReplaceNamespace( userInputForm.getHashReplaceNamespace() );
			}
		} else {
			logger.info( "Custom mapping file uploaded." );
			namespaceCleanupListManager.setCleanupFilePath( workDir + fileName + "_CLEAN.txt" );
			if ( !namespaceCleanupListManager.parseCleanupFile() ) {
				result.rejectValue("globalMessage", "NamespaceCleanupListManager.cannotParseCleanupFile");
				return "/index";
			}
		}
		HashMap<String, String> hashAddNamespace = namespaceCleanupListManager.getHashAddNamespace();
		HashMap<String, String> hashRemoveNamespace = namespaceCleanupListManager.getHashRemoveNamespace();
		HashMap<String, String> hashReplaceNamespace = namespaceCleanupListManager.getHashReplaceNamespace();
		
		if ( fileExt.equals( ".zip" ) ) {
		
			ZipFileManager zipFileManager = new ZipFileManager();
			zipFileManager.setWorkDir(workDir + fileName + File.separator);
			zipFileManager.setInputZipFilePath(workDir + userInputForm.getFileName() + ".zip");
			
			if(!zipFileManager.unZipIt()){
				result.rejectValue("globalMessage", "ZipFileManager.cannotUnZipFile");
				return "/index";
			}
			
			ArrayList<String> listFilePath = zipFileManager.getListFilePath();
			
			/*
			 * Process each .owl and .txt file
			 */
			for (String fileInZip : listFilePath) {
				if (fileInZip.toLowerCase().endsWith(".owl")) {
					OWLFileManager owlFileManager = new OWLFileManager();
					owlFileManager.setHashMapTermMapping(hashMapTermMapping);
					owlFileManager.setHashAddNamespace(hashAddNamespace);
					owlFileManager.setHashRemoveNamespace(hashRemoveNamespace);
					owlFileManager.setInputFile(fileInZip);
					owlFileManager.setOutputFile(fileInZip);
					owlFileManager.setWorkDir( workDir + fileName + File.separator );
					
					if (!owlFileManager.loadOntology()) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/index";
					}
					
					
					if (!owlFileManager.doTermConversion()) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/index";
					}	
					if ( !owlFileManager.doNamespaceCleanup() ) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/index";
					}
					if(!owlFileManager.saveChanges()) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/index";
					}
				}
				//TODO: OWLAPI OWLEntityRenamer still has problem
				//Hacky way to deal with owl file and text file.
				//OWLAPI OWLEntityRenamer has some bug? Some entities were not changed. Don't know why...
				TextFileManager textFileManager = new TextFileManager();
				textFileManager.setHashMapTermMapping(hashMapTermMapping);
				textFileManager.setInputFile(fileInZip);
				textFileManager.setOutputFile(fileInZip);
				
				if (!textFileManager.doTermConversion()) {
					result.rejectValue("globalMessage", "TextFileManager.errorConvertTextFile");
					return "/index";
				}
				
				
			}
			
			/*
			 * Create zip output file
			 */
			zipFileManager.setOutputZipFilePath(context.getRealPath("/userfiles") + File.separator + fileName + ".zip");
			
			if (!zipFileManager.writeZipFile()) {
				result.rejectValue("globalMessage", "ZipFileManager.cannotZipFile");
				return "/index";
			}
			
			model.addAttribute("outputFile", fileName + ".zip");
			
		} else if ( fileExt.equals( ".owl" ) ) {
			String fileFullPath = workDir + fileName + ".owl";
			OWLFileManager owlFileManager = new OWLFileManager();
			owlFileManager.setHashMapTermMapping(hashMapTermMapping);
			owlFileManager.setHashAddNamespace(hashAddNamespace);
			owlFileManager.setHashRemoveNamespace(hashRemoveNamespace);
			owlFileManager.setHashReplaceNamespace(hashReplaceNamespace);
			owlFileManager.setInputFile(fileFullPath);
			owlFileManager.setOutputFile(fileFullPath);
			
			if (!owlFileManager.loadOntology()) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/index";
			}
			
			
			if (!owlFileManager.doTermConversion()) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/index";
			}	
			if ( !owlFileManager.doNamespaceCleanup() ) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/index";
			}
			if(!owlFileManager.saveChanges()) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/index";
			}
			
			//TODO: OWLAPI OWLEntityRenamer still has problem
			//Hacky way to deal with owl file and text file.
			//OWLAPI OWLEntityRenamer has some bug? Some entities were not changed. Don't know why...
			TextFileManager textFileManager = new TextFileManager();
			textFileManager.setHashMapTermMapping(hashMapTermMapping);
			textFileManager.setInputFile(fileFullPath);
			textFileManager.setOutputFile(fileFullPath);
			
			if (!textFileManager.doTermConversion()) {
				result.rejectValue("globalMessage", "TextFileManager.errorConvertTextFile");
				return "/index";
			}
			
			try {
				File src = new File( fileFullPath );
				File des = new File( context.getRealPath("/userfiles") + File.separator + fileName + ".owl" );
				FileUtils.copyFile( src, des );
			} catch ( IOException e ) {
				result.rejectValue( "globalMessage", "OWLFileManager.errorSaveOWLFile" );
				return "/index";
			}
			
			model.addAttribute("outputFile",  fileName + ".owl" );
			
		} else {
			result.rejectValue("globalMessage", "ConvertController.unknownFileExtension");
			return "/index";
		}
		
		return "/success";
	}
	
	/**
	 * Show a BFOConvert success page with output file download link
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String convertSuccess(
			@RequestParam(required=true, value="outputFile") String outputFile,
			Model model) {
		
		logger.debug("outputFile: {}", outputFile);
		model.addAttribute("outputFile", outputFile);
		
		return "/success";
	}

	/**
	 * Validate and process BFOConvert data 
	 */
	@RequestMapping(value = "/bfoconvert", method = RequestMethod.POST)
	public String convertBFODo(
			@Valid @ModelAttribute("userInputForm") UserInputForm userInputForm,
	        BindingResult result,
			Model model
	        ) {

		if (result.hasErrors()) {
			logger.error(result.getFieldError().toString());
			return "/bfoconvert_index";
		} 
		
		String fileName = userInputForm.getFileName();
		String workDir = userInputForm.getWorkDir();
		
		String fileOriginalName = userInputForm.getUploadFile().getOriginalFilename();
		String fileExt = fileOriginalName.substring(fileOriginalName.lastIndexOf('.'));
		logger.info( "File Extension: " + fileExt );
		
		/*
		 * Get one to one term mapping.
		 */
		TermMappingListManager termMappingListManager = new TermMappingListManager();

		if ( !userInputForm.getCustomMap() ) {
			logger.info( "No mapping uploaded, use default setting." );
			termMappingListManager.setMappingFilePath(
					context.getRealPath("/resources") + File.separator + "alltermMapping2.0.txt");
		} else {
			logger.info( "Custom mapping file uploaded." );
			termMappingListManager.setMappingFilePath( workDir + fileName + "_MAP.txt" );
		}
				
		
		if (!termMappingListManager.parseMappingFile()) {
			result.rejectValue("globalMessage", "TermMappingListManager.cannotParseMappingFile");
			return "/bfoconvert_index";
		}

		HashMap<String, String> hashMapTermMapping = termMappingListManager.getHashMapTermMapping();
		
		/*
		 * BFOConvert
		 */
		NamespaceCleanupListManager namespaceCleanupListManager = new NamespaceCleanupListManager();
		if ( !userInputForm.getCustomClean() ) {
			logger.info( "No cleanup uploaded, use default setting." );
			namespaceCleanupListManager.setCleanupFilePath(
					context.getRealPath("/resources") + File.separator + "namespaceCleanup.txt");
		} else {
			logger.info( "Custom mapping file uploaded." );
			namespaceCleanupListManager.setCleanupFilePath( workDir + fileName + "_CLEAN.txt" );
		}
		if ( !namespaceCleanupListManager.parseCleanupFile() ) {
			result.rejectValue("globalMessage", "NamespaceCleanupListManager.cannotParseCleanupFile");
			return "/bfoconvert_index";
		}
		HashMap<String, String> hashAddNamespace = namespaceCleanupListManager.getHashAddNamespace();
		HashMap<String, String> hashRemoveNamespace = namespaceCleanupListManager.getHashRemoveNamespace();
		HashMap<String, String> hashReplaceNamespace = namespaceCleanupListManager.getHashReplaceNamespace();
		
		if ( fileExt.equals( ".zip" ) ) {
		
			ZipFileManager zipFileManager = new ZipFileManager();
			zipFileManager.setWorkDir(workDir + fileName + File.separator);
			zipFileManager.setInputZipFilePath(workDir + userInputForm.getFileName() + ".zip");
			
			if(!zipFileManager.unZipIt()){
				result.rejectValue("globalMessage", "ZipFileManager.cannotUnZipFile");
				return "/bfoconvert_index";
			}
			
			ArrayList<String> listFilePath = zipFileManager.getListFilePath();
			
			/*
			 * Process each .owl and .txt file
			 */
			for (String fileInZip : listFilePath) {
				if (fileInZip.toLowerCase().endsWith(".owl")) {
					OWLFileManager owlFileManager = new OWLFileManager();
					owlFileManager.setHashMapTermMapping(hashMapTermMapping);
					owlFileManager.setHashAddNamespace(hashAddNamespace);
					owlFileManager.setHashRemoveNamespace(hashRemoveNamespace);
					owlFileManager.setHashReplaceNamespace(hashReplaceNamespace);
					owlFileManager.setInputFile(fileInZip);
					owlFileManager.setOutputFile(fileInZip);
					owlFileManager.setWorkDir( workDir + fileName + File.separator );
					
					if (!owlFileManager.loadOntology()) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/bfoconvert_index";
					}
					
					
					if (!owlFileManager.doTermConversion()) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/bfoconvert_index";
					}	
					if ( !owlFileManager.doNamespaceCleanup() ) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/bfoconvert_index";
					}
					if(!owlFileManager.saveChanges()) {
						result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
						return "/bfoconvert_index";
					}
				}
				//TODO: OWLAPI OWLEntityRenamer still has problem
				//Hacky way to deal with owl file and text file.
				//OWLAPI OWLEntityRenamer has some bug? Some entities were not changed. Don't know why...
				TextFileManager textFileManager = new TextFileManager();
				textFileManager.setHashMapTermMapping(hashMapTermMapping);
				textFileManager.setInputFile(fileInZip);
				textFileManager.setOutputFile(fileInZip);
				
				if (!textFileManager.doTermConversion()) {
					result.rejectValue("globalMessage", "TextFileManager.errorConvertTextFile");
					return "/bfoconvert_index";
				}
				
				
			}
			
			/*
			 * Create zip output file
			 */
			zipFileManager.setOutputZipFilePath(context.getRealPath("/userfiles") + File.separator + fileName + ".zip");
			
			if (!zipFileManager.writeZipFile()) {
				result.rejectValue("globalMessage", "ZipFileManager.cannotZipFile");
				return "/bfoconvert_index";
			}
			
			model.addAttribute("outputFile", fileName + ".zip");
			
		} else if ( fileExt.equals( ".owl" ) ) {
			String fileFullPath = workDir + fileName + ".owl";
			OWLFileManager owlFileManager = new OWLFileManager();
			owlFileManager.setHashMapTermMapping(hashMapTermMapping);
			owlFileManager.setHashAddNamespace(hashAddNamespace);
			owlFileManager.setHashRemoveNamespace(hashRemoveNamespace);
			owlFileManager.setHashReplaceNamespace(hashReplaceNamespace);
			owlFileManager.setInputFile(fileFullPath);
			owlFileManager.setOutputFile(fileFullPath);
			
			if (!owlFileManager.loadOntology()) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/bfoconvert_index";
			}
			
			
			if (!owlFileManager.doTermConversion()) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/bfoconvert_index";
			}	
			if ( !owlFileManager.doNamespaceCleanup() ) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/bfoconvert_index";
			}
			if(!owlFileManager.saveChanges()) {
				result.rejectValue("globalMessage", "OWLFileManager.errorConvertOWLFile");
				return "/bfoconvert_index";
			}
			
			//TODO: OWLAPI OWLEntityRenamer still has problem
			//Hacky way to deal with owl file and text file.
			//OWLAPI OWLEntityRenamer has some bug? Some entities were not changed. Don't know why...
			TextFileManager textFileManager = new TextFileManager();
			textFileManager.setHashMapTermMapping(hashMapTermMapping);
			textFileManager.setInputFile(fileFullPath);
			textFileManager.setOutputFile(fileFullPath);
			
			if (!textFileManager.doTermConversion()) {
				result.rejectValue("globalMessage", "TextFileManager.errorConvertTextFile");
				return "/bfoconvert_index";
			}
			
			try {
				File src = new File( fileFullPath );
				File des = new File( context.getRealPath("/userfiles") + File.separator + fileName + ".owl" );
				FileUtils.copyFile( src, des );
			} catch ( IOException e ) {
				result.rejectValue( "globalMessage", "OWLFileManager.errorSaveOWLFile" );
				return "/bfoconvert_index";
			}
			
			model.addAttribute("outputFile",  fileName + ".owl" );
			
		} else {
			result.rejectValue("globalMessage", "ConvertController.unknownFileExtension");
			return "/bfoconvert_index";
		}
		
		return "/bfoconvert_success";
	}
	
	/**
	 * Show a BFOConvert success page with output file download link
	 */
	@RequestMapping(value = "/bfoconvert_success", method = RequestMethod.GET)
	public String convertBFOSuccess(
			@RequestParam(required=true, value="outputFile") String outputFile,
			Model model) {
		
		logger.debug("outputFile: {}", outputFile);
		model.addAttribute("outputFile", outputFile);
		
		return "/bfoconvert_success";
	}
	
}
