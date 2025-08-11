#!/bin/bash
set -e

# Update package index
sudo apt update

# Install OpenJDK 17 JRE
sudo apt install -y openjdk-17-jre

# Verify installation
java -version