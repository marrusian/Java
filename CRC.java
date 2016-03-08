
public class CRC
{
    public static int polynomialGrade(long pol)
    {
        int grade = 0;
        // Ignore the polynomial's constant term
        pol >>= 1;

        while (pol != 0) {
            ++grade;
            pol >>>= 1;
        }

        return grade;
    }

    public static int getCRC(int message, int generator)
    {
        // Quotient Bitmask
        int quotientMask  = 0xFFFFFFFF << polynomialGrade(generator);

        // Left pad the message with "polyomialGrade(generator)"-zeroes
        int paddedMessage = message << polynomialGrade(generator);

        // Get the padded message grade
        int paddedMsgGrade = polynomialGrade(paddedMessage);

        // Initially, the remainder is the message itself
        int rem = paddedMessage;

        for (int i = 0; (rem & quotientMask) != 0; ++i) {
            if ((rem >> (paddedMsgGrade-i)) != 1) 
                continue;

            int tempGen = generator;
            while ((tempGen >> (paddedMsgGrade-i)) != 1)
                tempGen <<= 1;

            rem ^= tempGen;
        }

        return rem;
    }

    public static int appendCRC(int message, int generator, int crc)
    {
    	return ((message << polynomialGrade(generator)) | crc);
    }

    public static void main(String[] args)
    {
        int crc = getCRC(0b1110_0101, 0b0001_1011);
        System.out.println("Remainder (in binary): " + Integer.toBinaryString(crc));
        int newMessage = appendCRC(0b1110_0101, 0b0001_1011, crc);
        System.out.println("New remainder (in binary): " + Integer.toBinaryString(getCRC(newMessage, 0b0001_1011)));
    }
}