package com.al7afez.al7afez.repositories;

import com.al7afez.al7afez.model.entities.AppUser;

import java.util.Optional;

public interface UserRepository extends BaseRepository<AppUser, Long> {
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByUsername(String username);
    Optional<AppUser> findByUsername(String username);
}
