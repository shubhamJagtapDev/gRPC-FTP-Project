package com.jshubham.ftpservice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class manages the configuration settings for the server.
 */
public class ServerConfiguration {

    private static final Logger logger = LogManager.getLogger(ServerConfiguration.class);
    private final Properties properties;

    //default constructor or testing purposes
    public ServerConfiguration() {
        properties = new Properties();
    }
    //parameterized constructor to read properties from a config file
    public ServerConfiguration(String configFileName) {
        properties = new Properties();

        // 1. Try loading from classpath first
        try (InputStream inputStream = ServerConfiguration.class.getClassLoader().getResourceAsStream(configFileName)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Loaded config file from classpath: {}", configFileName);
                return;
            }
        } catch (IOException e) {
            logger.warn("Failed to load config file from classpath: {}", configFileName, e);
        }

        // 2. Fallback to filesystem
        File file = new File(configFileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
            logger.info("Loaded config file from filesystem: {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Cannot read in config file from filesystem", e);
        }
    }
    //method to retrieve the DESTINATION_DIRECTORY_ON_SERVER property
    public String getDestinationDirectoryOnServer() {
        return properties.getProperty(ServerConstants.DESTINATION_DIRECTORY_ON_SERVER);
    }
    //method to retrieve the TEMP_WRITE_PATH property
    public String getTempWritePath() {
        return properties.getProperty(ServerConstants.TEMP_WRITE_PATH);
    }
}
