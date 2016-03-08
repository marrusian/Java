package cryptosystems.vigenere;

import cryptosystems.Cipher;

public class Vigenere implements Cipher
{
    public static String encrypt(String text, String key) throws IllegalArgumentException
    {
        Cipher.checkText(text);
        Cipher.checkText(key);

        StringBuilder sb = new StringBuilder();
        int kIndex = 0, kLength = key.length();
        int ch, vKey;

        for (int i = 0, lim = text.length(); i < lim; ++i) {
            ch = text.charAt(i);

            if (Character.isWhitespace(ch))
                continue;

            vKey = key.charAt(kIndex);
            if (Character.isUpperCase(vKey))
                vKey -= 'A';
            else
                vKey -= 'a';

            if (Character.isUpperCase(ch))
                ch = ((ch - 'A') + vKey) % 26 + 'A';
            else
                ch = ((ch - 'a') + vKey) % 26 + 'a';

            sb.append((char) ch);
            kIndex = (kIndex + 1) % kLength;
        }

        return sb.toString();
    }

    public static String decrypt(String text, String key) throws IllegalArgumentException
    {
        Cipher.checkText(text);
        Cipher.checkText(key);

        StringBuilder sb = new StringBuilder();
        int kIndex = 0, kLength = key.length();
        int ch, vKey;

        for (int i = 0, lim = text.length(); i < lim; ++i) {
            ch = text.charAt(i);

            if (Character.isWhitespace(ch))
                continue;

            vKey = key.charAt(kIndex);
            if (Character.isUpperCase(vKey))
                vKey -= 'A';
            else
                vKey -= 'a';

            if (Character.isUpperCase(ch))
                ch = Cipher.mod((ch - 'A') - vKey, 26) + 'A';
            else
                ch = Cipher.mod((ch - 'a') - vKey, 26) + 'a';

            sb.append((char) ch);
            kIndex = (kIndex + 1) % kLength;
        }

        return sb.toString();
    }
}
