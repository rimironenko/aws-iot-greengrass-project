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

# Create a Greengrass Core device
export AWS_ACCESS_KEY_ID=ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=SECRET_ACCCESS_KEY
-E java -Droot="/greengrass/v2" -Dlog.store=FILE \
  -jar ./GreengrassInstaller/lib/Greengrass.jar \
  --aws-region us-east-1 \
  --thing-name MySecondGroup_Core \
  --thing-group-name MySecondGroup \
  --thing-policy-name MySecondGroupIoTThingPolicy \
  --tes-role-name MySecondGroupTokenExchangeRole \
  --tes-role-alias-name MySecondGroupCoreTokenExchangeRoleAlias \
  --component-default-user ggc_user:ggc_group \
  --provision true \
  --setup-system-service true \
  --deploy-dev-tools true