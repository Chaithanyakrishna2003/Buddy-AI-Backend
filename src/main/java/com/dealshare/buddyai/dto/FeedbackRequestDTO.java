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
    
    // Manual getters for snake_case fields (Lombok workaround for Java 25)
    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public Integer getOrder_id() { return order_id; }
    public void setOrder_id(Integer order_id) { this.order_id = order_id; }
    public Boolean getAllow_contact() { return allow_contact; }
    public void setAllow_contact(Boolean allow_contact) { this.allow_contact = allow_contact; }
}






