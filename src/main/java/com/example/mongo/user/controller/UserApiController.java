package com.example.mongo.user.controller;

import com.example.mongo.user.dto.request.RegistrationRequest;
import com.example.mongo.user.dto.request.EditUserRequest;
import com.example.mongo.user.dto.response.UserResponse;
import com.example.mongo.user.entity.UserDoc;
import com.example.mongo.user.exception.BadRequestException;
import com.example.mongo.user.exception.ObjectIdParseException;
import com.example.mongo.user.exception.UserAlreadyExistException;
import com.example.mongo.user.exception.UserNotFoundException;
import com.example.mongo.user.repository.UserRepository;
import com.example.mongo.user.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${init.email}")
    private String initUser;
    @Value("${init.password}")
    private String iniPassword;

    @GetMapping(UserRoutes.BY_ID)
    public UserResponse findById(@PathVariable String id) throws UserNotFoundException, ObjectIdParseException {
        if (!ObjectId.isValid(id)) throw new ObjectIdParseException();
        UserDoc userDoc = userRepository.findById(new ObjectId(id)).orElseThrow(UserNotFoundException::new);
        return UserResponse.of(userDoc);
    }

    @GetMapping(UserRoutes.SEARCH)
    public List<UserResponse> search(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "") String query) {
        Pageable pageable = PageRequest.of(page, size);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<UserDoc> example = Example.of(
                UserDoc.builder().firstName(query).lastName(query).build(), exampleMatcher
        );

        Page<UserDoc> users = userRepository.findAll(example, pageable);

        return users.getContent().stream().map(UserResponse::of).collect(Collectors.toList());
    }


    @PostMapping(UserRoutes.REGISTRATION)
    public UserResponse registration(@RequestBody RegistrationRequest request) throws BadRequestException, UserAlreadyExistException {
        request.validation();

        Optional<UserDoc> check = userRepository.findByEmail(request.getEmail());
        if (check.isPresent()) throw new UserAlreadyExistException();

        UserDoc userDoc = UserDoc.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userDoc = userRepository.save(userDoc);
        return UserResponse.of(userDoc);
    }

    @PutMapping(UserRoutes.EDIT)
    public UserResponse edit(Principal principal, @RequestBody EditUserRequest request) throws ObjectIdParseException, UserNotFoundException {
        UserDoc userDoc = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        userDoc.setFirstName(request.getFirstName());
        userDoc.setLastName(request.getLastName());

        userDoc = userRepository.save(userDoc);

        return UserResponse.of(userDoc);
    }

    @DeleteMapping(UserRoutes.BY_ID)
    public String delete(@PathVariable String id) throws ObjectIdParseException {
        if (!ObjectId.isValid(id)) throw new ObjectIdParseException();

        userRepository.deleteById(new ObjectId(id));
        return HttpStatus.OK.name();
    }

    @GetMapping(UserRoutes.INIT)
    public UserResponse init() {
        Optional<UserDoc> checkUser = userRepository.findByEmail(initUser);
        UserDoc userDoc;

        if (checkUser.isEmpty()) {
            userDoc = UserDoc.builder()
                    .firstName("Default")
                    .lastName("Default")
                    .email(initUser)
                    .password(passwordEncoder.encode(iniPassword))
                    .build();
            userDoc = userRepository.save(userDoc);
        } else {
            userDoc = checkUser.get();
        }
        return UserResponse.of(userDoc);
    }
}
