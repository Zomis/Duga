package com.skiwi.githubhooksechatservice.mvc.beans;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.mvc.configuration.BotConfiguration;
import com.skiwi.githubhooksechatservice.stackapi.StackComments;

public class StackExchangeAPIBean {

	private final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private BotConfiguration config;
	
	public StackExchangeAPIBean() {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public StackComments fetchComments(String site, long fromDate) throws JsonParseException, JsonMappingException, IOException {
		final String filter = "!1zSk*x-OuqVk2k.(bS0NB";
		return apiCall("comments?page=1&pagesize=100&fromdate=" + fromDate +
				"&order=desc&sort=creation", site, filter, StackComments.class);
	}
	
	public <E> E apiCall(String apiCall, String site, String filter, Class<E> result) throws IOException {
		final String apiKey = config.getStackAPIKey();
		URL url = buildURL(apiCall, site, filter, apiKey);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
		try (GZIPInputStream stream = new GZIPInputStream(connection.getInputStream())) {
	        return mapper.readValue(stream, result);
		}
	}
	
	private URL buildURL(String apiCall, String site, String filter,
			String apiKey) throws MalformedURLException {
		if (!apiCall.contains("?")) {
			apiCall = apiCall + "?dummy";
		}
		return new URL("https://api.stackexchange.com/2.2/" + apiCall
					+ "&site=" + site
					+ "&filter=" + filter + "&key=" + apiKey);
	}

	public JsonNode apiCall(String apiCall, String site, String filter) throws IOException {
		final String apiKey = config.getStackAPIKey();
		URL url = buildURL(apiCall, site, filter, apiKey);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
		try (GZIPInputStream stream = new GZIPInputStream(connection.getInputStream())) {
	        return mapper.readTree(stream);
		}
	}
	
}
