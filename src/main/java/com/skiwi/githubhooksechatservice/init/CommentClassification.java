package com.skiwi.githubhooksechatservice.init;

public class CommentClassification {
	
    public static final float REAL = 0.7f;
	public static final float DEBUG = 0.01f;

	public static float calcInterestingLevelProgrammers(String comment) {
		String commentText = comment.toLowerCase();
		if (!commentText.contains("programmers")) {
			return 0;
		}
		
		if (commentText.contains("try programmers") || commentText.contains("for programmers")
				|| commentText.contains("on programmers") || commentText.contains("at programmers")
				|| commentText.contains("to programmers")) {
			return 0.8f;
		}
		return 0.3f;
	}



}
