package com.skiwi.githubhooksechatservice.mvc.beans;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;

public class GithubUtils {
	
    public AbstractEvent[] data(String name, String repository) {
    	ObjectMapper mapper = new ObjectMapper(); // just need one
    	try {
    		URL url = new URL("https://api.github.com/repos/" + name + "/" + repository + "/events");
			AbstractEvent[] data = mapper.readValue(url, AbstractEvent[].class);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

}
