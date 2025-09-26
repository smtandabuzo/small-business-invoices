#!/bin/bash

# Build the Docker image
docker build -t small-business-invoices:latest .

echo "Docker image built successfully"
echo "To run the container, use:"
echo "docker run -d -p 8080:8080 \\"
echo "  -e RDS_ENDPOINT=your-rds-endpoint \\"
echo "  -e RDS_USERNAME=your-username \\"
echo "  -e RDS_PASSWORD=your-password \\"
echo "  -e RDS_DB_NAME=small_business \\"
echo "  small-business-invoices:latest"
