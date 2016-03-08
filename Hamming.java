
public class Hamming
{
    public static void main(String[] args)
    {
        int msg = 0b011011001111;

        int errorBit = errorCheck(msg, 12);
        if (errorBit == 0)
            System.out.println("Data is fine");
        else if (isPowerOfTwo(errorBit))
            System.out.println("Check bit corrupted (bit number " + errorBit + "). Data is fine");
        else
            System.out.println("Data was corrupted (bit number " + errorBit + ")");

    }

    public static boolean isPowerOfTwo(int num)
    {
        return (num > 0) && ((num & (num - 1)) == 0);
    }

    /**
    *   Generates a single-error correcting code for a 32bit (sizeof(int)) message
    *   encoded as a Hamming code
    *   @param  message The message to be checked
    *   @return         The position of the bit in error (numbering from 1 instead of 0, left-to-right) 
    */
    public static int errorCheck(int message, int msgLength)
    {
        int errorBit    = 0;
        int length      = msgLength;
        int temp;

        for (int i = 1; i <= length; i *= 2) {
            temp = 0;

            for (int j = i; j <= length; ++j) {
                if ((j & i) != 0) {
                    temp ^= (message >>> (length - j)) & 1;
                    // System.out.println(((message >>> (length - j)) & 1) + " " + j);
                }
            }
            // System.out.println();

            if(temp != 0) {
                // System.out.println("Check bit " + i + " is wrong");
                errorBit += i;
            }
        }

        return errorBit;
    }
}