package net.zomis.duga

import org.springframework.beans.factory.annotation.Autowired

class BitbucketStringification {

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
        return "**\\[[$json.repository.full_name]($json.repository.links.html.href)]**"
    }

    String format(obj, String str) {
        str.replace('%repository%', repository(obj))
    }

    public String commit(def json, def change, commit) {
        String branch = change.new.name
        String branchURL = change.new.links.html.href
        String committer = json.actor.display_name;
        String committerURL = json.actor.links.html.href;
        String commitStr = "[**${commit.hash.substring(0, 7)}**]($commit.links.html.href)"
        String branchStr = "[**$branch**]($branchURL)"
        if (committer == null) {
            return format(json, "%repository% *Unrecognized author* pushed commit $commitStr to $branchStr")
        } else {
            return format(json, "%repository% [**$committer**]($committerURL) pushed commit $commitStr to $branchStr")
        }
    }

    void repo_push(List<String> result, def json) {
        List<Object> distinctCommits = new ArrayList<>();
        List<Object> nonDistinctCommits = new ArrayList<>();

        for (def change : json.push.changes) {
            for (def obj : change.commits) {
//            boolean distinct = obj.distinct
                boolean distinct = true
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
                if (commitObj.message.trim().indexOf('\n') > 0) {
                    result.add(commit(json, change, commitObj))
                    result.add(truncate(commitObj.message.trim()));
                } else {
                    result.add(truncate(commit(json, change, commitObj) + ": " + commitObj.message.trim()));
                }
            });
        }
    }

    public String pushEventSize(def json, int size) {
        String commitText = (size == 1 ? "commit" : "commits");
        String branch = json.ref.replace("refs/heads/", "");
        return format(json, "%repository% [**$json.pusher.name**](https://github.com/$json.pusher.name) pushed $size $commitText to [**$branch**]($json.repository.url/tree/$branch)")
    }

    List<String> postBitbucket(String type, def json) {
        type = type.replace(':', '_');
        List<String> result = new ArrayList<>()
        this."$type"(result, json)
        return result
    }

}
