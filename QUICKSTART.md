# Quick Start Guide

Get up and running with Buddy AI Backend in 5 minutes!

## Prerequisites Check

Before starting, ensure you have:
- ✅ Java 21 installed (`java -version`)
- ✅ Maven installed (`mvn -version`)
- ✅ Docker installed (`docker --version`) - for MongoDB/Redis
- ✅ OpenAI API key

## Step 1: Run Setup Script

```bash
./setup.sh
```

This will:
- Check prerequisites
- Create `.env` file
- Start MongoDB and Redis
- Build the project

## Step 2: Configure OpenAI API Key

Edit the `.env` file and add your OpenAI API key:

```bash
OPENAI_API_KEY=sk-your-actual-api-key-here
```

## Step 3: Run the Application

```bash
mvn spring-boot:run
```

## Step 4: Verify It's Working

```bash
# Health check
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

## That's It! 🎉

Your API is now running at `http://localhost:8080`

### Test the Chat Endpoint

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello, I need help with my order",
    "userId": 1
  }'
```

## Common Issues

**Java not found?**
```bash
# macOS
brew install openjdk@21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Maven not found?**
```bash
# macOS
brew install maven
```

**MongoDB connection error?**
```bash
# Start MongoDB
docker-compose up -d
```

**Need more help?**
See [SETUP.md](SETUP.md) for detailed instructions.

