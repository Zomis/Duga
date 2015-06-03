package com.skiwi.githubhooksechatservice.stackapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StackExchangeComment {
	
	@JsonProperty("post_id")
	private long postId;
	
	@JsonProperty("comment_id")
	private long commentId;
	
	@JsonProperty("creation_date")
	private long creationDate;
	
	@JsonProperty
	private String body;
	
	@JsonProperty
	private String link;
	
	@JsonProperty("body_markdown")
	private String bodyMarkdown;
	
	public String getBody() {
		return body;
	}
	
	public String getBodyMarkdown() {
		return bodyMarkdown;
	}
	
	public long getCommentId() {
		return commentId;
	}
	
	public long getPostId() {
		return postId;
	}

	public String getLink() {
		return link;
	}
	
	public long getCreationDate() {
		return creationDate;
	}
	
}
