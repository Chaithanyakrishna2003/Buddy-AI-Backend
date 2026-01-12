# Setup Status

## ✅ Completed Setup Steps

1. ✅ Created `setup.sh` automated setup script
2. ✅ Created comprehensive documentation:
   - `SETUP.md` - Detailed setup guide
   - `QUICKSTART.md` - Quick start guide
   - Updated `README.md` with setup instructions

## ⚠️ Manual Steps Required

Due to system restrictions, the following steps need to be completed manually:

### 1. Install Prerequisites

**Java 21:**
```bash
# macOS (using Homebrew)
brew install openjdk@21

# Add to your shell profile (~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"

# Reload shell
source ~/.zshrc
```

**Maven:**
```bash
# macOS
brew install maven

# Verify
mvn -version
```

**MongoDB:**
```bash
# macOS
brew tap mongodb/brew
brew install mongodb-community@7.0
brew services start mongodb-community@7.0
```

### 2. Create .env File

Create a `.env` file in the project root with the following content:

```bash
# OpenAI Configuration (REQUIRED - Update with your API key)
OPENAI_API_KEY=your-openai-api-key-here
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

**Important:** Replace `your-openai-api-key-here` with your actual OpenAI API key.

### 3. Verify MongoDB is Running

```bash
# Check MongoDB status
brew services list | grep mongodb

# Start MongoDB if not running
brew services start mongodb-community@7.0
```

### 4. Build the Project

```bash
# Build the project
mvn clean install

# Or skip tests for faster build
mvn clean install -DskipTests
```

### 5. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using JAR
java -jar target/buddy-ai-1.0.0.jar
```

### 6. Verify Setup

```bash
# Health check
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

## Quick Setup Command

Once prerequisites are installed, you can run:

```bash
./setup.sh
```

This will automate most of the setup process.

## Troubleshooting

### Java Not Found
- Ensure Java 21 is installed and JAVA_HOME is set
- Check: `java -version` should show version 21+

### Maven Not Found
- Install Maven: `brew install maven`
- Check: `mvn -version`

### MongoDB Connection Error
- Check if MongoDB is running: `brew services list | grep mongodb`
- Start MongoDB: `brew services start mongodb-community@7.0`
- Restart MongoDB: `brew services restart mongodb-community@7.0`

### OpenAI API Error
- Verify API key in `.env` file
- Check OpenAI account has credits
- Verify API key permissions

## Next Steps

After completing the manual steps above:

1. ✅ Verify all prerequisites are installed
2. ✅ Create `.env` file with your OpenAI API key
3. ✅ Verify MongoDB is running
4. ✅ Build and run the application
5. ✅ Test the API endpoints

See `QUICKSTART.md` for a condensed version of these instructions.

