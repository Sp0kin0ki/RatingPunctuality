package com.rating.punctuality.rating_punctuality.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AppUserDetailsService {

    public UserDetails loadUserByUsername(String username);
}