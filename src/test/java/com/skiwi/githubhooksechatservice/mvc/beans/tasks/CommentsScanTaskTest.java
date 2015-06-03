import static org.junit.Assert.*;

public class CommentsScanTaskTest {
  private Pattern interestingComment = Pattern.compile("[*`_]{,2}code[*`_]{,2}\s*[*`_]{,2}review[*`_]{,2}");
	
	private boolean isInterestingComment(String comment) {
		String commentText = comment.toLowerCase(Locale.ENGLISH);
		return interestingComment.matcher(commentText).find(0);
		// We're using `find()` instead of `matches()` because it can occur anywhere and we don't wanna reject if we can't match the whole comment
		// See https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html#matches-- and #find-int- for more information
		// (and we're using find(0) to make sure it searches from the start, not somewhere in the middle)
	}
}
