package com.example.mongo.user.dto.response;

import com.example.mongo.user.entity.UserDoc;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;

    public static UserResponse of(UserDoc userDoc) {
        return UserResponse.builder()
                .id(userDoc.getId().toString())
                .firstName(userDoc.getFirstName())
                .lastName(userDoc.getLastName())
                .email(userDoc.getEmail())
                .build();
    }
}
