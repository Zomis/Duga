package net.zomis.duga.github

import groovy.json.JsonSlurper
import net.zomis.duga.DugaStats
import net.zomis.duga.HookStringification
import net.zomis.duga.chat.WebhookParameters
import net.zomis.duga.tasks.qscan.TestBot
import org.junit.Test

class GitHubHookTest {

    static final String REQUEST = '''
{
    "action": "published",
  "release": {
        "url": "https://api.github.com/repos/Vannevelj/RoslynTester/releases/1779350",
    "assets_url": "https://api.github.com/repos/Vannevelj/RoslynTester/releases/1779350/assets",
    "upload_url": "https://uploads.github.com/repos/Vannevelj/RoslynTester/releases/1779350/assets{?name}",
    "html_url": "https://github.com/Vannevelj/RoslynTester/releases/tag/v1.5.0",
    "id": 1779350,
    "tag_name": "v1.5.0",
    "target_commitish": "master",
    "name": "RoslynTester",
    "draft": false,
    "author": {
            "login": "Vannevelj",
      "id": 2777107,
      "avatar_url": "https://avatars.githubusercontent.com/u/2777107?v=3",
      "gravatar_id": "",
      "url": "https://api.github.com/users/Vannevelj",
      "html_url": "https://github.com/Vannevelj",
      "followers_url": "https://api.github.com/users/Vannevelj/followers",
      "following_url": "https://api.github.com/users/Vannevelj/following{/other_user}",
      "gists_url": "https://api.github.com/users/Vannevelj/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/Vannevelj/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/Vannevelj/subscriptions",
      "organizations_url": "https://api.github.com/users/Vannevelj/orgs",
      "repos_url": "https://api.github.com/users/Vannevelj/repos",
      "events_url": "https://api.github.com/users/Vannevelj/events{/privacy}",
      "received_events_url": "https://api.github.com/users/Vannevelj/received_events",
      "type": "User",
      "site_admin": false
    },
    "prerelease": false,
    "created_at": "2015-09-07T21:20:55Z",
    "published_at": "2015-09-07T21:29:02Z",
    "assets": [
      {
        "url": "https://api.github.com/repos/Vannevelj/RoslynTester/releases/assets/848019",
        "id": 848019,
        "name": "RoslynTester.dll",
        "label": null,
        "uploader": {
          "login": "Vannevelj",
          "id": 2777107,
          "avatar_url": "https://avatars.githubusercontent.com/u/2777107?v=3",
          "gravatar_id": "",
          "url": "https://api.github.com/users/Vannevelj",
          "html_url": "https://github.com/Vannevelj",
          "followers_url": "https://api.github.com/users/Vannevelj/followers",
          "following_url": "https://api.github.com/users/Vannevelj/following{/other_user}",
          "gists_url": "https://api.github.com/users/Vannevelj/gists{/gist_id}",
          "starred_url": "https://api.github.com/users/Vannevelj/starred{/owner}{/repo}",
          "subscriptions_url": "https://api.github.com/users/Vannevelj/subscriptions",
          "organizations_url": "https://api.github.com/users/Vannevelj/orgs",
          "repos_url": "https://api.github.com/users/Vannevelj/repos",
          "events_url": "https://api.github.com/users/Vannevelj/events{/privacy}",
          "received_events_url": "https://api.github.com/users/Vannevelj/received_events",
          "type": "User",
          "site_admin": false
        },
        "content_type": "application/x-msdownload",
        "state": "uploaded",
        "size": 23040,
        "download_count": 0,
        "created_at": "2015-09-07T21:22:43Z",
        "updated_at": "2015-09-07T21:22:44Z",
        "browser_download_url": "https://github.com/Vannevelj/RoslynTester/releases/download/v1.5.0/RoslynTester.dll"
      }
    ],
    "tarball_url": "https://api.github.com/repos/Vannevelj/RoslynTester/tarball/v1.5.0",
    "zipball_url": "https://api.github.com/repos/Vannevelj/RoslynTester/zipball/v1.5.0",
    "body": "This release:\\r\\n\\r\\n* Fixed some issues we had with VB.NET code under test. \\r\\n* Allows you to specify what exact diagnostics are allowed to occur after a code-fix.\\r\\n* Removed the NUnit dependency altogether.\\r\\n\\r\\nFor an example with regards to no.2:\\r\\n\\r\\n    VerifyFix(original, result, allowedNewCompilerDiagnosticsId: \\"CS8019\\");\\r\\n\\r\\nThis will now allow the warning \\"CS8019\\" (*Unnecessary using directive*) to occur.\\r\\n\\r\\nAs always: if you encounter a problem, please let us know by creating an issue on Github."
  },
  "repository": {
    "id": 36010519,
    "name": "RoslynTester",
    "full_name": "Vannevelj/RoslynTester",
    "owner": {
        "login": "Vannevelj",
      "id": 2777107,
      "avatar_url": "https://avatars.githubusercontent.com/u/2777107?v=3",
      "gravatar_id": "",
      "url": "https://api.github.com/users/Vannevelj",
      "html_url": "https://github.com/Vannevelj",
      "followers_url": "https://api.github.com/users/Vannevelj/followers",
      "following_url": "https://api.github.com/users/Vannevelj/following{/other_user}",
      "gists_url": "https://api.github.com/users/Vannevelj/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/Vannevelj/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/Vannevelj/subscriptions",
      "organizations_url": "https://api.github.com/users/Vannevelj/orgs",
      "repos_url": "https://api.github.com/users/Vannevelj/repos",
      "events_url": "https://api.github.com/users/Vannevelj/events{/privacy}",
      "received_events_url": "https://api.github.com/users/Vannevelj/received_events",
      "type": "User",
      "site_admin": false
    },
    "private": false,
    "html_url": "https://github.com/Vannevelj/RoslynTester",
    "description": "A library that will help you unit test your Roslyn analyzers",
    "fork": false,
    "url": "https://api.github.com/repos/Vannevelj/RoslynTester",
    "forks_url": "https://api.github.com/repos/Vannevelj/RoslynTester/forks",
    "keys_url": "https://api.github.com/repos/Vannevelj/RoslynTester/keys{/key_id}",
    "collaborators_url": "https://api.github.com/repos/Vannevelj/RoslynTester/collaborators{/collaborator}",
    "teams_url": "https://api.github.com/repos/Vannevelj/RoslynTester/teams",
    "hooks_url": "https://api.github.com/repos/Vannevelj/RoslynTester/hooks",
    "issue_events_url": "https://api.github.com/repos/Vannevelj/RoslynTester/issues/events{/number}",
    "events_url": "https://api.github.com/repos/Vannevelj/RoslynTester/events",
    "assignees_url": "https://api.github.com/repos/Vannevelj/RoslynTester/assignees{/user}",
    "branches_url": "https://api.github.com/repos/Vannevelj/RoslynTester/branches{/branch}",
    "tags_url": "https://api.github.com/repos/Vannevelj/RoslynTester/tags",
    "blobs_url": "https://api.github.com/repos/Vannevelj/RoslynTester/git/blobs{/sha}",
    "git_tags_url": "https://api.github.com/repos/Vannevelj/RoslynTester/git/tags{/sha}",
    "git_refs_url": "https://api.github.com/repos/Vannevelj/RoslynTester/git/refs{/sha}",
    "trees_url": "https://api.github.com/repos/Vannevelj/RoslynTester/git/trees{/sha}",
    "statuses_url": "https://api.github.com/repos/Vannevelj/RoslynTester/statuses/{sha}",
    "languages_url": "https://api.github.com/repos/Vannevelj/RoslynTester/languages",
    "stargazers_url": "https://api.github.com/repos/Vannevelj/RoslynTester/stargazers",
    "contributors_url": "https://api.github.com/repos/Vannevelj/RoslynTester/contributors",
    "subscribers_url": "https://api.github.com/repos/Vannevelj/RoslynTester/subscribers",
    "subscription_url": "https://api.github.com/repos/Vannevelj/RoslynTester/subscription",
    "commits_url": "https://api.github.com/repos/Vannevelj/RoslynTester/commits{/sha}",
    "git_commits_url": "https://api.github.com/repos/Vannevelj/RoslynTester/git/commits{/sha}",
    "comments_url": "https://api.github.com/repos/Vannevelj/RoslynTester/comments{/number}",
    "issue_comment_url": "https://api.github.com/repos/Vannevelj/RoslynTester/issues/comments{/number}",
    "contents_url": "https://api.github.com/repos/Vannevelj/RoslynTester/contents/{+path}",
    "compare_url": "https://api.github.com/repos/Vannevelj/RoslynTester/compare/{base}...{head}",
    "merges_url": "https://api.github.com/repos/Vannevelj/RoslynTester/merges",
    "archive_url": "https://api.github.com/repos/Vannevelj/RoslynTester/{archive_format}{/ref}",
    "downloads_url": "https://api.github.com/repos/Vannevelj/RoslynTester/downloads",
    "issues_url": "https://api.github.com/repos/Vannevelj/RoslynTester/issues{/number}",
    "pulls_url": "https://api.github.com/repos/Vannevelj/RoslynTester/pulls{/number}",
    "milestones_url": "https://api.github.com/repos/Vannevelj/RoslynTester/milestones{/number}",
    "notifications_url": "https://api.github.com/repos/Vannevelj/RoslynTester/notifications{?since,all,participating}",
    "labels_url": "https://api.github.com/repos/Vannevelj/RoslynTester/labels{/name}",
    "releases_url": "https://api.github.com/repos/Vannevelj/RoslynTester/releases{/id}",
    "created_at": "2015-05-21T12:07:09Z",
    "updated_at": "2015-08-04T20:46:45Z",
    "pushed_at": "2015-09-07T21:21:15Z",
    "git_url": "git://github.com/Vannevelj/RoslynTester.git",
    "ssh_url": "git@github.com:Vannevelj/RoslynTester.git",
    "clone_url": "https://github.com/Vannevelj/RoslynTester.git",
    "svn_url": "https://github.com/Vannevelj/RoslynTester",
    "homepage": null,
    "size": 223,
    "stargazers_count": 2,
    "watchers_count": 2,
    "language": "C#",
    "has_issues": true,
    "has_downloads": true,
    "has_wiki": true,
    "has_pages": false,
    "forks_count": 2,
    "mirror_url": null,
    "open_issues_count": 3,
    "forks": 2,
    "open_issues": 3,
    "watchers": 2,
    "default_branch": "master"
  },
  "sender": {
    "login": "Vannevelj",
    "id": 2777107,
    "avatar_url": "https://avatars.githubusercontent.com/u/2777107?v=3",
    "gravatar_id": "",
    "url": "https://api.github.com/users/Vannevelj",
    "html_url": "https://github.com/Vannevelj",
    "followers_url": "https://api.github.com/users/Vannevelj/followers",
    "following_url": "https://api.github.com/users/Vannevelj/following{/other_user}",
    "gists_url": "https://api.github.com/users/Vannevelj/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/Vannevelj/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/Vannevelj/subscriptions",
    "organizations_url": "https://api.github.com/users/Vannevelj/orgs",
    "repos_url": "https://api.github.com/users/Vannevelj/repos",
    "events_url": "https://api.github.com/users/Vannevelj/events{/privacy}",
    "received_events_url": "https://api.github.com/users/Vannevelj/received_events",
    "type": "User",
    "site_admin": false
  }
}'''

    @Test
    public void testRelease() {
        def stringer = new HookStringification()
        stringer.stats = new DugaStats() {
            @Override void addCommit(def repo, def commit) {}
            @Override void addIssueComment(def Object repo) {}
            @Override def addIssue(def Object repo, int delta) {}
        }

        def obj = new JsonSlurper().parseText(REQUEST)
        def result = stringer.postGithub('release', obj)
        def bot = new TestBot()
        def param = WebhookParameters.toRoom('hookTest')
        bot.postChat(param, result)

        assert bot.messages[param] == ['**\\[[Vannevelj/RoslynTester](https://github.com/Vannevelj/RoslynTester)\\]** [**Vannevelj**](https://github.com/Vannevelj) published release [**v1.5.0**](https://github.com/Vannevelj/RoslynTester/releases/tag/v1.5.0)']

    }

}
