package org.example;

public class Present {

    private static final byte[] SBOX = {0xC, 0x5, 0x6, 0xB, 0x9, 0x0, 0xA, 0xD, 0x3, 0xE, 0xF, 0x8, 0x4, 0x7, 0x1, 0x2};

    private static final byte[] INV_S = {0x5, 0xe, 0xf, 0x8, 0xC, 0x1, 0x2, 0xD, 0xB, 0x4, 0x6, 0x3, 0x0, 0x7, 0x9, 0xA};

    private static final byte[] PERMUTATION = {0, 16, 32, 48, 1, 17, 33, 49, 2, 18, 34, 50, 3, 19, 35, 51,
            4, 20, 36, 52, 5, 21, 37, 53, 6, 22, 38, 54, 7, 23, 39, 55,
            8, 24, 40, 56, 9, 25, 41, 57, 10, 26, 42, 58, 11, 27, 43, 59,
            12, 28, 44, 60, 13, 29, 45, 61, 14, 30, 46, 62, 15, 31, 47, 63};

    private static final int ROUND_COUNT = 31;

    private final long[] roundKeys = new long[ROUND_COUNT + 1];

    public Present(String key) {
        generateRoundKeys(key);
    }

    private record Node(byte leftPart, byte rightPart) {

    }

    private long buildHighPart(String block) {
        long result = 0L;
        for (int i = 0; i < 16; i++) {
            char c = block.charAt(i);
            int value = (c >= '0' && c <= '9') ? (c - '0') : (c - 'a' + 10);
            result = (result << 4) | value;
        }
        return result;
    }

    private long buildLowPart(String block) {
        long keyLow = 0;
        for (int i = 16; i < 20; i++) {
            var c = block.charAt(i);
            keyLow = (keyLow << 4) | (((c >= '0' && c <= '9') ? (c - '0') : (c - 'a' + 10)) & 0xF);
        }
        return keyLow;
    }

    private void generateRoundKeys(String key) {
        var highPart = buildHighPart(key);
        var lowPart = buildLowPart(key);

        roundKeys[0] = highPart;
        for (long i = 1; i <= ROUND_COUNT; i++) {
            var longLowPart = lowPart;
            var tmpHighPart = highPart;
            highPart = highPart << 61 | longLowPart << 45 | highPart >>> 19;
            lowPart = (char) ((tmpHighPart >>> 3) & 0xFFFF);

            var highNibble = SBOX[(int) (highPart >>> 60)];
            highPart &= 0x0FFFFFFFFFFFFFFFL;
            highPart |= (long) highNibble << 60;

            lowPart ^= (char) ((i & 0x01) << 15);
            highPart ^= i >> 1;

            roundKeys[(int) i] = highPart;
        }
    }

    private Node[] buildNodes(long value, byte[] sbox) {
        var nodes = new Node[8];

        for (int i = 7; i >= 0; i--) {
            nodes[i] = new Node(sbox[(byte) ((value >> 2 * (7 - i) * 4) & 0xFL)], sbox[(byte) ((value >> (2 * (7 - i) + 1) * 4) & 0xFL)]);
        }
        return nodes;
    }

    private long permute(long source) {
        var permutation = 0L;
        int i;
        for (i = 0; i < 64; i++) {
            int distance = 63 - i;
            permutation = permutation | ((source >> distance & 0x1) << 63 - PERMUTATION[i]);
        }
        return permutation;
    }

    long inversepermute(long source) {
        var permutation = 0L;
        int i;
        for (i = 0; i < 64; i++) {
            int distance = 63 - PERMUTATION[i];
            permutation = (permutation << 1) | ((source >> distance) & 0x1);
        }
        return permutation;
    }

    private long toLong(Node[] nodes) {
        var result = 0L;
        int i;
        for (i = 0; i < 8; i++) {
            result = (result << 4) | (nodes[i].rightPart & 0xFL);
            result = (result << 4) | (nodes[i].leftPart & 0xFL);
        }
        return result;
    }

    private String fromLongToHexString(long block) {
        return String.format("%016x", block);
    }


    public String encrypt(String text) {
        var textValue = buildHighPart(text);

        for (int i = 0; i < ROUND_COUNT; i++) {
            textValue ^= roundKeys[i];

            var nodes = buildNodes(textValue, SBOX);

            textValue = permute(toLong(nodes));

        }
        textValue ^= roundKeys[ROUND_COUNT];

        return fromLongToHexString(textValue);
    }

    public String decrypt(String text) {
        var decryptValue = buildHighPart(text);

        for (int i = 0; i < ROUND_COUNT; i++) {
            decryptValue ^= roundKeys[ROUND_COUNT - i];
            decryptValue = inversepermute(decryptValue);

            var nodes = buildNodes(decryptValue, INV_S);

            decryptValue = toLong(nodes);

        }
        decryptValue ^= roundKeys[0];

        return fromLongToHexString(decryptValue);
    }

}
