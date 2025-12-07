package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String response;
    private String conversation_id;
    private List<String> suggested_questions;
    private String timestamp;
    private Boolean needs_more_info;
    private List<String> questions_to_ask;
    private Map<String, Object> collected_data;
    private String intent;
    private Map<String, Object> order_data;
    private List<Map<String, Object>> brand_options;
    private Boolean show_feedback_modal;
    private Map<String, Object> feedback_context;
    private List<Map<String, Object>> products; // Product tiles to display in chat
}

