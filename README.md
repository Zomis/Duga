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
    rootUrl = 'https://stackexchange.com'
    email = 'your@email.com'
    password = 'yourpassword'
    
    // API configuration
    stackAPI = 'xxxxxxxxx'
    githubAPI = 'xxxxxxxxx'
    commandPrefix = '@Duga ' // chat messages that begins with this will be considered as commands

    // Database configuration
    adminDefaultPass = 'xxxxxxxxx' // default password for username 'admin'
    
    dataSource {
        username = 'xxxxxxxxx'
        password = 'xxxxxxxxx'
    }

Also see the bottom part of `grails-app/conf/application.groovy` for more database configuration options.

Bot account setup
-----------------

In order to run a StackExchange account as a chat bot, you need to follow the following steps:

1. Create a StackExchange account on https://stackexchange.com
2. Make sure you can log in to it
3. Create an account on a specific site, for example https://codereview.stackexchange.com
4. Earn 20 reputation, following the rules of the particular site
5. Log in to https://chat.stackexchange.com
6. Confirm that you can talk

Import project into IntelliJ
----------------------------

On Mac OS X, you might get this error when trying to import the project from `build.gradle`:

> Unsupported major.minor version 51.0
> asset/pipeline/gradle/AssetPipelinePlugin : invalid plugin

A workaround is to have Gradle generate IntelliJ's files:

    ./gradlew idea

Select **File / Open...**, and select the directory of the project.

IntelliJ might still pop-up a window to import as a Gradle project,
which may result in the same error. I had no choice to cancel that import,
but actually everything worked fine.

If you still experience problems, this page might help:
https://github.com/libgdx/libgdx/wiki/Gradle-and-Intellij-IDEA

Build and run tests
-------------------

To build the project and run all tests:

    ./gradlew build
