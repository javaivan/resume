package com.ivanmix.resume.controller;

import com.ivanmix.resume.service.NameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PublicDataController {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private NameService nameService;

	@RequestMapping(value="/{uid}", method=RequestMethod.GET)
	public String getProfile(@PathVariable("uid") String uid, Model model){
		LOGGER.debug("getProfile");
		String fullName = nameService.convertName(uid);
		model.addAttribute("fullName", fullName);
		return "profile";
	}
	
	@RequestMapping(value="/error", method=RequestMethod.GET)
	public String getError(){
		LOGGER.debug("getError");
		return "error";
	}
}