package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketResponseDTO {
    private String id;
    private String ticketId;
    private String orderId;
    private int userId;
    private String issueType;
    private List<String> selectedItemNames;
    private int itemsCount;
    private String comment;
    private List<PhotoMetadataDTO> photos;
    private int photoCount;
    private String status;
    private String resolution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhotoMetadataDTO {
        private String name;
        private long size;
        private String type;
        private long lastModified;
        private String url;
    }
}
