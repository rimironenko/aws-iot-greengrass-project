!/bin/bash

# Make a folder for the Greengrass device data
cd /home/ec2-user
mkdir publisher_device
cd publisher_device

git clone https://github.com/aws/aws-iot-device-sdk-python-v2.git
python3 -m pip install --user ./aws-iot-device-sdk-python-v2