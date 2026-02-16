import json
import os
import base64
import boto3

sqs = boto3.client("sqs")
SQS_WEBHOOK_QUEUE = os.environ["SQS_WEBHOOK_QUEUE"]


def lambda_handler(event, context):
    # Read body (API Gateway v2)
    body = event.get("body", "")
    if event.get("isBase64Encoded"):
        body = base64.b64decode(body).decode("utf-8")

    room = event["pathParameters"].get("room_name", "unknown")

    # Forward entire webhook to SQS
    sqs.send_message(
        QueueUrl=SQS_WEBHOOK_QUEUE,
        MessageBody=body,
        MessageGroupId=room,
        MessageAttributes={
            "event-type": {
                "DataType": "String",
                "StringValue": event["headers"].get("x-github-event", "unknown")
            },
            "room": {
                "DataType": "String",
                "StringValue": room
            },
            "delivery": {
                "DataType": "String",
                "StringValue": event["headers"].get("x-github-delivery", "unknown")
            }
        }
    )

    # Fast acknowledgment to GitHub
    return {
        "statusCode": 200,
        "headers": {"Content-Type": "application/json"},
        "body": "ok"
    }
