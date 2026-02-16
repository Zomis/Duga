resource "aws_sqs_queue" "duga_incoming_webhook_dlq" {
  name                       = "duga-incoming-webhook-dlq.fifo"
  fifo_queue                 = true
  content_based_deduplication = true
  message_retention_seconds  = 1209600 # 14 days
}

resource "aws_sqs_queue" "duga_incoming_webhook" {
  name                        = "duga-incoming-webhook.fifo"
  fifo_queue                  = true
  content_based_deduplication = true
  visibility_timeout_seconds  = 60

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.duga_incoming_webhook_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue_redrive_allow_policy" "duga_incoming_webhook_dlq" {
  queue_url = aws_sqs_queue.duga_incoming_webhook_dlq.id

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue"
    sourceQueueArns   = [aws_sqs_queue.duga_incoming_webhook.arn]
  })
}

resource "aws_lambda_event_source_mapping" "sqs_webhook_trigger" {
  event_source_arn = aws_sqs_queue.duga_incoming_webhook.arn
  function_name    = aws_lambda_function.duga_lambda.arn
  batch_size       = 1
  enabled          = true
}
