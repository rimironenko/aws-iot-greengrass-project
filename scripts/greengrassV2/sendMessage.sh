#!/bin/bash
python3 sendMessage.py \
  --thing-name PublisherDevice \
  --topic greengrass/v2/message \
  --file test.yaml \
  --root-ca root.ca.pem \
  --cert device.pem.crt \
  --key private.pem.key \
  --region us-east-1 \
  --verbosity Warn