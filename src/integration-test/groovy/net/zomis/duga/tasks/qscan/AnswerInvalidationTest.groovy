package net.zomis.duga.tasks.qscan

import groovy.json.JsonSlurper
import net.zomis.duga.GithubBean
import net.zomis.duga.HookStringification
import net.zomis.duga.chat.TestBot
import net.zomis.duga.chat.WebhookParameters
import org.junit.Test
import org.springframework.core.env.Environment

import java.time.Instant

class AnswerInvalidationTest {
    // https://grails.github.io/grails-doc/latest/guide/testing.html

    final TestBot bot = new TestBot()

    @Test
    public void testAnswersInvalidatedMultipleCodeBlocks() {
        def stackAPI = new StackMockAPI()
        def questions = new JsonSlurper().parseText('''
{"items":[{"owner":{"reputation":38517,"user_id":31562,"user_type":"moderator","display_name":"Simon Forsberg","link":"http://codereview.stackexchange.com/users/31562/simon-forsberg"},"last_editor":{"reputation":38517,"user_id":31562,"user_type":"moderator","display_name":"Simon Forsberg","link":"http://codereview.stackexchange.com/users/31562/simon-forsberg"},"answer_count":1,"last_activity_date":1428425267,"creation_date":1428416878,"last_edit_date":1428420749,"question_id":86150,"link":"http://codereview.stackexchange.com/questions/86150/highest-pit-only-climbing-through-the-pit-once","body":"<p>For a problem description, see <a href=\\"http://codereview.stackexchange.com/questions/86139/highest-pit-algorithm/86142\\">other question</a>.</p>\\n\\n<p>Imagine I would write this code at an interview. What would you say?</p>\\n\\n<p>Time complexity: \\\\$O(n)\\\\$</p>\\n\\n<p>Space complexity: \\\\$O(n)\\\\$</p>\\n\\n<p>Auxiliary space complexity: \\\\$O(1)\\\\$</p>\\n\\n<pre><code>public class FindDeepestPit {\\n\\n    public static void main(String[] args) {\\n        int[] heights = { 0, 9, 6, -2, 7, 8, 0, -3, 2, 3 };\\n        int result = findDeepestPit(heights);\\n        System.out.println(result);\\n    }\\n\\n    private static int findDeepestPit(int[] heights) {\\n        int firstIndex = 0;\\n        int deepest = -1;\\n        int depth = 0;\\n        boolean climbingUp = false;\\n\\n        /*\\n        * mark current position as highest (firstIndex)\\n        * - go to next as long as we're going down\\n        * - when we're not going down anymore, switch to mark us going up\\n        * - go up until we can't go up anymore, then save the current depth of the pit, and mark the current position as highest\\n        * */\\n\\n        for (int i = 0; i &lt; heights.length - 1; i++) {\\n            int currentHeight = heights[i];\\n            int nextHeight = heights[i + 1];\\n            // find higher point\\n            if (!climbingUp) { // climbing down\\n                if (currentHeight &lt; nextHeight) {\\n                    // we can't go further down here\\n                    climbingUp = true;\\n                    deepest = i;\\n                }\\n            } else { // climbing up\\n                if (currentHeight &gt; nextHeight) {\\n                    // we can't get further up here.\\n                    int lastIndex = i;\\n                    int depthA = heights[firstIndex] - heights[deepest];\\n                    int depthB = heights[lastIndex] - heights[deepest];\\n                    int currDepth = Math.min(depthA, depthB);\\n                    depth = Math.max(depth, currDepth);\\n                    firstIndex = i;\\n                    climbingUp = false;\\n                }\\n            }\\n        }\\n\\n        int depthA = heights[firstIndex] - heights[deepest];\\n        int depthB = heights[heights.length - 1] - heights[deepest];\\n        int currDepth = Math.min(depthA, depthB);\\n        depth = Math.max(depth, currDepth);\\n\\n        return depth;\\n    }\\n}\\n</code></pre>\\n\\n<p>Primary concerns:</p>\\n\\n<ul>\\n<li>Is the approach clear?</li>\\n<li>Are there any edge cases I did not think about?</li>\\n<li>At an interview, would adding unit tests for this method be a good idea?</li>\\n</ul>\\n"}],"has_more":false,"quota_max":10000,"quota_remaining":8671}
        ''')

        def edits = new JsonSlurper().parseText('''
{
  "items": [
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "last_tags": [
        "c++",
        "classes",
        "playing-cards"
      ],
      "tags": [
        "c++",
        "oop",
        "playing-cards"
      ],
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1441747373,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 7,
      "body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n\\n<p><strong>Edit (as part of an answer-invalidation detection test):</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "added 1360 characters in body; edited tags",
      "revision_guid": "1B2FF853-7E28-4BB0-91AC-0EAE9A972506"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1418951129,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 6,
      "title": "Deck and Card classes and member-accessing with one header",
      "body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_title": "Is this a proper use of multiple classes and member-accessing with one header?",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "added 3 characters in body; edited title",
      "revision_guid": "D73E0B34-2345-46C2-91B7-B8549439DD64"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "last_tags": [
        "c++",
        "classes"
      ],
      "tags": [
        "c++",
        "classes",
        "playing-cards"
      ],
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1389933103,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 5,
      "title": "Is this a proper use of multiple classes and member-accessing with one header?",
      "last_title": "Proper use of multiple classes and member-accessing with one header?",
      "comment": "edited tags; edited title",
      "revision_guid": "A2FE8641-3E4D-41F8-8942-D66F97429AD1"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371174936,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 4,
      "title": "Proper use of multiple classes and member-accessing with one header?",
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_title": "Proper use of multiple classes and namespace in one header?",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: Here's a snippet of an an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Based on my research, this is a valid practice.  However, the variables are (obviously) still accessible to the entire program, which still looks bad (even though they're <code>const</code>).</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "Removed alternatives (to be added as answers)",
      "revision_guid": "5FB907BF-82EB-4D1A-B466-606B8889A191"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371170880,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 3,
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: Here's a snippet of an an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Based on my research, this is a valid practice.  However, the variables are (obviously) still accessible to the entire program, which still looks bad (even though they're <code>const</code>).</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: I've got an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Here's a snippet of the changes, and I'd like to know if this is better than what I have in the larger code block:</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "added 80 characters in body",
      "revision_guid": "1621042F-EEC1-4C27-90CC-3F9444C4FF43"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371169793,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 2,
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: I've got an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Here's a snippet of the changes, and I'd like to know if this is better than what I have in the larger code block:</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "Added updated code snippet",
      "revision_guid": "84955DB0-CEE6-4F3E-AE08-3806A3CC2B49"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "tags": [
        "c++",
        "classes"
      ],
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371168505,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 1,
      "title": "Proper use of multiple classes and namespace in one header?",
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "revision_guid": "347DCE6D-A34A-41AA-8FEF-918E73B6EED6"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 8757
}''')

        stackAPI.expect(QuestionScanTask.LATEST_QUESTIONS, questions)
        stackAPI.expect(AnswerInvalidationCheck.editCall(86150), edits)

        String a = '"last_body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter\'s construction, but I\'m not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn\'t include class implementations, but I\'ve kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; \'[\' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; \']\';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",'
        String b = '''
"body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n\\n<p><strong>Edit (as part of an answer-invalidation detection test):</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",'''
        println a.count('<code>')
        println b.count('<code>')
        a = AnswerInvalidationCheck.stripNonCode(a)
        println a
        b = AnswerInvalidationCheck.stripNonCode(b)
        println b

        assert !a.equals(b)

        QuestionScanTask task = new QuestionScanTask(stackAPI, new GithubBean(),
                new HookStringification(), bot,
                'codereview', 'answerInvalidation', 'roomAnswerInvalidation')
        task.lastCheck = Instant.ofEpochSecond(1428420748) // question was edited at 1428420749
        task.run()
        def messages = bot.messages.get(WebhookParameters.toRoom('roomAnswerInvalidation'))
        println bot.messages

        assert AnswerInvalidationCheck.codeChanged(edits, Instant.ofEpochSecond(1428420748))

        assert messages == ['*possible answer invalidation by Jamal on question by Simon Forsberg:* http://codereview.stackexchange.com/posts/86150/revisions'] : bot.messages
    }

    @Test
    public void testNoCodeChange() {
        def edits = new JsonSlurper().parseText('''
{
  "items": [
    {
      "user": {
        "reputation": 25565,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1441751307,
      "post_id": 104074,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 3,
      "title": "Eulerian Tour in Python",
      "body": "<p>This is a recursive algorithm implementation of Eulerian tour search.</p>\\n\\n<p>I guess there is no way to make it more efficient (except rewriting with loops instead of recursion). Any advice on style?</p>\\n\\n<pre><code>def sub(visited, _cur, graph):\\n    if not graph:\\n        return visited + [_cur]\\n    for i, edge in enumerate(graph):\\n        cur, nex = edge\\n        if _cur not in edge:\\n            continue\\n        _graph = graph[:]\\n        del _graph[i]\\n        if _cur == cur:\\n            res = sub(visited + [cur], nex, _graph)\\n        else:\\n            res = sub(visited + [nex], cur, _graph)\\n        if res:\\n            return res\\n\\n\\ndef find_eulerian_tour(graph):\\n    head, tail = graph[0], graph[1:]\\n    prev, nex = head\\n    return sub([prev], nex, tail)\\n\\nassert find_eulerian_tour([(1, 2), (2, 3), (3, 4), (4, 1)]) == [1, 2, 3, 4, 1]\\nassert find_eulerian_tour([\\n    (0, 1), (1, 5), (1, 7), (4, 5),\\n    (4, 8), (1, 6), (3, 7), (5, 9),\\n    (2, 4), (0, 4), (2, 5), (3, 6),\\n    (8, 9)\\n]) == [0, 1, 7, 3, 6, 1, 5, 4, 8, 9, 5, 2, 4, 0]\\n</code></pre>\\n",
      "last_title": "Eulerian Tour in python",
      "last_body": "<p>A recursive algorithm implementation of Eulerian tour search </p>\\n\\n<pre><code>def sub(visited, _cur, graph):\\n    if not graph:\\n        return visited + [_cur]\\n    for i, edge in enumerate(graph):\\n        cur, nex = edge\\n        if _cur not in edge:\\n            continue\\n        _graph = graph[:]\\n        del _graph[i]\\n        if _cur == cur:\\n            res = sub(visited + [cur], nex, _graph)\\n        else:\\n            res = sub(visited + [nex], cur, _graph)\\n        if res:\\n            return res\\n\\n\\ndef find_eulerian_tour(graph):\\n    head, tail = graph[0], graph[1:]\\n    prev, nex = head\\n    return sub([prev], nex, tail)\\n\\nassert find_eulerian_tour([(1, 2), (2, 3), (3, 4), (4, 1)]) == [1, 2, 3, 4, 1]\\nassert find_eulerian_tour([\\n    (0, 1), (1, 5), (1, 7), (4, 5),\\n    (4, 8), (1, 6), (3, 7), (5, 9),\\n    (2, 4), (0, 4), (2, 5), (3, 6),\\n    (8, 9)\\n]) == [0, 1, 7, 3, 6, 1, 5, 4, 8, 9, 5, 2, 4, 0]\\n</code></pre>\\n\\n<p>I guess there is no way to make it more efficient(except rewrite with loops instead of recursion). Any advice about style?</p>\\n",
      "comment": "added 4 characters in body; edited title",
      "revision_guid": "BF1DB730-67C9-45D1-A34E-A86C4CEB64A0"
    },
    {
      "user": {
        "reputation": 243,
        "user_id": 46070,
        "user_type": "registered",
        "profile_image": "https://i.stack.imgur.com/KBU1u.jpg?s=128&g=1",
        "display_name": "kharandziuk",
        "link": "http://codereview.stackexchange.com/users/46070/kharandziuk"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1441696908,
      "post_id": 104074,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 2,
      "body": "<p>A recursive algorithm implementation of Eulerian tour search </p>\\n\\n<pre><code>def sub(visited, _cur, graph):\\n    if not graph:\\n        return visited + [_cur]\\n    for i, edge in enumerate(graph):\\n        cur, nex = edge\\n        if _cur not in edge:\\n            continue\\n        _graph = graph[:]\\n        del _graph[i]\\n        if _cur == cur:\\n            res = sub(visited + [cur], nex, _graph)\\n        else:\\n            res = sub(visited + [nex], cur, _graph)\\n        if res:\\n            return res\\n\\n\\ndef find_eulerian_tour(graph):\\n    head, tail = graph[0], graph[1:]\\n    prev, nex = head\\n    return sub([prev], nex, tail)\\n\\nassert find_eulerian_tour([(1, 2), (2, 3), (3, 4), (4, 1)]) == [1, 2, 3, 4, 1]\\nassert find_eulerian_tour([\\n    (0, 1), (1, 5), (1, 7), (4, 5),\\n    (4, 8), (1, 6), (3, 7), (5, 9),\\n    (2, 4), (0, 4), (2, 5), (3, 6),\\n    (8, 9)\\n]) == [0, 1, 7, 3, 6, 1, 5, 4, 8, 9, 5, 2, 4, 0]\\n</code></pre>\\n\\n<p>I guess there is no way to make it more efficient(except rewrite with loops instead of recursion). Any advice about style?</p>\\n",
      "last_body": "<p>A recursive algorithm implementation of Eulerian tour search </p>\\n\\n<pre><code>def sub(visited, _cur, graph):\\n    if not graph:\\n        return visited + [_cur]\\n    for i, edge in enumerate(graph):\\n        cur, nex = edge\\n        if _cur not in edge:\\n            continue\\n        _graph = graph[:]\\n        del _graph[i]\\n        if _cur == cur:\\n            res = sub(visited + [cur], nex, _graph)\\n        else:\\n            res = sub(visited + [nex], cur, _graph)\\n        if res:\\n            return res\\n\\n\\ndef find_eulerian_tour(graph):\\n    head, tail = graph[0], graph[1:]\\n    prev, nex = head\\n    return sub([prev], nex, tail)\\n\\nassert find_eulerian_tour([(1, 2), (2, 3), (3, 4), (4, 1)]) == [1, 2, 3, 4, 1]\\nassert find_eulerian_tour([\\n    (0, 1), (1, 5), (1, 7), (4, 5),\\n    (4, 8), (1, 6), (3, 7), (5, 9),\\n    (2, 4), (0, 4), (2, 5), (3, 6),\\n    (8, 9)\\n]) == [0, 1, 7, 3, 6, 1, 5, 4, 8, 9, 5, 2, 4, 0]\\n</code></pre>\\n",
      "comment": "added 130 characters in body",
      "revision_guid": "A5C45E47-2A5F-46C8-AA03-B1D0A742D2FC"
    },
    {
      "user": {
        "reputation": 243,
        "user_id": 46070,
        "user_type": "registered",
        "profile_image": "https://i.stack.imgur.com/KBU1u.jpg?s=128&g=1",
        "display_name": "kharandziuk",
        "link": "http://codereview.stackexchange.com/users/46070/kharandziuk"
      },
      "tags": [
        "python",
        "graph"
      ],
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1441655382,
      "post_id": 104074,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 1,
      "title": "Eulerian Tour in python",
      "body": "<p>A recursive algorithm implementation of Eulerian tour search </p>\\n\\n<pre><code>def sub(visited, _cur, graph):\\n    if not graph:\\n        return visited + [_cur]\\n    for i, edge in enumerate(graph):\\n        cur, nex = edge\\n        if _cur not in edge:\\n            continue\\n        _graph = graph[:]\\n        del _graph[i]\\n        if _cur == cur:\\n            res = sub(visited + [cur], nex, _graph)\\n        else:\\n            res = sub(visited + [nex], cur, _graph)\\n        if res:\\n            return res\\n\\n\\ndef find_eulerian_tour(graph):\\n    head, tail = graph[0], graph[1:]\\n    prev, nex = head\\n    return sub([prev], nex, tail)\\n\\nassert find_eulerian_tour([(1, 2), (2, 3), (3, 4), (4, 1)]) == [1, 2, 3, 4, 1]\\nassert find_eulerian_tour([\\n    (0, 1), (1, 5), (1, 7), (4, 5),\\n    (4, 8), (1, 6), (3, 7), (5, 9),\\n    (2, 4), (0, 4), (2, 5), (3, 6),\\n    (8, 9)\\n]) == [0, 1, 7, 3, 6, 1, 5, 4, 8, 9, 5, 2, 4, 0]\\n</code></pre>\\n",
      "revision_guid": "14C6A617-8E18-44E8-800C-F1D158A4C5C5"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 8686
}''')

        String b = edits.items[0].last_body
        String a = edits.items[0].body

        a = AnswerInvalidationCheck.stripNonCode(a)
        b = AnswerInvalidationCheck.stripNonCode(b)

        println a
        println '---------------'
        println b

        assert a == b

        boolean changed = AnswerInvalidationCheck.codeChanged(edits, Instant.ofEpochSecond(1441751300))
        assert !changed
    }

    @Test
    public void testRollback() {
        def edits = new JsonSlurper().parseText('''
{
  "items": [
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "last_tags": [
        "c++",
        "classes",
        "playing-cards"
      ],
      "tags": [
        "c++",
        "oop",
        "playing-cards"
      ],
      "set_community_wiki": false,
      "is_rollback": true,
      "creation_date": 1441747373,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 7,
      "body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n\\n<p><strong>Edit (as part of an answer-invalidation detection test):</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "added 1360 characters in body; edited tags",
      "revision_guid": "1B2FF853-7E28-4BB0-91AC-0EAE9A972506"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1418951129,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 6,
      "title": "Deck and Card classes and member-accessing with one header",
      "body": "<p>Previous review of this project:</p>\\n\\n<p><a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_title": "Is this a proper use of multiple classes and member-accessing with one header?",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "added 3 characters in body; edited title",
      "revision_guid": "D73E0B34-2345-46C2-91B7-B8549439DD64"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "last_tags": [
        "c++",
        "classes"
      ],
      "tags": [
        "c++",
        "classes",
        "playing-cards"
      ],
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1389933103,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 5,
      "title": "Is this a proper use of multiple classes and member-accessing with one header?",
      "last_title": "Proper use of multiple classes and member-accessing with one header?",
      "comment": "edited tags; edited title",
      "revision_guid": "A2FE8641-3E4D-41F8-8942-D66F97429AD1"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371174936,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 4,
      "title": "Proper use of multiple classes and member-accessing with one header?",
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_title": "Proper use of multiple classes and namespace in one header?",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: Here's a snippet of an an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Based on my research, this is a valid practice.  However, the variables are (obviously) still accessible to the entire program, which still looks bad (even though they're <code>const</code>).</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "Removed alternatives (to be added as answers)",
      "revision_guid": "5FB907BF-82EB-4D1A-B466-606B8889A191"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371170880,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 3,
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: Here's a snippet of an an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Based on my research, this is a valid practice.  However, the variables are (obviously) still accessible to the entire program, which still looks bad (even though they're <code>const</code>).</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: I've got an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Here's a snippet of the changes, and I'd like to know if this is better than what I have in the larger code block:</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "added 80 characters in body",
      "revision_guid": "1621042F-EEC1-4C27-90CC-3F9444C4FF43"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371169793,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 2,
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>EDIT</strong>: I've got an alternate solution working using <code>extern</code> instead of <code>namespace</code>.  Here's a snippet of the changes, and I'd like to know if this is better than what I have in the larger code block:</p>\\n\\n<pre><code>// --- Deck.h ---\\n\\nextern const std::string RANKS;\\nextern const std::string SUITS;\\n// class declarations below\\n\\n// --- Card.cpp ---\\n\\nconst std::string RANKS = \\"A23456789TJQK\\";\\nconst std::string SUITS = \\"HDCS\\";\\n</code></pre>\\n\\n<hr>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "last_body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "comment": "Added updated code snippet",
      "revision_guid": "84955DB0-CEE6-4F3E-AE08-3806A3CC2B49"
    },
    {
      "user": {
        "reputation": 25550,
        "user_id": 22222,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/LI1za.jpg?s=128&g=1",
        "display_name": "Jamal",
        "link": "http://codereview.stackexchange.com/users/22222/jamal"
      },
      "tags": [
        "c++",
        "classes"
      ],
      "set_community_wiki": false,
      "is_rollback": false,
      "creation_date": 1371168505,
      "post_id": 27379,
      "post_type": "question",
      "revision_type": "single_user",
      "revision_number": 1,
      "title": "Proper use of multiple classes and namespace in one header?",
      "body": "<p>Previous review of this project: <a href=\\"http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes\\">http://codereview.stackexchange.com/questions/27154/am-i-following-any-bad-practices-in-my-updated-card-and-deck-classes</a></p>\\n\\n<p>This time, I considered combining both classes into one header file so that I can reduce my use of <code>#include</code> and to keep them together.  I also did this so that I can define my <code>const std::string</code> variables only once for both classes.  However, I decided to try using a <code>namespace</code> for these variables for easier access.  It makes more sense (to me) to keep them with <code>Card</code> and let <code>Deck</code> refer to them during the latter's construction, but I'm not sure how to do that.</p>\\n\\n<p>Is this the proper thing to do, or could something be done differently?  Is there a need for <code>extern</code> somewhere?  The code below doesn't include class implementations, but I've kept them intact from the linked code.</p>\\n\\n<p><strong>Deck.h</strong></p>\\n\\n<pre><code>#ifndef DECK_H\\n#define DECK_H\\n\\n#include &lt;iostream&gt;\\n#include &lt;string&gt;\\n#include &lt;array&gt;\\n\\nnamespace rs\\n{\\n    const std::string RANKS = \\"A23456789TJQK\\";\\n    const std::string SUITS = \\"HDCS\\";\\n}\\n\\nclass Card\\n{\\nprivate:\\n    unsigned rankValue;\\n    char rank;\\n    char suit;\\n\\npublic:\\n    Card();\\n    Card(char r, char s);\\n    bool operator&lt;(const Card &amp;rhs) const {return (rankValue &lt; rhs.rankValue);}\\n    bool operator&gt;(const Card &amp;rhs) const {return (rankValue &gt; rhs.rankValue);}\\n    bool operator==(const Card &amp;rhs) const {return (suit == rhs.suit);}\\n    bool operator!=(const Card &amp;rhs) const {return (suit != rhs.suit);}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Card &amp;aCard)\\n        {return out &lt;&lt; '[' &lt;&lt; aCard.rank &lt;&lt; aCard.suit &lt;&lt; ']';}\\n};\\n\\nclass Deck\\n{\\nprivate:\\n    static const unsigned MAX_SIZE = 52;\\n    std::array&lt;Card, MAX_SIZE&gt; cards;\\n    int topCardPos;\\n\\npublic:\\n    Deck();\\n    void shuffle();\\n    Card deal();\\n    unsigned size() const {return topCardPos+1;}\\n    bool empty() const {return topCardPos == -1;}\\n    friend std::ostream&amp; operator&lt;&lt;(std::ostream &amp;out, const Deck &amp;aDeck);\\n};\\n\\n#endif\\n</code></pre>\\n",
      "revision_guid": "347DCE6D-A34A-41AA-8FEF-918E73B6EED6"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 8757
}''')

        boolean changed = AnswerInvalidationCheck.codeChanged(edits, Instant.ofEpochSecond(1428420748))
        println 'changed ' + changed
        assert !changed
    }

    @Test
    public void testIndentationChange() {
        String a = '''<code>0\\na\\nb\\nc\\nd</code>'''
        String b = '''<code>0\\n    a\\n    b\\n    c\\nd</code>'''

        assert AnswerInvalidationCheck.stripNonCode(a) == AnswerInvalidationCheck.stripNonCode(b)
    }

    @Test
    public void testCodeBackticksChanged() {
        String a = '''<code>0\\na\\nb\\nc\\nd</code>'''
        String b = '''<code>0\\na\\nb\\nc\\nd</code> some text <code>backtick</code>'''

        assert AnswerInvalidationCheck.stripNonCode(a) == AnswerInvalidationCheck.stripNonCode(b)
    }

}
