resource "aws_scheduler_schedule" "anti_freeze_whiteboard" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-anti-freeze-whiteboard"
  schedule_expression = "cron(0 0 ? * MON *)"
  description         = "Duga task: Prevent chatroom from freezing"
  flexible_time_window {
    mode = "FLEXIBLE"
    maximum_window_in_minutes = 10
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "anti-freeze",
      room = "21"
    })
  }
}

resource "aws_scheduler_schedule" "anti_freeze_software_engineers_meta" {
  group_name          = aws_scheduler_schedule_group.duga_tasks.name
  name                = "duga-task-anti-freeze-software-engineers-meta"
  schedule_expression = "cron(0 0 ? * MON *)"
  description         = "Duga task: Prevent chatroom from freezing"
  flexible_time_window {
    mode = "FLEXIBLE"
    maximum_window_in_minutes = 10
  }

  target {
    arn = aws_lambda_function.duga_lambda.arn
    role_arn = aws_iam_role.scheduler_role.arn
    input = jsonencode({
      task = "anti-freeze",
      room = "160979"
    })
  }
}
