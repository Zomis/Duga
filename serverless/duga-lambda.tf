resource "aws_s3_bucket" "lambda_code_bucket" {
  bucket = "duga-lambda-artifact-${random_id.suffix.hex}"
}

resource "random_id" "suffix" {
  byte_length = 4
}

locals {
  jar_hash = filemd5("../duga-ktor/build/libs/duga.jar")
}

# Upload JAR to S3
resource "aws_s3_object" "lambda_jar" {
  bucket = aws_s3_bucket.lambda_code_bucket.id
  key    = "duga-${local.jar_hash}.jar"
  source = "../duga-ktor/build/libs/duga.jar"
}

resource "aws_lambda_function" "duga_lambda" {
  function_name = "duga-lambda"
  role          = aws_iam_role.lambda_role.arn

  runtime = "java17"
  handler = "net.zomis.duga.DugaLambda::handleRequest"

  s3_bucket = aws_s3_bucket.lambda_code_bucket.id
  s3_key    = aws_s3_object.lambda_jar.key

  source_code_hash = filebase64sha256("../duga-ktor/build/libs/duga.jar")

  memory_size = 512
  timeout     = 30

  environment {
    variables = {
      SQS_QUEUE = aws_sqs_queue.duga_messages.url
      GITHUB_API = local.github_api_key
      STACK_EXCHANGE_API = local.stack_exchange_api_key
    }
  }

  depends_on = [
    aws_s3_object.lambda_jar
  ]
}
