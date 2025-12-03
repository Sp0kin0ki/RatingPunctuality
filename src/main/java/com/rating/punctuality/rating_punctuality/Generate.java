package com.rating.punctuality.rating_punctuality;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Generate {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin");
        System.out.println("Вставь этот хеш в SQL:");
        System.out.println(hash);
    }
}