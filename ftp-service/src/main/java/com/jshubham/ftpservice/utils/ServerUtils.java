package com.jshubham.ftpservice.utils;

import com.google.protobuf.ByteString;
import utils.ClientConstants;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ServerUtils {
    public static boolean isNullOrEmpty(String str) {
        return "".equals(str) || str == null;
    }

    /**
     * Returns false if the hash of the received data doesn't match with the
     * hash computed on the client side for the same chunk of data.
     *
     * @param content
     * @param receivedHash
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static boolean doCheckSumMatch(ByteString content, ByteString receivedHash) throws NoSuchAlgorithmException {
        byte[] computedHash = MessageDigest.getInstance("MD5").digest(content.toByteArray());
        return Arrays.equals(computedHash, receivedHash.toByteArray());
    }

    /**
     * Computes the checksum of the passed-in byte array using MD5
     */
    static public byte[] computeChecksum(byte[] chunk, int size) {
        byte[] content = Arrays.copyOfRange(chunk, 0, size);
        byte[] result = null;
        try {
            result = MessageDigest.getInstance("MD5").digest(content);
        } catch (NoSuchAlgorithmException nsa) {
            throw new RuntimeException("MD5 algorithm not found. " + nsa);
        }
        return result;
    }

    public static File resolveCredFile(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file;
        }

        // Try common location offsets based on CWD
        File[] fallbacks = new File[]{
                new File("ftp-service", path),
                new File("src/main/resources", path),
                new File("ftp-service/src/main/resources", path),
                new File("target", path),
                new File("ftp-service/target", path)
        };

        for (File fallback : fallbacks) {
            if (fallback.exists() && fallback.isFile()) {
                return fallback;
            }
        }
        // Build a helpful error message listing all attempted absolute paths
        StringBuilder sb = new StringBuilder();
        sb.append("Credential file not found at: ").append(file.getAbsolutePath()).append("\n");
        sb.append("Also tried the following fallback locations:\n");
        for (File fallback : fallbacks) {
            sb.append("  - ").append(fallback.getAbsolutePath()).append("\n");
        }
        sb.append("Please verify that the file exists and that the working directory is set correctly.");
        throw new java.io.FileNotFoundException(sb.toString());
    }
}
