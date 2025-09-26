#!/bin/bash

# Configuration
CLUSTER_NAME="small-business-cluster"
SERVICE_NAME="small-business-invoices-service"
TASK_DEFINITION_FILE="ecs/task-definition.json"
AWS_REGION="eu-north-1"
VPC_ID="vpc-0e0bf8db40755d79a"  # You'll need to provide your VPC ID
SUBNET_IDS="subnet-007a8c2be665983f2"  # Comma-separated list of at least 2 subnet IDs
SECURITY_GROUP_ID="sg-035e28f552080156a"  # Security group that allows inbound traffic on port 8080

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is not installed. Please install it first (sudo apt-get install jq)."
    exit 1
fi

# Check if required parameters are provided
if [[ -z "$VPC_ID" || -z "$SUBNET_IDS" || -z "$SECURITY_GROUP_ID" ]]; then
    echo "Error: Missing required parameters. Please set VPC_ID, SUBNET_IDS, and SECURITY_GROUP_ID in the script."
    exit 1
fi

# Create ECS cluster if it doesn't exist
echo "Checking ECS cluster..."
CLUSTER_EXISTS=$(aws ecs describe-clusters --cluster $CLUSTER_NAME --region $AWS_REGION --query 'clusters[0].status' --output text 2>/dev/null)

if [ "$CLUSTER_EXISTS" != "ACTIVE" ]; then
    echo "Creating ECS cluster: $CLUSTER_NAME"
    aws ecs create-cluster --cluster-name $CLUSTER_NAME --region $AWS_REGION
    
    # Wait for cluster to be active
    echo "Waiting for cluster to be active..."
    aws ecs wait cluster-active --cluster $CLUSTER_NAME --region $AWS_REGION
    
    # Verify cluster was created
    CLUSTER_STATUS=$(aws ecs describe-clusters --cluster $CLUSTER_NAME --region $AWS_REGION --query 'clusters[0].status' --output text)
    if [ "$CLUSTER_STATUS" != "ACTIVE" ]; then
        echo "Error: Failed to create ECS cluster. Please check AWS Console or try again."
        exit 1
    fi
    
    echo "ECS cluster created successfully"
else
    echo "ECS cluster already exists"
fi

# Create CloudWatch Logs log group if it doesn't exist
echo "Setting up CloudWatch Logs..."
aws logs describe-log-groups --log-group-name-prefix /ecs/small-business-invoices --region $AWS_REGION > /dev/null 2>&1 || \
    aws logs create-log-group --log-group-name /ecs/small-business-invoices --region $AWS_REGION

# Register task definition
echo "Registering task definition..."
TASK_DEFINITION_ARN=$(aws ecs register-task-definition \
    --cli-input-json file://$TASK_DEFINITION_FILE \
    --region $AWS_REGION \
    --output text \
    --query 'taskDefinition.taskDefinitionArn')

echo "Task definition registered: $TASK_DEFINITION_ARN"

# Check if service exists
echo "Checking if service exists..."
SERVICE_EXISTS=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION --query 'services[0].status' --output text 2>/dev/null)

if [ "$SERVICE_EXISTS" != "ACTIVE" ]; then
    # Create new service
    echo "Creating ECS service: $SERVICE_NAME"
    aws ecs create-service \
        --cluster $CLUSTER_NAME \
        --service-name $SERVICE_NAME \
        --task-definition $TASK_DEFINITION_ARN \
        --desired-count 1 \
        --launch-type FARGATE \
        --platform-version LATEST \
        --deployment-controller type=ECS \
        --network-configuration "awsvpcConfiguration={subnets=[${SUBNET_IDS}],securityGroups=[$SECURITY_GROUP_ID],assignPublicIp=ENABLED}" \
        --region $AWS_REGION \
        --enable-execute-command
    
    # Verify service was created
    SERVICE_STATUS=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION --query 'services[0].status' --output text 2>/dev/null)
    if [ "$SERVICE_STATUS" != "ACTIVE" ]; then
        echo "Error: Failed to create ECS service. Please check AWS Console or try again."
        exit 1
    fi
    
    echo "ECS service created successfully"
else
    # Update existing service
    echo "Updating ECS service: $SERVICE_NAME"
    aws ecs update-service \
        --cluster $CLUSTER_NAME \
        --service $SERVICE_NAME \
        --task-definition $TASK_DEFINITION_ARN \
        --region $AWS_REGION \
        --force-new-deployment
    
    echo "ECS service update initiated"
fi

# Wait for service to stabilize
echo "Waiting for service to stabilize..."
aws ecs wait services-stable \
    --cluster $CLUSTER_NAME \
    --services $SERVICE_NAME \
    --region $AWS_REGION

echo "Deployment initiated successfully!"
echo "Service URL: http://<ALB_DNS_NAME>:8080"  # Replace with your ALB DNS or EC2 public IP

echo -e "\nTo check the service status, run:"
echo "aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION"

echo -e "\nTo view the logs, run:"
echo "aws logs tail --follow /ecs/small-business-invoices --region $AWS_REGION"
