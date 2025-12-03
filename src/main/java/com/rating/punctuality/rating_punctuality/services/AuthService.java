package com.rating.punctuality.rating_punctuality.services;

import com.rating.punctuality.rating_punctuality.model.auth.UserRegistrationDto;
import com.rating.punctuality.rating_punctuality.model.entities.User;

public interface AuthService {
    User register(UserRegistrationDto registrationDTO);

    User getUser(String username);
}

