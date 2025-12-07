package com.dealshare.buddyai.service;

import com.dealshare.buddyai.dto.FeedbackRequestDTO;
import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.model.Feedback;
import com.dealshare.buddyai.model.User;
import com.dealshare.buddyai.repository.FeedbackRepository;
import com.dealshare.buddyai.repository.OrderRepository;
import com.dealshare.buddyai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ResponseDTO<Feedback> submitFeedback(FeedbackRequestDTO request) {
        try {
            // Validate user exists
            Optional<User> userOpt = userRepository.findByUserId(request.getUser_id());
            if (userOpt.isEmpty()) {
                return new ResponseDTO<>(false, "User not found", null);
            }

            // Validate order if provided
            if (request.getOrder_id() != null) {
                var orderOpt = orderRepository.findByOrderId(request.getOrder_id());
                if (orderOpt.isEmpty()) {
                    return new ResponseDTO<>(false, "Order not found", null);
                }
                var order = orderOpt.get();
                if (order.getUserId() != request.getUser_id()) {
                    return new ResponseDTO<>(false, "Order does not belong to the specified user", null);
                }
            }

            // Generate feedback ID
            int feedbackId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

            Feedback feedback = Feedback.builder()
                    .feedbackId(feedbackId)
                    .userId(request.getUser_id())
                    .orderId(request.getOrder_id())
                    .category(request.getCategory())
                    .subject(request.getSubject())
                    .rating(request.getRating())
                    .comments(request.getComments())
                    .channel(request.getChannel() != null ? request.getChannel() : "web")
                    .allowContact(request.getAllow_contact() != null ? request.getAllow_contact() : false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Feedback saved = feedbackRepository.save(feedback);
            return new ResponseDTO<>(true, "Feedback submitted successfully", saved);
        } catch (Exception e) {
            log.error("Error submitting feedback", e);
            return new ResponseDTO<>(false, "Failed to submit feedback: " + e.getMessage(), null);
        }
    }

    public ResponseDTO<List<Feedback>> getFeedbackByUser(Integer userId, Integer limit) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);
            if (limit != null && limit > 0) {
                feedbacks = feedbacks.stream().limit(limit).toList();
            }
            return new ResponseDTO<>(true, "Retrieved " + feedbacks.size() + " feedback record(s)", feedbacks);
        } catch (Exception e) {
            log.error("Error fetching user feedback", e);
            return new ResponseDTO<>(false, "Failed to fetch feedback: " + e.getMessage(), null);
        }
    }

    public ResponseDTO<List<Feedback>> getFeedbackByOrder(Integer orderId, Integer limit) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
            if (limit != null && limit > 0) {
                feedbacks = feedbacks.stream().limit(limit).toList();
            }
            return new ResponseDTO<>(true, "Retrieved " + feedbacks.size() + " feedback record(s) for order " + orderId, feedbacks);
        } catch (Exception e) {
            log.error("Error fetching order feedback", e);
            return new ResponseDTO<>(false, "Failed to fetch feedback: " + e.getMessage(), null);
        }
    }
}

