package com.rating.punctuality.rating_punctuality.utils;

import java.io.*;

public class DockerRunner {

    public static void runContainer() {
        try {
            new ProcessBuilder()
                .command("docker", "compose", "-f", "./src/main/docker/docker-compose.yml", "run", "--rm", "analytics-job")
                .start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}