package com.dealshare.buddyai.service;

import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.model.User;
import com.dealshare.buddyai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.default.user-id:1}")
    private Integer defaultUserId;

    public ResponseDTO<Map<String, Object>> getProfile() {
        try {
            Optional<User> userOpt = userRepository.findByUserId(defaultUserId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> profile = new HashMap<>();
                profile.put("user_id", user.getUserId());
                profile.put("name", user.getFullName());
                profile.put("phone", user.getPhoneNumber());
                profile.put("email", user.getEmail());
                profile.put("address", user.getAddress());
                profile.put("city", user.getCity());
                profile.put("pincode", user.getPincode());
                return new ResponseDTO<>(true, "Profile retrieved successfully", profile);
            } else {
                // Return default profile if user not found
                Map<String, Object> defaultProfile = new HashMap<>();
                defaultProfile.put("user_id", defaultUserId);
                defaultProfile.put("name", "Guest User");
                defaultProfile.put("phone", "+91 1234567890");
                return new ResponseDTO<>(true, "Default profile", defaultProfile);
            }
        } catch (Exception e) {
            log.error("Error fetching profile", e);
            return new ResponseDTO<>(false, "Failed to fetch profile: " + e.getMessage(), null);
        }
    }
}






