#!/bin/bash

# Uninstall AWS CLI version 1
yum update -y
pip3 install --upgrade --user awscli
pip3 uninstall awscli -y

# Install AWS CLI version 2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install -i /usr/local/aws-cli -b /usr/local/bin
source ~/.bash_profile

# Install Java 11
yum install -y java-11-amazon-corretto

# Install Greengrass Core software
curl -s https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-nucleus-latest.zip > greengrass-nucleus-latest.zip
unzip greengrass-nucleus-latest.zip -d GreengrassInstaller && rm greengrass-nucleus-latest.zip

# Provide AWS API credentials
export AWS_ACCESS_KEY_ID=INSERT_ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=INSERT_SECRET_ACCCESS_KEY

# Create a Greengrass Core device
# Specify your own names
java -Droot="/greengrass/v2" -Dlog.store=FILE \
  -jar ./GreengrassInstaller/lib/Greengrass.jar \
  --aws-region us-east-1 \
  --thing-name GreengrassV2Core \
  --thing-group-name GreengrassV2Group \
  --thing-policy-name GreengrassV2IoTThingPolicy \
  --tes-role-name GreengrassV2TokenExchangeRole \
  --tes-role-alias-name GreengrassV2TokenExchangeRoleAlias \
  --component-default-user ggc_user:ggc_group \
  --provision true \
  --setup-system-service true \
  --deploy-dev-tools true


# Create a folder for the device data
cd /home/ec2-user
mkdir publisher_device
cd publisher_device

# Install the Greengrass v2 Python SDK
yum -y install git
git clone https://github.com/aws/aws-iot-device-sdk-python-v2.git
python3 -m pip install --user ./aws-iot-device-sdk-python-v2

# Upload the device certificates
aws s3 cp INSERT_S3_DEVICE_CERTIFICATE_URI device.pem.crt
aws s3 cp INSERT_S3_DEVICE_PRIVATE_KEY_URI private.pem.key
wget -O root.ca.pem https://www.amazontrust.com/repository/AmazonRootCA1.pem