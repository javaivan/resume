package com.ivanmix.resume.controller.edit;

import com.ivanmix.resume.form.HobbyForm;
import com.ivanmix.resume.service.EditMemberHobbyService;
import com.ivanmix.resume.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class HobbiesController {

    @Autowired
    private EditMemberHobbyService hobbyService;

    @RequestMapping(value="/edit/hobbies", method= RequestMethod.GET)
    public String getEditHobbies(Model model){
        model.addAttribute("hobbies", hobbyService.listHobbiesWithProfileSelected(SecurityUtil.getCurrentIdMember()));
        return "edit/hobbies";
}
    @RequestMapping(value="/edit/hobbies", method=RequestMethod.POST)
    public String saveEditHobbies(@Valid @ModelAttribute("hobbyForm") HobbyForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "edit/hobbies";
        }
        hobbyService.updateHobbies(SecurityUtil.getCurrentIdMember(), form.getItems());
        return "redirect:/";
    }
}
