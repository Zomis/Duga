GithubHookSEChatService
=======================

[A bot named Duga](http://codereview.stackexchange.com/users/51786/duga) for chat rooms on the Stack Exchange network. Responsibilities include:

- When used as a Github webhook, instantly informs a chatroom about the activity
- Can make requests to Github's API every now and then to check for recent events, informs a chat room if there is activity
- Uses the Stack Exchange API to check comments refering users to Code Review and Programmers. Posts these comments in The 2nd Monitor and The Whiteboard, respectively.
- Listens for chat commands in Duga's Playground.

Configuration
-------------

In the directory `grails-app/conf`, create a file named `duga.groovy`

    // Configurations for the bot's Stack Exchange account:
    rootUrl = 'http://stackexchange.com'
    email = 'your@email.com'
    password = 'yourpassword'
    
    // API configuration
    stackAPI = 'xxxxxxxxx'
    githubAPI = 'xxxxxxxxx'

    // Database configuration
    adminDefaultPass = 'xxxxxxxxx' // default password for username 'admin'
    
    dataSource {
        username = 'xxxxxxxxx'
        password = 'xxxxxxxxx'
    }

Also see the bottom part of `grails-app/conf/application.groovy` for more database configuration options.

Bot account setup
-----------------

In order to run a StackExchange account as a bot, you need to follow the following steps:

1. Go to https://openid.stackexchange.com.
2. Create an account.
3. Create a StackExchange account on http://stackexchange.com.
4. Make sure you can log in to it.
5. Create an account on a specific site, for example http://stackoverflow.com.
6. Earn 20 reputation, following the rules of the particular site.
7. Log in to http://chat.stackexchange.com.
8. Confirm that you can talk.
