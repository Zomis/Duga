resource "aws_scheduler_schedule" "week_update" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-weekly-update"
  schedule_expression = "cron(0 17 ? * MON *)"
  description         = "Duga task: Simon's custom reminder"
  flexible_time_window {
    mode = "OFF"
    # FLEXIBLE or OFF
    # maximum_window_in_minutes = 2
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "week-update"
    })
  }
}
