/**
    author: Simon Farre
    email: simon.farre.x@gmail.com
    Written 2018 for a lab at school.
*/

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;
import static java.math.BigInteger.*;

public class RSA {
    public static final BigInteger TWO = ONE.add(ONE);
    /*
        According to: https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test
        if n < 3,317,044,064,679,887,385,961,981,
        it is enough to test a=[2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41]
    */
    public static final int[] aBases = new int[]{2,3,5,7,11,13,17,19,23,29,31,37,41};
    public static void main(String[] args) {
        Stream<BigInteger> infiniteStream = Stream.iterate(ZERO, i -> i.add(TWO));
        RSA rsa = new RSA();
        // tar tal mellan 300,000 och 3,000,000 och testar ifall de är primtal.
        // skulle lika väl välja "3_000_000_000_000_000_000_000_000_000_000_000_000_000_000 och 3_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000"
        // här, med hjälp av klassen BigIntegerProgression.
        BigIntegerProgression bigIntegerProgression =
            new BigIntegerProgression(BigInteger.valueOf(3L), BigInteger.valueOf(100L), 2L);
        bigIntegerProgression.forEach((BigInteger i) -> {
            if(rsa.isMillerRabin(i, 35))
                System.out.println("Prob prime: " + i.toString());
        });
        System.out.println("Hello World!");
    }
    /**
     * @param n - Input: n > 3, an odd integer to be tested for primality; n = 2^r * s + 1
     * @param k - amount of witnesses, base a
     * @return - result of test for primality.
     */
    public boolean isMillerRabin(BigInteger n, int k) {
        assert n.getLowestSetBit() == 0;
        int idx = 0;
        int end_abases;;
        if(n.compareTo(valueOf(2047)) < 0)
            end_abases = 1;
        else end_abases = aBases.length;
        // nMinusOne => n - 1 = 2^r * s
        BigInteger nMinusOne = n.subtract(ONE);
        // r is factor of 2. Since numbers are represented in two's complement, it means that
        // the lowest set bit of n, is the exponent of 2^r, i.e. r
        int r = 0;
        BigInteger s = ZERO;
        do {
            s = nMinusOne.shiftRight(r);
            // first shift is a no-op. This way we don't have to go s % 2 every loop. But if
            // the next factor of 2, is 8 for example, we can shift that amount immediately, instead of doing big modulo ops on big heavy
            // big integers.
            r += s.getLowestSetBit();
        } while(s.getLowestSetBit() != 0); // if lowest set bit is not 0, it means we have an even number. We need an odd. keep on.
        Random rnd = new Random();
        {
        Witness:
            for (int aBase = aBases[idx]; idx < end_abases; aBase = aBases[++idx]) {
                BigInteger x = valueOf(aBase).modPow(s , n);
                if (x.equals(ONE) || x.equals(nMinusOne)) continue Witness;
                for(int j = 0; j < r; ++j) {
                    x = x.modPow(TWO, n);
                    if(x.equals(ONE))
                        return false;
                    if(x.equals(nMinusOne))
                        continue Witness;
                }
                return false;
            }
            return true;
        }
    }


}
