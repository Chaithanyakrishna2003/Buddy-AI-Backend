# Complete Setup Guide - Step by Step

Follow these steps in order to complete the setup:

## Step 1: Install Prerequisites

### 1.1 Install Homebrew (if not already installed)
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 1.2 Install Java 21
```bash
brew install openjdk@21
```

### 1.3 Configure Java in your shell
Add these lines to your `~/.zshrc` file (or `~/.bash_profile` if using bash):

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

Then reload your shell:
```bash
source ~/.zshrc
```

Verify Java is installed:
```bash
java -version
```
Should show: `openjdk version "21.x.x"`

### 1.4 Install Maven
```bash
brew install maven
```

Verify Maven is installed:
```bash
mvn -version
```

### 1.5 Install Docker Desktop
```bash
brew install --cask docker
```

Or download from: https://www.docker.com/products/docker-desktop

**Important:** After installing Docker Desktop, make sure to:
1. Open Docker Desktop application
2. Wait for it to start (whale icon in menu bar)
3. Verify it's running: `docker ps`

## Step 2: Run the Setup Script

Once all prerequisites are installed, navigate to the project directory and run:

```bash
cd /Users/CHAITHANYA/IdeaProjects/Buddy-AI-Backend
./setup.sh
```

This script will:
- ✅ Check that Java 21 and Maven are installed
- ✅ Create a `.env` file with all required environment variables
- ✅ Start MongoDB and Redis using Docker Compose
- ✅ Build the project

## Step 3: Configure OpenAI API Key

After the setup script runs, you need to add your OpenAI API key:

1. Open the `.env` file in the project root
2. Find the line: `OPENAI_API_KEY=your-openai-api-key-here`
3. Replace `your-openai-api-key-here` with your actual OpenAI API key

**To get an OpenAI API key:**
1. Go to https://platform.openai.com/
2. Sign up or log in
3. Navigate to API Keys: https://platform.openai.com/api-keys
4. Create a new API key
5. Copy the key (starts with `sk-`)

## Step 4: Verify Docker Services

Make sure MongoDB and Redis are running:

```bash
docker-compose ps
```

You should see both `buddy-ai-mongodb` and `buddy-ai-redis` with status "Up".

If they're not running:
```bash
docker-compose up -d
```

## Step 5: Build the Project

```bash
mvn clean install
```

Or skip tests for faster build:
```bash
mvn clean install -DskipTests
```

## Step 6: Run the Application

```bash
mvn spring-boot:run
```

You should see Spring Boot starting up. Wait for the message:
```
Started BuddyAiApplication in X.XXX seconds
```

## Step 7: Verify Everything Works

Open a new terminal and test the API:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}

# Root endpoint
curl http://localhost:8080/

# Should return welcome message
```

## Troubleshooting

### Java not found after installation
```bash
# Check if Java is installed
/usr/libexec/java_home -V

# If Java 21 is installed but not found, add to ~/.zshrc:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
source ~/.zshrc
```

### Maven not found
```bash
# Verify Maven installation
brew list maven

# If installed but not in PATH, check:
echo $PATH
# Should include /opt/homebrew/bin or /usr/local/bin
```

### Docker not running
```bash
# Start Docker Desktop manually from Applications
# Or check if it's running:
docker ps

# If error, make sure Docker Desktop is started
```

### MongoDB connection error
```bash
# Check if MongoDB container is running
docker ps | grep mongo

# If not running, start it:
docker-compose up -d mongodb

# Check logs:
docker-compose logs mongodb
```

### Port 8080 already in use
```bash
# Find what's using port 8080
lsof -i :8080

# Kill the process or change port in .env:
# SERVER_PORT=8081
```

## Quick Command Reference

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Check health
curl http://localhost:8080/actuator/health
```

## Next Steps After Setup

Once everything is running:

1. ✅ Test the chat endpoint with a sample message
2. ✅ Check MongoDB has the database created
3. ✅ Review the API endpoints in README.md
4. ✅ Configure CORS if connecting a frontend
5. ✅ Set up production environment variables

## Need Help?

- See `SETUP.md` for detailed troubleshooting
- See `QUICKSTART.md` for a condensed guide
- Check application logs in the console output

