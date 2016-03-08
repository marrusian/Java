package cryptosystems.caesar;

import cryptosystems.Cipher;

public class Caesar implements Cipher
{
    public static String encrypt(String text, int key) throws IllegalArgumentException
    {
        Cipher.checkText(text);

        StringBuilder sb = new StringBuilder();
        int ch;

        for (int i = 0, lim = text.length(); i < lim; ++i) {
            ch = text.charAt(i);

            if (Character.isWhitespace(ch))
                continue;

            if (Character.isUpperCase(ch))
                ch = ((ch - 'A') + key) % 26 + 'A';
            else
                ch = ((ch - 'a') + key) % 26 + 'a';

            sb.append((char) ch);
        }

        return sb.toString();
    }

    public static String decrypt(String text, int key) throws IllegalArgumentException
    {
        Cipher.checkText(text);

        StringBuilder sb = new StringBuilder();
        int ch;

        for (int i = 0, lim = text.length(); i < lim; ++i) {
            ch = text.charAt(i);

            if (Character.isWhitespace(ch))
                continue;

            if (Character.isUpperCase(ch))
                ch = Cipher.mod((ch - 'A') - key, 26) + 'A';
            else
                ch = Cipher.mod((ch - 'a') - key, 26) + 'a';

            sb.append((char) ch);
        }

        return sb.toString();
    }
}
