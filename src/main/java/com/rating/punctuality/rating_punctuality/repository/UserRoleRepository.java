package com.rating.punctuality.rating_punctuality.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.entities.Role;
import com.rating.punctuality.rating_punctuality.model.enums.UserRoles;

@Repository
public interface UserRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findRoleByName(UserRoles role);
}
