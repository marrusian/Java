package cryptosystems;

public interface Cipher
{
    static void checkText(String text) throws IllegalArgumentException {
        int ch;
        for (int i = 0, lim = text.length(); i < lim; ++i) {
            ch = text.charAt(i);
            if ((Character.toUpperCase(text.charAt(i)) < 'A' || Character.toUpperCase(text.charAt(i)) > 'Z')
                    && !Character.isWhitespace(text.charAt(i)))
                throw new IllegalArgumentException("Invalid character: " + text.charAt(i));
        }
    }

    static int mod(int x, int y) {
        int rem = x % y;
        return (rem >= 0)? rem : rem + y;
    }
}