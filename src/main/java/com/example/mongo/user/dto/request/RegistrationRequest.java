package com.example.mongo.user.dto.request;

import com.example.mongo.user.exception.BadRequestException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;

    public void validation() throws BadRequestException {
        if (email == null || email.isBlank()) throw new BadRequestException();
        if (password == null || password.isBlank()) throw new BadRequestException();
    }
}
