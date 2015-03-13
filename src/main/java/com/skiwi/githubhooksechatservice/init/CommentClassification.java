package com.skiwi.githubhooksechatservice.init;

import java.util.regex.Pattern;

import com.skiwi.githubhooksechatservice.stackapi.StackExchangeComment;

public class CommentClassification {
	
    public static final float REAL = 0.49f;
	public static final float DEBUG = 0.01f;
	
	public static final Pattern PROG_LINK = Pattern.compile(
			Pattern.quote("<a href=\"http") + "s?" + Pattern.quote("://programmers.stackexchange.com") + "(/|/help/.*)?" + Pattern.quote("\">"));

	public static boolean bodyContainsProgrammersLink(String body) {
		return PROG_LINK.matcher(body).find();
	}
	
	public static float calcInterestingLevelProgrammers(StackExchangeComment comment) {
		float matchPattern = bodyContainsProgrammersLink(comment.getBody()) ? 1.0f : 0f;
		return matchPattern + calcInterestingLevelProgrammers(comment.getBodyMarkdown());
	}
	
	public static float calcInterestingLevelProgrammers(String comment) {
		comment = comment.toLowerCase();
		if (!comment.contains("programmers")) {
			return 0;
		}
		float points = 0.4f;
		
		points += score(0.3f, comment, "better fit");
		points += score(0.3f, comment, "better suited");
		points += score(0.3f, comment, "better place");
		
		points += score(0.01f, comment, "close");
		points += score(0.05f, comment, "off-topic");
		points += score(0.05f, comment, "design");
		points += score(0.05f, comment, "whiteboard");
		points += score(0.05f, comment, "this question");
		points += score(0.15f, comment, "this site");
		points += score(0.2f, comment, "programmers.se");
		points += score(0.07f, comment, "help at");
		points += score(0.07f, comment, "place to ask");
		points += score(0.15f, comment, "migrate");
		points += score(0.1f, comment, "belong");
		points += score(0.02f, comment, "instead");
		points += score(0.03f, comment, "the place for");
		
		points += score(0.03f, comment, "try programmers");
		points += score(0.03f, comment, "for programmers");
		points += score(0.03f, comment, "on programmers");
		points += score(0.03f, comment, "at programmers");
		points += score(0.03f, comment, "to programmers");
		
		return points;
	}

	private static float score(float f, String comment, String string) {
		if (comment.contains(string)) {
			System.out.println(string + " --- " + comment + " --- " + f);
			return f;
		}
		return 0;
	}

}
