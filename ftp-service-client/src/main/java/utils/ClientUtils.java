package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ClientUtils {
    /**
     * Computes the checksum of the passed-in byte array using MD5
     */
    static public byte[] computeChecksum(byte[] chunk, int size) {
        byte[] content = Arrays.copyOfRange(chunk, 0, size);
        byte[] result = null;
        try {
            result = MessageDigest.getInstance(ClientConstants.MD5).digest(content);
        } catch (NoSuchAlgorithmException nsa) {
            throw new RuntimeException("MD5 algorithm not found. " + nsa);
        }
        return result;
    }
}
