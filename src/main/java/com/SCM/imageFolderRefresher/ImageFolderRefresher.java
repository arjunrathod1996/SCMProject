package com.SCM.imageFolderRefresher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImageFolderRefresher {

    // Define the path to the static/images folder
    private static final String IMAGES_FOLDER_PATH = "src/main/resources/static/images";

    // Define the interval at which the folder should be refreshed (in milliseconds)
    private static final long REFRESH_INTERVAL_MS = 60000; // Refresh every 60 seconds

    // Scheduled task to refresh the images folder
   // @Scheduled(fixedDelay = REFRESH_INTERVAL_MS)
    public void refreshImagesFolder() {
        try {
            // Implement logic to refresh the images folder
            // For example, you can scan for new files or update existing files
            
            // Print a message indicating that the folder refresh operation has started
            System.out.println("Refreshing images folder...");

            // Perform any necessary actions to update the folder contents
            
            // For example, you can list files in the folder and perform operations on them
            Path imagesFolderPath = Paths.get(IMAGES_FOLDER_PATH);
            Files.list(imagesFolderPath)
                 .forEach(file -> {
                     // Perform actions on each file, such as updating metadata or performing checks
                     // For example: System.out.println(file.getFileName());
                 });
            
            // Log a message indicating that the folder has been refreshed
            System.out.println("Images folder refreshed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any exceptions that occur during the folder refresh process
        }
    }
}
