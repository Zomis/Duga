resource "aws_scheduler_schedule" "answer_invalidation" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-answer-invalidation"
  schedule_expression = "cron(0/5 * 1/1 * ? *)"
  flexible_time_window {
    mode = "FLEXIBLE"
    maximum_window_in_minutes = 3
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "answer-invalidation"
    })
  }
}
