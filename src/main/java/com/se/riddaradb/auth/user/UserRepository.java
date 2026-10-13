package com.se.riddaradb.auth.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);

    void deleteByUsername(String username);

    void deleteAllByRolesNotContaining(Role role);

    Optional<UserEntity> findByRoles(Role role);
}
