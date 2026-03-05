### Build by yourself

Run Gradle build:

`./gradlew shadowJar`

Run Terraform (personally using OpenTofu):

`tofu apply`

Fill in any missing Terraform configuration that may be required. If you are not using some of the features, you can use empty strings (e.g. github api key)

### Usage

... undocumented

### Troubleshooting

If messages to Stack Exchange are not working, try creating a lambda that executes [the Chat Exchange 'hello world' example](https://github.com/Manishearth/ChatExchange/blob/master/examples/hello_world.py)
