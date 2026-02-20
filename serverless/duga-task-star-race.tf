resource "aws_scheduler_schedule" "star_race" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-star-race"
  schedule_expression = "cron(45 23 1/1 * ? *)"
  description         = "Duga task: Post GitHub star-race (currently hardcoded for Rubberduck vs. oletools)"
  flexible_time_window {
    mode = "FLEXIBLE"
    # FLEXIBLE or OFF
    maximum_window_in_minutes = 10
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "star-race"
    })
  }
}
