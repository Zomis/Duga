package com.skiwi.githubhooksechatservice.mvc.beans;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.init.StackComments;

public class StackExchangeAPIBean {

	private final ObjectMapper mapper = new ObjectMapper();
	
	public StackExchangeAPIBean() {
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public StackComments fetchComments(String site, long fromDate) throws JsonParseException, JsonMappingException, IOException {
		final String filter = "5)xweUQV.bOepb44njLJ";
		URL url = new URL("https://api.stackexchange.com/2.2/comments?page=1&pagesize=100&fromdate=" + fromDate +
				"&order=desc&sort=creation&site=" + site + "&filter=" + filter);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        
        return mapper.readValue(new GZIPInputStream(connection.getInputStream()), StackComments.class);
	}
	
}
