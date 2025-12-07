package com.dealshare.buddyai.service;

import com.dealshare.buddyai.dto.ChatRequestDTO;
import com.dealshare.buddyai.dto.ChatResponseDTO;
import com.dealshare.buddyai.dto.ProductDetailsDTO;
import com.dealshare.buddyai.service.ProductService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.HttpException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${app.openai.api-key}")
    private String openaiApiKey;

    @Value("${app.openai.model:gpt-4o-mini}")
    private String openaiModel;

    @Value("${app.openai.temperature:0.8}")
    private Double temperature;

    @Value("${app.openai.max-tokens:250}")
    private Integer maxTokens;

    @Value("${app.name:Buddy AI}")
    private String companyName;

    @Value("${app.ai-assistant-name:Buddy AI}")
    private String assistantName;

    // In-memory conversation storage (use Redis in production)
    private final Map<String, List<ChatMessage>> conversations = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> conversationContext = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> conversationMetadata = new ConcurrentHashMap<>();
    
    // Product service for searching products
    private final ProductService productService;

    public ChatResponseDTO chat(ChatRequestDTO request) {
        try {
            // Generate or use existing conversation ID
            String conversationId = request.getConversation_id();
            if (conversationId == null || conversationId.isEmpty()) {
                conversationId = "conv_" + System.currentTimeMillis();
            }

            // Initialize conversation if new
            if (!conversations.containsKey(conversationId)) {
                conversations.put(conversationId, new ArrayList<>());
                conversationContext.put(conversationId, new HashMap<>());
                conversationMetadata.put(conversationId, new HashMap<>());
            }
            
            // Store metadata
            if (request.getOrder_info() != null) {
                conversationMetadata.get(conversationId).put("order_info", request.getOrder_info());
            }
            if (request.getIs_general_issue() != null) {
                conversationMetadata.get(conversationId).put("is_general_issue", request.getIs_general_issue());
            }
            if (request.getIs_issue_reporting() != null) {
                conversationMetadata.get(conversationId).put("is_issue_reporting", request.getIs_issue_reporting());
            }

            // Add user message to conversation
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), request.getMessage());
            conversations.get(conversationId).add(userMessage);

            // Get conversation history (last 10 messages for context)
            List<ChatMessage> conversationHistory = conversations.get(conversationId);
            List<ChatMessage> recentMessages = conversationHistory.size() > 10 
                ? conversationHistory.subList(conversationHistory.size() - 10, conversationHistory.size())
                : conversationHistory;

            // Build system prompt
            String systemPrompt = buildSystemPrompt(request);

            // Prepare messages for OpenAI
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
            messages.addAll(recentMessages);

            // Validate API key
            if (openaiApiKey == null || openaiApiKey.isEmpty()) {
                log.error("OpenAI API key is not configured");
                throw new RuntimeException("OpenAI API key is not configured");
            }

            // Call OpenAI API with retry logic for rate limits
            OpenAiService service = new OpenAiService(openaiApiKey);
            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                    .model(openaiModel)
                    .messages(messages)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            log.info("Calling OpenAI API with model: {}, messages: {}, apiKey prefix: {}", 
                    openaiModel, messages.size(), 
                    openaiApiKey != null && openaiApiKey.length() > 10 ? openaiApiKey.substring(0, 10) + "..." : "null");
            
            // Retry logic for rate limits (429 errors)
            com.theokanning.openai.completion.chat.ChatCompletionResult result = null;
            int maxRetries = 3;
            int retryCount = 0;
            long baseDelayMs = 2000; // Start with 2 seconds
            
            while (retryCount < maxRetries) {
                try {
                    result = service.createChatCompletion(chatRequest);
                    break; // Success, exit retry loop
                } catch (Exception e) {
                    // Check if it's a 429 rate limit error
                    boolean isRateLimit = false;
                    int statusCode = 0;
                    
                    // Check the exception and all its causes
                    Throwable cause = e;
                    int depth = 0;
                    while (cause != null && depth < 5) { // Limit depth to avoid infinite loops
                        String className = cause.getClass().getName();
                        String message = cause.getMessage();
                        
                        log.info("🔍 Checking exception (depth {}): {} - message: '{}'", depth, className, message);
                        
                        // Check if it's an HttpException (from retrofit2 or rxjava2)
                        if (className.contains("HttpException") || className.contains("OpenAiHttpException")) {
                            // Check message for quota exceeded (this is different from rate limit)
                            if (message != null && message.contains("exceeded your current quota")) {
                                // Quota exceeded - this is a billing issue, not a rate limit
                                // Don't retry, just return a helpful error message
                                log.error("❌ OpenAI quota exceeded - billing issue, not retrying");
                                throw new RuntimeException("QUOTA_EXCEEDED: " + message);
                            }
                            // Check message for 429 rate limit (handles "HTTP 429", "429", etc.)
                            if (message != null && (message.contains("429") || message.contains("rate limit"))) {
                                isRateLimit = true;
                                statusCode = 429;
                                log.warn("✅ Detected 429 rate limit in exception message: '{}'", message);
                                break;
                            }
                            // Try to get status code via reflection
                            try {
                                java.lang.reflect.Method getCode = cause.getClass().getMethod("code");
                                statusCode = (Integer) getCode.invoke(cause);
                                log.info("📊 Extracted status code via reflection: {}", statusCode);
                                if (statusCode == 429) {
                                    isRateLimit = true;
                                    log.warn("✅ Detected 429 rate limit via reflection");
                                    break;
                                }
                            } catch (Exception reflectionEx) {
                                log.debug("⚠️ Reflection failed for status code: {}", reflectionEx.getMessage());
                            }
                        }
                        cause = cause.getCause();
                        depth++;
                    }
                    
                    if (isRateLimit && retryCount < maxRetries - 1) {
                        // Rate limit hit, wait and retry with exponential backoff
                        long delayMs = baseDelayMs * (long) Math.pow(2, retryCount); // Exponential backoff: 2s, 4s, 8s
                        log.warn("⏳ Rate limit hit (429), retrying in {}ms (attempt {}/{})", delayMs, retryCount + 1, maxRetries);
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Retry interrupted", ie);
                        }
                        retryCount++;
                    } else {
                        // Not a 429 or max retries reached, throw the exception to be caught by outer handler
                        if (isRateLimit) {
                            log.error("❌ Max retries ({}) reached for rate limit", maxRetries);
                        } else {
                            log.error("❌ Exception is not a retryable rate limit. Throwing: {} - message: {}", 
                                    e.getClass().getName(), e.getMessage());
                        }
                        throw e;
                    }
                }
            }
            
            if (result == null) {
                log.error("Failed to get response from OpenAI after {} attempts", maxRetries);
                throw new RuntimeException("Failed to get response from OpenAI after " + maxRetries + " attempts");
            }
            
            String aiResponse = result.getChoices().get(0).getMessage().getContent().trim();
            
            log.info("OpenAI API response received: {}", aiResponse.substring(0, Math.min(100, aiResponse.length())));

            // Add AI response to conversation
            ChatMessage assistantMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), aiResponse);
            conversations.get(conversationId).add(assistantMessage);

            // Search for products if user mentioned product names
            List<Map<String, Object>> products = searchProductsFromMessage(request.getMessage(), userMessage.getContent());
            
            // Build response
            return ChatResponseDTO.builder()
                    .response(aiResponse)
                    .conversation_id(conversationId)
                    .suggested_questions(Collections.emptyList())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .needs_more_info(false)
                    .questions_to_ask(Collections.emptyList())
                    .collected_data(conversationContext.get(conversationId))
                    .intent(null)
                    .order_data(null)
                    .brand_options(null)
                    .show_feedback_modal(false)
                    .feedback_context(null)
                    .products(products)
                    .build();

        } catch (HttpException e) {
            // Handle HTTP errors from OpenAI API
            int statusCode = e.code();
            log.error("HTTP Error from OpenAI API: {} - {}", statusCode, e.message(), e);
            
            String responseMessage;
            if (statusCode == 401) {
                responseMessage = "I apologize, but there's an authentication issue with the AI service. Please ensure the API key is valid.";
            } else if (statusCode == 429) {
                responseMessage = "I apologize, but the AI service is currently experiencing high demand or quota limits. Please try again in a moment or check your OpenAI billing.";
            } else if (statusCode == 500 || statusCode == 502 || statusCode == 503) {
                responseMessage = "The AI service is temporarily unavailable. Please try again in a moment.";
            } else {
                responseMessage = "I'm sorry, I'm having trouble connecting to the AI service right now. Please try again in a moment.";
            }
            
            return ChatResponseDTO.builder()
                    .response(responseMessage)
                    .conversation_id(request.getConversation_id())
                    .suggested_questions(Collections.emptyList())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .needs_more_info(false)
                    .questions_to_ask(Collections.emptyList())
                    .build();
        } catch (Exception e) {
            // Check if it's a quota exceeded error
            String exceptionMessage = e.getMessage();
            if (exceptionMessage != null && exceptionMessage.contains("QUOTA_EXCEEDED")) {
                log.error("OpenAI quota exceeded - billing issue");
                return ChatResponseDTO.builder()
                        .response("I apologize, but the AI service quota has been exceeded. Please check your OpenAI account billing and add credits to continue using the service.")
                        .conversation_id(request.getConversation_id())
                        .suggested_questions(Collections.emptyList())
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .needs_more_info(false)
                        .questions_to_ask(Collections.emptyList())
                        .build();
            }
            log.error("Error in chat service: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            String responseMessage;
            
            if (errorMessage != null) {
                if (errorMessage.contains("api key") || errorMessage.contains("authentication") || errorMessage.contains("401")) {
                    responseMessage = "I apologize, but there's an authentication issue with the AI service. Please check the API key configuration.";
                } else if (errorMessage.contains("429") || errorMessage.contains("rate limit") || errorMessage.contains("quota")) {
                    responseMessage = "I apologize, but the AI service is currently experiencing high demand. Please try again in a moment.";
                } else if (errorMessage.contains("timeout")) {
                    responseMessage = "The request took too long to process. Please try again.";
                } else {
                    responseMessage = "I'm sorry, I'm having trouble connecting right now. Please try again in a moment.";
                }
            } else {
                responseMessage = "I'm sorry, I'm having trouble connecting right now. Please try again in a moment.";
            }
            
            return ChatResponseDTO.builder()
                    .response(responseMessage)
                    .conversation_id(request.getConversation_id())
                    .suggested_questions(Collections.emptyList())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .needs_more_info(false)
                    .questions_to_ask(Collections.emptyList())
                    .build();
        }
    }

    private String buildSystemPrompt(ChatRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are ").append(assistantName).append(", a friendly and conversational AI assistant for ").append(companyName).append(".\n\n");
        
        prompt.append(companyName).append(" is a social e-commerce and quick commerce platform focused on providing affordable groceries, household essentials, and everyday products to value-conscious customers across India.\n\n");
        
        prompt.append("Guidelines:\n");
        prompt.append("- Be friendly, warm, and conversational - respond naturally to greetings like \"hi\", \"hello\", \"good morning\", etc.\n");
        prompt.append("- If user says \"hi\" or \"hello\", respond with a friendly greeting back\n");
        prompt.append("- If user says \"good morning\", respond appropriately to the greeting\n");
        prompt.append("- Keep responses natural and human-like (2-4 sentences is fine)\n");
        prompt.append("- Be helpful and engaging\n");
        prompt.append("- IMPORTANT: When users mention product names (e.g., \"1 kg tomato\", \"rice\", \"milk\"), acknowledge that you'll help them find and add those products to their cart. The system will automatically search for and display matching products.\n");
        prompt.append("- If they're asking about products, help them find what they need\n");
        prompt.append("- If they need support, offer to help\n");
        prompt.append("- Use simple, clear language\n");
        prompt.append("- Be empathetic and understanding\n");
        prompt.append("- Common support areas: account management, payment/wallet issues, offers/coupons, product availability, delivery options\n");
        
        // Add order context if available
        if (request.getOrder_info() != null && !request.getOrder_info().isEmpty()) {
            prompt.append("\nCustomer has an order context available. Reference it when relevant.\n");
        }
        
        if (request.getIs_general_issue() != null && request.getIs_general_issue()) {
            prompt.append("\nThis is a general support inquiry. Help the user with their questions.\n");
        }
        
        return prompt.toString();
    }

    public ChatResponseDTO quickReply(String conversationId, String questionType) {
        // Map question types to messages
        Map<String, String> questionMap = Map.of(
            "refund", "I would like to request a refund for my order",
            "damage", "My items arrived damaged or broken",
            "missing", "I'm missing some items from my order",
            "wrong", "I received wrong items in my order",
            "delivery", "I have a question about my delivery",
            "return", "I want to return my order",
            "cancel", "I want to cancel my order",
            "track", "Where is my order? Can you track it?",
            "replacement", "I need a replacement for damaged items"
        );

        String message = questionMap.getOrDefault(questionType, "I need help with " + questionType);
        
        // Get conversation metadata
        Map<String, Object> metadata = conversationMetadata.getOrDefault(conversationId, new HashMap<>());
        
        ChatRequestDTO request = ChatRequestDTO.builder()
                .conversation_id(conversationId)
                .message(message)
                .order_info((Map<String, Object>) metadata.get("order_info"))
                .is_general_issue((Boolean) metadata.getOrDefault("is_general_issue", false))
                .is_issue_reporting((Boolean) metadata.getOrDefault("is_issue_reporting", false))
                .build();
        
        return chat(request);
    }

    public Map<String, Object> getConversation(String conversationId) {
        Map<String, Object> result = new HashMap<>();
        result.put("messages", conversations.getOrDefault(conversationId, new ArrayList<>()));
        result.put("context", conversationContext.getOrDefault(conversationId, new HashMap<>()));
        result.put("metadata", conversationMetadata.getOrDefault(conversationId, new HashMap<>()));
        return result;
    }

    public void deleteConversation(String conversationId) {
        conversations.remove(conversationId);
        conversationContext.remove(conversationId);
        conversationMetadata.remove(conversationId);
    }
    
    /**
     * Extract product names from user message and search for matching products
     */
    private List<Map<String, Object>> searchProductsFromMessage(String userMessage, String fullMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            // Common product keywords to detect
            String message = userMessage.toLowerCase();
            
            // Check if message contains product-related keywords
            boolean hasProductIntent = message.contains("kg") || message.contains("gram") || 
                                      message.contains("pack") || message.contains("order") ||
                                      message.contains("buy") || message.contains("add") ||
                                      message.contains("need") || message.contains("want") ||
                                      message.matches(".*\\d+\\s*(kg|gram|g|pack|piece|pc).*");
            
            if (!hasProductIntent) {
                // Check for common product names
                String[] commonProducts = {"tomato", "onion", "potato", "rice", "milk", "bread", 
                                          "egg", "chicken", "fish", "vegetable", "fruit", "dal",
                                          "oil", "sugar", "salt", "spice", "flour", "atta"};
                hasProductIntent = Arrays.stream(commonProducts)
                    .anyMatch(message::contains);
            }
            
            if (!hasProductIntent) {
                return Collections.emptyList();
            }
            
            // Extract potential product keywords from message
            // Remove common words and extract meaningful terms
            String[] stopWords = {"i", "want", "need", "to", "buy", "order", "add", "get", "please", 
                                 "can", "you", "help", "me", "with", "the", "a", "an", "some", "kg", "gram"};
            String cleanedMessage = message;
            for (String stopWord : stopWords) {
                cleanedMessage = cleanedMessage.replaceAll("\\b" + stopWord + "\\b", " ");
            }
            cleanedMessage = cleanedMessage.trim().replaceAll("\\s+", " ");
            
            // Extract numbers and units (e.g., "1 kg tomato" -> "tomato")
            Pattern pattern = Pattern.compile("(\\d+\\s*(kg|gram|g|pack|piece|pc)\\s+)?([a-z]+)");
            Matcher matcher = pattern.matcher(cleanedMessage);
            
            List<String> searchTerms = new ArrayList<>();
            while (matcher.find()) {
                String productTerm = matcher.group(3);
                if (productTerm != null && productTerm.length() > 2) {
                    searchTerms.add(productTerm);
                }
            }
            
            // If no pattern matches, try to extract words that might be products
            if (searchTerms.isEmpty()) {
                String[] words = cleanedMessage.split("\\s+");
                for (String word : words) {
                    if (word.length() > 2 && !word.matches("\\d+")) {
                        searchTerms.add(word);
                    }
                }
            }
            
            // Search for products using the extracted terms
            List<Map<String, Object>> foundProducts = new ArrayList<>();
            for (String term : searchTerms) {
                if (term.length() < 2) continue;
                
                log.info("Searching for products with keyword: {}", term);
                var searchResult = productService.searchProducts(term, null, null, null);
                
                if (searchResult.isSuccess() && searchResult.getData() != null) {
                    for (ProductDetailsDTO product : searchResult.getData()) {
                        // Convert to map for JSON response
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("product_id", product.getProduct_id());
                        productMap.put("product_name", product.getProduct_name());
                        productMap.put("brand", product.getBrand());
                        productMap.put("category", product.getCategory());
                        productMap.put("price", product.getPrice());
                        productMap.put("discounted_price", product.getDiscounted_price());
                        productMap.put("image_url", product.getImage_url());
                        productMap.put("rating", product.getRating());
                        productMap.put("available_stock", product.getAvailable_stock());
                        productMap.put("sku_code", product.getSku_code());
                        productMap.put("description", product.getDescription());
                        productMap.put("is_popular", product.getIs_popular());
                        
                        // Avoid duplicates
                        boolean alreadyAdded = foundProducts.stream()
                            .anyMatch(p -> p.get("product_id").equals(product.getProduct_id()));
                        if (!alreadyAdded) {
                            foundProducts.add(productMap);
                        }
                    }
                }
                
                // Limit to 10 products to avoid overwhelming the UI
                if (foundProducts.size() >= 10) {
                    break;
                }
            }
            
            log.info("Found {} products for message: {}", foundProducts.size(), userMessage);
            return foundProducts;
            
        } catch (Exception e) {
            log.error("Error searching products from message: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

