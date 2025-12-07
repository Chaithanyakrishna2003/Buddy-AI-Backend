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
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Field("user_id")
    private Integer userId;

    @Field("full_name")
    private String fullName;

    @Field("phone_number")
    private String phoneNumber;

    private String email;
    private String address;
    private String city;
    private String pincode;

    @Field("preferred_language")
    private String preferredLanguage;

    @Field("membership_status")
    private String membershipStatus;

    @Field("payment_mode_preference")
    private String paymentModePreference;

    @Field("last_active_at")
    private LocalDateTime lastActiveAt;
}

