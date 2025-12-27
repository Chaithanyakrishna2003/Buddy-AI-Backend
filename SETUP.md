# Detailed Setup Guide

This guide provides step-by-step instructions for setting up the Buddy AI Backend project.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Database Setup](#database-setup)
4. [Configuration](#configuration)
5. [Building and Running](#building-and-running)
6. [Verification](#verification)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Software

1. **Java 21**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or [OpenJDK](https://adoptium.net/)
   - Verify installation: `java -version`
   - Should show version 21 or higher

2. **Maven 3.6+**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`
   - Should show version 3.6 or higher

3. **Docker & Docker Compose** (Recommended)
   - Download [Docker Desktop](https://www.docker.com/products/docker-desktop)
   - Verify installation: `docker --version` and `docker-compose --version`

4. **OpenAI API Key**
   - Sign up at [OpenAI](https://platform.openai.com/)
   - Create an API key from the [API Keys page](https://platform.openai.com/api-keys)

### Optional Software

- **Redis** (for conversation context caching)
  - Can be run via Docker Compose (included)
  - Or install locally: `brew install redis` (macOS) or `apt install redis` (Linux)

## Environment Setup

### macOS Setup

```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 21
brew install openjdk@21

# Add Java to PATH (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"

# Install Maven
brew install maven

# Install Docker Desktop
brew install --cask docker
```

### Linux (Ubuntu/Debian) Setup

```bash
# Update package list
sudo apt update

# Install Java 21
sudo apt install openjdk-21-jdk

# Set JAVA_HOME (add to ~/.bashrc)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

# Install Maven
sudo apt install maven

# Install Docker
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

### Windows Setup

1. **Install Java 21**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21)
   - Run installer and follow prompts
   - Set `JAVA_HOME` environment variable

2. **Install Maven**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Extract to `C:\Program Files\Apache\maven`
   - Add `C:\Program Files\Apache\maven\bin` to PATH

3. **Install Docker Desktop**
   - Download from [Docker Desktop](https://www.docker.com/products/docker-desktop)
   - Run installer and follow prompts

## Database Setup

### Option 1: Using Docker Compose (Recommended)

The project includes a `docker-compose.yml` file that sets up both MongoDB and Redis:

```bash
# Start MongoDB and Redis
docker-compose up -d

# Check if containers are running
docker-compose ps

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### Option 2: Local MongoDB Installation

**macOS:**
```bash
brew tap mongodb/brew
brew install mongodb-community@7.0
brew services start mongodb-community@7.0
```

**Linux:**
```bash
# Add MongoDB repository
wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
sudo apt-get update
sudo apt-get install -y mongodb-org
sudo systemctl start mongod
sudo systemctl enable mongod
```

## Configuration

### Environment Variables

Create a `.env` file in the project root directory:

```bash
# OpenAI Configuration (REQUIRED)
OPENAI_API_KEY=sk-your-api-key-here
OPENAI_MODEL=gpt-4o-mini
OPENAI_MAX_TOKENS=250
OPENAI_TEMPERATURE=0.8

# MongoDB Configuration
MONGO_URI=mongodb://localhost:27017
MONGO_DB_NAME=dealshare

# Redis Configuration (Optional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Application Configuration
COMPANY_NAME=Buddy AI
AI_ASSISTANT_NAME=Buddy AI
AI_ENABLED=true
USE_REDIS_FOR_CONTEXT=true
MOCK_MODE_ENABLED=false

# Default User/Location
DEFAULT_CITY=Jaipur
DEFAULT_STATE=Rajasthan
DEFAULT_PINCODE=302001

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://*.vercel.app,https://support-agent-*.vercel.app
```

**Important:** Replace `sk-your-api-key-here` with your actual OpenAI API key.

### Alternative: System Environment Variables

Instead of a `.env` file, you can set environment variables in your shell:

```bash
export OPENAI_API_KEY=sk-your-api-key-here
export MONGO_URI=mongodb://localhost:27017
export MONGO_DB_NAME=dealshare
export SERVER_PORT=8080
```

## Building and Running

### Build the Project

```bash
# Clean and build
mvn clean install

# Skip tests (faster build)
mvn clean install -DskipTests
```

### Run the Application

**Option 1: Using Maven (Recommended for development)**
```bash
mvn spring-boot:run
```

**Option 2: Using JAR file**
```bash
# Build JAR first
mvn clean package

# Run JAR
java -jar target/buddy-ai-1.0.0.jar
```

**Option 3: Using IDE**
- Open the project in IntelliJ IDEA or Eclipse
- Run `BuddyAiApplication.java` as a Spring Boot application

## Verification

### 1. Check Application Health

```bash
# Health endpoint
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

### 2. Check Root Endpoint

```bash
curl http://localhost:8080/

# Expected response:
# {"message":"Welcome to Buddy AI AI Support Agent API","assistant":"Buddy AI","status":"running"}
```

### 3. Test Chat Endpoint

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello",
    "userId": 1
  }'
```

### 4. Check MongoDB Connection

```bash
# If using Docker
docker exec -it buddy-ai-mongodb mongosh

# Or using local MongoDB
mongosh mongodb://localhost:27017/dealshare

# List databases
show dbs

# Use dealshare database
use dealshare

# List collections
show collections
```

## Troubleshooting

### Java Not Found

**Problem:** `java: command not found`

**Solution:**
```bash
# macOS - Add to ~/.zshrc or ~/.bash_profile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"

# Reload shell
source ~/.zshrc  # or source ~/.bash_profile
```

### Maven Not Found

**Problem:** `mvn: command not found`

**Solution:**
```bash
# macOS
brew install maven

# Linux
sudo apt install maven

# Verify installation
mvn -version
```

### MongoDB Connection Error

**Problem:** `Cannot connect to MongoDB`

**Solution:**
```bash
# Check if MongoDB is running
docker ps | grep mongo

# Or for local MongoDB
brew services list | grep mongodb  # macOS
sudo systemctl status mongod        # Linux

# Check MongoDB logs
docker-compose logs mongodb

# Restart MongoDB
docker-compose restart mongodb
```

### OpenAI API Error

**Problem:** `OpenAI API error` or `401 Unauthorized`

**Solution:**
1. Verify your API key is correct in `.env` file
2. Check if you have credits in your OpenAI account
3. Verify the API key has proper permissions
4. Check OpenAI service status: https://status.openai.com/

### Port Already in Use

**Problem:** `Port 8080 is already in use`

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process or change port in .env
export SERVER_PORT=8081
```

### Build Failures

**Problem:** Maven build fails

**Solution:**
```bash
# Clean Maven cache
mvn clean

# Remove target directory
rm -rf target/

# Rebuild
mvn clean install -U  # -U forces update of dependencies
```

### Docker Issues

**Problem:** Docker containers won't start

**Solution:**
```bash
# Check Docker is running
docker ps

# Check Docker Compose version
docker-compose --version

# Restart Docker Desktop (macOS/Windows)
# Or restart Docker service (Linux)
sudo systemctl restart docker

# Remove and recreate containers
docker-compose down -v
docker-compose up -d
```

## Next Steps

After successful setup:

1. ✅ Verify all endpoints are working
2. ✅ Test the chat functionality with OpenAI
3. ✅ Seed the database with sample products (if applicable)
4. ✅ Configure CORS for your frontend application
5. ✅ Set up production environment variables
6. ✅ Review security settings for production deployment

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Docker Documentation](https://docs.docker.com/)

## Getting Help

If you encounter issues not covered in this guide:

1. Check the application logs: `logs/application.log` or console output
2. Review the `COMPLETION_GUIDE.md` for project-specific details
3. Check GitHub issues (if applicable)
4. Review Spring Boot and MongoDB documentation

