package com.se.riddaradb.auth.security;

import com.se.riddaradb.auth.user.UserEntity;
import com.se.riddaradb.auth.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
public class AuthenticationController {

    private final UserRepository userRepository;

    public AuthenticationController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("csrf")
    public CsrfToken csrf(CsrfToken csrfToken){
        return csrfToken;
    }

    @GetMapping("me")
    public ResponseEntity<UserResponse> me (Authentication authentication){

        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found in database"));

        return ResponseEntity.ok(new UserResponse(
                authentication.getName(),
                user.getFirstName(),
                user.getLastNames(),
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()));
    }
}
