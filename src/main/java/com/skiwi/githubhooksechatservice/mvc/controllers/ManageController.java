package com.skiwi.githubhooksechatservice.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.skiwi.githubhooksechatservice.service.UserService;

@Controller
public class ManageController {
	
	@Autowired
	private UserService userService;
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signup() {
		return "signup";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signupSubmit(@RequestParam("j_username") String username, 
			@RequestParam("j_password") String password, 
			@RequestParam("j_password_confirm") String passwordConfirm) {
		if (password.equals(passwordConfirm)) {
			userService.createUser(username, password);
		}
		return "home";
	}
	
}
