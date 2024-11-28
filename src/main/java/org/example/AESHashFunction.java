package org.example;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AESHashFunction {

    public static byte[] hash(String input, String key) throws Exception {
        byte[] keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] inputData = pad(input.getBytes(StandardCharsets.UTF_8), 16);

        byte[] hash = new byte[16];
        Arrays.fill(hash, (byte) 0);

        for (int i = 0; i < inputData.length; i += 16) {
            byte[] block = Arrays.copyOfRange(inputData, i, i + 16);
            byte[] encryptedBlock = cipher.doFinal(xor(hash, block));
            hash = Arrays.copyOf(encryptedBlock, 16);
        }

        return hash;
    }

    private static byte[] pad(byte[] data, int blockSize) {
        int paddingLength = blockSize - (data.length % blockSize);
        byte[] paddedData = Arrays.copyOf(data, data.length + paddingLength);
        Arrays.fill(paddedData, data.length, paddedData.length, (byte) paddingLength);
        return paddedData;
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            String input = "Hello, world!";
            String key = "my_secret_key123";

            byte[] hash = hash(input, key);
            System.out.println("Input: " + input);
            System.out.println("Hash: " + bytesToHex(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

