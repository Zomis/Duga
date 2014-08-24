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
3. Add a post web hook to your Github project, and point it to http://yourdomain.com/GithubHookSEChatService/hooks/github

Commands
--------

- Test: The bot will say test in the appointed room if you call http://yourdomain.com/GithubHookSEChatService/bot/test
- Say: The bot will say custom text in the appointed room if you call http://yourdomain.com/GithubHookSEChatService/bot/say/yourtest