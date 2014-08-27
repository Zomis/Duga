GithubHookSEChatService
=======================

A Spring MVC web service implementation that posts Github post hook events to the StackExchange chat.

Configuration
-------------

1. Create a githubhooksechatservice-environment.properties file in $CATALINA_HOME$/lib.
2. Populate it with the following properties for example:
 - env.rootUrl = http://stackexchange.com
 - env.chatUrl = http://chat.stackexchange.com
 - env.botEmail = your@email.com
 - env.botPassword = yourpassword
 - env.roomId = 16134
 - env.chatThrottle = 10000 (the minimum time in milliseconds between each message)
 - env.chatMaxBurst = 2 (the maximum amount of message without being throttled)
 - env.chatMinimumDelay = 500 (the minimum delay in milliseconds between messages) 
 - env.deployGreetingOn = true (whether the bot will say something in the room when deployed)
 - env.deployGreetingText = Hello world!
3. Add a post web hook to your Github project, and point the Payload URL to http://yourdomain.com/GithubHookSEChatService/hooks/github/payload

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

Commands
--------

- Test: The bot will say test in the appointed room if you call http://yourdomain.com/GithubHookSEChatService/bot/test
- Say: The bot will say custom text in the appointed room if you call http://yourdomain.com/GithubHookSEChatService/bot/say/yourtest