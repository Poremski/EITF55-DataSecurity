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

public class RSA {

    interface BigIntGenerate {
        BigInteger randomStep(BigInteger b);
    }

    public static final BigInteger TWO = ONE.add(ONE);
    public static void main(String[] args) {
        /**
         *  Assignment 2.1 & 2.3 & 2.4
         */
        // 100 consecutive 512-bit primes
        List<BigInteger> consecutive512bitPrimes = generate_100_primes(512, (num) -> num.add(TWO));
        // 100 randomized 512-bit primes
        List<BigInteger> random512bitPrimes = generate_100_primes(512, (n) -> {
            BigInteger bi = new BigInteger(512, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });

        // 100 randomized 1024-bit primes
        List<BigInteger> random1024bitPrimes = generate_100_primes(1024, (n) -> {
            BigInteger bi = new BigInteger(1024, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });

        // 100 randomized 2048-bit primes
        List<BigInteger> random2048bitPrimes = generate_100_primes(2048, (n) -> {
            BigInteger bi = new BigInteger(2048, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });
        System.out.println("Done.");
    }

    private static List<BigInteger> generate_100_primes(int bitSize, BigIntGenerate big) {
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

}
