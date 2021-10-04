!/bin/bash

# Create a folder for the device data
cd /home/ec2-user
mkdir publisher_device
cd publisher_device

# Install the Greengrass v2 Python SDK
yum -y install git
git clone https://github.com/aws/aws-iot-device-sdk-python-v2.git
python3 -m pip install ./aws-iot-device-sdk-python-v2

# Upload the device certificates
aws s3 cp {S3_DEVICE_CERTIFICATE_URI} device.pem.crt
aws s3 cp {S3_DEVICE_PRIVATE_KEY_URI} private.pem.key
wget -O root.ca.pem https://www.amazontrust.com/repository/AmazonRootCA1.pem

