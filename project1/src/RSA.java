/**
    author: Simon Farre
    email: simon.farre.x@gmail.com
    Written 2018 for a lab at school.
*/

import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.math.BigInteger.*;

public class RSA {
    public static final BigInteger TWO = ONE.add(ONE);
    /*
        According to: https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test
        if n < 3,317,044,064,679,887,385,961,981,
        it is enough to test a=[2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41]
    */
    public static final int[] aBases = new int[]{2,3,5,7,11,13,17,19,23,29,31,37,41, 2047};
    public static void main(String[] args) {
        // List<String> lst = Stream.iterate(valueOf(1000001), i -> i.add(TWO)).filter(RSA::isMillerRabin).map(BigInteger::toString).limit(100).collect(Collectors.toList());;
        Random rnd = new Random(Long.MAX_VALUE ^ System.currentTimeMillis());
        BigInteger begin;
        do begin = new BigInteger(2048, rnd); while(begin.getLowestSetBit() != 0);
        List<BigInteger> _100consecutive2048bit = generate_100_consecutive_2048bit_primes(begin);
        do begin = new BigInteger(512, rnd); while(begin.getLowestSetBit() != 0);
        List<BigInteger> stringList512 = generate_100_512bit_primes(begin);
        do begin = new BigInteger(1024, rnd); while(begin.getLowestSetBit() != 0);
        List<BigInteger> stringList1024 = generate_100_1024bit_primes(begin);
        do begin = new BigInteger(2048, rnd); while(begin.getLowestSetBit() != 0);
        List<BigInteger> stringList2048 = generate_100_2048bit_primes(begin);
        System.out.println("Done.");
    }

    public static List<BigInteger> generate_100_512bit_primes(BigInteger start) {
        ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
        Long starttime = threadMX.getCurrentThreadUserTime();
        List<BigInteger> primes =
            Stream.iterate(start, i -> new BigInteger(512, new Random(Long.MAX_VALUE ^ System.nanoTime())))
            .filter(RSA::isMillerRabin)
            .sequential()
            .limit(100)
            .collect(Collectors.toList());
        Long endtime = threadMX.getCurrentThreadUserTime();
        primes.forEach(System.out::println);
        System.out.println(String.format("it took %d ms to generate 100 512-bit primes, avg: %d ms/prime", (endtime-starttime)/1000000, (endtime-starttime)/(1000000*100)));
        return primes;
    }
    public static List<BigInteger> generate_100_1024bit_primes(BigInteger start) {
        ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
        Long starttime = threadMX.getCurrentThreadCpuTime();
        List<BigInteger> primes =
            Stream.iterate(start, i -> new BigInteger(1024, new Random(Long.MAX_VALUE ^ System.nanoTime())))
            .filter(RSA::isMillerRabin)
            .limit(100)
            .collect(Collectors.toList());
        Long endtime = threadMX.getCurrentThreadCpuTime();
        primes.forEach(System.out::println);
        System.out.println(String.format("it took %d ms to generate 100 1024-bit primes, avg: %d ms/prime", (endtime-starttime)/1000000, (endtime-starttime)/(1000000*100)));
        return primes;
    }
    public static List<BigInteger> generate_100_2048bit_primes(BigInteger start) {
        ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
        Long starttime = threadMX.getCurrentThreadCpuTime();
        List<BigInteger> primes = Stream
            .iterate(start,
                i -> new BigInteger(2048, new Random(Long.MAX_VALUE ^ System.nanoTime())))
            .filter(RSA::isMillerRabin)
            .limit(100)
            .collect(Collectors.toList());
        Long endtime = threadMX.getCurrentThreadCpuTime();
        primes.forEach(System.out::println);
        System.out.println(String.format("it took %d ms to generate 100 2048-bit primes, avg: %d ms/prime", (endtime-starttime)/1000000, (endtime-starttime)/(1000000*100)));
        return primes;
    }

    public static List<BigInteger> generate_100_consecutive_2048bit_primes(BigInteger start) {
        ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
        Long starttime = threadMX.getCurrentThreadUserTime();
        List<BigInteger> primes = Stream
            .iterate(start, i -> i.add(TWO))
            .filter(RSA::isMillerRabin)
            .limit(100)
            .collect(Collectors.toList());
        Long endtime = threadMX.getCurrentThreadUserTime();
        primes.forEach(System.out::println);
        System.out.println(String.format("it took %d ms to generate 100 2048-bit primes, avg: %d ms/prime", (endtime-starttime)/1000000, (endtime-starttime)/(1000000*100)));
        return primes;
    }


    public static boolean isMillerRabin(BigInteger n) {
        if(n.getLowestSetBit() != 0) return false;
        assert n.getLowestSetBit() == 0;
        int idx = 0;
        int bases[] = compute_aBases();
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
            for (int aBase = bases[idx]; idx < 20; aBase = bases[idx++]) {
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

    public static int[] compute_aBases() {
        int a_basis[] = new int[20];
        for(int i = 0; i < 13; ++i) {
            a_basis[i] = aBases[i];
        }
        for(int i = 13; i < 20; ++i) {
            a_basis[i] = new Random(Long.MAX_VALUE ^ System.currentTimeMillis() - 203042L).nextInt();
        }
        return a_basis;
    }

}
