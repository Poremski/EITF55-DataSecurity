import java.lang.Thread.sleep
import java.math.BigInteger
import java.math.BigInteger.*
import java.util.*
import java.lang.management.ManagementFactory
import kotlin.collections.ArrayList

val TWO = BigInteger("2")

fun time_rabin_miller(n: BigInteger,  benchmarkIter: Int?, f: (n: BigInteger, iter: Int) -> Boolean)
{
    val its = if(benchmarkIter == null) 1000 else benchmarkIter
    val threadMX = ManagementFactory.getThreadMXBean()
    assert(threadMX.isCurrentThreadCpuTimeSupported)
    threadMX.isThreadCpuTimeEnabled = true
    val start = threadMX.currentThreadCpuTime

    val END = valueOf(benchmarkIter as Long)

    for (j in 0L until its step 2) {
        f(n + j.toBigInteger(), 50)
    }
    val end = threadMX.currentThreadCpuTime
    println("Finding if $n is composite or probably prime, 1000 times: takes ${(end-start)/1000000}ms")

}

fun probablePrime(n: BigInteger, k: Int): Boolean {
    require(n.lowestSetBit == 0 && n > TWO, {
        println("Number needs to be odd and greater than 2: Numbers lowest set bit ${n.lowestSetBit} and ${n}")
    })
    //  require(n.toInt() > k, { println("Iterations of k, 'a base' > n, does not make sense.") })
    val nMinusOne = n - ONE
    // var r = nMinusOne.lowestSetBit
    var r = 0
    var s: BigInteger
    do {
        s = nMinusOne.shiftRight(r)
        r += s.lowestSetBit // s.lowestSetBit // s.lowestSetBit
    } while (s.lowestSetBit != 0)
    val rnd = Random(n.toLong())
    var aBase: BigInteger = ZERO
    var aList: ArrayList<BigInteger> = ArrayList()
    outer@ for (i in 1 until k) {
        do {
            aBase = BigInteger(n.bitLength(), rnd)
        } while (aBase > nMinusOne || aBase < TWO || aList.contains(aBase))
        require(aBase < (n))
        aList.add(aBase)
        var x = aBase.modPow(s, n)
        if (x == ONE || x == nMinusOne) continue@outer // verifiera med att iterera k gånger, så att chansen för att n är komposit blir =(1/4)^k
        loop@ for (j in 1 until (r)) {
            x = x.modPow(TWO, n)
            when (x) {
                ONE -> {
                    return false
                }
                nMinusOne -> {
                    break@outer
                }
            }
        }
        return false
    }
    return true
}

fun main(args: Array<String>) {


    val rnd = Random(Long.MAX_VALUE xor Long.MAX_VALUE/2-23124154L)
    var st = BigInteger(512, rnd)
    val nd = st + valueOf(1000000)

    runPrimeFind(run {
        val rng: (BigIntegerProgression) = if (args.size >= 2) {
            val begin = args[0].toBigInteger()
            val end = args[1].toInt().toBigInteger()
            require(begin % TWO == ONE && end % TWO == ONE, {
                println("Begin-End range needs to start/end with odd number. Even numbers invalid for Miller Rabin")
            })
            begin..end step 2
        } else {
            val threeCK = valueOf(300_001L)
            val tenM = valueOf(10_000_001L)
            threeCK..tenM step 2
            st..nd
        }
        rng
    })
    runPrimeFind(BigInteger.valueOf(2000_000L)..BigInteger.valueOf(200_000_000_000L))
    // benchmark()
}

fun runPrimeFind(bigIntRange: BigIntegerProgression) {
    println("Running miller-rabin prime finder in 5 seconds..., rangesize: ${bigIntRange.start} to ${bigIntRange.endInclusive}")
    sleep(1500)
    // println("bigIntRange step is ${bigIntRange.longSteps}")
    val filter = bigIntRange.step(steps = 2L).asSequence().filter {
        probablePrime(it, 50) // test against 50 a bases a = (1/4)^50 = 7.888609052210118e-31 chance of being wrong
    }
    var idx = 1
    for(n in filter) {
        println("prob prime (i): ${idx++} = ${n}")
    }
}

// probablePrimeSlow: println(args.size)(BigInteger, Int) -> Boolean, probablePrime: (BigInteger, Int) -> Boolean
fun benchmark() {
//    time_rabin_miller(2315095910041209493.toBigInteger(), ::probablePrime)
    time_rabin_miller(BigInteger("231509591004120949323150959100666666493131"), 1000, ::probablePrime)
    time_rabin_miller(BigInteger("231509591004120949323150959100412094999999931231509591004120949323150959100412094999999931231509591004120949323150959100412094999999931231509591004120949323150959100412094999999931231509591004120949323150959100412098888887231231509591004120949323150959100412098888887231231509591004120949323150959100412094999999931231509591004120949323150959100412094999999931"), 100, ::probablePrime)
    val rnd = Random(Long.MAX_VALUE xor Long.MAX_VALUE/2-23124154L)
    val st = BigInteger(512, rnd)
    val start = if (st.lowestSetBit != 0) {
        st
    } else {
        st + valueOf(1)
    }
    time_rabin_miller(start, 150, ::probablePrime)
}
