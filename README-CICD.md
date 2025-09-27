# AWS CI/CD Pipeline Setup for Small Business Invoices

This guide will help you set up a complete CI/CD pipeline for the Small Business Invoices application using AWS services within the Free Tier.

## Prerequisites

1. AWS Account with appropriate permissions
2. AWS CLI configured with credentials
3. GitHub account with access to the repository
4. GitHub Personal Access Token with `repo` and `admin:repo_hook` permissions

## Architecture

The CI/CD pipeline consists of the following AWS services:

- **AWS CodePipeline**: Orchestrates the pipeline
- **AWS CodeBuild**: Builds and tests the application
- **Amazon ECR**: Stores Docker images
- **Amazon ECS**: Runs the application in containers
- **AWS CloudFormation**: Infrastructure as Code for the pipeline

## Setup Instructions

### 1. Create ECR Repository

First, create an ECR repository to store your Docker images:

```bash
aws ecr create-repository --repository-name small-business-invoices --region eu-north-1
```

### 2. Create GitHub Personal Access Token

1. Go to GitHub → Settings → Developer settings → Personal access tokens
2. Generate a new token with `repo` and `admin:repo_hook` permissions
3. Save the token securely (you'll need it in the next step)

### 3. Deploy the CI/CD Pipeline

Deploy the CloudFormation stack that sets up the pipeline:

```bash
aws cloudformation create-stack \
  --stack-name small-business-invoices-pipeline \
  --template-body file://cicd-pipeline.yml \
  --parameters \
      ParameterKey=GitHubOwner,ParameterValue=YOUR_GITHUB_USERNAME \
      ParameterKey=RepositoryName,ParameterValue=small-business-invoices \
      ParameterKey=BranchName,ParameterValue=main \
      ParameterKey=GitHubToken,ParameterValue=YOUR_GITHUB_TOKEN \
      ParameterKey=ECSClusterName,ParameterValue=small-business-cluster \
      ParameterKey=ECSServiceName,ParameterValue=small-business-service \
  --capabilities CAPABILITY_IAM \
  --region eu-north-1
```

### 4. Update ECS Task Definition

Update the ECS task definition with the ECR repository URI:

1. Go to AWS ECS Console
2. Navigate to "Task Definitions"
3. Select your task definition and create a new revision
4. Update the container image URI to point to your ECR repository
5. Save and update the service to use the new task definition

### 5. Push to GitHub

Commit and push your changes to trigger the pipeline:

```bash
git add .
git commit -m "Add CI/CD pipeline configuration"
git push origin main
```

## Pipeline Workflow

1. **Source Stage**: The pipeline is triggered on every push to the specified branch in GitHub
2. **Build Stage**:
   - Builds the application using Maven
   - Runs tests
   - Builds a Docker image
   - Pushes the image to Amazon ECR
3. **Deploy Stage**:
   - Updates the ECS service with the new task definition
   - Performs a rolling update of the service

## Monitoring

- **CodePipeline Console**: View the status of your pipeline and each stage
- **CloudWatch Logs**: Check build logs in the `/aws/codebuild/small-business-invoices-build` log group
- **ECS Console**: Monitor the status of your service and tasks

## Clean Up

To avoid incurring charges, clean up your resources when you're done:

1. Delete the CloudFormation stack:
   ```bash
   aws cloudformation delete-stack --stack-name small-business-invoices-pipeline --region eu-north-1
   ```
2. Delete the ECR repository:
   ```bash
   aws ecr delete-repository --repository-name small-business-invoices --force --region eu-north-1
   ```

## Cost Considerations

This setup uses the following AWS Free Tier eligible services:
- AWS CodePipeline: First 1 active pipeline per month
- AWS CodeBuild: 100 build minutes per month (Linux)
- Amazon ECR: 500MB-month of storage
- AWS CloudFormation: Free (you only pay for the resources created)

Make sure to monitor your AWS usage to stay within the Free Tier limits.
