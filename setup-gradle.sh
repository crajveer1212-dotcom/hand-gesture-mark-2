#!/bin/sh

# This script will download and setup Gradle wrapper
echo "Setting up Gradle wrapper..."

# Create wrapper directory
mkdir -p gradle/wrapper

# Download gradle-wrapper.jar
curl -L https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar -o gradle/wrapper/gradle-wrapper.jar

echo "Gradle wrapper setup complete!"
