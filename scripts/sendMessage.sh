#!/bin/bash
python3 sendMessage.py \
        --endpoint ENDPOINT \
        --rootCA AmazonRootCA1.pem \
        --cert HASH.cert.pem \
        --key HASH.private.key \
        --thingName THING_NAME \
        --topic TOPIC \
         --file FILE