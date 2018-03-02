/*
* author: Simon Farre
* email: simon.farre.x@gmail.com
*/
package RSA;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigInteger.*;
import static java.lang.System.nanoTime;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Stream.iterate;

public class RSA {

    private static final BigInteger TWO = ONE.add(ONE);
    // p and q should be discarded after e, d and N is calculated, but for purposes of
    // printing values in project, they are kept here.
    private BigInteger p, q, d;
    private BigInteger e = TWO.pow(16).add(ONE); // 2^16 + 1
    private BigInteger N;
    public BigInteger getN() { return this.N;}
    @Override
    public String toString() {
        return "RSA {" +
              "\n\t" + "Size: " + p.bitLength() + "\t p=" + p +
            ", \n\t" + "Size: " + q.bitLength() + "\t q=" + q +
            ", \n\t" + "Size: " + d.bitLength() + "\t d=" + d +
            ", \n\t" + "Size: " + e.bitLength() + "\t e=" + e +
            ", \n\t" + "Size: " + N.bitLength() + "\t N=" + N +
            "\n}";
    }
    static private BigInteger[] generatePrimePair(final int keySize, App.BigIntGenerate bigen) {
        Stack<BigInteger> stack = new Stack<>();
        // Set<BigInteger> primes =
            iterate(new BigInteger(keySize,
                new Random(Long.MAX_VALUE ^ nanoTime())),
            bigen::randomStep)
            .filter((b) -> b.bitLength() == keySize)
            .filter(RSA::isMillerRabin)
            .limit(2)
            .collect(Collectors.toSet())
            .forEach(stack::push);
        System.out.println("Done generating p and q");
        return new BigInteger[]{stack.pop(), stack.pop()};
    }

    RSA(int keySize) {
        BigInteger primes[] = generatePrimePair(keySize, b -> new BigInteger(keySize, new Random(Long.MAX_VALUE ^ nanoTime())));
        this.p = primes[0];
        this.q = primes[1];
        BigInteger m = (p.subtract(ONE))
            .multiply
                (q.subtract(ONE));
        this.N = p.multiply(q);
        d = modInversem(e, m);
        // ---- !  e x d ≡ 1 (mod (p-1)(q-1)) ! ----
        assert e.multiply(d).mod(m).compareTo(ONE) == 0;
        // -----------------------------------------

        if(!(e.multiply(d).mod(m).compareTo(ONE) == 0)) {
            System.out.println("Construction of RSA scheme failed");
        }
        this.N = p.multiply(q);
    }

    public BigInteger getD() {
        return this.d;
    }
    // for testing purpose, _only_
    RSA(int keysize, BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        BigInteger m =  (p.subtract(ONE))
                        .multiply
                        (q.subtract(ONE));
        this.N = p.multiply(q);
        d = modInversem(e, m);
        // ---- !  e x d ≡ 1 (mod (p-1)(q-1)) ! ----
        assert e.multiply(d).mod(m).compareTo(ONE) == 0;
        // -----------------------------------------
        if(!(e.multiply(d).mod(m).compareTo(ONE) == 0)) {
            System.out.println("Construction of RSA scheme failed");
        }
        this.N = p.multiply(q);
    }

    static boolean isMillerRabin(BigInteger n) {
        if(n.getLowestSetBit() != 0) {
            return false;
        }
        assert(n.getLowestSetBit() == 0);
        int idx = 0;
        BigInteger aBasesBig[] = new BigInteger[20];
        for (int i = 0; i < aBasesBig.length; i++) {
            // an attempt to randomizing the a's
            do
                aBasesBig[i] = new BigInteger(n.bitLength(),
                    new Random(
                        new Random(Long.MAX_VALUE ^ n.bitLength() ^ currentTimeMillis())
                            .nextLong())); while(aBasesBig[i].compareTo(n) > 0);
        }
        // nMinusOne => n - 1 = 2^r * s
        BigInteger nMinusOne = n.subtract(ONE);
        // r is factor of 2. Since numbers are represented in two's complement, it means that
        // the lowest set bit of n, is the exponent of 2^r, i.e. r
        int r = 0;
        BigInteger s;
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
                if (x.compareTo(ONE) == 0 || x.compareTo(nMinusOne) == 0)
                    continue; // a is _not_ a witness for the compositeness
                for(int j = 0; j < r; ++j) {
                    x = x.modPow(TWO, n);
                    if(x.compareTo(ONE) == 0)
                        return false;
                    if(x.compareTo(nMinusOne) == 0)
                        continue Witnesses;
                }
                return false;
            }
            return true;
        }
    }
    public static BigInteger modInversem(BigInteger a, BigInteger m) {
        BigInteger d1, v1, v2;
        d1 = m;
        v1 = ZERO; v2 = ONE;
        BigInteger q, t2,
            t3;
        for (BigInteger d2 = a; d2.compareTo(ZERO) != 0; d2 = t3) {
            q = d1.divide(d2);
            t2 = v1.subtract(q.multiply(v2));
            t3 = d1.subtract(q.multiply(d2));
            v1 = v2;
            d1 = d2;
            v2 = t2;
        }
        BigInteger v = v1;
        if(v.compareTo(ZERO) < 0) return v.add(m);
        return v;
    }
    public BigInteger encrypt(BigInteger s) {
        return s.modPow(e, N);
    }
    public BigInteger decrypt(BigInteger c) {
        return c.modPow(this.d, N);
    }
}
