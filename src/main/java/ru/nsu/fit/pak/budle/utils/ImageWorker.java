package ru.nsu.fit.pak.budle.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ImageWorker {

    public String saveImage(String imageName) {
        String databaseFilepath = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + ".jpg";
        String saveFilepath = "./images/" + databaseFilepath;
        try {
            File file = new File(saveFilepath);
            byte[] imageBytes = Base64.getDecoder().decode(imageName);
            FileUtils.writeByteArrayToFile(file, imageBytes);
            return databaseFilepath;
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
            return null;

        }
    }

    public String loadImage(String imageName) {
        try {
            File inputFile = new File("./images/" + imageName);
            byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    public String getImageFromResource(String imageName) {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("images" + imageName)) {
            if (stream != null) {
                return Base64.getEncoder().encodeToString(stream.readAllBytes());
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
            return null;
        }
        return null;
    }
}
