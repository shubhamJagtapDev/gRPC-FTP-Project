package com.jshubham.ftpservice.server;

import com.jshubham.ftpservice.utils.ServerConfiguration;
import com.jshubham.ftpservice.utils.ServerConstants;
import com.jshubham.ftpservice.utils.ServerUtils;
import io.grpc.*;
import io.grpc.util.AdvancedTlsX509KeyManager;
import io.grpc.util.AdvancedTlsX509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FTPServer {
    private static final Logger logger = LogManager.getLogger(FTPServer.class.getName());
    private final Server server;
    private final String mode;

    /**
     * @param serverConfiguration
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public FTPServer(ServerConfiguration serverConfiguration) throws IOException, GeneralSecurityException {
        if(serverConfiguration.getRunInInsecureMode()) {
            server = ServerBuilder.forPort(ServerConstants.INSECURE_MODE_PORT)
                    .addService(new FTPService(serverConfiguration))
                    .build();
            mode = "IN-secure";
        } else {
            // Cred File Paths Setup
            File certChainFile = ServerUtils.resolveCredFile(serverConfiguration.getServerCertPath());
            File privateKeyFile = ServerUtils.resolveCredFile(serverConfiguration.getServerKeyPath());
            File caCertFile = ServerUtils.resolveCredFile(serverConfiguration.getCAPath());

            // Create advanced key manager to enable hot reloading of server key and cert
            ScheduledExecutorService keyReloader = Executors.newScheduledThreadPool(1);
            AdvancedTlsX509KeyManager serverKeyManager = new AdvancedTlsX509KeyManager();
            serverKeyManager.updateIdentityCredentials(certChainFile, privateKeyFile, 1, TimeUnit.HOURS, keyReloader);

            // Create advanced trust manager to enable hot reloading of root cert
            ScheduledExecutorService trustReloader = Executors.newScheduledThreadPool(1);
            AdvancedTlsX509TrustManager serverTrustManager = AdvancedTlsX509TrustManager.newBuilder()
                    .setVerification(AdvancedTlsX509TrustManager.Verification.CERTIFICATE_ONLY_VERIFICATION)
                    .build();
            serverTrustManager.updateTrustCredentials(caCertFile, 1, TimeUnit.HOURS, trustReloader);

            // TlsServerCredentials Setup
            TlsServerCredentials.Builder tlsBuilder = TlsServerCredentials.newBuilder()
                    .keyManager(serverKeyManager)
                    .trustManager(serverTrustManager)
                    .clientAuth(TlsServerCredentials.ClientAuth.REQUIRE);

            // Create ServerCredentials for server authentication
            ServerCredentials serverCredentials = tlsBuilder.build();

            // Build the server with serverCredentials
            server = Grpc.newServerBuilderForPort(ServerConstants.DEFAULT_SERVER_PORT, serverCredentials)
                    .addService(new FTPService(serverConfiguration))
                    .build();

            mode = "secure";
        }

    }

    /**
     * This method starts the server and adds shutdown hooks for logging. In
     * case of an abrupt shutdown, the server waits thirty seconds for all
     * channels to cleanly shutdown before a forced shutdown is executed.
     */
    public void start() throws IOException {
        server.start();
        logger.info("FTP Server started {} mode", mode);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                if(server!=null) {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("gRPC server shut down");
        }));
    }

    /**
     * Shuts down the gRCP server.
     * this method will only be used for testing purpose only.
     */
    public void shutDown() {
        if(server!=null) {
            server.shutdown();
        }
    }

    /**
     * Graceful shutdown. Await termination on the main thread since the gRPC
     * library uses daemon threads. This method can hang indefinitely if
     * existing channels don't shutdown.
     * this method will only be used for testing purpose only.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
