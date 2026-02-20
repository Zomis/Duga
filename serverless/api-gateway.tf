resource "aws_apigatewayv2_api" "api" {
  name          = "duga-http"
  protocol_type = "HTTP"
  description   = "Duga API Gateway, for webhook and stats"
}

resource "aws_apigatewayv2_integration" "lambda" {
  api_id                 = aws_apigatewayv2_api.api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.duga_webhook_lambda.invoke_arn
  payload_format_version = "2.0"
  description            = "Duga call webhook lambda (needs immediate response to not time out)"
}

resource "aws_apigatewayv2_route" "webhook" {
  api_id    = aws_apigatewayv2_api.api.id
  route_key = "POST /github/{room_name}"
  target    = "integrations/${aws_apigatewayv2_integration.lambda.id}"
}



resource "aws_cloudwatch_log_group" "api_logs" {
  name              = "/aws/apigateway/duga-http-api"
  retention_in_days = 14
}


resource "aws_apigatewayv2_integration" "duga_lambda" {
  api_id                 = aws_apigatewayv2_api.api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.duga_lambda.invoke_arn
  payload_format_version = "2.0"
  description            = "Duga call main lambda (for things not requiring an immediate response)"
}

resource "aws_apigatewayv2_route" "stats" {
  api_id    = aws_apigatewayv2_api.api.id
  route_key = "POST /stats"
  target    = "integrations/${aws_apigatewayv2_integration.duga_lambda.id}"
}

resource "aws_apigatewayv2_stage" "prod" {
  api_id      = aws_apigatewayv2_api.api.id
  name        = "$default"
  auto_deploy = true
  description = "Duga default production stage for API"
  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_logs.arn

    format = jsonencode({
      requestId      = "$context.requestId"
      ip             = "$context.identity.sourceIp"
      requestTime    = "$context.requestTime"
      httpMethod     = "$context.httpMethod"
      routeKey       = "$context.routeKey"
      status         = "$context.status"
      protocol       = "$context.protocol"
      responseLength = "$context.responseLength"
      integrationErr = "$context.integrationErrorMessage"
    })
  }
}

resource "aws_lambda_permission" "apigw_stats" {
  statement_id  = "AllowAPIGatewayInvoke-stats"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.duga_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.api.execution_arn}/*/*"
}


resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.duga_webhook_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.api.execution_arn}/*/*"
}

resource "aws_apigatewayv2_domain_name" "domain" {
  domain_name = local.domain

  domain_name_configuration {
    certificate_arn = aws_acm_certificate_validation.cert.certificate_arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}

resource "aws_apigatewayv2_api_mapping" "mapping" {
  api_id      = aws_apigatewayv2_api.api.id
  domain_name = aws_apigatewayv2_domain_name.domain.id
  stage       = aws_apigatewayv2_stage.prod.id
}

resource "aws_route53_record" "api" {
  zone_id = data.aws_route53_zone.route53_domain.zone_id
  name    = local.domain
  type    = "A"

  alias {
    name                   = aws_apigatewayv2_domain_name.domain.domain_name_configuration[0].target_domain_name
    zone_id                = aws_apigatewayv2_domain_name.domain.domain_name_configuration[0].hosted_zone_id
    evaluate_target_health = false
  }
}