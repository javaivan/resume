package com.ivanmix.resume.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.ivanmix.resume.Constants;
import com.ivanmix.resume.entity.Member;
import com.ivanmix.resume.form.RegistrationForm;
import com.ivanmix.resume.form.SignUpForm;
import com.ivanmix.resume.repository.storage.MemberRepository;
import com.ivanmix.resume.repository.storage.SkillCategoryRepository;
import com.ivanmix.resume.form.SkillForm;
import com.ivanmix.resume.service.EditMemberService;
import com.ivanmix.resume.service.FindMemberService;
import com.ivanmix.resume.model.CurrentMember;
import com.ivanmix.resume.util.SecurityUtil;

import com.sun.tracing.dtrace.ModuleAttributes;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PublicDataController {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private SkillCategoryRepository skillCategoryRepository;

	@Autowired
	private FindMemberService findMemberService;

	@Autowired
	private EditMemberService editMemberService;

	@RequestMapping(value="/{uid}", method=RequestMethod.GET)
	public String getProfile(@PathVariable("uid") Long uid, Model model){

		Member member = findMemberService.findById(uid);
		if(member == null) {
			return "profile_not_found";
		}
		model.addAttribute("member", member);
		return "profile";
	}
	
	@RequestMapping(value="/error", method=RequestMethod.GET)
	public String getError(){
		LOGGER.debug("getError");
		return "error";
	}


	@RequestMapping(value = { "/welcome" })
	public String listAll(Model model) {
		Page<Member> members = findMemberService.findAll(new PageRequest(0, 2, new Sort("id")));
		//Page<Member> members = findMemberService.findAll();

		model.addAttribute("profiles", members.getContent());
		model.addAttribute("page", members);
		return "profiles";
	}


	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchProfiles(@RequestParam(value="query", required=false) String query, Model model,
								 @PageableDefault(size=2) @SortDefault(sort="id") Pageable pageable) throws UnsupportedEncodingException {
		if(StringUtils.isBlank(query)){
			return "redirect:/welcome";
		} else {
			Page<Member> members = findMemberService.findBySearchQuery( Long.parseLong(query), pageable);
			/*model.addAttribute("profiles", members.getContent());*/
			model.addAttribute("page", members);
			/*model.addAttribute("query", URLDecoder.decode(query, "UTF-8"));*/
			return "search-results";
		}
	}

	@RequestMapping(value = "/fragment/more", method = RequestMethod.GET)
	public String moreProfiles(Model model, @PageableDefault(size=2) @SortDefault(sort="id") Pageable pageable) throws UnsupportedEncodingException {
		Page<Member> members = findMemberService.findAll(pageable);
		model.addAttribute("profiles", members.getContent());
		return "fragment/profile-items";
	}

	@RequestMapping(value = "/login")
	public String login() {
		CurrentMember currentMember = SecurityUtil.getCurrentMember();
		if(currentMember != null) {
				return "redirect:/" + currentMember.getId();
		}else{
				return "login";
		}
	}

	@RequestMapping(value = "/login-failed", method = RequestMethod.GET)
	public String loginFailed(HttpSession session) {
		if (session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") == null) {
				return "redirect:/login";
			}
		return "login";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String getRegistration(Model model){
		CurrentMember currentMember = SecurityUtil.getCurrentMember();
		if(currentMember != null) {
			return "redirect:/" + currentMember.getId();
		}else{
			model.addAttribute("member", new SignUpForm());
			return "registration";
		}
	}


/*

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String getSignOut(Model model){
		return "redirect:/";
	}
*/

	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}



	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String setRegistration(@Valid @ModelAttribute("member") SignUpForm form, BindingResult bindingResult, Model model){
		LOGGER.debug("setRegistration");
		System.out.println("setRegistration 1 " + form);
		if(bindingResult.hasErrors()){
			System.out.println("setRegistration hasErrors " + bindingResult);
			model.addAttribute("member", new SignUpForm());
			return "registration";
		}

		editMemberService.createNewMember(form);
		return "redirect:/";

	}
}
