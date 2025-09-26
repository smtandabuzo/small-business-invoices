#!/bin/bash

# Configuration
AWS_ACCOUNT_ID="810772959397"
AWS_REGION="eu-north-1"

# Create the ECS task execution role if it doesn't exist
echo "Creating ECS task execution role..."
aws iam create-role --role-name ecsTaskExecutionRole --assume-role-policy-document '{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "Service": ["ecs-tasks.amazonaws.com"]
    },
    "Action": ["sts:AssumeRole"]
  }]
}' 2>/dev/null || echo "Role already exists, continuing..."

# Attach the AmazonECSTaskExecutionRolePolicy to the role
echo "Attaching policies to ECS task execution role..."
aws iam attach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy

# Create the ECS task role if it doesn't exist
echo "Creating ECS task role..."
aws iam create-role --role-name ecsTaskRole --assume-role-policy-document '{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "Service": ["ecs-tasks.amazonaws.com"]
    },
    "Action": ["sts:AssumeRole"]
  }]
}' 2>/dev/null || echo "Role already exists, continuing..."

# Create and attach an inline policy for the task role
echo "Creating and attaching inline policy for ECS task role..."
aws iam put-role-policy --role-name ecsTaskRole --policy-name ECS-Task-Policy --policy-document '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:CreateLogGroup"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage"
      ],
      "Resource": "*"
    }
  ]
}'

echo "IAM roles setup complete!"
echo "Task Execution Role ARN: arn:aws:iam::${AWS_ACCOUNT_ID}:role/ecsTaskExecutionRole"
echo "Task Role ARN: arn:aws:iam::${AWS_ACCOUNT_ID}:role/ecsTaskRole"
