package net.zomis.duga

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class HookStringification {

    private static final Logger logger = LoggerFactory.getLogger(HookStringification.class)

    @Autowired
    DugaStats stats

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
    private static final int TRUNCATE_TARGET = 498; // max chars in a message is 500, there's two chars in the front of the truncated string

    private static String truncate(String string) {
        return substr(string, 0, TRUNCATE_TARGET)
    }

    static String repository(json) {
        if (!json.repository) {
            return ''
        }
        return "**\\[[$json.repository.full_name]($json.repository.html_url)\\]**"
    }

    String format(obj, String str) {
        str.replaceAll('%repository%', repository(obj))
            .replaceAll('%sender%', user(obj.sender))
    }

    static String issue(json) {
        "[**#$json.number: ${json.title.trim()}**]($json.html_url)"
    }

    static String labelJson(json) {
        "[**$json.label.name**]($json.repository.html_url/labels/${json.label.name.replace(' ', '%20')})"
    }

    static String user(json) {
        if (!json) {
            return ''
        }
        return "[**$json.login**]($json.html_url)"
    }

    void ping(List<String> result, def json) {
        result << format(json, "%repository% Ping: $json.zen")
    }

    String sender(json) {
        "[**$json.sender.login**]($json.sender.html_url)"
    }

    void commit_comment(List<String> result, def json) {
        String path = json.comment.path
        String commitId = json.comment.commit_id.substring(0, 8)
        String commitLink = "[$commitId]($json.repository.html_url/commit/$json.comment.commit_id)"
        if (path == null || path.isEmpty()) {
            result << format(json, "%repository% %sender% [commented]($json.comment.html_url) on commit $commitLink")
        } else {
            result << format(json, "%repository% %sender% [commented on $json.comment.path]($json.comment.html_url) of commit $commitLink")
        }
        result << '> ' + truncate(json.comment.body)
    }

    void create(List<String> result, def json) {
        String refUrl = null;
        switch (json.ref_type) {
            case "branch":
                refUrl = json.repository.html_url + '/tree/' + json.ref
                break;
            case "tag":
                refUrl = json.repository.html_url + "/releases/tag/" + json.ref;
                break;
            case "repository":
                result << format(json, "%repository% %sender% created $json.ref_type")
                return;
        }
        result << format(json, "%repository% %sender% created $json.ref_type [**$json.ref**]($refUrl)")
    }

    void release(List<String> result, def json) {
        boolean prerelease = json.release.prerelease
        String release = prerelease ? 'prerelease' : 'release'
        release = (json.release.draft ? 'draft ' : '') + release
        String refUrl = json.release.html_url
        result << format(json, "%repository% %sender% $json.action $release [**$json.release.tag_name**]($refUrl)")
    }

    void delete(List<String> result, def json) {
        result << format(json, "%repository% %sender% deleted $json.ref_type **$json.ref**")
    }

    void fork(List<String> result, def json) {
        result << format(json, "%repository% %sender% forked us into [**$json.forkee.full_name**]($json.forkee.html_url)")
    }

    void gollum(List<String> result, def json) {
        for (def page : json.pages) {
            result << format(json, "%repository% %sender% $page.action wiki page [**${page.title.trim()}**]($page.html_url)")
        }
    }

    void issues(List<String> result, def json) {
        String issue = "[**#$json.issue.number: ${json.issue.title.trim()}**]($json.issue.html_url)"
        String extra = ''
        if (json.assignee) {
            extra = "[**$json.assignee.login**]($json.assignee.html_url)"
        }
        if (json.label) {
            extra = labelJson(json)
        }
        switch (json.action) {
            case 'assigned':
                result << format(json, "%repository% %sender% $json.action $extra to issue $issue")
                break;
            case 'unassigned':
                result << format(json, "%repository% %sender% $json.action $extra from issue $issue")
                break;
            case "labeled":
                result << format(json, "%repository% %sender% added label $extra to issue $issue")
                break;
            case "unlabeled":
                result << format(json, "%repository% %sender% removed label $extra from issue $issue")
                break;
            case "opened":
                result << format(json, "%repository% %sender% opened issue $issue")
                if (json.issue.body != null && !json.issue.body.isEmpty()) {
                    result << "> " + truncate(json.issue.body as String)
                }
                stats.addIssue(json.repository, 1)
                break;
            case "closed":
                result << format(json, "%repository% %sender% closed issue $issue")
                stats.addIssue(json.repository, -1)
                break;
            case "reopened":
                result << format(json, "%repository% %sender% reopened issue $issue")
                stats.addIssue(json.repository, 1)
                break;
            default:
                result << format(json, "%repository% %sender% $json.action issue $issue")
                break;
        }
    }

    void issue_comment(List<String> result, def json) {
        String issue = issue(json.issue)
        String commentTarget = (json.issue.pull_request == null) ? "issue" : "pull request";
        result << format(json, "%repository% %sender% $json.action [comment]($json.comment.html_url) on $commentTarget $issue");
        result.add('> ' + truncate(json.comment.body))
        stats.addIssueComment(json.repository)
    }

    void label(List<String> result, def json) {
        result << format(json, "%repository% %sender% $json.action label ${labelJson(json)}")
    }

    void repository(List<String> result, def json) {
        result << format(json, "%repository% %sender% $json.action repository %repository%")
    }

    Random random = new Random()
    void project_card(List<String> result, def json) {
        String[] options = [
            "%repository% %sender% $json.action project card",
            "%repository% %sender% did something with some project card",
            "%repository% %sender% is playing around with a project card",
            "%repository% %sender% is checking what fun stuff @Duga can say about project cards",
            "%repository% %sender% is bored so why not move a project card",
            "%repository% %sender% project card. Enough said."
        ]
        result << format(json, options[random.nextInt(options.size())])
    }

    void status(List<String> result, def json) {
        def event = json
        if (event.state == 'pending') {
            logger.info('Status pending.')
            return
        }
        String repoURL = "http://github.com/$event.name"
        String commitId = event.sha.substring(0, 8)
        String branch = null
        if (event.branches.size() > 0) {
            branch = event.branches[0].name
        }
        String build = event.target_url && !event.target_url.isEmpty() ? "[**build**]($event.target_url)" : 'build';

        branch = branch == null ? 'unknown branch' : "[**$branch**]($repoURL/tree/$branch)";
        String mess = "**\\[[$event.name]($repoURL)\\]** " +
                "$build for commit " +
                "[**$commitId**]($repoURL/commit/$commitId) " +
                "on $branch: $event.description"
        result << mess
        if (event.state != 'pending' && event.state != 'success') {
            result << '**BUILD FAILURE!**'
        }
    }

    void member(List<String> result, def json) {
        result << format(json, "%repository% %sender% $json.action [**$json.member.login**]($json.member.html_url)");
    }

    void pull_request_review_comment(List<String> result, def json) {
        result << format(json, "%repository% %sender% [commented on **$json.comment.path**]($json.comment.html_url) of pull request ${issue(json.pull_request)}");
    }

    void pull_request(List<String> result, def json) {
        def head = json.pull_request.head
        def base = json.pull_request.base
        String headText;
        String baseText;
        String pr = issue(json.pull_request)
        String assignee = user(json.assignee)
        if (head.repo.equals(base.repo)) {
            headText = head.ref
            baseText = base.ref
        } else {
            headText = head.repo.full_name + "/" + head.ref
            baseText = base.repo.full_name + "/" + base.ref
        }
        String headStr = "[**$headText**]($head.repo.html_url/tree/$head.ref)"
        String baseStr = "[**$baseText**]($base.repo.html_url/tree/$base.ref)"
        String label = json.label ? "[**$json.label.name**]($json.repository.html_url/labels/${json.label.name.replace(' ', '%20')})" : ''
        switch (json.action) {
            case "assigned":
                result << format(json, "%repository% %sender% assigned $assignee to pull request $pr")
                break;
            case "unassigned":
                result << format(json, "%repository% %sender% unassigned $assignee from pull request $pr")
                break;
            case "labeled":
                result << format(json, "%repository% %sender% added label $label to pull request $pr")
                break;
            case "unlabeled":
                result << format(json, "%repository% %sender% removed label $label from pull request $pr")
                break;
            case "opened":
                result << format(json, "%repository% %sender% created pull request $pr to merge $headStr into $baseStr")
                if (json.pull_request.body != null && !json.pull_request.body.isEmpty()) {
                    String prBody = json.pull_request.body
                    result << '> ' + prBody
                }
                break;
            case "closed":
                if (json.pull_request.merged) {
                    result << format(json, "%repository% %sender% merged pull request $pr from $headStr into $baseStr")
                } else {
                    result << format(json, "%repository% %sender% rejected pull request $pr")
                }
                break;
            case "reopened":
                result << format(json, "%repository% %sender% reopened pull request $pr")
                break;
            case "synchronize":
                result << format(json, "%repository% %sender% synchronized pull request $pr")
                break;
            default:
                result << format(json, "%repository% %sender% $json.action pull request $pr")
        }
    }

    void watch(List<String> result, def json) {
        String action = json.action == 'started' ? 'starred' : json.action;
        result << format(json, "%repository% %sender% $action us")
    }

    void team_add(List<String> result, def json) {
        String team = "[**$json.team.name**]($json.sender.html_url/$json.team.name)"
        if (json.user == null) {
            result << format(json, "%repository% %sender% added us to team $team")
        }
        else {
            result << format(json, "%repository% %sender% added ${user(json.user)} to team $team")
        }
    }

    public String commit(def json, commit) {
        String branch = json.ref.replace("refs/heads/", "");
        String committer;
        if (commit.committer != null) {
            committer = commit.committer.username;
        }
        else {
            committer = json.pusher_login;
        }
        String commitStr = "[**${commit.id.substring(0, 8)}**]($commit.url)"
        String branchStr = "[**$branch**]($json.repository.url/tree/$branch)"
        if (committer == null) {
            return format(json, "%repository% *Unrecognized author* pushed commit $commitStr to $branchStr")
        } else {
            return format(json, "%repository% [**$committer**](http://github.com/$committer) pushed commit $commitStr to $branchStr")
        }
    }

    void push(List<String> result, def json) {
        List<Object> distinctCommits = new ArrayList<>();
        List<Object> nonDistinctCommits = new ArrayList<>();

        for (Object obj : json.commits) {
            logger.info('commit: ' + obj)
            boolean distinct = obj.distinct
            List<Object> addTo = distinct ? distinctCommits : nonDistinctCommits
            addTo.add(obj)
        }

        if (!nonDistinctCommits.isEmpty()) {
            result.add(pushEventSize(json, nonDistinctCommits.size()));
        }

        distinctCommits.forEach({commitObj ->
            // All commits should be stored in stats
            stats.addCommit(json.repository, commitObj);
        })

        final int MAX_DISTINCT_COMMITS = 10;
        if (distinctCommits.size() > MAX_DISTINCT_COMMITS) {
            // if there's too many commits, not all should be informed about
            result.add(pushEventSize(json, distinctCommits.size()) + ' (only showing some of them below)');
            distinctCommits = distinctCommits.subList(distinctCommits.size() - MAX_DISTINCT_COMMITS, distinctCommits.size())
        }

        distinctCommits.forEach({commitObj ->
            if (commitObj.message.indexOf('\n') > 0) {
                result.add(commit(json, commitObj))
                result.add(truncate(commitObj.message));
            } else {
                result.add(truncate(commit(json, commitObj) + ": " + commitObj.message));
            }
        });
    }

    public String pushEventSize(def json, int size) {
        String commitText = (size == 1 ? "commit" : "commits");
        String branch = json.ref.replace("refs/heads/", "");
        return format(json, "%repository% [**$json.pusher.name**](https://github.com/$json.pusher.name) pushed $size $commitText to [**$branch**]($json.repository.url/tree/$branch)")
    }

    List<String> postGithub(String type, def json) {
        List<String> result = new ArrayList<>()
        this."$type"(result, json)
        return result
    }

}
