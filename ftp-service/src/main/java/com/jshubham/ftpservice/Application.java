package com.jshubham.ftpservice;

import com.jshubham.ftpservice.server.FTPServer;
import com.jshubham.ftpservice.utils.ServerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;

public class Application {
     private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException, CertificateException, GeneralSecurityException {
        final ServerConfiguration serverConfiguration = new ServerConfiguration("config.properties");
        final FTPServer server = new FTPServer(serverConfiguration);
        server.start();
        server.blockUntilShutdown();
        logger.info("FTP server shutting down ..... ");
    }
}
