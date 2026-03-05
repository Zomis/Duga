# Duga

[A bot named Duga](http://codereview.stackexchange.com/users/51786/duga) for chat rooms on the Stack Exchange network. Responsibilities include:

- When used as a Github webhook, instantly informs a chatroom about the activity
- Can make requests to Github's API every now and then to check for recent events, informs a chat room if there is activity
- Uses the Stack Exchange API to check comments refering users to Code Review and Software Engineers. Posts these comments in The 2nd Monitor and The Whiteboard, respectively.
- Checks Code Review for possible answer invalidations from code edits

## Bot account setup

In order to run a StackExchange account as a chat bot, you need to follow the following steps:

1. Create a StackExchange account on https://stackexchange.com
2. Make sure you can log in to it
3. Create an account on a specific site, for example https://codereview.stackexchange.com
4. Earn 20 reputation, following the rules of the particular site
5. Log in to https://chat.stackexchange.com
6. Confirm that you can talk

## Building / Running

In the duga-ktor folder, create a shadow jar using `./gradlew shadowJar`

Run `java -jar duga-ktor/build/libs/duga.jar`

## Parameters

By default Duga doesn't really do anything, see the file DugaServer.kt for the most up-to-date parameters you can use. Below are some that may or may not still exist:

- hello-world: Posts a Hello World message on start in room 16134
- weekly-update-reminder: Sends a weekly reminder to Simon in room 16134
- vba-star-race: Compare starts on github for two different VBA tool projects and sends message in room 20298 (Duga's Neighborhood)
- refresh: Sends a REFRESH message on UTC midnight in room 16134
- comment-scan: Scans Stack Overflow for comments suggesting OP to post on Code Review or Software Engineers and posts a message in The 2nd Monitor or The Whiteboard, respectively.
- answer-invalidation: Scans Code Review for answer invalidations and notifies The 2nd Monitor
- unanswered: Sends REFRESH message with information about unanswered questions to The 2nd Monitor (room 8595)
- daily-stats: Daily statistics from webhooks

## Serverless

Terraform configuration is in the serverless folder. I have used OpenTofu and AWS. See that folder's README.md for more information.
