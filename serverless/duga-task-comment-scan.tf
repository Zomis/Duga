resource "aws_scheduler_schedule" "comment_scan" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-comment-scan"
  schedule_expression = "cron(0/1 * 1/1 * ? *)"
  description         = "Duga task: Scan for Stack Overflow comments about posting elsewhere"
  flexible_time_window {
    mode = "OFF"
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "comment-scan"
    })
  }
}
