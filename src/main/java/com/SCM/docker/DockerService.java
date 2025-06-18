package com.SCM.docker;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


@Service
public class DockerService {

    @Value("${docker.enabled}")
    private boolean dockerEnabled;

    @PostConstruct
    public void manageDockerContainers() {
        if (dockerEnabled) {
            startDockerContainers();
            // Add a shutdown hook to stop the containers
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopDockerContainers));
        } else {
            System.out.println("Docker is disabled.");
        }
    }

    public void startDockerContainers() {
        try {
            File composeFile = new File(getClass().getClassLoader().getResource("docker-compose.yml").getFile());
            if (!composeFile.exists()) {
                System.err.println("docker-compose.yml file not found at: " + composeFile.getAbsolutePath());
                return;
            }

            // Build Docker image using Dockerfile
            ProcessBuilder buildProcessBuilder = new ProcessBuilder();
            buildProcessBuilder.command("docker", "build", "-t", "demo-app", ".");
            buildProcessBuilder.directory(new File("."));
            buildProcessBuilder.redirectErrorStream(true);
            Process buildProcess = buildProcessBuilder.start();

            BufferedReader buildReader = new BufferedReader(new InputStreamReader(buildProcess.getInputStream()));
            String buildLine;
            while ((buildLine = buildReader.readLine()) != null) {
                System.out.println(buildLine);
            }

            int buildExitCode = buildProcess.waitFor();
            if (buildExitCode != 0) {
                System.err.println("Failed to build Docker image. Exit code: " + buildExitCode);
                return;
            }

            // Start Docker containers using docker-compose
            ProcessBuilder startProcessBuilder = new ProcessBuilder();
            startProcessBuilder.command("docker-compose", "-f", composeFile.getAbsolutePath(), "up", "-d");
            startProcessBuilder.redirectErrorStream(true);
            Process startProcess = startProcessBuilder.start();

            BufferedReader startReader = new BufferedReader(new InputStreamReader(startProcess.getInputStream()));
            String startLine;
            while ((startLine = startReader.readLine()) != null) {
                System.out.println(startLine);
            }

            int startExitCode = startProcess.waitFor();
            if (startExitCode == 0) {
                System.out.println("Docker containers started successfully.");
            } else {
                System.err.println("Failed to start Docker containers. Exit code: " + startExitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception occurred while managing Docker containers: " + e.getMessage());
        }
    }

    public void stopDockerContainers() {
        try {
            File composeFile = new File(getClass().getClassLoader().getResource("docker-compose.yml").getFile());
            if (!composeFile.exists()) {
                System.err.println("docker-compose.yml file not found at: " + composeFile.getAbsolutePath());
                return;
            }

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("docker-compose", "-f", composeFile.getAbsolutePath(), "down");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Docker containers stopped successfully.");
            } else {
                System.err.println("Failed to stop Docker containers. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception occurred while stopping Docker containers: " + e.getMessage());
        }
    }
}

//@Service
//public class DockerService {   
//
//    @Value("${docker.enabled}")
//    private boolean dockerEnabled;
//
//    @PostConstruct
//    public void startDockerContainers() {
//        if (dockerEnabled) {
//            try {
//                ProcessBuilder processBuilder = new ProcessBuilder();
//                processBuilder.command("docker-compose", "up", "-d");
//                processBuilder.redirectErrorStream(true);
//                Process process = processBuilder.start();
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                }
//
//                int exitCode = process.waitFor();
//                if (exitCode == 0) {
//                    System.out.println("Docker containers started successfully.");
//                } else {
//                    System.err.println("Failed to start Docker containers. Exit code: " + exitCode);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.err.println("Exception occurred while starting Docker containers: " + e.getMessage());
//            }
//
//            // Add a shutdown hook to stop the containers
//            Runtime.getRuntime().addShutdownHook(new Thread(this::stopDockerContainers));
//        } else {
//            System.out.println("Docker is disabled.");
//        }
//    }
//
//    private void stopDockerContainers() {
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder();
//            processBuilder.command("docker-compose", "down");
//            processBuilder.redirectErrorStream(true);
//            Process process = processBuilder.start();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                System.out.println("Docker containers stopped successfully.");
//            } else {
//                System.err.println("Failed to stop Docker containers. Exit code: " + exitCode);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Exception occurred while stopping Docker containers: " + e.getMessage());
//        }
//    }
//}