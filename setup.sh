#!/bin/bash

# Buddy AI Backend Setup Script
# This script helps set up the development environment

set -e
# Don't exit on errors in conditionals - we'll handle them explicitly
set +e

echo "🚀 Setting up Buddy AI Backend..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if Java 21 is installed
echo -e "\n${YELLOW}Checking Java installation...${NC}"

# First, try to find Java 21 using macOS java_home utility
JAVA_21_FOUND=false
JAVA_21_HOME=""

# Try macOS java_home utility first
if [ -x "/usr/libexec/java_home" ]; then
    JAVA_21_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null)
fi

# If not found, try Homebrew installation paths
if [ -z "$JAVA_21_HOME" ] || [ ! -d "$JAVA_21_HOME" ]; then
    # Check common Homebrew locations
    if [ -d "/opt/homebrew/opt/openjdk@21" ]; then
        JAVA_21_HOME="/opt/homebrew/opt/openjdk@21"
    elif [ -d "/usr/local/opt/openjdk@21" ]; then
        JAVA_21_HOME="/usr/local/opt/openjdk@21"
    fi
fi

# If Java 21 is found, configure it
if [ -n "$JAVA_21_HOME" ] && [ -d "$JAVA_21_HOME" ]; then
    # For Homebrew installations, JAVA_HOME should point to the actual JDK home
    # Check if there's a libexec/openjdk.jdk/Contents/Home directory (Homebrew structure)
    if [ -d "$JAVA_21_HOME/libexec/openjdk.jdk/Contents/Home" ]; then
        JAVA_21_HOME="$JAVA_21_HOME/libexec/openjdk.jdk/Contents/Home"
    fi
    
    # Verify java binary exists
    if [ -f "$JAVA_21_HOME/bin/java" ]; then
        # Configure it for this session
        export JAVA_HOME="$JAVA_21_HOME"
        export PATH="$JAVA_HOME/bin:$PATH"
        JAVA_21_FOUND=true
        echo -e "${GREEN}✓ Java 21 found at $JAVA_21_HOME${NC}"
        echo -e "${YELLOW}  Configured JAVA_HOME for this session${NC}"
    fi
fi

# Check if java command is available (either in PATH or via JAVA_HOME)
# Re-enable error checking for the rest of the script
set -e

if [ "$JAVA_21_FOUND" = true ] || command -v java &> /dev/null; then
    # Use the java from JAVA_HOME if set, otherwise use system java
    if [ "$JAVA_21_FOUND" = true ]; then
        JAVA_CMD="$JAVA_HOME/bin/java"
    else
        JAVA_CMD="java"
    fi
    # Try to get Java version - handle different output formats
    JAVA_OUTPUT=$($JAVA_CMD -version 2>&1 | head -1)
    JAVA_VERSION=""
    
    # Try different parsing methods
    # Method 1: Extract from "version "X.Y.Z" format (e.g., "openjdk version "21.0.9"")
    JAVA_VERSION=$(echo "$JAVA_OUTPUT" | sed -nE 's/.*version "([0-9]+).*/\1/p')
    
    # If still empty, try alternative method
    if [ -z "$JAVA_VERSION" ]; then
        JAVA_VERSION=$(echo "$JAVA_OUTPUT" | grep -oE "version [0-9]+" | grep -oE "[0-9]+" | head -1)
    fi
    
    # Last resort: extract any number after "version"
    if [ -z "$JAVA_VERSION" ]; then
        JAVA_VERSION=$(echo "$JAVA_OUTPUT" | grep -oE "[0-9]+\.[0-9]+\.[0-9]+" | cut -d'.' -f1)
    fi
    
    # Check if we got a valid version number
    if [ -z "$JAVA_VERSION" ] || [ "$JAVA_VERSION" = "" ]; then
        echo -e "${YELLOW}⚠ Could not determine Java version${NC}"
        echo "Java command found, but version detection failed."
        echo ""
        echo -e "${YELLOW}Please ensure Java 21 is installed and configured:${NC}"
        echo "  macOS: brew install openjdk@21"
        echo "  Then add to ~/.zshrc:"
        echo "    export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"
        echo "    export PATH=\"\$JAVA_HOME/bin:\$PATH\""
        echo "  Then run: source ~/.zshrc"
        exit 1
    fi
    
    # Compare version (handle both numeric and string comparison)
    if [ "$JAVA_VERSION" -ge 21 ] 2>/dev/null; then
        if [ "$JAVA_21_FOUND" = false ]; then
            echo -e "${GREEN}✓ Java $JAVA_VERSION is installed${NC}"
        else
            echo -e "${GREEN}✓ Java $JAVA_VERSION is ready to use${NC}"
        fi
    else
        echo -e "${RED}✗ Java 21 or higher is required. Found Java $JAVA_VERSION${NC}"
        echo "Please install Java 21:"
        echo "  macOS: brew install openjdk@21"
        echo "  Then add to ~/.zshrc:"
        echo "    export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"
        echo "    export PATH=\"\$JAVA_HOME/bin:\$PATH\""
        echo "  Then run: source ~/.zshrc"
        exit 1
    fi
else
    echo -e "${RED}✗ Java is not installed or not in PATH${NC}"
    echo "Please install Java 21:"
    echo "  macOS: brew install openjdk@21"
    echo "  Then add to ~/.zshrc:"
    echo "    export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"
    echo "    export PATH=\"\$JAVA_HOME/bin:\$PATH\""
    echo "  Then run: source ~/.zshrc"
    exit 1
fi

# Check if Maven is installed
echo -e "\n${YELLOW}Checking Maven installation...${NC}"
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo -e "${GREEN}✓ Maven $MVN_VERSION is installed${NC}"
else
    echo -e "${RED}✗ Maven is not installed${NC}"
    echo "Please install Maven:"
    echo "  macOS: brew install maven"
    echo "  Or download from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Create .env file if it doesn't exist
echo -e "\n${YELLOW}Setting up environment variables...${NC}"
if [ ! -f .env ]; then
    if cat > .env << 'EOF' 2>/dev/null
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
EOF
    then
        echo -e "${GREEN}✓ Created .env file${NC}"
        echo -e "${YELLOW}⚠ IMPORTANT: Please edit .env file and add your OPENAI_API_KEY${NC}"
    else
        echo -e "${YELLOW}⚠ Could not create .env file automatically (permission issue)${NC}"
        echo -e "${YELLOW}  Please create .env file manually with the following content:${NC}"
        echo ""
        echo "See SETUP.md or COMPLETE_SETUP.md for the .env file template"
        echo ""
    fi
else
    echo -e "${GREEN}✓ .env file already exists${NC}"
fi

# Build the project
echo -e "\n${YELLOW}Building the project...${NC}"
mvn clean install -DskipTests
echo -e "${GREEN}✓ Project built successfully${NC}"

echo -e "\n${GREEN}✅ Setup complete!${NC}"

# If Java 21 was found but not in PATH, remind user to configure it permanently
if [ "$JAVA_21_FOUND" = true ] && ! command -v java &> /dev/null; then
    echo -e "\n${YELLOW}⚠ Note: Java 21 is installed but not in your PATH${NC}"
    echo "To make Java 21 available permanently, add these lines to ~/.zshrc:"
    echo "  export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"
    echo "  export PATH=\"\$JAVA_HOME/bin:\$PATH\""
    echo "Then run: source ~/.zshrc"
    echo ""
fi

echo -e "\n${YELLOW}Next steps:${NC}"
echo "1. Edit .env file and add your OPENAI_API_KEY"
echo "2. Ensure MongoDB and Redis are running (local installation)"
echo "3. Run the application:"
echo "   mvn spring-boot:run"
echo "   OR"
echo "   java -jar target/buddy-ai-1.0.0.jar"
echo ""
echo "The API will be available at: http://localhost:8080"

