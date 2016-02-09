package net.zomis.duga.chat;

/*
 * Copyright 2014 Clemens Lieb (aka. Vogel612)
 * You may not use the files except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class to help handle oversized messages and maybe some other things.
 * This class has been adapted from JavaBot's PrintUtils class, which is
 * licensed under Apache Software License, Version 2.0
 * 
 * @author Vogel612<<a href="mailto:vogel612@gmx.de"
 *         >vogel612@gmx.de</a>>
 */
class ChatMessageHelper {

	private static final String LINK_TOKEN_REGEX =
			"\\[(?>[^\\]]+|(?<=\\\\)])+\\]\\((?:https?|ftp):\\/\\/\\S*(?:\\s+\"(?:[^\"]|(?<=\\\\)\")+\")?\\)";
	private static final String TAG_TOKEN_REGEX = "\\[(meta-)?+tag:[^\\]]++\\]";
	private static final String STRIKETHROUGH_TOKEN_REGEX = "---.*?---";
	private static final String CODE_TOKEN_REGEX = "\\`[^\\`]++\\`";
	private static final String MARKDOWN_TOKEN_REGEX = "[*_]{1,3}.*?[*_]{1,3}";
	private static final String WORD_TOKEN_REGEX = "[^\\s]++";
	private static final String MESSAGE_TOKEN_REGEX = "(" + LINK_TOKEN_REGEX + "|" + TAG_TOKEN_REGEX + "|" +
		MARKDOWN_TOKEN_REGEX + "|" + STRIKETHROUGH_TOKEN_REGEX + "|" + CODE_TOKEN_REGEX + "|" + WORD_TOKEN_REGEX + ")*";

	private static final Pattern MARKDOWN_TOKENIZER = Pattern.compile(MESSAGE_TOKEN_REGEX, Pattern.CASE_INSENSITIVE
		| Pattern.DOTALL | Pattern.UNICODE_CASE);

	public static List<String> splitToTokens(String message) {
		final Matcher m = MARKDOWN_TOKENIZER.matcher(message);
		final List<String> tokens = new ArrayList<>();
		while (m.find()) {
			String match = m.group(0);
			if (match != null && !match.trim().isEmpty()) {
				tokens.add(match);
			}
		}
		return tokens;
	}

	public static List<String> reassembleTokens(
		final List<String> tokens, final int maxLength, final String continuation) {
		final List<String> messages = new ArrayList<>();

		StringBuilder messageBuilder = new StringBuilder(maxLength);
		for (final String token : tokens) {
			// if we can safely append the next token
			if (messageBuilder.length() + token.length() < (maxLength - continuation.length())) {
				messageBuilder.append(" " + token);
			}
			else {
				messageBuilder.append(continuation);
				messages.add(messageBuilder.toString());
				messageBuilder = new StringBuilder(token);
			}
		}
		messages.add(messageBuilder.toString());
		return messages;
	}
}
