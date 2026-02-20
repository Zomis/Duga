resource "aws_iam_role" "scheduler_role" {
  name        = "kotlin-lambda-scheduler-role"
  description = "Duga IAM role for scheduled tasks"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "scheduler.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy" "scheduler_invoke_lambda" {
  name = "scheduler-invoke-lambda"
  role = aws_iam_role.scheduler_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = "lambda:InvokeFunction"
      Resource = aws_lambda_function.duga_lambda.arn
    }]
  })
}

resource "aws_scheduler_schedule_group" "duga_tasks" {
  name = "duga-tasks"
}
