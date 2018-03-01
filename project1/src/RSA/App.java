package RSA;
import static java.math.BigInteger.valueOf;

public class App {
    public static void main(String[] args) {
        /**
         *  Assignment 2.1 & 2.3 & 2.4
         */
        /*
        // 100 consecutive 512-bit primes
        List<BigInteger> consecutive512bitPrimes = RSA.generate_100_primes(512, (num) -> num.add(TWO));
        // 100 randomized 512-bit primes
        List<BigInteger> random512bitPrimes = RSA.generate_100_primes(512, (n) -> {
            BigInteger bi = new BigInteger(512, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });

        // 100 randomized 1024-bit primes
        List<BigInteger> random1024bitPrimes = RSA.generate_100_primes(1024, (n) -> {
            BigInteger bi = new BigInteger(1024, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });

        // 100 randomized 2048-bit primes
        List<BigInteger> random2048bitPrimes = RSA.generate_100_primes(2048, (n) -> {
            BigInteger bi = new BigInteger(2048, new Random(Long.MAX_VALUE ^ System.currentTimeMillis()));
            return bi.getLowestSetBit() == 0 ? bi : bi.subtract(ONE);
        });
        System.out.println("Done generating all prime numbers.");
        */

        System.out.println(RSA.modInversem(valueOf(42), valueOf(2017)).toString());
    }
}
