package RSA;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.lang.System.nanoTime;
public class App {
    public static void main(String[] args) {
        /**
         *  Assignment 2.1 & 2.3 & 2.4
         */
        /*
        // 100 consecutive 512-bit primes
        List<BigInteger> consecutive512bitPrimes = RSA.test_generate_100_primes(512, (num) -> num.add(TWO));
        // 100 randomized 512-bit primes
        List<BigInteger> random512bitPrimes = RSA.test_generate_100_primes(512, (n) -> {
            BigInteger bi = new BigInteger(512, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });

        // 100 randomized 1024-bit primes
        List<BigInteger> random1024bitPrimes = RSA.test_generate_100_primes(1024, (n) -> {
            BigInteger bi = new BigInteger(1024, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });

        // 100 randomized 2048-bit primes
        List<BigInteger> random2048bitPrimes = RSA.test_generate_100_primes(2048, (n) -> {
            BigInteger bi = new BigInteger(2048, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });
        System.out.println("Done generating all prime numbers.");
        */
        RSA rsa = null;
        try {
            rsa = new RSA(512);
            Random random = new Random(Long.MIN_VALUE ^ nanoTime());
            BigInteger s;
            do {
                s = new BigInteger(512, random);
            } while(s.compareTo(ONE) < 1
                || s.compareTo(rsa.getN()) > 1);

            BigInteger c = rsa.encrypt(s);
            BigInteger d = rsa.decrypt(c);
            System.out.println(String.format("s = %s \nd = %s \nc= %s", s, d, c));
            System.out.println(String.format("Bitsizes: s is: %d \nc = %d \nd= %d \nN = %d", s.bitLength(), c.bitLength(), d.bitLength(), rsa.getN().bitLength()));
            if(d.compareTo(s) == 0) {
                System.out.println("Encryption of s succeeded, since s === d");
            } else {
                System.out.println("Encryption of s FAILED, since s !== d");
            }
            System.out.println(rsa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BigInteger gcd(BigInteger m, BigInteger n) {
        if(n.compareTo(ZERO) == 0) return m;
        else return gcd(n, m.mod(n));
    }

    static List<BigInteger> test_generate_100_primes(int bitSize, BigIntGenerate big) {
        BigInteger begin;
        do
            begin = new BigInteger(bitSize, new Random(nanoTime() ^ Long.MAX_VALUE));
        while(begin.getLowestSetBit() != 0);
        Long startTime = nanoTime();
        List<BigInteger> primes = Stream.iterate(begin, big::randomStep)
            .filter(RSA::isMillerRabin)
            .limit(100)
            .collect(Collectors.toList());
        Long endTime = nanoTime();
        primes.forEach(System.out::println);
        System.out.println(String.format("it took %d ms to generate 100 %d-bit primes, avg: %d ms/prime", (endTime-startTime)/1000000, bitSize, (endTime-startTime)/(1000000*100)));
        return primes;
    }
}
