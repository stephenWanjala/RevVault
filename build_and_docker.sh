#!/bin/bash

# Step 1: Build the Ktor project
./gradlew clean build

# Check if the build was successful
if [ $? -ne 0 ]; then
    echo "Ktor project build failed. Aborting Docker image build."
    exit 1
fi

# Step 2: Build the Docker image
docker buildx  build -t rev_vault .

# Check if the Docker image build was successful
if [ $? -ne 0 ]; then
    echo "Docker image build failed."
    exit 1
fi

# Step 3: Tag the Docker image
docker tag rev_vault stephenwanjala/rev_vault:latest

# Step 4: Push the Docker image to Docker Hub
docker push stephenwanjala/rev_vault:latest

# Check if the Docker image push was successful
if [ $? -ne 0 ]; then
    echo "Docker image push failed."
    exit 1
fi

echo "Ktor project build, Docker image build, and Docker image push completed successfully."