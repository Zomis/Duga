
package com.skiwi.githubhooksechatservice.apis.commit;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;
import com.skiwi.githubhooksechatservice.events.github.User;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponse extends AnySetterJSONObject {
	@JsonProperty
	private String sha;
	
	@JsonProperty
	private CommitResponseCommit commit;
	
	@JsonProperty
	private String url;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty("comments_url")
	private String commentsUrl;
	
	@JsonProperty
	private User author;
	
	@JsonProperty
	private User committer;
	
	@JsonProperty
	private CommitResponseParent[] parents;
	
	@JsonProperty
	private CommitResponseStats stats;
	
	@JsonProperty
	private CommitResponseFile[] files;

	public String getSha() {
		return sha;
	}

	public CommitResponseCommit getCommit() {
		return commit;
	}

	public String getUrl() {
		return url;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getCommentsUrl() {
		return commentsUrl;
	}

	public User getAuthor() {
		return author;
	}

	public User getCommitter() {
		return committer;
	}

	public List<CommitResponseParent> getParents() {
		return Arrays.asList(parents);
	}

	public CommitResponseStats getStats() {
		return stats;
	}

	public List<CommitResponseFile> getFiles() {
		return Arrays.asList(files);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.sha);
		hash = 79 * hash + Objects.hashCode(this.commit);
		hash = 79 * hash + Objects.hashCode(this.url);
		hash = 79 * hash + Objects.hashCode(this.htmlUrl);
		hash = 79 * hash + Objects.hashCode(this.commentsUrl);
		hash = 79 * hash + Objects.hashCode(this.author);
		hash = 79 * hash + Objects.hashCode(this.committer);
		hash = 79 * hash + Arrays.deepHashCode(this.parents);
		hash = 79 * hash + Objects.hashCode(this.stats);
		hash = 79 * hash + Arrays.deepHashCode(this.files);
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CommitResponse other = (CommitResponse)obj;
		if (!Objects.equals(this.sha, other.sha)) {
			return false;
		}
		if (!Objects.equals(this.commit, other.commit)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.commentsUrl, other.commentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.author, other.author)) {
			return false;
		}
		if (!Objects.equals(this.committer, other.committer)) {
			return false;
		}
		if (!Arrays.deepEquals(this.parents, other.parents)) {
			return false;
		}
		if (!Objects.equals(this.stats, other.stats)) {
			return false;
		}
		if (!Arrays.deepEquals(this.files, other.files)) {
			return false;
		}
		return true;
	}
}
