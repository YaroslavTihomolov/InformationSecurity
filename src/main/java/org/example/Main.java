package org.example;


import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static String toHex(String input) {
        StringBuilder hexString = new StringBuilder();
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static String fromHex(String hex) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < hex.length(); i += 2) {
            String byteHex = hex.substring(i, i + 2);
            result.append((char) Integer.parseInt(byteHex, 16));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку: ");
        String input = scanner.nextLine();
        scanner.close();

        Present cipher = new Present("0123456789abcdef0123");
        String encryptedText = cipher.encrypt(toHex(input).substring(0, 16));
        System.out.println("После шифровки: " + encryptedText);

        String decryptedText = cipher.decrypt(encryptedText);
        System.out.println("После расшифровки: " + fromHex(decryptedText));

    }
}
