# DevOps and CI/CD Setup

This document provides detailed information about the CI/CD pipeline and deployment setup for the Small Business Invoices application.

## CI/CD Pipeline

### Architecture

The CI/CD pipeline is built using AWS services:
- **AWS CodePipeline**: Orchestrates the deployment pipeline
- **AWS CodeBuild**: Handles the build and test process
- **Amazon ECR**: Stores Docker container images
- **Amazon ECS**: Runs the application in containers
- **Amazon S3**: Stores pipeline artifacts

### Pipeline Components

1. **Source Stage**
   - Monitors the GitHub repository for changes to the `main` branch
   - Triggers a new pipeline execution on code changes

2. **Build Stage**
   - Builds the application using Maven
   - Runs unit and integration tests
   - Builds a Docker image
   - Pushes the image to Amazon ECR
   - Generates deployment artifacts

3. **Deploy Stage**
   - Updates the ECS service with the new container image
   - Handles blue/green deployment if configured
   - Monitors the deployment status

### Prerequisites

1. **AWS Account** with appropriate permissions
2. **GitHub Repository** with the application code
3. **AWS Resources** that must exist before pipeline creation:
   - ECR Repository: `small-business-invoices`
   - ECS Cluster: `small-business-cluster`
   - ECS Service: `small-business-invoices-service`
   - S3 Bucket: `small-business-invoices-pipeline-artifacts`

### Setup Instructions

1. **Prepare AWS Resources**
   ```bash
   # Create ECR repository (if not exists)
   aws ecr create-repository --repository-name small-business-invoices --region eu-north-1
   
   # Create S3 bucket (if not exists)
   aws s3api create-bucket \
     --bucket small-business-invoices-pipeline-artifacts \
     --region eu-north-1 \
     --create-bucket-configuration LocationConstraint=eu-north-1
   ```

2. **Deploy the Pipeline**
   ```bash
   aws cloudformation create-stack \
     --stack-name small-business-invoices-pipeline \
     --template-body file://pipeline-with-existing-bucket.yml \
     --parameters \
         ParameterKey=GitHubOwner,ParameterValue=YOUR_GITHUB_USERNAME \
         ParameterKey=RepositoryName,ParameterValue=small-business-invoices \
         ParameterKey=BranchName,ParameterValue=main \
         ParameterKey=GitHubToken,ParameterValue=YOUR_GITHUB_TOKEN \
         ParameterKey=ECSClusterName,ParameterValue=small-business-cluster \
         ParameterKey=ECSServiceName,ParameterValue=small-business-invoices-service \
     --capabilities CAPABILITY_IAM \
     --region eu-north-1
   ```

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `AWS_ACCOUNT_ID` | Your AWS account ID | `123456789012` |
| `AWS_DEFAULT_REGION` | AWS region | `eu-north-1` |
| `ECR_REPOSITORY_URI` | ECR repository URI | `810772959397.dkr.ecr.eu-north-1.amazonaws.com/small-business-invoices` |
| `ECS_SERVICE_NAME` | ECS service name | `small-business-invoices-service` |
| `ECS_CLUSTER_NAME` | ECS cluster name | `small-business-cluster` |

### Troubleshooting

1. **Pipeline Failing at Source Stage**
   - Verify GitHub token has correct permissions
   - Check repository name and branch name

2. **Build Failures**
   - Check CodeBuild logs in AWS Console
   - Verify buildspec.yml is correctly configured

3. **Deployment Failures**
   - Check ECS service events
   - Verify IAM roles have correct permissions
   - Check container logs in CloudWatch

### Monitoring

1. **Pipeline Status**
   - AWS CodePipeline Console
   - Pipeline execution history and details

2. **Build Logs**
   - AWS CodeBuild Console
   - Detailed build logs and test results

3. **Application Logs**
   - Amazon CloudWatch Logs
   - Application and container logs

### Cleanup

To remove all resources:

```bash
# Delete the pipeline stack
aws cloudformation delete-stack \
  --stack-name small-business-invoices-pipeline \
  --region eu-north-1

# Delete ECR repository (if needed)
aws ecr delete-repository \
  --repository-name small-business-invoices \
  --force \
  --region eu-north-1

# Delete S3 bucket (if empty)
aws s3 rb s3://small-business-invoices-pipeline-artifacts --force
```

## Security Best Practices

1. **Secrets Management**
   - Store sensitive information in AWS Secrets Manager or Parameter Store
   - Use IAM roles instead of access keys when possible

2. **Least Privilege**
   - Follow the principle of least privilege for IAM roles
   - Regularly review and audit permissions

3. **Infrastructure as Code**
   - All infrastructure is defined in CloudFormation templates
   - Version control all configuration files
