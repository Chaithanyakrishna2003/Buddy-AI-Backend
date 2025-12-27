package com.dealshare.buddyai.controller;

import com.dealshare.buddyai.dto.FeedbackRequestDTO;
import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.model.Feedback;
import com.dealshare.buddyai.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Feedback>> submitFeedback(@RequestBody FeedbackRequestDTO request) {
        log.info("Submit feedback request - user_id: {}, order_id: {}", 
                request.getUser_id(), request.getOrder_id());
        
        ResponseDTO<Feedback> response = feedbackService.submitFeedback(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<Feedback>>> getFeedbackByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        log.info("Get feedback by user - user_id: {}, limit: {}", userId, limit);
        
        ResponseDTO<List<Feedback>> response = feedbackService.getFeedbackByUser(userId, limit);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseDTO<List<Feedback>>> getFeedbackByOrder(
            @PathVariable Integer orderId,
            @RequestParam(defaultValue = "20") Integer limit) {
        log.info("Get feedback by order - order_id: {}, limit: {}", orderId, limit);
        
        ResponseDTO<List<Feedback>> response = feedbackService.getFeedbackByOrder(orderId, limit);
        
        return ResponseEntity.ok(response);
    }
}






