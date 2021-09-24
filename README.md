# aws-iot-greengrass-project

It is a simple application built with AWS SAM that contains the Lambda function to be built and deployed to an AWS IoT Greengrass Core device.
The function consumes String messages from device with content of a YAML file, transforms it to JSON directly on the Core device and sends to a SQS queue. Please see the detailed explanation of the applicaton in the [Medium blog post](https://medium.com/@rostyslav.myronenko/aws-edge-computing-example-with-lambda-and-iot-greengrass-version-1-6bb710249d9a).

## Prerequisites
- Java 1.8+
- Apache Maven
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- Docker

## Project structure
- [ec2-greengrass-core-setup.sh](scripts/ec2-greengrass-core-setup.sh) - The User Data script for AWS EC2 instance to install the Greengrass Software and configure the instance as the Greengrass Core device.
- [ec2-greengrass-device-setup.sh](scripts/ec2-greengrass-device-setup.sh) - The User Data script for AWS EC2 instance to configure it as a Greengrass device and install the Greengrass Python SDK to run Python scripts that can interact with Greengrass.
- [sendMessage.py](scripts/sendMessage.py) - Python3 script to publish a test message from the device to the Core device via MQTT.
- [sendMessage.sh](scripts/sendMessage.sh) - Bash script to trigger the Python script and send the message instead of inputting into the CLI.
- [template.yaml](template.yaml) - AWS SAM template.
- [SubscribeFunction.java](src/main/java/com/home/amazon/iot/lamnda/SubscribeFunction.java) - The Lambda function for the edge computing on the Core device.

## Development

The generated function handler class just returns the input. The configured AWS Java SDK client is created in `DependencyFactory` class, and you can 
add the code to interact with the SDK client based on your use case.

#### Building the project
```
sam build
```

#### Testing it locally
```
sam local invoke
```

#### Adding more SDK clients
To add more service clients, you need to add the specific services modules in `pom.xml` and create the clients in `DependencyFactory` following the same 
pattern as sqsClient.

## Deployment

The generated project contains a default [SAM template](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-resource-function.html) file `template.yaml` where you can 
configure different properties of your lambda function such as memory size and timeout. You might also need to add specific policies to the lambda function
so that it can access other AWS resources.

To deploy the application, you can run the following command:

```
sam deploy --guided
```

See [Deploying Serverless Applications](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-deploying.html) for more info.



