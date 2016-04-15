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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class IndexController {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	/**
	 * Show OntoCow main page
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String convertIndex(
			@ModelAttribute("userInputForm") UserInputForm userInputForm,
			Model model, 
	        HttpServletRequest request) {
		logger.debug(request.getSession().getServletContext().getRealPath("/"));
		
		return "/index";
	}
	

	/**
	 * Show BFOConverter
	 */
	@RequestMapping(value = "/bfoconvert", method = RequestMethod.GET)
	public String bfoconvertIndex(
			@ModelAttribute("userInputForm") UserInputForm userInputForm,
			Model model, 
	        HttpServletRequest request) {
		logger.debug(request.getSession().getServletContext().getRealPath("/"));
		
		return "/bfoconvert_index";
	}
	
	/**
	 * Show acknowledge 
	 */
	@RequestMapping(value = "/acknowledge", method = RequestMethod.GET)
	public String acknowledgeIndex() {
		return "/acknowledge";
	}
	
	/**
	 * Show contactus 
	 */
	@RequestMapping(value = "/contactus", method = RequestMethod.GET)
	public String contactusIndex() {
		return "/contactus";
	}
	
	/**
	 * Show download 
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public String downloadIndex() {
		return "/download";
	}
	
	/**
	 * Show faqs 
	 */
	@RequestMapping(value = "/faqs", method = RequestMethod.GET)
	public String faqsIndex() {
		return "/faqs";
	}
	
	/**
	 * Show introduction 
	 */
	@RequestMapping(value = "/introduction", method = RequestMethod.GET)
	public String introductionIndex() {
		return "/introduction";
	}
	
	/**
	 * Show links 
	 */
	@RequestMapping(value = "/links", method = RequestMethod.GET)
	public String linksIndex() {
		return "/links";
	}
	
	/**
	 * Show news 
	 */
	@RequestMapping(value = "/news", method = RequestMethod.GET)
	public String newsIndex() {
		return "/news";
	}
	
	/**
	 * Show references 
	 */
	@RequestMapping(value = "/references", method = RequestMethod.GET)
	public String referencesIndex() {
		return "/references";
	}
	
	/**
	 * Show tutorial 
	 */
	@RequestMapping(value = "/tutorial", method = RequestMethod.GET)
	public String tutorialIndex() {
		return "/tutorial";
	}

	
}
