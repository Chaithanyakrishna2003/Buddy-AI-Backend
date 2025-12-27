package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {
    private Integer user_id;
    private Integer order_id;
    private String category;
    private String subject;
    private Integer rating;
    private String comments;
    private String channel;
    private Boolean allow_contact;
}






