package com.dealshare.buddyai.controller;

import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getProfile() {
        log.info("Get profile request");
        
        ResponseDTO<Map<String, Object>> response = userService.getProfile();
        
        return ResponseEntity.ok(response);
    }
}






