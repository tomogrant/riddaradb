package com.se.riddaradb.auth.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDto mapToDto(UserEntity userEntity){

        return new UserDto(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastNames(),
                "Password hidden"
                );
    }
}
