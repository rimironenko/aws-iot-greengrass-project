#!/bin/bash

# Create Greengrass user and group
adduser --system ggc_user
groupadd --system ggc_group

# Extract and run the following script to mount cgroups.
# This allows AWS IoT Greengrass to set the memory limit for Lambda functions.
# Cgroups are also required to run AWS IoT Greengrass in the default containerization mode.
cd /home/ec2-user
curl https://raw.githubusercontent.com/tianon/cgroupfs-mount/951c38ee8d802330454bdede20d85ec1c0f8d312/cgroupfs-mount > cgroupfs-mount.sh
chmod +x cgroupfs-mount.sh
bash ./cgroupfs-mount.sh

# Install Java
amazon-linux-extras enable corretto8
yum -y install java-1.8.0-amazon-corretto-devel
# Greengrass Lambdas require 'java8' executable, not 'java'
# See https://gist.github.com/noahcoad/92133670d6189440f883d9369211aeca
mv /usr/bin/java /usr/bin/java8

# Download and install Core software
curl https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/1.11.4/greengrass-linux-x86-64-1.11.4.tar.gz > greengrass-linux-x86-64-1.11.4.tar.gz
tar -xzvf greengrass-linux-x86-64-1.11.4.tar.gz -C /

# Download and install Core device certificates
export AWS_ACCESS_KEY_ID=ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=SECRET_ACCCESS_KEY
aws s3 cp S3_URI certs.tar.gz
tar -xzvf certs.tar.gz -C /greengrass

# Download and install Root CA certificate
cd /greengrass/certs/
wget -O root.ca.pem https://www.amazontrust.com/repository/AmazonRootCA1.pem

# Start the Greengrass daemon
cd /greengrass/ggc/core/
./greengrassd start