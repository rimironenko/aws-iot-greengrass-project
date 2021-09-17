#!/bin/bash
python3 sendMessage.py \
        --endpoint ENDPOINT \
        --rootCA root.ca.pem \
        --cert HASH.cert.pem \
        --key HASH.private.key \
        --thingName THING_NAME \
        --topic TOPIC \
         --file FILE