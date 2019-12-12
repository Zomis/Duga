
gradlew shadowJar
aws lambda update-function-code --function-name duga-post-from-sqs --zip-file fileb://duga-aws/build/libs/duga-aws-1.0-SNAPSHOT-all.jar
