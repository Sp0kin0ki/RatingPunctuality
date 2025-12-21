package com.rating.punctuality.rating_punctuality.utils;

import java.io.*;

public class DockerRunner {

    public static void runContainer() {
        try {
            new ProcessBuilder()
                .command("docker", "run", "--rm", "delay-analysis:latest")
                .start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}