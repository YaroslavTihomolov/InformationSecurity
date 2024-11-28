package org.example;

public class RC4 {

    private static final int KEY_LENGTH = 256;

    private final char[] s = new char[KEY_LENGTH];

    private Integer x = 0;
    private Integer y = 0;

    public RC4(String key) {
        init(buildKey(key));
    }

    private void init(String key) {
        for (int i = 0; i < KEY_LENGTH; i++) {
            s[i] = (char) i;
        }
        int j = 0;
        for (int i = 0; i < KEY_LENGTH; i++) {
            j = (j + s[i] + key.charAt(i % key.length())) % KEY_LENGTH;
            swap(s, i, j);
        }
    }

    private int keyItem(char[] sCopy) {
        this.x = (this.x + 1) % KEY_LENGTH;
        this.y = (this.y + sCopy[this.x]) % KEY_LENGTH;

        swap(sCopy, this.x, this.y);

        return sCopy[(sCopy[this.x] + sCopy[this.y]) % KEY_LENGTH];
    }

    public char[] encode(char[] data) {
        char[] encodeData = new char[data.length];

        this.x = 0;
        this.y = 0;
        var sCopy = this.s.clone();
        for (int i = 0; i < data.length; i++) {
            encodeData[i] = (char) (data[i] ^ keyItem(sCopy));
        }
        return encodeData;
    }

    public char[] decode(char[] data) {
        return encode(data);
    }

    private void swap(char[] array, int index1, int index2) {
        var tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
    }

    private String buildKey(String inputKey) {
        if (inputKey.length() > KEY_LENGTH) {
            throw new IllegalArgumentException("Ключ не может быть длинее 255");
        }
        var builder = new StringBuilder();
        for (int i = 0; i < KEY_LENGTH; i++) {
            builder.append(inputKey.charAt(i % inputKey.length()));
        }
        return builder.toString();
    }

}
