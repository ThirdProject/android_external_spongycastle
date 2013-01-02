package java.math;

import java.util.Random;
import java.util.Stack;

import org.bouncycastle.util.Arrays;

public class BigInteger
{
    // The first few odd primes
    /*
            3   5   7   11  13  17  19  23  29
        31  37  41  43  47  53  59  61  67  71
        73  79  83  89  97  101 103 107 109 113
        127 131 137 139 149 151 157 163 167 173
        179 181 191 193 197 199 211 223 227 229
        233 239 241 251 257 263 269 271 277 281
        283 293 307 311 313 317 331 337 347 349
        353 359 367 373 379 383 389 397 401 409
        419 421 431 433 439 443 449 457 461 463
        467 479 487 491 499 503 509 521 523 541
        547 557 563 569 571 577 587 593 599 601
        607 613 617 619 631 641 643 647 653 659
        661 673 677 683 691 701 709 719 727 733
        739 743 751 757 761 769 773 787 797 809
        811 821 823 827 829 839 853 857 859 863
        877 881 883 887 907 911 919 929 937 941
        947 953 967 971 977 983 991 997 1009
        1013 1019 1021 1031 1033 1039 1049 1051
        1061 1063 1069 1087 1091 1093 1097 1103
        1109 1117 1123 1129 1151 1153 1163 1171
        1181 1187 1193 1201 1213 1217 1223 1229
        1231 1237 1249 1259 1277 1279 1283 1289
    */

    // Each list has a product < 2^31
    private static final int[][] primeLists = new int[][]
    {
        new int[]{ 3, 5, 7, 11, 13, 17, 19, 23 },
        new int[]{ 29, 31, 37, 41, 43 },
        new int[]{ 47, 53, 59, 61, 67 },
        new int[]{ 71, 73, 79, 83 },
        new int[]{ 89, 97, 101, 103 },

        new int[]{ 107, 109, 113, 127 },
        new int[]{ 131, 137, 139, 149 },
        new int[]{ 151, 157, 163, 167 },
        new int[]{ 173, 179, 181, 191 },
        new int[]{ 193, 197, 199, 211 },

        new int[]{ 223, 227, 229 },
        new int[]{ 233, 239, 241 },
        new int[]{ 251, 257, 263 },
        new int[]{ 269, 271, 277 },
        new int[]{ 281, 283, 293 },

        new int[]{ 307, 311, 313 },
        new int[]{ 317, 331, 337 },
        new int[]{ 347, 349, 353 },
        new int[]{ 359, 367, 373 },
        new int[]{ 379, 383, 389 },

        new int[]{ 397, 401, 409 },
        new int[]{ 419, 421, 431 },
        new int[]{ 433, 439, 443 },
        new int[]{ 449, 457, 461 },
        new int[]{ 463, 467, 479 },

        new int[]{ 487, 491, 499 },
        new int[]{ 503, 509, 521 },
        new int[]{ 523, 541, 547 },
        new int[]{ 557, 563, 569 },
        new int[]{ 571, 577, 587 },

        new int[]{ 593, 599, 601 },
        new int[]{ 607, 613, 617 },
        new int[]{ 619, 631, 641 },
        new int[]{ 643, 647, 653 },
        new int[]{ 659, 661, 673 },

        new int[]{ 677, 683, 691 },
        new int[]{ 701, 709, 719 },
        new int[]{ 727, 733, 739 },
        new int[]{ 743, 751, 757 },
        new int[]{ 761, 769, 773 },

        new int[]{ 787, 797, 809 },
        new int[]{ 811, 821, 823 },
        new int[]{ 827, 829, 839 },
        new int[]{ 853, 857, 859 },
        new int[]{ 863, 877, 881 },

        new int[]{ 883, 887, 907 },
        new int[]{ 911, 919, 929 },
        new int[]{ 937, 941, 947 },
        new int[]{ 953, 967, 971 },
        new int[]{ 977, 983, 991 },

        new int[]{ 997, 1009, 1013 },
        new int[]{ 1019, 1021, 1031 },
        new int[]{ 1033, 1039, 1049 },
        new int[]{ 1051, 1061, 1063 },
        new int[]{ 1069, 1087, 1091 },

        new int[]{ 1093, 1097, 1103 },
        new int[]{ 1109, 1117, 1123 },
        new int[]{ 1129, 1151, 1153 },
        new int[]{ 1163, 1171, 1181 },
        new int[]{ 1187, 1193, 1201 },

        new int[]{ 1213, 1217, 1223 },
        new int[]{ 1229, 1231, 1237 },
        new int[]{ 1249, 1259, 1277 },
        new int[]{ 1279, 1283, 1289 },
    };

    private static int[] primeProducts;

    private static final long IMASK = 0xffffffffL;

    private static final int[] ZERO_MAGNITUDE = new int[0];

    private static final BigInteger[] SMALL_CONSTANTS = new BigInteger[17];
    public static final BigInteger ZERO;
    public static final BigInteger ONE;
    public static final BigInteger TWO;
    public static final BigInteger THREE;
    public static final BigInteger TEN;

    static
    {
        ZERO = new BigInteger(0, ZERO_MAGNITUDE);
        ZERO.nBits = 0; ZERO.nBitLength = 0;

        SMALL_CONSTANTS[0] = ZERO;
        int numBits = 0;
        for (int i = 1; i < SMALL_CONSTANTS.length; ++i)
        {
            SMALL_CONSTANTS[i] = createValueOf(i);

            // Check for a power of two
            if ((i & -i) == i)
            {
                SMALL_CONSTANTS[i].nBits = 1;
                ++numBits;
            }

            SMALL_CONSTANTS[i].nBitLength = numBits;
        }

        ONE = SMALL_CONSTANTS[1];
        TWO = SMALL_CONSTANTS[2];
        THREE = SMALL_CONSTANTS[3];
        TEN = SMALL_CONSTANTS[10];

        primeProducts = new int[primeLists.length];

        for (int i = 0; i < primeLists.length; ++i)
        {
            int[] primeList = primeLists[i];
            int product = 1;
            for (int j = 0; j < primeList.length; ++j)
            {
                product *= primeList[j];
            }
            primeProducts[i] = product;
        }
    }
    
    private int sign; // -1 means -ve; +1 means +ve; 0 means 0;
    private int[] magnitude; // array of ints with [0] being the most significant
    private int nBits = -1; // cache bitCount() value
    private int nBitLength = -1; // cache bitLength() value
    private int mQuote = 0; // -m^(-1) mod b, b = 2^32 (see Montgomery mult.), 0 when uninitialised

    private BigInteger()
    {
    }

    private BigInteger(int signum, int[] mag)
    {
        if (mag.length > 0)
        {
            sign = signum;

            int i = 0;
            while (i < mag.length && mag[i] == 0)
            {
                i++;
            }
            if (i == 0)
            {
                magnitude = mag;
            }
            else
            {
                // strip leading 0 bytes
                int[] newMag = new int[mag.length - i];
                System.arraycopy(mag, i, newMag, 0, newMag.length);
                magnitude = newMag;
                if (newMag.length == 0)
                    sign = 0;
            }
        }
        else
        {
            magnitude = mag;
            sign = 0;
        }
    }

    public BigInteger(String sval) throws NumberFormatException
    {
        this(sval, 10);
    }

    public BigInteger(String sval, int rdx) throws NumberFormatException
    {
        if (sval.length() == 0)
        {
            throw new NumberFormatException("Zero length BigInteger");
        }

        if (rdx < Character.MIN_RADIX || rdx > Character.MAX_RADIX)
        {
            throw new NumberFormatException("Radix out of range");
        }

        int index = 0;
        sign = 1;

        if (sval.charAt(0) == '-')
        {
            if (sval.length() == 1)
            {
                throw new NumberFormatException("Zero length BigInteger");
            }

            sign = -1;
            index = 1;
        }

        // strip leading zeros from the string value
        while (index < sval.length() && Character.digit(sval.charAt(index), rdx) == 0)
        {
            index++;
        }

        if (index >= sval.length())
        {
            // zero value - we're done
            sign = 0;
            magnitude = new int[0];
            return;
        }

        //////
        // could we work out the max number of ints required to store
        // sval.length digits in the given base, then allocate that
        // storage in one hit?, then generate the magnitude in one hit too?
        //////

        BigInteger b = ZERO;
        BigInteger r = valueOf(rdx);
        while (index < sval.length())
        {
            // (optimise this by taking chunks of digits instead?)
            b = b.multiply(r).add(valueOf(Character.digit(sval.charAt(index), rdx)));
            index++;
        }

        magnitude = b.magnitude;
        return;
    }

    public BigInteger(byte[] bval) throws NumberFormatException
    {
        if (bval.length == 0)
        {
            throw new NumberFormatException("Zero length BigInteger");
        }

        sign = 1;
        if (bval[0] < 0)
        {
            sign = -1;
        }
        magnitude = makeMagnitude(bval, sign);
        if (magnitude.length == 0) {
            sign = 0;
        }
    }

    /**
     * If sign >= 0, packs bytes into an array of ints, most significant first
     * If sign <  0, packs 2's complement of bytes into 
     * an array of ints, most significant first,
     * adding an extra most significant byte in case bval = {0x80, 0x00, ..., 0x00}
     *
     * @param bval
     * @param sign
     * @return
     */
    private int[] makeMagnitude(byte[] bval, int sign)
    {
        if (sign >= 0) {
            int i;
            int[] mag;
            int firstSignificant;

            // strip leading zeros
            for (firstSignificant = 0; firstSignificant < bval.length
                    && bval[firstSignificant] == 0; firstSignificant++);

            if (firstSignificant >= bval.length)
            {
                return new int[0];
            }

            int nInts = (bval.length - firstSignificant + 3) / 4;
            int bCount = (bval.length - firstSignificant) % 4;            
            if (bCount == 0)
                bCount = 4;
            // n = k * (n / k) + n % k
            // bval.length - firstSignificant + 3 = 4 * nInts + bCount - 1
            // bval.length - firstSignificant + 4 - bCount = 4 * nInts

            mag = new int[nInts];
            int v = 0;
            int magnitudeIndex = 0;
            for (i = firstSignificant; i < bval.length; i++)
            {
                // bval.length + 4 - bCount - i + 4 * magnitudeIndex = 4 * nInts
                // 1 <= bCount <= 4
                v <<= 8;
                v |= bval[i] & 0xff;
                bCount--;
                if (bCount <= 0)
                {
                    mag[magnitudeIndex] = v;
                    magnitudeIndex++;
                    bCount = 4;
                    v = 0;
                }
            }
            // 4 - bCount + 4 * magnitudeIndex = 4 * nInts
            // bCount = 4 * (1 + magnitudeIndex - nInts)
            // 1 <= bCount <= 4
            // So bCount = 4 and magnitudeIndex = nInts = mag.length

//            if (magnitudeIndex < mag.length)
//            {
//                mag[magnitudeIndex] = v;
//            }
            return mag;
        }
        else {
            int i;
            int[] mag;
            int firstSignificant;
            

            // strip leading -1's
            for (firstSignificant = 0; firstSignificant < bval.length - 1
                    && bval[firstSignificant] == 0xff; firstSignificant++);

            int nBytes = bval.length;
            boolean leadingByte = false;

            // check for -2^(n-1)
            if (bval[firstSignificant] == 0x80) {
                for (i = firstSignificant + 1; i < bval.length; i++) {
                    if (bval[i] != 0) {
                        break;
                    }
                }
                if (i == bval.length) {
                    nBytes++;
                    leadingByte = true;
                }
            }

            int nInts = (nBytes - firstSignificant + 3) / 4;
            int bCount = (nBytes - firstSignificant) % 4;
            if (bCount == 0)
                bCount = 4;

            // n = k * (n / k) + n % k
            // nBytes - firstSignificant + 3 = 4 * nInts + bCount - 1
            // nBytes - firstSignificant + 4 - bCount = 4 * nInts
            // 1 <= bCount <= 4

            mag = new int[nInts];
            int v = 0;
            int magnitudeIndex = 0;
            // nBytes + 4 - bCount - i + 4 * magnitudeIndex = 4 * nInts
            // 1 <= bCount <= 4
            if (leadingByte) {
                // bval.length + 1 + 4 - bCount - i + 4 * magnitudeIndex = 4 * nInts
                bCount--;
                // bval.length + 1 + 4 - (bCount + 1) - i + 4 * magnitudeIndex = 4 * nInts
                // bval.length + 4 - bCount - i + 4 * magnitudeIndex = 4 * nInts
                if (bCount <= 0)
                {
                    magnitudeIndex++;
                    bCount = 4;
                }
                // bval.length + 4 - bCount - i + 4 * magnitudeIndex = 4 * nInts
                // 1 <= bCount <= 4
            }
            for (i = firstSignificant; i < bval.length; i++)
            {
                // bval.length + 4 - bCount - i + 4 * magnitudeIndex = 4 * nInts
                // 1 <= bCount <= 4
                v <<= 8;
                v |= ~bval[i] & 0xff;
                bCount--;
                if (bCount <= 0)
                {
                    mag[magnitudeIndex] = v;
                    magnitudeIndex++;
                    bCount = 4;
                    v = 0;
                }
            }
            // 4 - bCount + 4 * magnitudeIndex = 4 * nInts
            // 1 <= bCount <= 4
            // bCount = 4 * (1 + magnitudeIndex - nInts)
            // 1 <= bCount <= 4
            // So bCount = 4 and magnitudeIndex = nInts = mag.length

//            if (magnitudeIndex < mag.length)
//            {
//                mag[magnitudeIndex] = v;
//            }
            mag = inc(mag);

            // TODO Fix above so that this is not necessary?
            if (mag[0] == 0)
            {
                int[] tmp = new int[mag.length - 1];
                System.arraycopy(mag, 1, tmp, 0, tmp.length);
                mag = tmp;
            }

            return mag;
        }
    }
    
    

    public BigInteger(int sign, byte[] mag) throws NumberFormatException
    {
        if (sign < -1 || sign > 1)
        {
            throw new NumberFormatException("Invalid sign value");
        }

        if (sign == 0)
        {
            this.sign = 0;
            this.magnitude = new int[0];
            return;
        }

        // copy bytes
        this.magnitude = makeMagnitude(mag, 1);
        this.sign = sign;
    }

    public BigInteger(int numBits, Random rnd) throws IllegalArgumentException
    {
        if (numBits < 0)
        {
            throw new IllegalArgumentException("numBits must be non-negative");
        }

        this.nBits = -1;
        this.nBitLength = -1;

        if (numBits == 0)
        {
//          this.sign = 0;
            this.magnitude = ZERO_MAGNITUDE;
            return;
        }

        int nBytes = (numBits + 7) / 8;

        byte[] b = new byte[nBytes];
        nextRndBytes(rnd, b);

        // strip off any excess bits in the MSB
        int xBits = BITS_PER_BYTE * nBytes - numBits;
        b[0] &= (byte)(255 >>> xBits);

        this.magnitude = makeMagnitude(b, 1);
        this.sign = this.magnitude.length < 1 ? 0 : 1;
    }

    private static final int BITS_PER_BYTE = 8;
    private static final int BYTES_PER_INT = 4;

    /**
     * strictly speaking this is a little dodgey from a compliance
     * point of view as it forces people to be using SecureRandom as
     * well, that being said - this implementation is for a crypto
     * library and you do have the source!
     */
    private void nextRndBytes(Random rnd, byte[] bytes)
    {
        int numRequested = bytes.length;
        int numGot = 0, 
        r = 0;

        if (rnd instanceof java.security.SecureRandom)
        {
            ((java.security.SecureRandom)rnd).nextBytes(bytes);
        }
        else
        {
            for (; ; )
            {
                for (int i = 0; i < BYTES_PER_INT; i++)
                {
                    if (numGot == numRequested)
                    {
                        return;
                    }

                    r = (i == 0 ? rnd.nextInt() : r >> BITS_PER_BYTE);
                    bytes[numGot++] = (byte)r;
                }
            }
        }
    }

    public BigInteger(int bitLength, int certainty, Random rnd) throws ArithmeticException
    {
        if (bitLength < 2)
        {
            throw new ArithmeticException("bitLength < 2");
        }

        this.sign = 1;
        this.nBitLength = bitLength;

        if (bitLength == 2)
        {
            this.magnitude = rnd.nextInt() < 0
                ?   TWO.magnitude
                :   THREE.magnitude;
            return;
        }

        int nBytes = (bitLength + 7) / BITS_PER_BYTE;
        int xBits = BITS_PER_BYTE * nBytes - bitLength;
        byte mask = (byte)(255 >>> xBits);

        byte[] b = new byte[nBytes];

        for (;;)
        {
            nextRndBytes(rnd, b);

            // strip off any excess bits in the MSB
            b[0] &= mask;

            // ensure the leading bit is 1 (to meet the strength requirement)
            b[0] |= (byte)(1 << (7 - xBits));

            // ensure the trailing bit is 1 (i.e. must be odd)
            b[nBytes - 1] |= (byte)1;

            this.magnitude = makeMagnitude(b, 1);
            this.nBits = -1;
            this.mQuote = 0;

            if (certainty < 1)
                break;

            if (this.isProbablePrime(certainty))
                break;

            if (bitLength > 32)
            {
                for (int rep = 0; rep < 10000; ++rep)
                {
                    int n = 33 + (rnd.nextInt() >>> 1) % (bitLength - 2);
                    this.magnitude[this.magnitude.length - (n >>> 5)] ^= (1 << (n & 31));
                    this.magnitude[this.magnitude.length - 1] ^= (rnd.nextInt() << 1);
                    this.mQuote = 0;

                    if (this.isProbablePrime(certainty))
                        return;
                }
            }
        }
    }

    public BigInteger abs()
    {
        return (sign >= 0) ? this : this.negate();
    }

    /**
     * return a = a + b - b preserved.
     */
    private int[] add(int[] a, int[] b)
    {
        int tI = a.length - 1;
        int vI = b.length - 1;
        long m = 0;

        while (vI >= 0)
        {
            m += (((long)a[tI]) & IMASK) + (((long)b[vI--]) & IMASK);
            a[tI--] = (int)m;
            m >>>= 32;
        }

        while (tI >= 0 && m != 0)
        {
            m += (((long)a[tI]) & IMASK);
            a[tI--] = (int)m;
            m >>>= 32;
        }

        return a;
    }

    /**
     * return a = a + 1.
     */
    private int[] inc(int[] a)
    {
        int tI = a.length - 1;
        long m = 0;

        m = (((long)a[tI]) & IMASK) + 1L;
        a[tI--] = (int)m;
        m >>>= 32;

        while (tI >= 0 && m != 0)
        {
            m += (((long)a[tI]) & IMASK);
            a[tI--] = (int)m;
            m >>>= 32;
        }

        return a;
    }

    public BigInteger add(BigInteger val) throws ArithmeticException
    {
        if (val.sign == 0 || val.magnitude.length == 0)
            return this;
        if (this.sign == 0 || this.magnitude.length == 0)
            return val;

        if (val.sign < 0)
        {
            if (this.sign > 0)
                return this.subtract(val.negate());
        }
        else
        {
            if (this.sign < 0)
                return val.subtract(this.negate());
        }

        return addToMagnitude(val.magnitude);
    }

    private BigInteger addToMagnitude(
        int[] magToAdd)
    {
        int[] big, small;
        if (this.magnitude.length < magToAdd.length)
        {
            big = magToAdd;
            small = this.magnitude;
        }
        else
        {
            big = this.magnitude;
            small = magToAdd;
        }

        // Conservatively avoid over-allocation when no overflow possible
        int limit = Integer.MAX_VALUE;
        if (big.length == small.length)
            limit -= small[0];

        boolean possibleOverflow = (big[0] ^ (1 << 31)) >= limit;
        int extra = possibleOverflow ? 1 : 0;

        int[] bigCopy = new int[big.length + extra];
        System.arraycopy(big, 0, bigCopy, extra, big.length);

        bigCopy = add(bigCopy, small);

        return new BigInteger(this.sign, bigCopy);
    }

    public BigInteger and(
        BigInteger value)
    {
        if (this.sign == 0 || value.sign == 0)
        {
            return ZERO;
        }

        int[] aMag = this.sign > 0
            ? this.magnitude
            : add(ONE).magnitude;

        int[] bMag = value.sign > 0
            ? value.magnitude
            : value.add(ONE).magnitude;

        boolean resultNeg = sign < 0 && value.sign < 0;
        int resultLength = Math.max(aMag.length, bMag.length);
        int[] resultMag = new int[resultLength];

        int aStart = resultMag.length - aMag.length;
        int bStart = resultMag.length - bMag.length;

        for (int i = 0; i < resultMag.length; ++i)
        {
            int aWord = i >= aStart ? aMag[i - aStart] : 0;
            int bWord = i >= bStart ? bMag[i - bStart] : 0;

            if (this.sign < 0)
            {
                aWord = ~aWord;
            }

            if (value.sign < 0)
            {
                bWord = ~bWord;
            }

            resultMag[i] = aWord & bWord;

            if (resultNeg)
            {
                resultMag[i] = ~resultMag[i];
            }
        }

        BigInteger result = new BigInteger(1, resultMag);

        // TODO Optimise this case
        if (resultNeg)
        {
            result = result.not();
        }

        return result;
    }

    public BigInteger andNot(
        BigInteger value)
    {
        return and(value.not());
    }

    public int bitCount()
    {
        if (nBits == -1)
        {
            if (sign < 0)
            {
                // TODO Optimise this case
                nBits = not().bitCount();
            }
            else
            {
                int sum = 0;
                for (int i = 0; i < magnitude.length; i++)
                {
                    sum += bitCounts[magnitude[i] & 0xff];
                    sum += bitCounts[(magnitude[i] >> 8) & 0xff];
                    sum += bitCounts[(magnitude[i] >> 16) & 0xff];
                    sum += bitCounts[(magnitude[i] >> 24) & 0xff];
                }
                nBits = sum;
            }
        }

        return nBits;
    }

    private final static byte[] bitCounts = {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1,
        2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4,
        4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3,
        4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5,
        3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2,
        3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3,
        3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6,
        7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6,
        5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5,
        6, 6, 7, 6, 7, 7, 8};

    private int bitLength(int indx, int[] mag)
    {
        int bitLength;

        if (mag.length == 0)
        {
            return 0;
        }
        else
        {
            while (indx != mag.length && mag[indx] == 0)
            {
                indx++;
            }

            if (indx == mag.length)
            {
                return 0;
            }

            // bit length for everything after the first int
            bitLength = 32 * ((mag.length - indx) - 1);

            // and determine bitlength of first int
            bitLength += bitLen(mag[indx]);

            if (sign < 0)
            {
                // Check if magnitude is a power of two
                boolean pow2 = ((bitCounts[mag[indx] & 0xff])
                        + (bitCounts[(mag[indx] >> 8) & 0xff])
                        + (bitCounts[(mag[indx] >> 16) & 0xff])
                        + (bitCounts[(mag[indx] >> 24) & 0xff])) == 1;

                for (int i = indx + 1; i < mag.length && pow2; i++)
                {
                    pow2 = (mag[i] == 0);
                }

                bitLength -= (pow2 ? 1 : 0);
            }
        }

        return bitLength;
    }

    public int bitLength()
    {
        if (nBitLength == -1)
        {
            if (sign == 0)
            {
                nBitLength = 0;
            }
            else
            {
                nBitLength = bitLength(0, magnitude);
            }
        }

        return nBitLength;
    }

    //
    // bitLen(val) is the number of bits in val.
    //
    static int bitLen(int w)
    {
        // Binary search - decision tree (5 tests, rarely 6)
        return (w < 1 << 15 ? (w < 1 << 7
                ? (w < 1 << 3 ? (w < 1 << 1
                        ? (w < 1 << 0 ? (w < 0 ? 32 : 0) : 1)
                        : (w < 1 << 2 ? 2 : 3)) : (w < 1 << 5
                        ? (w < 1 << 4 ? 4 : 5)
                        : (w < 1 << 6 ? 6 : 7)))
                : (w < 1 << 11
                        ? (w < 1 << 9 ? (w < 1 << 8 ? 8 : 9) : (w < 1 << 10 ? 10 : 11))
                        : (w < 1 << 13 ? (w < 1 << 12 ? 12 : 13) : (w < 1 << 14 ? 14 : 15)))) : (w < 1 << 23 ? (w < 1 << 19
                ? (w < 1 << 17 ? (w < 1 << 16 ? 16 : 17) : (w < 1 << 18 ? 18 : 19))
                : (w < 1 << 21 ? (w < 1 << 20 ? 20 : 21) : (w < 1 << 22 ? 22 : 23))) : (w < 1 << 27
                ? (w < 1 << 25 ? (w < 1 << 24 ? 24 : 25) : (w < 1 << 26 ? 26 : 27))
                : (w < 1 << 29 ? (w < 1 << 28 ? 28 : 29) : (w < 1 << 30 ? 30 : 31)))));
    }

    private boolean quickPow2Check()
    {
        return sign > 0 && nBits == 1;
    }

    public int compareTo(Object o)
    {
        return compareTo((BigInteger)o);
    }

    /**
     * unsigned comparison on two arrays - note the arrays may
     * start with leading zeros.
     */
    private int compareTo(int xIndx, int[] x, int yIndx, int[] y)
    {
        while (xIndx != x.length && x[xIndx] == 0)
        {
            xIndx++;
        }

        while (yIndx != y.length && y[yIndx] == 0)
        {
            yIndx++;
        }

        return compareNoLeadingZeroes(xIndx, x, yIndx, y);
    }

    private int compareNoLeadingZeroes(int xIndx, int[] x, int yIndx, int[] y)
    {
        int diff = (x.length - y.length) - (xIndx - yIndx);

        if (diff != 0)
        {
            return diff < 0 ? -1 : 1;
        }

        // lengths of magnitudes the same, test the magnitude values

        while (xIndx < x.length)
        {
            int v1 = x[xIndx++];
            int v2 = y[yIndx++];

            if (v1 != v2)
            {
                return (v1 ^ Integer.MIN_VALUE) < (v2 ^ Integer.MIN_VALUE) ? -1 : 1;
            }
        }

        return 0;
    }

    public int compareTo(BigInteger val)
    {
        if (sign < val.sign)
            return -1;
        if (sign > val.sign)
            return 1;
        if (sign == 0)
            return 0;

        return sign * compareTo(0, magnitude, 0, val.magnitude);
    }

    /**
     * return z = x / y - done in place (z value preserved, x contains the
     * remainder)
     */
    private int[] divide(int[] x, int[] y)
    {
        int xyCmp = compareTo(0, x, 0, y);
        int[] count;

        if (xyCmp > 0)
        {
            int[] c;

            int shift = bitLength(0, x) - bitLength(0, y);

            if (shift > 1)
            {
                c = shiftLeft(y, shift - 1);
                count = shiftLeft(ONE.magnitude, shift - 1);
                if (shift % 32 == 0)
                {
                    // Special case where the shift is the size of an int.
                    int countSpecial[] = new int[shift / 32 + 1];
                    System.arraycopy(count, 0, countSpecial, 1, countSpecial.length - 1);
                    countSpecial[0] = 0;
                    count = countSpecial;
                }
            }
            else
            {
                c = new int[x.length];
                count = new int[1];

                System.arraycopy(y, 0, c, c.length - y.length, y.length);
                count[0] = 1;
            }

            int[] iCount = new int[count.length];

            subtract(0, x, 0, c);
            System.arraycopy(count, 0, iCount, 0, count.length);

            int xStart = 0;
            int cStart = 0;
            int iCountStart = 0;

            for (; ; )
            {
                int cmp = compareTo(xStart, x, cStart, c);

                while (cmp >= 0)
                {
                    subtract(xStart, x, cStart, c);
                    add(count, iCount);
                    cmp = compareTo(xStart, x, cStart, c);
                }

                xyCmp = compareTo(xStart, x, 0, y);

                if (xyCmp > 0)
                {
                    if (x[xStart] == 0)
                    {
                        xStart++;
                    }

                    shift = bitLength(cStart, c) - bitLength(xStart, x);

                    if (shift == 0)
                    {
                        shiftRightOneInPlace(cStart, c);
                        shiftRightOneInPlace(iCountStart, iCount);
                    }
                    else
                    {
                        shiftRightInPlace(cStart, c, shift);
                        shiftRightInPlace(iCountStart, iCount, shift);
                    }

                    if (c[cStart] == 0)
                    {
                        cStart++;
                    }

                    if (iCount[iCountStart] == 0)
                    {
                        iCountStart++;
                    }
                }
                else if (xyCmp == 0)
                {
                    add(count, ONE.magnitude);
                    for (int i = xStart; i != x.length; i++)
                    {
                        x[i] = 0;
                    }
                    break;
                }
                else
                {
                    break;
                }
            }
        }
        else if (xyCmp == 0)
        {
            count = new int[1];
            count[0] = 1;
            Arrays.fill(x, 0);
        }
        else
        {
            count = new int[1];
            count[0] = 0;
        }

        return count;
    }

    public BigInteger divide(BigInteger val) throws ArithmeticException
    {
        if (val.sign == 0)
        {
            throw new ArithmeticException("Divide by zero");
        }

        if (sign == 0)
        {
            return BigInteger.ZERO;
        }

        if (val.compareTo(BigInteger.ONE) == 0)
        {
            return this;
        }

        int[] mag = new int[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, mag, 0, mag.length);

        return new BigInteger(this.sign * val.sign, divide(mag, val.magnitude));
    }

    public BigInteger[] divideAndRemainder(BigInteger val) throws ArithmeticException
    {
        if (val.sign == 0)
        {
            throw new ArithmeticException("Divide by zero");
        }

        BigInteger biggies[] = new BigInteger[2];

        if (sign == 0)
        {
            biggies[0] = biggies[1] = BigInteger.ZERO;

            return biggies;
        }

        if (val.compareTo(BigInteger.ONE) == 0)
        {
            biggies[0] = this;
            biggies[1] = BigInteger.ZERO;

            return biggies;
        }

        int[] remainder = new int[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, remainder, 0, remainder.length);

        int[] quotient = divide(remainder, val.magnitude);

        biggies[0] = new BigInteger(this.sign * val.sign, quotient);
        biggies[1] = new BigInteger(this.sign, remainder);

        return biggies;
    }

    public boolean equals(Object val)
    {
        if (val == this)
            return true;

        if (!(val instanceof BigInteger))
            return false;
        BigInteger biggie = (BigInteger)val;

        if (biggie.sign != sign || biggie.magnitude.length != magnitude.length)
            return false;

        for (int i = 0; i < magnitude.length; i++)
        {
            if (biggie.magnitude[i] != magnitude[i])
                return false;
        }

        return true;
    }

    public BigInteger gcd(BigInteger val)
    {
        if (val.sign == 0)
            return this.abs();
        else if (sign == 0)
            return val.abs();

        BigInteger r;
        BigInteger u = this;
        BigInteger v = val;

        while (v.sign != 0)
        {
            r = u.mod(v);
            u = v;
            v = r;
        }

        return u;
    }

    public int hashCode()
    {
        int hc = magnitude.length;

        if (magnitude.length > 0)
        {
            hc ^= magnitude[0];

            if (magnitude.length > 1)
            {
                hc ^= magnitude[magnitude.length - 1];
            }
        }

        return sign < 0 ? ~hc : hc;
    }

    public int intValue()
    {
        if (magnitude.length == 0)
        {
            return 0;
        }

        if (sign < 0)
        {
            return -magnitude[magnitude.length - 1];
        }
        else
        {
            return magnitude[magnitude.length - 1];
        }
    }
    
    public byte byteValue()
    {
        return (byte)intValue();
    }

    /**
     * return whether or not a BigInteger is probably prime with a
     * probability of 1 - (1/2)**certainty.
     * <p>
     * From Knuth Vol 2, pg 395.
     */
    public boolean isProbablePrime(int certainty)
    {
        if (certainty <= 0)
            return true;

        if (sign == 0)
            return false;

        BigInteger n = this.abs();

        if (!n.testBit(0))
            return n.equals(TWO);

        if (n.equals(ONE))
            return false;

        // Try to reduce the penalty for really small numbers
        int numLists = Math.min(n.bitLength() - 1, primeLists.length);

        for (int i = 0; i < numLists; ++i)
        {
            int test = n.remainder(primeProducts[i]);

            int[] primeList = primeLists[i];
            for (int j = 0; j < primeList.length; ++j)
            {
                int prime = primeList[j];
                int qRem = test % prime;
                if (qRem == 0)
                {
                    // We may find small numbers in the list
                    return n.bitLength() < 16 && n.intValue() == prime;
                }
            }
        }

        //
        // let n = 1 + 2^kq
        //
        BigInteger nMinusOne = n.subtract(ONE);
        int s = nMinusOne.getLowestSetBit();
        BigInteger r = nMinusOne.shiftRight(s);

        Random random = new Random();
        do
        {
            BigInteger a;

            do
            {
                a = new BigInteger(n.bitLength(), random);
            }
            while (a.compareTo(ONE) <= 0 || a.compareTo(nMinusOne) >= 0);

            BigInteger y = a.modPow(r, n);

            if (!y.equals(ONE))
            {
                int j = 0;
                while (!y.equals(nMinusOne))
                {
                    if (++j == s)
                    {
                        return false;
                    }

                    y = y.modPow(TWO, n);

                    if (y.equals(ONE))
                    {
                        return false;
                    }
                }
            }

            certainty -= 2; // composites pass for only 1/4 possible 'a'
        }
        while (certainty > 0);

        return true;
    }

    public long longValue()
    {
        long val = 0;

        if (magnitude.length == 0)
        {
            return 0;
        }

        if (magnitude.length > 1)
        {
            val = ((long)magnitude[magnitude.length - 2] << 32)
                    | (magnitude[magnitude.length - 1] & IMASK);
        }
        else
        {
            val = (magnitude[magnitude.length - 1] & IMASK);
        }

        if (sign < 0)
        {
            return -val;
        }
        else
        {
            return val;
        }
    }

    public BigInteger max(BigInteger val)
    {
        return (compareTo(val) > 0) ? this : val;
    }

    public BigInteger min(BigInteger val)
    {
        return (compareTo(val) < 0) ? this : val;
    }

    public BigInteger mod(BigInteger m) throws ArithmeticException
    {
        if (m.sign <= 0)
        {
            throw new ArithmeticException("BigInteger: modulus is not positive");
        }

        BigInteger biggie = this.remainder(m);

        return (biggie.sign >= 0 ? biggie : biggie.add(m));
    }

    public BigInteger modInverse(BigInteger m) throws ArithmeticException
    {
        if (m.sign < 1)
        {
            throw new ArithmeticException("Modulus must be positive");
        }

        if (m.quickPow2Check())
        {
            return modInversePow2(m);
        }

        BigInteger d = this.remainder(m);
        BigInteger x = new BigInteger();
        BigInteger gcd = BigInteger.extEuclid(d, m, x, null);

        if (!gcd.equals(BigInteger.ONE))
        {
            throw new ArithmeticException("Numbers not relatively prime.");
        }

        if (x.compareTo(BigInteger.ZERO) < 0)
        {
            x = x.add(m);
        }

        return x;
    }

    private BigInteger modInversePow2(BigInteger m)
    {
//        assert m.signum() > 0;
//        assert m.bitCount() == 1;

        if (!testBit(0))
        {
            throw new ArithmeticException("Numbers not relatively prime.");
        }

        int pow = m.bitLength() - 1;

        if (pow <= 64)
        {
            long inv = modInverse64(longValue());
            if (pow < 64)
            {
                inv &= (m.longValue() - 1);
            }
            return BigInteger.valueOf(inv);
        }

        BigInteger d = this.remainder(m);
        BigInteger x = d;
        int bitsCorrect = 3;

        while (bitsCorrect < pow)
        {
            BigInteger t = x.multiply(d).remainder(m);
            x = x.multiply(TWO.subtract(t)).remainder(m);
            bitsCorrect <<= 1;
        }

        if (x.sign < 0)
        {
            x = x.add(m);
        }

        return x;
    }

    private int modInverse32(int d)
    {
        // Newton-Raphson division (roughly)
        int x = d;        // d.x == 1 mod 2**3
        x *= 2 - d * x;   // d.x == 1 mod 2**6
        x *= 2 - d * x;   // d.x == 1 mod 2**12
        x *= 2 - d * x;   // d.x == 1 mod 2**24
        x *= 2 - d * x;   // d.x == 1 mod 2**48
//        assert d * x == 1;
        return  x;
    }

    private long modInverse64(long d)
    {
        // Newton-Raphson division (roughly)
        long x = d;       // d.x == 1 mod 2**3
        x *= 2 - d * x;   // d.x == 1 mod 2**6
        x *= 2 - d * x;   // d.x == 1 mod 2**12
        x *= 2 - d * x;   // d.x == 1 mod 2**24
        x *= 2 - d * x;   // d.x == 1 mod 2**48
        x *= 2 - d * x;   // d.x == 1 mod 2**96
//        assert d * x == 1L;
        return  x;
    }

    /**
     * Calculate the numbers u1, u2, and u3 such that:
     *
     * u1 * a + u2 * b = u3
     *
     * where u3 is the greatest common divider of a and b.
     * a and b using the extended Euclid algorithm (refer p. 323
     * of The Art of Computer Programming vol 2, 2nd ed).
     * This also seems to have the side effect of calculating
     * some form of multiplicative inverse.
     *
     * @param a    First number to calculate gcd for
     * @param b    Second number to calculate gcd for
     * @param u1Out      the return object for the u1 value
     * @param u2Out      the return object for the u2 value
     * @return     The greatest common divisor of a and b
     */
    private static BigInteger extEuclid(BigInteger a, BigInteger b, BigInteger u1Out,
            BigInteger u2Out)
    {
        BigInteger u1 = BigInteger.ONE;
        BigInteger u3 = a;
        BigInteger v1 = BigInteger.ZERO;
        BigInteger v3 = b;

        while (v3.sign > 0)
        {
            BigInteger[] q = u3.divideAndRemainder(v3);

            BigInteger tn = u1.subtract(v1.multiply(q[0]));
            u1 = v1;
            v1 = tn;

            u3 = v3;
            v3 = q[1];
        }

        if (u1Out != null)
        {
            u1Out.sign = u1.sign;
            u1Out.magnitude = u1.magnitude;
        }

        if (u2Out != null)
        {
            BigInteger res = u3.subtract(u1.multiply(a)).divide(b);
            u2Out.sign = res.sign;
            u2Out.magnitude = res.magnitude;
        }

        return u3;
    }

    /**
     * zero out the array x
     */
    private void zero(int[] x)
    {
        for (int i = 0; i != x.length; i++)
        {
            x[i] = 0;
        }
    }

    public BigInteger modPow(BigInteger exponent, BigInteger m) throws ArithmeticException
    {
        if (m.sign < 1)
        {
            throw new ArithmeticException("Modulus must be positive");
        }

        if (m.equals(ONE))
        {
            return ZERO;
        }

        // Zero exponent check
        if (exponent.sign == 0)
        {
            return ONE;
        }

        if (sign == 0)
            return ZERO;

        int[] zVal = null;
        int[] yAccum = null;
        int[] yVal;

        // Montgomery exponentiation is only possible if the modulus is odd,
        // but AFAIK, this is always the case for crypto algo's
        boolean useMonty = ((m.magnitude[m.magnitude.length - 1] & 1) == 1);
        int mQ = 0;
        if (useMonty)
        {
            mQ = m.getMQuote();

            // tmp = this * R mod m
            BigInteger tmp = this.shiftLeft(32 * m.magnitude.length).mod(m);
            zVal = tmp.magnitude;

            useMonty = (zVal.length <= m.magnitude.length);

            if (useMonty)
            {
                yAccum = new int[m.magnitude.length + 1];
                if (zVal.length < m.magnitude.length)
                {
                    int[] longZ = new int[m.magnitude.length];
                    System.arraycopy(zVal, 0, longZ, longZ.length - zVal.length, zVal.length);
                    zVal = longZ;  
                }
            }
        }

        if (!useMonty)
        {
            if (magnitude.length <= m.magnitude.length)
            {
                //zAccum = new int[m.magnitude.length * 2];
                zVal = new int[m.magnitude.length];

                System.arraycopy(magnitude, 0, zVal, zVal.length - magnitude.length,
                        magnitude.length);
            }
            else
            {
                //
                // in normal practice we'll never see this...
                //
                BigInteger tmp = this.remainder(m);

                //zAccum = new int[m.magnitude.length * 2];
                zVal = new int[m.magnitude.length];

                System.arraycopy(tmp.magnitude, 0, zVal, zVal.length - tmp.magnitude.length,
                        tmp.magnitude.length);
            }

            yAccum = new int[m.magnitude.length * 2];
        }

        yVal = new int[m.magnitude.length];

        //
        // from LSW to MSW
        //
        for (int i = 0; i < exponent.magnitude.length; i++)
        {
            int v = exponent.magnitude[i];
            int bits = 0;

            if (i == 0)
            {
                while (v > 0)
                {
                    v <<= 1;
                    bits++;
                }

                //
                // first time in initialise y
                //
                System.arraycopy(zVal, 0, yVal, 0, zVal.length);

                v <<= 1;
                bits++;
            }

            while (v != 0)
            {
                if (useMonty)
                {
                    // Montgomery square algo doesn't exist, and a normal
                    // square followed by a Montgomery reduction proved to
                    // be almost as heavy as a Montgomery mulitply.
                    multiplyMonty(yAccum, yVal, yVal, m.magnitude, mQ);
                }
                else
                {
                    square(yAccum, yVal);
                    remainder(yAccum, m.magnitude);
                    System.arraycopy(yAccum, yAccum.length - yVal.length, yVal, 0, yVal.length);
                    zero(yAccum);
                }
                bits++;

                if (v < 0)
                {
                    if (useMonty)
                    {
                        multiplyMonty(yAccum, yVal, zVal, m.magnitude, mQ);
                    }
                    else
                    {
                        multiply(yAccum, yVal, zVal);
                        remainder(yAccum, m.magnitude);
                        System.arraycopy(yAccum, yAccum.length - yVal.length, yVal, 0,
                                yVal.length);
                        zero(yAccum);
                    }
                }

                v <<= 1;
            }

            while (bits < 32)
            {
                if (useMonty)
                {
                    multiplyMonty(yAccum, yVal, yVal, m.magnitude, mQ);
                }
                else
                {
                    square(yAccum, yVal);
                    remainder(yAccum, m.magnitude);
                    System.arraycopy(yAccum, yAccum.length - yVal.length, yVal, 0, yVal.length);
                    zero(yAccum);
                }
                bits++;
            }
        }

        if (useMonty)
        {
            // Return y * R^(-1) mod m by doing y * 1 * R^(-1) mod m
            zero(zVal);
            zVal[zVal.length - 1] = 1;
            multiplyMonty(yAccum, yVal, zVal, m.magnitude, mQ);
        }

        BigInteger result = new BigInteger(1, yVal);

        return exponent.sign > 0
            ?   result
            :   result.modInverse(m);
    }

    /**
     * return w with w = x * x - w is assumed to have enough space.
     */
    private int[] square(int[] w, int[] x)
    {
        // Note: this method allows w to be only (2 * x.Length - 1) words if result will fit
//        if (w.length != 2 * x.length)
//        {
//            throw new IllegalArgumentException("no I don't think so...");
//        }

        long u1, u2, c;

        int wBase = w.length - 1;

        for (int i = x.length - 1; i != 0; i--)
        {
            long v = (x[i] & IMASK);

            u1 = v * v;
            u2 = u1 >>> 32;
            u1 = u1 & IMASK;

            u1 += (w[wBase] & IMASK);

            w[wBase] = (int)u1;
            c = u2 + (u1 >> 32);

            for (int j = i - 1; j >= 0; j--)
            {
                --wBase;
                u1 = (x[j] & IMASK) * v;
                u2 = u1 >>> 31; // multiply by 2!
                u1 = (u1 & 0x7fffffff) << 1; // multiply by 2!
                u1 += (w[wBase] & IMASK) + c;

                w[wBase] = (int)u1;
                c = u2 + (u1 >>> 32);
            }
            c += w[--wBase] & IMASK;
            w[wBase] = (int)c;

            if (--wBase >= 0)
            {
                w[wBase] = (int)(c >> 32);
            }
            wBase += i;
        }

        u1 = (x[0] & IMASK);
        u1 = u1 * u1;
        u2 = u1 >>> 32;
        u1 = u1 & IMASK;

        u1 += (w[wBase] & IMASK);

        w[wBase] = (int)u1;
        if (--wBase >= 0)
        {
            w[wBase] = (int)(u2 + (u1 >> 32) + w[wBase]);
        }

        return w;
    }

    /**
     * return x with x = y * z - x is assumed to have enough space.
     */
    private int[] multiply(int[] x, int[] y, int[] z)
    {
        int i = z.length;

        if (i < 1)
        {
            return x;
        }

        int xBase = x.length - y.length;

        for (;;)
        {
            long a = z[--i] & IMASK;
            long val = 0;

            for (int j = y.length - 1; j >= 0; j--)
            {
                val += a * (y[j] & IMASK) + (x[xBase + j] & IMASK);

                x[xBase + j] = (int)val;

                val >>>= 32;
            }

            --xBase;

            if (i < 1)
            {
                if (xBase >= 0)
                {
                    x[xBase] = (int)val;
                }
                break;
            }

            x[xBase] = (int)val;
        }

        return x;
    }

    /**
     * Calculate mQuote = -m^(-1) mod b with b = 2^32 (32 = word size)
     */
    private int getMQuote()
    {
        if (mQuote != 0)
        {
            return mQuote; // already calculated
        }

//        assert this.sign > 0;

        int d = -magnitude[magnitude.length - 1];

//        assert (d & 1) != 0;

        return mQuote = modInverse32(d);
    }

    /**
     * Montgomery multiplication: a = x * y * R^(-1) mod m
     * <br>
     * Based algorithm 14.36 of Handbook of Applied Cryptography.
     * <br>
     * <li> m, x, y should have length n </li>
     * <li> a should have length (n + 1) </li>
     * <li> b = 2^32, R = b^n </li>
     * <br>
     * The result is put in x
     * <br>
     * NOTE: the indices of x, y, m, a different in HAC and in Java
     */
    private void multiplyMonty(int[] a, int[] x, int[] y, int[] m, int mDash)
        // mDash = -m^(-1) mod b
    {
        int n = m.length;
        long y_0 = y[n - 1] & IMASK;

        // 1. a = 0 (Notation: a = (a_{n} a_{n-1} ... a_{0})_{b} )
        for (int i = 0; i <= n; i++)
        {
            a[i] = 0;
        }

        // 2. for i from 0 to (n - 1) do the following:
        for (int i = n; i > 0; i--)
        {
            long a0 = a[n] & IMASK;
            long x_i = x[i - 1] & IMASK;

            long prod1 = x_i * y_0;
            long carry = (prod1 & IMASK) + a0;

            // 2.1 u = ((a[0] + (x[i] * y[0]) * mDash) mod b
            long u = ((int)carry * mDash) & IMASK;

            // 2.2 a = (a + x_i * y + u * m) / b
            long prod2 = u * (m[n - 1] & IMASK);
            carry += (prod2 & IMASK);
//            assert (int)carry == 0;
            carry = (carry >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);

            for (int j = n - 2; j >= 0; j--)
            {
                prod1 = x_i * (y[j] & IMASK);
                prod2 = u * (m[j] & IMASK);

                carry += (prod1 & IMASK) + (prod2 & IMASK) + (a[j + 1] & IMASK);
                a[j + 2] = (int)carry;
                carry = (carry >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);
            }

            carry += (a[0] & IMASK);
            a[1] = (int)carry;
            a[0] = (int)(carry >>> 32);
        }

        // 3. if x >= m the x = x - m
        if (compareTo(0, a, 0, m) >= 0)
        {
            subtract(0, a, 0, m);
        }

        // put the result in x
        System.arraycopy(a, 1, x, 0, n);
    }

    public BigInteger multiply(BigInteger val)
    {
        if (sign == 0 || val.sign == 0)
            return BigInteger.ZERO;

        int resLength = (this.bitLength() + val.bitLength()) / 32 + 1;
        int[] res = new int[resLength];

        if (val == this)
        {
            square(res, this.magnitude);
        }
        else
        {
            multiply(res, this.magnitude, val.magnitude);
        }

        return new BigInteger(sign * val.sign, res);
    }

    public BigInteger negate()
    {
        if (sign == 0)
        {
            return this;
        }

        return new BigInteger( -sign, magnitude);
    }

    public BigInteger not()
    {
        return add(ONE).negate();
    }

    public BigInteger pow(int exp) throws ArithmeticException
    {
        if (exp <= 0)
        {
            if (exp < 0)
                throw new ArithmeticException("Negative exponent");

            return ONE;
        }

        if (sign == 0)
        {
            return this;
        }

        if (quickPow2Check())
        {
            long powOf2 = (long)exp * (bitLength() - 1);
            if (powOf2 > Integer.MAX_VALUE)
            {
                throw new ArithmeticException("Result too large");
            }
            return ONE.shiftLeft((int)powOf2); 
        }

        BigInteger y = BigInteger.ONE, z = this;

        while (exp != 0)
        {
            if ((exp & 0x1) == 1)
            {
                y = y.multiply(z);
            }
            exp >>= 1;
            if (exp != 0)
            {
                z = z.multiply(z);
            }
        }

        return y;
    }

    public static BigInteger probablePrime(
        int bitLength,
        Random random)
    {
        return new BigInteger(bitLength, 100, random);
    }

    private int remainder(int m)
    {
        long acc = 0;
        for (int pos = 0; pos < magnitude.length; ++pos)
        {
            acc = (acc << 32 | ((long)magnitude[pos] & 0xffffffffL)) % m;
        }

        return (int) acc;
    }
    
    /**
     * return x = x % y - done in place (y value preserved)
     */
    private int[] remainder(int[] x, int[] y)
    {
        int xStart = 0;
        while (xStart < x.length && x[xStart] == 0)
        {
            ++xStart;
        }

        int yStart = 0;
        while (yStart < y.length && y[yStart] == 0)
        {
            ++yStart;
        }

        int xyCmp = compareNoLeadingZeroes(xStart, x, yStart, y);

        if (xyCmp > 0)
        {
            int yBitLength = bitLength(yStart, y);
            int xBitLength = bitLength(xStart, x);
            int shift = xBitLength - yBitLength;

            int[] c;
            int cStart = 0;
            int cBitLength = yBitLength;
            if (shift > 0)
            {
                c = shiftLeft(y, shift);
                cBitLength += shift;
            }
            else
            {
                int len = y.length - yStart; 
                c = new int[len];
                System.arraycopy(y, yStart, c, 0, len);
            }

            for (;;)
            {
                if (cBitLength < xBitLength
                    || compareNoLeadingZeroes(xStart, x, cStart, c) >= 0)
                {
                    subtract(xStart, x, cStart, c);

                    while (x[xStart] == 0)
                    {
                        if (++xStart == x.length)
                        {
                            return x;
                        }
                    }

                    xyCmp = compareNoLeadingZeroes(xStart, x, yStart, y);

                    if (xyCmp <= 0)
                    {
                        break;
                    }

                    //xBitLength = bitLength(xStart, x);
                    xBitLength = 32 * (x.length - xStart - 1) + bitLen(x[xStart]);
                }

                shift = cBitLength - xBitLength;

                if (shift < 2)
                {
                    shiftRightOneInPlace(cStart, c);
                    --cBitLength;
                }
                else
                {
                    shiftRightInPlace(cStart, c, shift);
                    cBitLength -= shift;
                }

//              cStart = c.length - ((cBitLength + 31) / 32);
                while (c[cStart] == 0)
                {
                    ++cStart;
                }
            }
        }

        if (xyCmp == 0)
        {
            for (int i = xStart; i < x.length; ++i)
            {
                x[i] = 0;
            }
        }

        return x;
    }

    public BigInteger remainder(BigInteger n) throws ArithmeticException
    {
        if (n.sign == 0)
        {
            throw new ArithmeticException("BigInteger: Divide by zero");
        }

        if (sign == 0)
        {
            return BigInteger.ZERO;
        }

        // For small values, use fast remainder method
        if (n.magnitude.length == 1)
        {
            int val = n.magnitude[0];

            if (val > 0)
            {
                if (val == 1)
                    return ZERO;

                int rem = remainder(val);

                return rem == 0
                    ?   ZERO
                    :   new BigInteger(sign, new int[]{ rem });
            }
        }

        if (compareTo(0, magnitude, 0, n.magnitude) < 0)
            return this;

        int[] res;
        if (n.quickPow2Check())  // n is power of two
        {
            // TODO Move before small values branch above?
            res = lastNBits(n.abs().bitLength() - 1);
        }
        else
        {
            res = new int[this.magnitude.length];
            System.arraycopy(this.magnitude, 0, res, 0, res.length);
            res = remainder(res, n.magnitude);
        }

        return new BigInteger(sign, res);
    }

    private int[] lastNBits(
        int n)
    {
        if (n < 1)
        {
            return ZERO_MAGNITUDE;
        }

        int numWords = (n + 31) / 32;
        numWords = Math.min(numWords, this.magnitude.length);
        int[] result = new int[numWords];

        System.arraycopy(this.magnitude, this.magnitude.length - numWords, result, 0, numWords);

        int excessBits = (numWords << 5) - n;
        if (excessBits > 0)
        {
            result[0] &= (-1 >>> excessBits);
        }

        return result;
    }
    
    /**
     * do a left shift - this returns a new array.
     */
    private int[] shiftLeft(int[] mag, int n)
    {
        int nInts = n >>> 5;
        int nBits = n & 0x1f;
        int magLen = mag.length;
        int newMag[] = null;

        if (nBits == 0)
        {
            newMag = new int[magLen + nInts];
            System.arraycopy(mag, 0, newMag, 0, magLen);
        }
        else
        {
            int i = 0;
            int nBits2 = 32 - nBits;
            int highBits = mag[0] >>> nBits2;

            if (highBits != 0)
            {
                newMag = new int[magLen + nInts + 1];
                newMag[i++] = highBits;
            }
            else
            {
                newMag = new int[magLen + nInts];
            }

            int m = mag[0];
            for (int j = 0; j < magLen - 1; j++)
            {
                int next = mag[j + 1];

                newMag[i++] = (m << nBits) | (next >>> nBits2);
                m = next;
            }

            newMag[i] = mag[magLen - 1] << nBits;
        }

        return newMag;
    }

    public BigInteger shiftLeft(int n)
    {
        if (sign == 0 || magnitude.length == 0)
        {
            return ZERO;
        }

        if (n == 0)
        {
            return this;
        }

        if (n < 0)
        {
            return shiftRight( -n);
        }

        BigInteger result = new BigInteger(sign, shiftLeft(magnitude, n));

        if (this.nBits != -1)
        {
            result.nBits = sign > 0
                ?   this.nBits
                :   this.nBits + n;
        }

        if (this.nBitLength != -1)
        {
            result.nBitLength = this.nBitLength + n;
        }

        return result;
    }

    /**
     * do a right shift - this does it in place.
     */
    private static void shiftRightInPlace(int start, int[] mag, int n)
    {
        int nInts = (n >>> 5) + start;
        int nBits = n & 0x1f;
        int magEnd = mag.length - 1;

        if (nInts != start)
        {
            int delta = (nInts - start);

            for (int i = magEnd; i >= nInts; i--)
            {
                mag[i] = mag[i - delta];
            }
            for (int i = nInts - 1; i >= start; i--)
            {
                mag[i] = 0;
            }
        }

        if (nBits != 0)
        {
            int nBits2 = 32 - nBits;
            int m = mag[magEnd];

            for (int i = magEnd; i >= nInts + 1; i--)
            {
                int next = mag[i - 1];

                mag[i] = (m >>> nBits) | (next << nBits2);
                m = next;
            }

            mag[nInts] >>>= nBits;
        }
    }

    /**
     * do a right shift by one - this does it in place.
     */
    private static void shiftRightOneInPlace(int start, int[] mag)
    {
        int magEnd = mag.length - 1;

        int m = mag[magEnd];

        for (int i = magEnd; i > start; i--)
        {
            int next = mag[i - 1];

            mag[i] = (m >>> 1) | (next << 31);
            m = next;
        }

        mag[start] >>>= 1;
    }

    public BigInteger shiftRight(int n)
    {
        if (n == 0)
        {
            return this;
        }

        if (n < 0)
        {
            return shiftLeft( -n);
        }

        if (n >= bitLength())
        {
            return (this.sign < 0 ? valueOf( -1) : BigInteger.ZERO);
        }

        int[] res = new int[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, res, 0, res.length);
        shiftRightInPlace(0, res, n);

        return new BigInteger(this.sign, res);

        // TODO Port C# version's optimisations...
    }

    public int signum()
    {
        return sign;
    }

    /**
     * returns x = x - y - we assume x is >= y
     */
    private int[] subtract(int xStart, int[] x, int yStart, int[] y)
    {
        int iT = x.length;
        int iV = y.length;
        long m;
        int borrow = 0;

        do
        {
            m = ((long)x[--iT] & IMASK) - ((long)y[--iV] & IMASK) + borrow;
            x[iT] = (int)m;

//            borrow = (m < 0) ? -1 : 0;
            borrow = (int)(m >> 63);
        }
        while (iV > yStart);

        if (borrow != 0)
        {
            while (--x[--iT] == -1)
            {
            }
        }

        return x;
    }

    public BigInteger subtract(BigInteger val)
    {
        if (val.sign == 0 || val.magnitude.length == 0)
        {
            return this;
        }
        if (sign == 0 || magnitude.length == 0)
        {
            return val.negate();
        }
        if (this.sign != val.sign)
        {
            return this.add(val.negate());
        }

        int compare = compareTo(0, magnitude, 0, val.magnitude);
        if (compare == 0)
        {
            return ZERO;
        }

        BigInteger bigun, littlun;
        if (compare < 0)
        {
            bigun = val;
            littlun = this;
        }
        else
        {
            bigun = this;
            littlun = val;
        }

        int res[] = new int[bigun.magnitude.length];

        System.arraycopy(bigun.magnitude, 0, res, 0, res.length);

        return new BigInteger(this.sign * compare, subtract(0, res, 0, littlun.magnitude));
    }

    public byte[] toByteArray()
    {
        if (sign == 0)
        {
            return new byte[1]; 
        }

        int bitLength = bitLength();
        byte[] bytes = new byte[bitLength / 8 + 1];

        int magIndex = magnitude.length;
        int bytesIndex = bytes.length;

        if (sign > 0)
        {
            while (magIndex > 1)
            {
                int mag = magnitude[--magIndex];
                bytes[--bytesIndex] = (byte) mag;
                bytes[--bytesIndex] = (byte)(mag >>> 8);
                bytes[--bytesIndex] = (byte)(mag >>> 16);
                bytes[--bytesIndex] = (byte)(mag >>> 24);
            }

            int lastMag = magnitude[0];
            while ((lastMag & 0xFFFFFF00) != 0)
            {
                bytes[--bytesIndex] = (byte) lastMag;
                lastMag >>>= 8;
            }

            bytes[--bytesIndex] = (byte) lastMag;
        }
        else
        {
            boolean carry = true;

            while (magIndex > 1)
            {
                int mag = ~magnitude[--magIndex];

                if (carry)
                {
                    carry = (++mag == 0);
                }

                bytes[--bytesIndex] = (byte) mag;
                bytes[--bytesIndex] = (byte)(mag >>> 8);
                bytes[--bytesIndex] = (byte)(mag >>> 16);
                bytes[--bytesIndex] = (byte)(mag >>> 24);
            }

            int lastMag = magnitude[0];

            if (carry)
            {
                // Never wraps because magnitude[0] != 0
                --lastMag;
            }

            while ((lastMag & 0xFFFFFF00) != 0)
            {
                bytes[--bytesIndex] = (byte) ~lastMag;
                lastMag >>>= 8;
            }

            bytes[--bytesIndex] = (byte) ~lastMag;

            if (bytesIndex > 0)
            {
                bytes[--bytesIndex] = (byte)0xFF;
            }
        }

        return bytes;
    }

    public BigInteger xor(BigInteger val) 
    {
        if (this.sign == 0)
        {
            return val;
        }

        if (val.sign == 0)
        {
            return this;
        }

        int[] aMag = this.sign > 0
            ? this.magnitude
            : this.add(ONE).magnitude;

        int[] bMag = val.sign > 0
            ? val.magnitude
            : val.add(ONE).magnitude;

        boolean resultNeg = (sign < 0 && val.sign >= 0) || (sign >= 0 && val.sign < 0);
        int resultLength = Math.max(aMag.length, bMag.length);
        int[] resultMag = new int[resultLength];

        int aStart = resultMag.length - aMag.length;
        int bStart = resultMag.length - bMag.length;

        for (int i = 0; i < resultMag.length; ++i)
        {
            int aWord = i >= aStart ? aMag[i - aStart] : 0;
            int bWord = i >= bStart ? bMag[i - bStart] : 0;

            if (this.sign < 0)
            {
                aWord = ~aWord;
            }

            if (val.sign < 0)
            {
                bWord = ~bWord;
            }

            resultMag[i] = aWord ^ bWord;

            if (resultNeg)
            {
                resultMag[i] = ~resultMag[i];
            }
        }

        BigInteger result = new BigInteger(1, resultMag);

        if (resultNeg)
        {
            result = result.not();
        }

        return result;
    }

    public BigInteger or(
        BigInteger value)
    {
        if (this.sign == 0)
        {
            return value;
        }

        if (value.sign == 0)
        {
            return this;
        }

        int[] aMag = this.sign > 0
                        ? this.magnitude
                        : this.add(ONE).magnitude;

        int[] bMag = value.sign > 0
                        ? value.magnitude
                        : value.add(ONE).magnitude;

        boolean resultNeg = sign < 0 || value.sign < 0;
        int resultLength = Math.max(aMag.length, bMag.length);
        int[] resultMag = new int[resultLength];

        int aStart = resultMag.length - aMag.length;
        int bStart = resultMag.length - bMag.length;

        for (int i = 0; i < resultMag.length; ++i)
        {
            int aWord = i >= aStart ? aMag[i - aStart] : 0;
            int bWord = i >= bStart ? bMag[i - bStart] : 0;

            if (this.sign < 0)
            {
                aWord = ~aWord;
            }

            if (value.sign < 0)
            {
                bWord = ~bWord;
            }

            resultMag[i] = aWord | bWord;

            if (resultNeg)
            {
                resultMag[i] = ~resultMag[i];
            }
        }

        BigInteger result = new BigInteger(1, resultMag);

        if (resultNeg)
        {
            result = result.not();
        }

        return result;
    }
    
    public BigInteger setBit(int n) 
        throws ArithmeticException 
    {
        if (n < 0)
        {
            throw new ArithmeticException("Bit address less than zero");
        }

        if (testBit(n))
        {
            return this;
        }

        // TODO Handle negative values and zero
        if (sign > 0 && n < (bitLength() - 1))
        {
            return flipExistingBit(n);
        }

        return or(ONE.shiftLeft(n));
    }
    
    public BigInteger clearBit(int n) 
        throws ArithmeticException 
    {
        if (n < 0)
        {
            throw new ArithmeticException("Bit address less than zero");
        }

        if (!testBit(n))
        {
            return this;
        }

        // TODO Handle negative values
        if (sign > 0 && n < (bitLength() - 1))
        {
            return flipExistingBit(n);
        }

        return andNot(ONE.shiftLeft(n));
    }

    public BigInteger flipBit(int n) 
        throws ArithmeticException 
    {
        if (n < 0)
        {
            throw new ArithmeticException("Bit address less than zero");
        }

        // TODO Handle negative values and zero
        if (sign > 0 && n < (bitLength() - 1))
        {
            return flipExistingBit(n);
        }

        return xor(ONE.shiftLeft(n));
    }

    private BigInteger flipExistingBit(int n)
    {
        int[] mag = new int[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, mag, 0, mag.length);
        mag[mag.length - 1 - (n >>> 5)] ^= (1 << (n & 31)); // Flip 0 bit to 1
        //mag[mag.Length - 1 - (n / 32)] |= (1 << (n % 32));
        return new BigInteger(this.sign, mag);
    }

    public String toString()
    {
        return toString(10);
    }

    public String toString(int rdx)
    {
        if (magnitude == null)
        {
            return "null";
        }
        if (sign == 0)
        {
            return "0";
        }
        if (rdx < Character.MIN_RADIX || rdx > Character.MAX_RADIX)
        {
            rdx = 10;
        }

        
        // NOTE: This *should* be unnecessary, since the magnitude *should* never have leading zero digits
        int firstNonZero = 0;
        while (firstNonZero < magnitude.length)
        {
            if (magnitude[firstNonZero] != 0)
            {
                break;
            }
            ++firstNonZero;
        }

        if (firstNonZero == magnitude.length)
        {
            return "0";
        }


        StringBuffer sb = new StringBuffer();
        if (sign == -1)
        {
            sb.append('-');
        }

        switch (rdx)
        {
        case 2:
        {
            int pos = firstNonZero;
            sb.append(Integer.toBinaryString(magnitude[pos]));
            while (++pos < magnitude.length)
            {
                appendZeroExtendedString(sb, Integer.toBinaryString(magnitude[pos]), 32);
            }
            break;
        }
        case 4:
        {
            int pos = firstNonZero;
            int mag = magnitude[pos];
            if (mag < 0)
            {
                sb.append(Integer.toString(mag >>> 30, 4));
                mag &= (1 << 30) - 1;
                appendZeroExtendedString(sb, Integer.toString(mag, 4), 15);
            }
            else
            {
                sb.append(Integer.toString(mag, 4));
            }
            int mask = (1 << 16) - 1;
            while (++pos < magnitude.length)
            {
                mag = magnitude[pos];
                appendZeroExtendedString(sb, Integer.toString(mag >>> 16, 4), 8);
                appendZeroExtendedString(sb, Integer.toString(mag & mask, 4), 8);
            }
            break;
        }
        case 8:
        {
            long mask = (1L << 63) - 1;
            BigInteger u = this.abs();
            int bits = u.bitLength();
            Stack S = new Stack();
            while (bits > 63)
            {
                S.push(Long.toString((u.longValue() & mask),8));
                u = u.shiftRight(63);
                bits -= 63;
            }
            sb.append(Long.toString(u.longValue(), 8));
            while (!S.empty())
            {
                appendZeroExtendedString(sb, (String)S.pop(), 21);
            }
            break;
        }
        case 16:
        {
            int pos = firstNonZero;
            sb.append(Integer.toHexString(magnitude[pos]));
            while (++pos < magnitude.length)
            {
                appendZeroExtendedString(sb, Integer.toHexString(magnitude[pos]), 8);
            }
            break;
        }
        default:
        {
            BigInteger q = this.abs();
            if (q.bitLength() < 64)
            {
                sb.append(Long.toString(q.longValue(), rdx));
                break;
            }

            // Based on algorithm 1a from chapter 4.4 in Seminumerical Algorithms (Knuth)

            // Work out the largest power of 'rdx' that is a positive 64-bit integer
            // TODO possibly cache power/exponent against radix?
            long limit = Long.MAX_VALUE / rdx;
            long power = rdx;
            int exponent = 1;
            while (power <= limit)
            {
                power *= rdx;
                ++exponent;
            }

            BigInteger bigPower = BigInteger.valueOf(power);

            Stack S = new Stack();
            while (q.compareTo(bigPower) >= 0)
            {
                BigInteger[] qr = q.divideAndRemainder(bigPower);
                S.push(Long.toString(qr[1].longValue(), rdx));
                q = qr[0];
            }

            sb.append(Long.toString(q.longValue(), rdx));
            while (!S.empty())
            {
                appendZeroExtendedString(sb, (String)S.pop(), exponent);
            }
            break;
        }
        }

        return sb.toString();
    }

    private static void appendZeroExtendedString(StringBuffer sb, String s, int minLength)
    {
        for (int len = s.length(); len < minLength; ++len)
        {
            sb.append('0');
        }
        sb.append(s);
    }

    public static BigInteger valueOf(long val)
    {
        if (val >= 0 && val < SMALL_CONSTANTS.length)
        {
            return SMALL_CONSTANTS[(int)val];
        }

        return createValueOf(val);
    }

    private static BigInteger createValueOf(long val)
    {
        if (val < 0)
        {
            if (val == Long.MIN_VALUE)
            {
                return valueOf(~val).not();
            }

            return valueOf(-val).negate();
        }

        // store val into a byte array
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++)
        {
            b[7 - i] = (byte)val;
            val >>= 8;
        }

        return new BigInteger(b);
    }

    public int getLowestSetBit()
    {
        if (this.sign == 0)
        {
            return -1;
        }

        int w = magnitude.length;

        while (--w > 0)
        {
            if (magnitude[w] != 0)
            {
                break;
            }
        }

        int word = magnitude[w];

        int b = (word & 0x0000FFFF) == 0
            ?   (word & 0x00FF0000) == 0
                ?   7
                :   15
            :   (word & 0x000000FF) == 0
                ?   23
                :   31;

        while (b > 0)
        {
            if ((word << b) == 0x80000000)
            {
                break;
            }

            b--;
        }

        return ((magnitude.length - w) * 32 - (b + 1));
    }

    public boolean testBit(int n) 
        throws ArithmeticException
    {
        if (n < 0)
        {
            throw new ArithmeticException("Bit position must not be negative");
        }

        if (sign < 0)
        {
            return !not().testBit(n);
        }

        int wordNum = n / 32;
        if (wordNum >= magnitude.length)
            return false;

        int word = magnitude[magnitude.length - 1 - wordNum];
        return ((word >> (n % 32)) & 1) > 0;
    }
}