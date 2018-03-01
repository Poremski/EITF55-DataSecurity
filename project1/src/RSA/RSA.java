/*
* author: Simon Farre
* email: simon.farre.x@gmail.com
* Written 2018 for a lab at school.
*/
package RSA;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.math.BigInteger.*;
import static java.lang.System.nanoTime;
import static java.lang.System.currentTimeMillis;
interface BigIntGenerate {
    BigInteger randomStep(BigInteger b);
}

public class RSA {

    private static final BigInteger TWO = ONE.add(ONE);

    private BigInteger p, q, d;
    private BigInteger e = TWO.pow(16).add(ONE); // 2^16 + 1
    private BigInteger N;

    @Override
    public String toString() {
        return "RSA {" +
              "\n" + "Size: " + p.bitLength()   + "\tp=" + p +
            ", \n" + "Size: " + q.bitLength() + "\tq=" + q +
            ", \n" + "Size: " + d.bitLength() + "\td=" + d +
            ", \n" + "Size: " + e.bitLength() + "\te=" + e +
            ", \n" + "Size: " + N.bitLength() + "\tN=" + N +
            "\n}";
    }

    /**
     * Default constructor. Returns an RSA scheme, with BigIntegers of bitsize 512
     */
    public RSA() throws Exception {
        this.e = TWO.pow(16).add(ONE); // 2^16 + 1
        new RSA(512);
    }

    public void setE(BigInteger e) { this.e = e; }

    private RSA(BigInteger P, BigInteger Q) throws Exception {
        this.p = P;
        this.q = Q;
        BigInteger m = (P.subtract(ONE))
                        .multiply
                        (Q.subtract(ONE));
        this.N = P.multiply(Q);
        d = modInversem(e, m);
        assert(e.multiply(d).mod(m).compareTo(ONE) == 0);
        if(!(e.multiply(d).mod(m).compareTo(ONE) == 0)) {
            System.out.println("Construction of RSA scheme failed");
            throw new Exception("RSA Construction of RSA scheme failed");
        }
        this.N = p.multiply(q);
    }

    RSA(int keyBitSize) throws Exception {
        BigInteger _q;
        BigInteger _p;
        do {
            _q = new BigInteger(keyBitSize, new Random(Long.MAX_VALUE ^ nanoTime()));
        } while(!isMillerRabin(_q));
        do {
            _p = new BigInteger(keyBitSize, new Random(Long.MAX_VALUE ^ currentTimeMillis()));
        } while(!isMillerRabin(_p));
        System.out.println("Done generating p and q");
        this.p = _p;
        this.q = _q;
        BigInteger m = (p.subtract(ONE))
            .multiply
                (q.subtract(ONE));
        this.N = p.multiply(q);
        d = modInversem(e, m);
        assert((e.multiply(d)).mod(m).compareTo(ONE) == 0);
        if(!(e.multiply(d).mod(m).compareTo(ONE) == 0)) {
            System.out.println("Construction of RSA scheme failed");
            throw new Exception("RSA Construction of RSA scheme failed");
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

    public BigInteger getN() { return this.N;}
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
