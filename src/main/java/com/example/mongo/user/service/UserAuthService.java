package com.example.mongo.user.service;

import com.example.mongo.user.entity.UserDoc;
import com.example.mongo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserAuthService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserDoc> optionalUserDoc = userRepository.findByEmail(email);
        if (optionalUserDoc.isEmpty()) throw new UsernameNotFoundException("User with this email not found");
        List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("user"));
        UserDoc userDoc = optionalUserDoc.get();
        return new User(userDoc.getEmail(), userDoc.getPassword(), authorityList);
    }
}
