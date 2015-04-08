package com.skiwi.githubhooksechatservice.mvc.controllers;

public class StringUtils {
	public static String substr(final String str, int index, int length) {
		if (index < 0) { 
			index = str.length() + index;
		}
		
		if (length < 0) { 
			length = str.length() + length;
		}
		else {
			length = index + length;
		}
		
		if (index > str.length()) { 
			return "";
		}
		length = Math.min(length, str.length());
		
		return str.substring(index, length);
	}
	
	public static String substr(final String str, final int index) {
		if (index >= 0) {
			return substr(str, index, str.length() - index);
		}
		else {
			return substr(str, index, -index);
		}
	}
}
