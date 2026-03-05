### Serverless setup

Remove any irrelevant terraform files that you don't need (e.g. duga-task-star-race and route-53)

Configure the following variables in locals.tf:

locals {
    email = "your.stackexchange.account@example.com"
    password = "your-stackexchange-password"

    stack_exchange_api_key = "stack-exchange API key for making Stack Exchange requests, can be left empty if unused"
    github_api_key = "GitHub API key, can be left empty if unused"
    discord_key = "can be left empty, only used for potential future other communication ways"
}

Make sure to run `gradlew shadowJar` inside duga-ktor before running `tofu apply`

### Updating the chatexchange layer:

`docker run --rm -it -v ${PWD}:/var/task -w /var/task python:3.11 bash`

Inside docker:

pip install -r requirements.txt -t layer/python/

Then zip the created python directory.
