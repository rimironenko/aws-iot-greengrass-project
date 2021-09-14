#!/bin/bash
python3 publishMessageFromDevice.py \
        --endpoint ENDPOINT \
        --rootCA root-ca-cert.pem \
        --cert pubDevice.cert.pem \
        --key pubDevice.private.key \
        --thingName THING_NAME \
        --topic TOPIC \
         --file FILE