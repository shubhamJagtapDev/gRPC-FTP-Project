package com.jshubham.ftpservice.utils;

import com.google.protobuf.ByteString;
import utils.ClientConstants;

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
}
