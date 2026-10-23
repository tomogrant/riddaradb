package com.se.riddaradb.auth.user;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security's authentication logic uses loadUserByUsername, which needs access to the user repository
    // that I define; this is why I need to define my own implementation.
    // The user is retrieved from the repository and then wrapped in a custom implementation of UserDetails.
    public UserDetailsServiceCustom(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with this username not found."));

        return new UserDetailsCustom(userEntity);
    }
}
