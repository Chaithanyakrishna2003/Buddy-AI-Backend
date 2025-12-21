package com.dealshare.buddyai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "feedback")
public class Feedback {
    @Id
    private String id;

    @Field("feedback_id")
    private Integer feedbackId;

    @Field("user_id")
    private Integer userId;

    @Field("order_id")
    private Integer orderId;

    private String category;
    private String subject;
    private Integer rating;
    private String comments;
    private String channel;

    @Field("allow_contact")
    private Boolean allowContact;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}





