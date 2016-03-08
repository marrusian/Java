package cryptosystems;

import cryptosystems.caesar.Caesar;
import cryptosystems.vigenere.Vigenere;

public class Main
{
    public static void main(String[] args) {
        try {
            System.out.println(Caesar.decrypt(Caesar.encrypt("Thiswillbecaesared", 3), 3));
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        }

        try {
            System.out.println(Vigenere.decrypt(Vigenere.encrypt("ATTACK AT DAWN", "LEMON"), "LEMON"));
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        }
    }
}
