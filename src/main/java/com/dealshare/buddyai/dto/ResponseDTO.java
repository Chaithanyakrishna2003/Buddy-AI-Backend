package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    
    // Builder pattern for generic types
    public static <T> ResponseDTOBuilder<T> builder() {
        return new ResponseDTOBuilder<T>();
    }
    
    public static class ResponseDTOBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        
        public ResponseDTOBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }
        
        public ResponseDTOBuilder<T> message(String message) {
            this.message = message;
            return this;
        }
        
        public ResponseDTOBuilder<T> data(T data) {
            this.data = data;
            return this;
        }
        
        public ResponseDTO<T> build() {
            return new ResponseDTO<>(success, message, data);
        }
    }
}
