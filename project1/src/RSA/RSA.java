package RSA;
/**
    author: Simon Farre
    email: simon.farre.x@gmail.com
    Written 2018 for a lab at school.
*/

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.math.BigInteger.*;
import static java.lang.Math.pow;
interface BigIntGenerate {
    BigInteger randomStep(BigInteger b);
}

public class RSA {

    public BigInteger p, q, d;
    public long exp;    // exponent e

    /**
     * Default constructor. Returns an RSA scheme, with BigIntegers of bitsize 512
     */
    public RSA() {
        this.exp = 65537; // 2^16 + 1
        new RSA(512);
    }

    /**
     * Construct a RSA scheme with parameters P and Q.
     * @param P
     * @param Q
     */

    public RSA(Long p, Long q)  {
        new RSA(valueOf(p), valueOf(q));
    }

    public RSA(BigInteger P, BigInteger Q) {
        BigInteger m = (P.subtract(ONE))
                        .multiply
                        (Q.subtract(ONE));
        d = modInversem(valueOf(exp), m);
    }

    public RSA(int bitsize) {
        Random r_p = new Random(Long.MAX_VALUE ^ System.currentTimeMillis());
        Random r_q = new Random(Long.MIN_VALUE ^ System.nanoTime());
        new RSA(new BigInteger(bitsize, r_p), new BigInteger(bitsize, r_q));
    }

    public static final BigInteger TWO = ONE.add(ONE);
    static List<BigInteger> generate_100_primes(int bitSize, BigIntGenerate big) {
        BigInteger begin;
        do
            begin = new BigInteger(bitSize, new Random(System.nanoTime() ^ Long.MAX_VALUE));
        while(begin.getLowestSetBit() != 0);
        Long startTime = System.nanoTime();
        List<BigInteger> primes = Stream.iterate(begin, big::randomStep)
                .filter(RSA::isMillerRabin)
                .limit(100)
                .collect(Collectors.toList());
        Long endTime = System.nanoTime();
        primes.forEach(System.out::println);
        System.out.println(String.format("it took %d ms to generate 100 %d-bit primes, avg: %d ms/prime", (endTime-startTime)/1000000, bitSize, (endTime-startTime)/(1000000*100)));
        return primes;
    }
    private static boolean isMillerRabin(BigInteger n) {
        if(n.getLowestSetBit() != 0) {
            System.out.println("isMillerRabin: false");
            return false;
        }
        assert n.getLowestSetBit() == 0;
        int idx = 0;
        BigInteger aBasesBig[] = new BigInteger[20];
        for (int i = 0; i < aBasesBig.length; i++) {
            // an attempt to randomizing the a's
            do
                aBasesBig[i] = new BigInteger(n.bitLength(),
                    new Random(
                        new Random(Long.MAX_VALUE ^ n.bitLength() ^ System.currentTimeMillis())
                            .nextLong())); while(aBasesBig[i].compareTo(n) > 0);
        }
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
        {
        Witnesses:
            for (BigInteger aBase = aBasesBig[idx]; idx < 20; aBase = aBasesBig[idx++]) {
                BigInteger x = aBase.modPow(s , n);
                if (x.equals(ONE) || x.equals(nMinusOne))
                    continue Witnesses; // a is _not_ a witness for the compositeness
                for(int j = 0; j < r; ++j) {
                    x = x.modPow(TWO, n);
                    if(x.equals(ONE))
                        return false;
                    if(x.equals(nMinusOne))
                        continue Witnesses;
                }
                return false;
            }
            return true;
        }
    }

    public BigInteger eea(BigInteger a, BigInteger m) {
        BigInteger u1, u2, d1, v1, v2, d2;
        d1 = m;
        d2 = a;
        u1 = v2 = ONE;
        u2 = v1 = ZERO;

        while(d2.compareTo(ZERO) != 0) {
            BigInteger q = d1.divide(d2);
            BigInteger t1 = u1.subtract(q.multiply(u2));
            BigInteger t2 = v1.subtract(q.multiply(v2));
            BigInteger t3 = d1.subtract(q.multiply(d2));

            u1 = u2; v1 = v2; d1 = d2;
            u2 = t1; v2 = t2; d2 = t3;
        }
        return ZERO;
    }

    public static BigInteger modInversem(BigInteger a, BigInteger m) {
        BigInteger d1, d2, v1, v2;
        d1 = m;
        d2 = a;
        v1 = ZERO; v2 = ONE;
        BigInteger q, t1,t2,t3;
        for (d2 = a; d2.compareTo(ZERO) != 0; d2 = t3) {
            q = d1.divide(d2);
            t2 = v1.subtract(q.multiply(v2));
            t3 = d1.subtract(q.multiply(d2));
            v1 = v2;
            d1 = d2;
            v2 = t2;
        }
        BigInteger v = v1;
        BigInteger d = d1;
        if(v.compareTo(ZERO) < 0) return v.add(m);
        return v;
    }
}
