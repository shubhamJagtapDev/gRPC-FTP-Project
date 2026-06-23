package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientConfiguration {
    private Properties properties;


    public ClientConfiguration() {
        try (InputStream input = ClientConfiguration.class.getClassLoader().getResourceAsStream("client.config.properties")){
            properties = new Properties();
            properties.load(input);
        } catch (IOException ioException) {
            System.out.println("Cannot read in config file " + ioException);
        }
    }

    public String getCAPath() {
        return properties.getProperty(ClientConstants.CA_CERT_PATH);
    }

    public String getClientKeyPath() {
        return properties.getProperty(ClientConstants.CLIENT_KEY_PATH);
    }

    public String getClientCertPath() {
        return properties.getProperty(ClientConstants.CLIENT_CERT_PATH);
    }
}
