package net.zomis.duga

import groovy.json.JsonSlurper
import org.springframework.core.env.Environment;

import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;

class StackExchangeAPI {

	@Autowired
	private Environment config;
	
	def fetchComments(String site, long fromDate) {
		final String filter = "!1zSk*x-OuqVk2k.(bS0NB";
		return apiCall("comments?page=1&pagesize=100&fromdate=" + fromDate +
				"&order=desc&sort=creation", site, filter);
	}

	private static URL buildURL(String apiCall, String site, String filter, String apiKey)
            throws MalformedURLException {
		if (!apiCall.contains("?")) {
			apiCall = apiCall + "?dummy";
		}
		return new URL("https://api.stackexchange.com/2.2/" + apiCall
					+ "&site=" + site
					+ "&filter=" + filter + "&key=" + apiKey);
	}

	def apiCall(String apiCall, String site, String filter) throws IOException {
		final String apiKey = config.getProperty('stackAPI');
		URL url = buildURL(apiCall, site, filter, apiKey);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        def stream = new GZIPInputStream(connection.getInputStream())
        return new JsonSlurper().parse(stream)
	}
	
}
