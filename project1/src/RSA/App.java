package RSA;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;
import static java.lang.System.nanoTime;

public class App {

    interface BigIntGenerate {
        BigInteger randomStep(BigInteger b);
    }
    static BigInteger TWO = ONE.add(ONE);
    static BigInteger e = TWO.pow(16).add(ONE);
    public static void main(String[] args) {
        if(args.length > 0 && args[0].equals("--all"))
        {
            /**
             *  Assignment 2.1 & 2.3 & 2.4
             */
            // 100 consecutive 512-bit primes
            List<BigInteger> consecutive512bitPrimes = test_generate_100_primes(512, (num) -> num.add(TWO));
            // 100 randomized 512-bit primesN
            List<BigInteger> random512bitPrimes = test_generate_100_primes(512, (n) -> {
                BigInteger bi = new BigInteger(512, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
                return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
            });

            // 100 randomized 1024-bit primes
            List<BigInteger> random1024bitPrimes = test_generate_100_primes(1024, (n) -> {
                BigInteger bi = new BigInteger(1024, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
                return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
            });

            // 100 randomized 2048-bit primes
            List<BigInteger> random2048bitPrimes = test_generate_100_primes(2048, (n) -> {
                BigInteger bi = new BigInteger(2048, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
                return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
            });
            System.out.println("Done generating all prime numbers.");
        }
        /* 4.1 */
        System.out.println("\n ---- /* 4.1 */ -----");
        {
            BigInteger a = valueOf(49);
            BigInteger m = valueOf(93);
            System.out.println(String.format("a⁻¹ mod m = %s⁻¹ mod %s = ", a, m) + RSA.modInversem(a, m));

            a = valueOf(11);
            m = valueOf(87);
            System.out.println(String.format("a⁻¹ mod m = %s⁻¹ mod %s = ", a, m) + RSA.modInversem(a, m));

            a = valueOf(7);
            m = valueOf(997);
            System.out.println(String.format("a⁻¹ mod m = %s⁻¹ mod %s = ", a, m) + RSA.modInversem(a, m));
        }

        /* 4.2 */
        System.out.println("\n ---- /* 4.2 */ -----");
        {

            BigInteger p = new BigInteger("11679969927324382130141599722719662693092279397672598815622637814351917472310009335974776557486485283308840811769176348137025272689903706129056302629460699");
            BigInteger q = new BigInteger("8756697129242005878294566988983696974103201282795185090373542917395938566418975485129253521809338972618940101405301185435961617595861444003943762201506441");
            BigInteger m = p.subtract(ONE).multiply(q.subtract(ONE));
            BigInteger d = RSA.modInversem(e, m);
            RSA rsa = new RSA(512, p, q);
            assert e.multiply(d).mod(m).compareTo(ONE) == 0; // assert that e x d is congruent with 1 (mod m) meaning, e x d (mod m) = 1
            System.out.println("d = e⁻¹ mod m = " + d);
            System.out.println("e * d mod m = " + e.multiply(d).mod(m));
            System.out.println(rsa);
        }


        System.out.println("\n ---- /* 5.1, 5.2, 5.3 */ -----");
        {
            BigInteger p = new BigInteger("11679969927324382130141599722719662693092279397672598815622637814351917472310009335974776557486485283308840811769176348137025272689903706129056302629460699");
            BigInteger q = new BigInteger("8756697129242005878294566988983696974103201282795185090373542917395938566418975485129253521809338972618940101405301185435961617595861444003943762201506441");
            RSA rsa = new RSA(512, p, q);
            Random random = new Random(Long.MIN_VALUE ^ nanoTime());
            BigInteger s;
            // 1 < s < N
            do {
                s = new BigInteger(512, random);
            } while (s.compareTo(ONE) < 1
                || s.compareTo(rsa.getN()) > 0);

            // 5.1: print s
            System.out.println("s = " + s);
            // 5.2: compute c = s^e mod N
            BigInteger c = rsa.encrypt(s);
            System.out.println("c = " + c);
            // 5.3: compute z = c^d mod N
            BigInteger z = rsa.decrypt(c);
            System.out.println("z = " + z);
            if (z.compareTo(s) == 0) {
                assert z.toString().equals(s.toString());
                System.out.println("Encryption of s succeeded, since z === s");
            } else {
                System.out.println("Encryption/decryption of s FAILED, since s !== d");
            }
            System.out.println("Assignment 5: \n" + rsa);
        }
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
