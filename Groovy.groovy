import groovy.transform.CompileStatic;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

@CompileStatic
public class Groovy {

    public static class For extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }

        final int width;

        For(final int width) {
            this.width = width;
        }
        
        public void run() {
            final int[] ary = new int[width];
            final int times = (int) getOps();
            
            for(int i = 0; i < times; ++i) {
                int index = (int) (i % width);
                ary[index] += 1;
            }
            
            setState(ary[width - 1]);
        }
    }

    public static RunnableState forLoop(final int width) {
        return new For(width);
    }

    public static class While extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }

        final int width;

        While(final int width) {
            this.width = width;
        }
        
        public void run() {
            final int[] ary = new int[width];
            final int times = (int) getOps();
            int i = 0;

            while(i < times) {
                int index = (int) (i % width);
                ary[index] += 1;
                ++i
            }
            
            setState(ary[width - 1]);
        }
    }

    public static RunnableState whileLoop(final int width) {
        return new While(width);
    }

    public static class ObjectArray extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }

        final int width;
        final Object[] source;
        final int sourceWidth; 
        
        public ObjectArray(final int width) {
            this.width = width;
            this.sourceWidth = width + 31;
            source = new Object[sourceWidth];
            for(int i = 0; i < sourceWidth; ++i) {
                source[i] = "${i}" as String;
            }
        }

        public void run() {
            Object[] target = new Object[width];
            
            int times = (int) getOps();
            for(int i = 0; i < times; ++i) {
                target[i % width] = source[i % sourceWidth];
            }
            
            setState(target[width - 1]);
        }
    }
    
    public static RunnableState objectArray(final int width) {
        return new ObjectArray(width);
    }

    public static class Switch extends RunnableState {

        public void run() {
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            int findThis = tlr.nextInt(10);
            String str;
            switch(findThis) {
            case 0: state = "0"; return;
            case 1: state = "1"; return;
            case 2: state = "2"; return;
            case 3: state = "3"; return;
            case 4: state = "4"; return;
            case 5: state = "5"; return;
            case 6: state = "6"; return;
            case 7: state = "7"; return;
            case 8: state = "8"; return;
            case 9: state = "9"; return;
            default: return;
            }
        }
    }

    public static RunnableState groovySwitch() {
        return new Switch();
    }

    public static class ReferenceEquals extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }
        
        final Object[] ary;
        final TimeUnit toCmp = TimeUnit.SECONDS;

        //comment next line to see groovy casting mess things up performance wise
        Boolean state;
        
        public ReferenceEquals() {
            ary = new Object[TimeUnit.values().length];
            TimeUnit.values().eachWithIndex { def tu, int index -> ary[index] = tu; };
        }

        public void run() {
            int mod = ary.length;
            int times = (int) ops;
            
            for(int i = 0; i < times; ++i) {
                state = toCmp.is(ary[i % mod]) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
    }

    public static RunnableState referenceEquals() {
        return new ReferenceEquals();
    }

    static class Pair {
        //mutable for easy use later
        int one;
        int two;

        public Pair(final int one, final int two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public boolean equals(final Object rhs) {
            if(!(rhs instanceof Pair)) {
                return false;
            }

            Pair toCmp = (Pair) rhs;
            return (one == toCmp.one && two == toCmp.two);
        }

        @Override
        public int hashCode() {
            return one + two;
        }
    }

    public static class Equality extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }
        
        final Pair[] pairs;

        public Equality(final int num) {
            List<Pair> tmp = [];
            for(int i = 0; i < num; ++i) {
                tmp << new Pair(i, i + 1);
            }

            this.pairs = tmp as Pair[];
        }

        public void run() {
            int mod = pairs.length;
            int times = (int) ops;
            Pair mutable = new Pair(0, 0);
            boolean result;
            
            for(int i = 0; i < times; ++i) {
                mutable.one = i;
                mutable.two = i + 1;
                //fast version
                result = mutable.equals(pairs[i % mod]);
                //slow version:
                //result = mutable == pairs[i % mod];
            }

            state = result;
        }
    }

    public static RunnableState equality(final int num) {
        return new Equality(num);
    }

    public static void main(String[] args) {
        final long val = Long.parseLong(args[0]);
        final RunnableState rs = equality(100); //referenceEquals(); //objectArray(100);
        rs.setOps(val);
        rs.run();
        long start = System.nanoTime();
        rs.run();
        long end = System.nanoTime();
        long millis = TimeUnit.NANOSECONDS.toMillis(end - start);
        System.out.println("Total ops: " + rs.getOps() + " Total Time: " + millis +
                           " ms, ops/ms " + rs.getOps() / millis);
    }
}
