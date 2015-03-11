package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.events.github.classes.GithubRepository;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;
import com.skiwi.githubhooksechatservice.mvc.beans.TaskManager;
import com.skiwi.githubhooksechatservice.service.GithubService;
import com.skiwi.githubhooksechatservice.service.UserService;

@Controller
public class ManageController {
	
	@Autowired
	private GithubBean githubBean;
	
	@Autowired
	private GithubService githubService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private Statistics statistics;
	
	@Autowired
	private TaskManager tasks;
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signup() {
		return "signup";
	}
	
	@RequestMapping(value="/user-login", method=RequestMethod.GET)
	public ModelAndView loginForm() {
		return new ModelAndView("login-form");
	}
	
	@RequestMapping(value="/error-login", method=RequestMethod.GET)
	public ModelAndView invalidLogin() {
		ModelAndView modelAndView = new ModelAndView("login-form");
		modelAndView.addObject("error", true);
		return modelAndView;
	}
	
	@RequestMapping(value="/success-login", method=RequestMethod.GET)
	public ModelAndView successLogin() {
		return new ModelAndView("success-login");
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
	
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public String config() {
        return "admin-config";
    }

	@RequestMapping(value = "/config/threads", method = RequestMethod.GET)
	public String threads(Model model) {
		model.addAttribute("threads", Thread.getAllStackTraces());
		return "threads";
	}    
    
	@RequestMapping(value = "/config/tasks", method = RequestMethod.GET)
	@ResponseBody
	public String tasks() {
		tasks.reload();
		return tasks.getTasks().toString();
	}    
    
	@RequestMapping(value = "/config/addtask", method = RequestMethod.GET)
	@ResponseBody
	public String addTask() {
		tasks.add("0 42 * * * *", "message;16134;%time%");
		return "added task";
	}    
    
    @RequestMapping(value = "/manage", method = RequestMethod.GET)
    public String manage() {
        return "manage";
    }

    @RequestMapping(value = "/config/track", method = RequestMethod.GET)
    @ResponseBody
    public String gitTrack(@RequestParam("name") String name, @RequestParam("user") Boolean user) {
    	if (user == null) {
    		user = false;
    	}
    	List<AbstractEvent> blocks;
		try {
			blocks = githubBean.fetchEvents(user, name, -1);
	    	long eventId = blocks.stream().mapToLong(bl -> bl.getId()).max().orElse(0);
	    	githubService.update(name, Instant.now().getEpochSecond(), eventId, user);
	        return String.valueOf(blocks.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString() + ": " + e.getMessage();
		}
    }

    @RequestMapping(value = "/config/fakestat", method = RequestMethod.GET)
    @ResponseBody
    public String fakeDailyStat(@RequestParam("name") String name, @RequestParam("url") String url) {
    	statistics.informAboutURL(new GithubRepository() {
			
			@Override
			public String getUrl() {
				return url;
			}
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public String getHtmlUrl() {
				return url;
			}
			
			@Override
			public String getFullName() {
				return name;
			}
		});
    	return "Added entry for " + name + ": " + url;
    }

    @RequestMapping(value = "/speak", method = RequestMethod.GET)
    public String say(WebhookParameters params) {
        return "speak";
    }
}
