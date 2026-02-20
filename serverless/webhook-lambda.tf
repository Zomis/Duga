resource "aws_iam_role" "duga_webhook_lambda_role" {
  name        = "duga-webhook-lambda-role"
  description = "Duga IAM role for webhook Lambda function"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

# IAM policy for Lambda to access SQS, DynamoDB, etc.
resource "aws_iam_role_policy" "duga_webhook_lambda_policy" {
  name = "duga-webhook-lambda-policy"
  role = aws_iam_role.duga_webhook_lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Effect = "Allow"
        Action = [
          "sqs:GetQueueAttributes",
          "sqs:SendMessage",
          "sqs:SendMessageBatch"
        ]
        Resource = aws_sqs_queue.duga_incoming_webhook.arn
      }
    ]
  })
}

resource "aws_lambda_function" "duga_webhook_lambda" {
  source_code_hash = data.archive_file.duga_webhook_lambda.output_base64sha256
  filename         = ".zip/duga-webhook.zip"
  function_name    = "duga-webhook"
  role            = aws_iam_role.duga_webhook_lambda_role.arn
  handler         = "webhook_lambda.lambda_handler"
  runtime         = "python3.11"
  timeout         = 30
  memory_size     = 512
  description     = "Duga lambda for things requiring instant response (e.g. GitHub webhook)"

  environment {
    variables = {
      SQS_WEBHOOK_QUEUE = aws_sqs_queue.duga_incoming_webhook.url
    }
  }
}

resource "aws_cloudwatch_log_group" "duga_webhook_logs" {
  name              = "/aws/lambda/duga-webhook"
  retention_in_days = 30
}

data "archive_file" "duga_webhook_lambda" {
  type        = "zip"
  source_file = "code/webhook_lambda.py"
  output_path = ".zip/duga-webhook.zip"
}
