resource "aws_sqs_queue" "duga_messages_dlq" {
  name                       = "duga-messages-dlq.fifo"
  fifo_queue                 = true
  content_based_deduplication = true
  message_retention_seconds  = 1209600 # 14 days
}

resource "aws_sqs_queue" "duga_messages" {
  name                        = "duga-messages.fifo"
  fifo_queue                  = true
  content_based_deduplication = true
  visibility_timeout_seconds  = 60

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.duga_messages_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue_redrive_allow_policy" "duga_messages_dlq" {
  queue_url = aws_sqs_queue.duga_messages_dlq.id

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue"
    sourceQueueArns   = [aws_sqs_queue.duga_messages.arn]
  })
}

# Lambda layer with chatexchange package
resource "aws_lambda_layer_version" "chatexchange" {
  filename            = "${path.module}/chatexchange-layer.zip"
  source_code_hash    = filebase64sha256("${path.module}/chatexchange-layer.zip")
  layer_name          = "chatexchange-layer"
  compatible_runtimes = ["python3.9", "python3.10", "python3.11", "python3.12"]

  lifecycle {
    create_before_destroy = true
  }
}

# Lambda function (Python)
resource "aws_lambda_function" "duga_poster" {
  source_code_hash = data.archive_file.duga_poster.output_base64sha256
  filename         = ".zip/duga-poster.zip"
  function_name    = "duga-poster"
  role            = aws_iam_role.poster_lambda_role.arn
  handler         = "poster.lambda_handler"
  runtime         = "python3.11"
  timeout         = 30
  memory_size     = 512
  description     = "Duga lambda for posting chat messages from SQS"

  layers = [aws_lambda_layer_version.chatexchange.arn]

  environment {
    variables = {
      USER_EMAIL = local.email
      USER_PASSWORD = local.password
    }
  }

}

# IAM role for Lambda function
resource "aws_iam_role" "poster_lambda_role" {
  name        = "duga-poster-lambda-role"
  description = "Duga IAM role for posting chat messages"

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

resource "aws_iam_role_policy" "poster_lambda_policy" {
  name = "duga-poster-lambda-policy"
  role = aws_iam_role.poster_lambda_role.id

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
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Resource = aws_sqs_queue.duga_messages.arn
      }
    ]
  })
}

# Event source mapping: SQS to Lambda
resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.duga_messages.arn
  function_name    = aws_lambda_function.duga_poster.arn
  batch_size       = 1
  enabled          = true
}

# CloudWatch Log Group for Lambda
resource "aws_cloudwatch_log_group" "duga_poster_logs" {
  name              = "/aws/lambda/duga-poster"
  retention_in_days = 30
}

data "archive_file" "duga_poster" {
  type        = "zip"
  source_file = "code/poster.py"
  output_path = ".zip/duga-poster.zip"
}
