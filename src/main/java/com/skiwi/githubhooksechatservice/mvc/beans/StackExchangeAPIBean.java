package com.skiwi.githubhooksechatservice.mvc.beans;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.init.StackComments;
import com.skiwi.githubhooksechatservice.mvc.configuration.BotConfiguration;

public class StackExchangeAPIBean {

	private final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private BotConfiguration config;
	
	public StackExchangeAPIBean() {
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public StackComments fetchComments(String site, long fromDate) throws JsonParseException, JsonMappingException, IOException {
		final String filter = "!SWJ_BpB*hGj5g523vF";
		final String apiKey = config.getStackAPIKey();
		URL url = new URL("https://api.stackexchange.com/2.2/comments?page=1&pagesize=100&fromdate=" + fromDate +
				"&order=desc&sort=creation&site=" + site + "&filter=" + filter + "&key=" + apiKey);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        
        return mapper.readValue(new GZIPInputStream(connection.getInputStream()), StackComments.class);
	}
	
}
