#!/bin/bash

# Configuration
AWS_ACCOUNT_ID="810772959397"
AWS_REGION="eu-north-1"
REPOSITORY_NAME="small-business-invoices"
IMAGE_TAG="latest"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build the Docker image
echo "Building Docker image..."
docker build -t ${REPOSITORY_NAME}:${IMAGE_TAG} .

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Error: Docker build failed."
    exit 1
fi

echo "Docker image built successfully"

# Login to ECR
echo "Logging in to Amazon ECR..."
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

# Check if ECR repository exists, create if it doesn't
echo "Checking ECR repository..."
aws ecr describe-repositories --repository-names ${REPOSITORY_NAME} --region ${AWS_REGION} > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "Repository does not exist. Creating ECR repository..."
    aws ecr create-repository --repository-name ${REPOSITORY_NAME} --region ${AWS_REGION}
    
    # Add lifecycle policy to clean up old images
    aws ecr put-lifecycle-policy \
        --repository-name ${REPOSITORY_NAME} \
        --region ${AWS_REGION} \
        --lifecycle-policy-text '{
            "rules": [
                {
                    "rulePriority": 1,
                    "description": "Keep last 30 images",
                    "selection": {
                        "tagStatus": "any",
                        "countType": "imageCountMoreThan",
                        "countNumber": 30
                    },
                    "action": {
                        "type": "expire"
                    }
                }
            ]
        }'
fi

# Tag the image for ECR
echo "Tagging image for ECR..."
docker tag ${REPOSITORY_NAME}:${IMAGE_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPOSITORY_NAME}:${IMAGE_TAG}

# Push the image to ECR
echo "Pushing image to ECR..."
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPOSITORY_NAME}:${IMAGE_TAG}

echo ""
echo "Image successfully pushed to ECR!"
echo ""
echo "ECR Image URI:"
echo "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPOSITORY_NAME}:${IMAGE_TAG}"

# Show how to run the containerecho -e "\nTo run the container locally, use:"
echo "docker run -d -p 8080:8080 \\"
echo "  -e SPRING_DATASOURCE_URL=jdbc:mysql://small-business-db.cfka0qwomp9i.eu-north-1.rds.amazonaws.com:3306/small_business \\"
echo "  -e SPRING_DATASOURCE_USERNAME=your_username \\"
echo "  -e SPRING_DATASOURCE_PASSWORD=Ch4ng31sg00d \\"
echo "  -e SPRING_PROFILES_ACTIVE=prod \\"
echo "  ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPOSITORY_NAME}:${IMAGE_TAG}"
