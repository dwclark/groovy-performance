import groovy.transform.CompileStatic;
import groovy.transform.TailRecursive;
import groovy.transform.Memoized;

@CompileStatic
class Fibonacci {

    static void display() {
        println("""
F0	F1	F2	F3	F4	F5	F6	F7	F8	F9	F10	F11	F12	F13	F14	F15	F16	F17	 F18  F19  F20
0	1	1	2	3	5	8	13	21	34	55	89	144	233	377	610	987	1597 2584 4181 6765""");
    }

    //NOTE: Both recursive and memoized have a bug in that they throw StackOverflow
    //for large values of 
    static BigInteger recursive(final BigInteger val) {
        if(val < 0G) {
            return 0G;
        }
        else if(val == 1G) {
            return 1G;
        }
        else {
            return recursive(val - 1G) + recursive(val - 2G);
        }
    }

    @Memoized
    static BigInteger memoized(final BigInteger val) {
        if(val == 0G) {
            return 0G;
        }
        else if(val == 1G) {
            return 1G;
        }
        else {
            return memoized(val - 1G) + memoized(val - 2G);
        }
    }

    static BigInteger iterative(final BigInteger val) {
        BigInteger x = 0G;
        BigInteger y = 1G;
        BigInteger z = 1G;
        
        for(BigInteger i = 0G; i < val; i++) {
            x = y;
            y = z;
            z = x + y;
        }

        return x;
    }

    @TailRecursive
    private static BigInteger tailIter(final BigInteger prev, final BigInteger next, final BigInteger count) { 
        if(count == 0) {
            return prev;
        }
        else {
            return tailIter(next, next + prev, count - 1G);
        }
    }
    
    static BigInteger tailRecursive(final BigInteger val) {
        if(val == 0G) {
            return 0G;
        }
        else if(val == 1G) {
            return 1G;
        }
        else {
            return tailIter(0G, 1G, val) 
        }
    }
}
