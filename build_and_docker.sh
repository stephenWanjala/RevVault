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

echo "Both Ktor project build and Docker image build completed successfully."