# Buddy AI Backend

AI-powered customer support backend service built with Spring Boot and MongoDB.

## Tech Stack

- **Java 21**
- **Spring Boot 3.1.5**
- **MongoDB** - Database
- **OpenAI GPT-4o-mini** - AI chat processing
- **Maven** - Build tool

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MongoDB (local or remote)
- OpenAI API key

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd buddy-AI
   ```

2. **Configure environment variables**
   
   Create `.env` file or set environment variables:
   ```bash
   export OPENAI_API_KEY=your-api-key-here
   export MONGO_URI=mongodb://localhost:27017
   export MONGO_DB_NAME=dealshare
   export SERVER_PORT=8080
   ```

3. **Build the project**
   ```bash
   mvn clean package
   ```

4. **Run the application**
   ```bash
   java -jar target/buddy-ai-1.0.0.jar
   ```
   
   Or with Maven:
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`

## API Endpoints

### Chat
- `POST /api/chat` - Send message to AI assistant
- `POST /api/quick-reply` - Handle quick reply actions
- `GET /api/conversation/{conversationId}` - Get conversation history

### Products
- `GET /api/products/search?keyword={keyword}` - Search products
- `GET /api/products/{id}` - Get product details
- `GET /api/products` - Get all products
- `GET /api/products/recommendations` - Get product recommendations

### Orders
- `POST /api/orders` - Place new order
- `GET /api/orders` - Get all orders
- `GET /api/orders/{orderId}` - Get order details
- `GET /api/orders/{orderId}/items` - Get order items

### Profile
- `GET /api/profile` - Get user profile

### Feedback
- `POST /api/feedback` - Submit feedback
- `GET /api/feedback/user/{userId}` - Get user feedback
- `GET /api/feedback/order/{orderId}` - Get order feedback

## Configuration

Key configuration options in `application.yml`:

- `server.port` - Server port (default: 8080)
- `spring.data.mongodb.uri` - MongoDB connection URI
- `app.openai.api-key` - OpenAI API key
- `app.openai.model` - OpenAI model (default: gpt-4o-mini)
- `app.openai.max-tokens` - Max tokens per response (default: 250)

## Features

- 🤖 AI-powered conversational chat with context management
- 🔍 Product search with keyword matching
- 📦 Order management and processing
- 💬 Context-aware conversation handling
- 🔄 Exponential backoff retry for API rate limits
- 📊 Health monitoring endpoints (`/actuator/health`)

## License

MIT

