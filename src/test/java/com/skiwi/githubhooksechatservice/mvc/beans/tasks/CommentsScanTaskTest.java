import static org.junit.Assert.*;

public class CommentsScanTaskTest {
	private Pattern interestingComment = Pattern.compile("[*`_]{,3}code[*`_]{,3}\s*[*`_]{,3}review[*`_]{,3}");
	
	private static boolean isInterestingComment(String comment) {
		String commentText = comment.toLowerCase(Locale.ENGLISH);
		return interestingComment.matcher(commentText).find(0);
	}
	
	public void test() throws Exception {
		String[] shouldMatch = {
			"This is a demo comment containing the text Code Review.",
			"This is another with a formatted _Code_ **Review**",
			"This isn't valid Markdown, but *_Code Review*_'s Duga don't care.",
			"Then, of course, CodeReview and coderevIEW and COde       REView should also all be matched",
			"This is a comment about how you need a code review!"
		};
		String[] shouldntMatch = {
			"This is a comment without the C/R words in it.",
			"This is Co*de Review* with formatting in the middle, which isn't matched by design",
			"[Code](http://google.com) Review definitely should not though.",
			"This is a comment about apologizing for code and review.",
			"This is testing that that great site, Code Review, is matched"
		}
		for (String match : shouldMatch)
			assertTrue(match + " should be interesting, but isn't!", isInterestingComment(match));
		for (String match : shouldntMatch)
			assertFalse(match + " shouldn't be interesting, but is!", isInterestingComment(match));
	}
}
