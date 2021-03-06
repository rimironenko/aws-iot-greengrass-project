#!/bin/bash

# Make a folder for the Greengrass device data
cd /home/ec2-user
mkdir publisher_device
cd publisher_device

# Download the Device certificates
export AWS_ACCESS_KEY_ID=ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=SECRET_ACCCESS_KEY
aws s3 cp S3_URI device-certs.tar.gz
tar -xzvf device-certs.tar.gz
wget -O root.ca.pem https://www.amazontrust.com/repository/AmazonRootCA1.pem

# Install the Python SDK to run the script that will send a message to the Greengrass Core
yum -y install git
git clone https://github.com/aws/aws-iot-device-sdk-python.git
cd aws-iot-device-sdk-python
python3 setup.py install
cp -R AWSIoTPythonSDK ../AWSIoTPythonSDK
cd ..
rm -rf aws-iot-device-sdk-python