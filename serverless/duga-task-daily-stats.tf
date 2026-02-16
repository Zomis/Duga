resource "aws_scheduler_schedule" "daily_stats" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-daily-stats"
  schedule_expression = "cron(0 0 1/1 * ? *)"
  flexible_time_window {
    mode = "OFF"
    # FLEXIBLE or OFF
    # maximum_window_in_minutes = 2
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "daily-stats"
    })
  }
}
