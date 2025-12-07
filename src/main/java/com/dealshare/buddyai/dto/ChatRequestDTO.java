package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    private String conversation_id;
    private String message;
    private Integer order_id;
    private Map<String, Object> order_info;
    private Boolean is_general_issue;
    private Boolean is_issue_reporting;
}

