/*
    BigInteger progression class for use with miller rabin algorithm and RSA encryption for use in a lab at LTH Campus Helsingborg.
    Author: Simon Farre (theIDinside @ github, simon.farre.cx@gmail.com)
 */

import java.math.BigInteger
import java.math.BigInteger.*

/**
 * Progression and iterator over BigIntegers.
 * @param start - Starting value for range
 * @param endInclusive - Ending value, inclusive
 * @param longSteps - Steps taken, in type Long
 */
class BigIntegerProgression(override val start: BigInteger,
                            override val endInclusive: BigInteger,
                            val longSteps: Long = 1L) // could use BigInteger here, but I have a suspicion we would take a heavy performance hit
                            : Iterable<BigInteger>,
                            ClosedRange<BigInteger>
{
    override fun iterator(): Iterator<BigInteger> = BigIntegerProgressionIterator(start, endInclusive, longSteps)
    infix fun step(steps: Long) = BigIntegerProgression(start, endInclusive, steps)
    internal class BigIntegerProgressionIterator(start: BigInteger, val endInclusive: BigInteger, val longSteps: Long) : Iterator<BigInteger> {
        var current = start
        override fun hasNext(): Boolean = current <= endInclusive

        override fun next(): BigInteger {
            val next = current
            current += valueOf(longSteps)
            return next
        }
    }
}

operator fun BigInteger.rangeTo(other: BigInteger) = BigIntegerProgression(this, other)
infix fun BigInteger.until(to: BigInteger): BigIntegerProgression {
    return this .. (to - ONE)
}