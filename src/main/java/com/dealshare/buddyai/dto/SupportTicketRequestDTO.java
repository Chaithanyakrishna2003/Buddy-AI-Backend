package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketRequestDTO {
    private String orderId;
    private int userId;
    private String issueType;
    private List<String> selectedItemNames;
    private int itemsCount;
    private String comment;
    private List<PhotoMetadataDTO> photos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhotoMetadataDTO {
        private String name;
        private long size;
        private String type;
        private long lastModified;
        private String url; // Base64 encoded data URL (e.g., "data:image/png;base64,...")
    }
}
