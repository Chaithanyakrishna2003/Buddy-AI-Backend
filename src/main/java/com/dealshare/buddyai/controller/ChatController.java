package com.dealshare.buddyai.controller;

import com.dealshare.buddyai.dto.ChatRequestDTO;
import com.dealshare.buddyai.dto.ChatResponseDTO;
import com.dealshare.buddyai.dto.QuickReplyRequestDTO;
import com.dealshare.buddyai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {
        log.info("Chat request received - conversation_id: {}, message: {}", 
                request.getConversation_id(), request.getMessage());
        
        ChatResponseDTO response = chatService.chat(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quick-reply")
    public ResponseEntity<ChatResponseDTO> quickReply(@RequestBody QuickReplyRequestDTO request) {
        log.info("Quick reply request - conversation_id: {}, question_type: {}", 
                request.getConversation_id(), request.getQuestion_type());
        
        ChatResponseDTO response = chatService.quickReply(
                request.getConversation_id(), 
                request.getQuestion_type()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> getConversation(@PathVariable String conversationId) {
        log.info("Get conversation request - conversation_id: {}", conversationId);
        
        Map<String, Object> conversation = chatService.getConversation(conversationId);
        
        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, String>> deleteConversation(@PathVariable String conversationId) {
        log.info("Delete conversation request - conversation_id: {}", conversationId);
        
        chatService.deleteConversation(conversationId);
        
        return ResponseEntity.ok(Map.of("message", "Conversation deleted successfully"));
    }
}

