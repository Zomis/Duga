import chatexchange
import os
import json
import logging
import time
from collections import defaultdict

email = os.environ.get('USER_EMAIL')
password = os.environ.get('USER_PASSWORD')

logging.basicConfig(level=logging.DEBUG)
client = chatexchange.Client('stackexchange.com', email, password)
me = client.get_me()

def lambda_handler(event, context):
    print(json.dumps(event))
    messages_by_room = defaultdict(list)
    for record in event["Records"]:
        payload = record["body"]
        room_id = record['messageAttributes'].get('room').get('stringValue')
        messages_by_room[int(room_id)].append(payload)

    for room_id, texts in messages_by_room.items():
        logging.debug(f"Handling room {room_id}")
        room = client.get_room(room_id)
        send_messages_to_room(room, texts, me)

    return {
        'statusCode': 204
    }

def send_messages_to_room(room, texts, me, timeout=10):
    pending = set(texts)
    deadline = time.time() + timeout

    with room.new_messages() as stream:
        for text in texts:
            logging.debug(f"Sending message {text}")
            room.send_message(text)

        for msg in stream:
            if msg.owner is me and msg.content in pending:
                pending.remove(msg.content)

                if not pending:
                    return

            if time.time() > deadline:
                raise TimeoutError(
                    f"Timed out waiting for {len(pending)} messages"
                )