package com.skiwi.githubhooksechatservice.init;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommentClassificationTest {

	@Test
	public void testComments() {
		assertIgnore("a dummy comment not containing the p-word");
		assertPost("this question would be a better fit at programmers");
		assertDebug("a dummy comment containing programmers");
	}

	private void assertDebug(String string) {
		float score = CommentClassification.calcInterestingLevelProgrammers(string);
		assertTrue("Expected DEBUG but was " + score + " for comment: " + string,
			score < CommentClassification.REAL && score >= CommentClassification.DEBUG);
	}

	private void assertIgnore(String string) {
		float score = CommentClassification.calcInterestingLevelProgrammers(string);
		assertTrue("Expected NONE but was " + score + " for comment: " + string,
				score < CommentClassification.DEBUG);
	}

	private void assertPost(String string) {
		float score = CommentClassification.calcInterestingLevelProgrammers(string);
		assertTrue("Expected REAL but was " + score + " for comment: " + string,
				score >= CommentClassification.REAL);
	}

}
