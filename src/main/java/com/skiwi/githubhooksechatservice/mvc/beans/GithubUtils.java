package com.skiwi.githubhooksechatservice.mvc.beans;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

public class GithubUtils {
	
	
	
    public AnySetterJSONObject[] data(String name, String repository) {
    	ObjectMapper mapper = new ObjectMapper(); // just need one
    	try {
    		URL url = new URL("https://api.github.com/repos/" + name + "/" + repository + "/events");
			AnySetterJSONObject[] data = mapper.readValue(url, AnySetterJSONObject[].class);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }



}
