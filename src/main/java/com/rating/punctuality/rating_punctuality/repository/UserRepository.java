package com.rating.punctuality.rating_punctuality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.entities.User;
import com.rating.punctuality.rating_punctuality.model.enums.UserRoles;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Integer countByRolesName(UserRoles roleName);
}
