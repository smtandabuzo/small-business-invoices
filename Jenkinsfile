pipeline {
    agent any
    environment {
        // AWS Configuration
        AWS_ACCOUNT_ID = '810772959397'
        AWS_REGION = 'eu-north-1'
        ECR_REPOSITORY = 'small-business-invoices'
        ECS_SERVICE = 'small-business-invoices-service'
        ECS_CLUSTER = 'small-business-invoices-cluster'
        ECS_TASK_DEFINITION = 'small-business-invoices-task'

        // Git Configuration
        GIT_COMMIT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        IMAGE_TAG = "${GIT_COMMIT}"
        ECR_IMAGE_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Login to ECR
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                    // Build and tag the Docker image
                    sh "docker build -t ${ECR_IMAGE_URI} ."

                    // Push to ECR
                    sh "docker push ${ECR_IMAGE_URI}"
                }
            }
        }

        stage('Update ECS Service') {
            steps {
                script {
                    // Update the ECS service with the new task definition
                    sh """
                    aws ecs update-service \
                        --cluster ${ECS_CLUSTER} \
                        --service ${ECS_SERVICE} \
                        --force-new-deployment \
                        --region ${AWS_REGION}
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Check the logs for details.'
        }
    }
}