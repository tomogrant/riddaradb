package com.se.riddaradb.auth.user;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

//Wrapper class for creating user object which Spring Security can understand
public class UserDetailsCustom implements UserDetails {

    private final UserEntity userEntity;

    // UserDetailsCustom object constructed when loading user from the
    // database; see the overridden loadUserByUsername in UserDetailsServiceCustom
    public UserDetailsCustom(UserEntity userEntity){
        this.userEntity = userEntity;
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public @NonNull String getUsername() {
        return userEntity.getUsername();
    }
}
