package com.dealshare.buddyai.repository;

import com.dealshare.buddyai.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Feedback> findByOrderIdOrderByCreatedAtDesc(Integer orderId);
}





