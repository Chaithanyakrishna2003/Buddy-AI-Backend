package com.dealshare.buddyai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "support_tickets")
public class SupportTicket {
    @Id
    private String id;

    @Field("ticket_id")
    private String ticketId;

    @Field("order_id")
    private String orderId;

    @Field("user_id")
    private int userId;

    @Field("issue_type")
    private String issueType;

    @Field("selected_items")
    private List<String> selectedItemNames;

    @Field("items_count")
    private int itemsCount;

    @Field("comment")
    private String comment;

    @Field("photos")
    private List<PhotoMetadata> photos;

    @Field("photo_count")
    private int photoCount;

    @Field("status")
    private String status; // "open", "in_progress", "resolved", "closed"

    @Field("resolution")
    private String resolution;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("resolved_at")
    private LocalDateTime resolvedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhotoMetadata {
        private String name;
        private long size;
        private String type;
        private long lastModified;
        private String url; // For future: actual uploaded file URL
    }
}

