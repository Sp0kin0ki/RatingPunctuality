package com.rating.punctuality.rating_punctuality.model.entities;

import com.rating.punctuality.rating_punctuality.model.enums.UserRoles;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private UserRoles name;

    public Role(UserRoles name) {
        this.name = name;
    }

    public Role() {
    }

    public UserRoles getName() {
        return name;
    }

    public void setName(UserRoles name) {
        this.name = name;
    }
}