import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

public class Java {

    public static RunnableState arrays(final int width) {
        
        return new RunnableState() {
            @Override public boolean getDoesIteration() { return true; }
            public void run() {
                final int[] ary = new int[width];
                final int times = (int) getOps();
                
                for(int i = 0; i < times; ++i) {
                    ary[i % width] += 1;
                }
                
                setState(ary[width - 1]);
            }
        };
    }

    private static class ObjectArray extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }
        
        final int width;
        final Object[] source;
        final int sourceWidth; 
        
        public ObjectArray(final int width) {
            this.width = width;
            this.sourceWidth = width + 31;
            source = new Object[sourceWidth];
            for(int i = 0; i < sourceWidth; ++i) {
                source[i] = Integer.toString(i);
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
    
    public static RunnableState objects(final int width) {
        return new ObjectArray(width);
    }

    public static RunnableState javaSwitch() {
        return new RunnableState() {
            public void run() {
                ThreadLocalRandom tlr = ThreadLocalRandom.current();
                int findThis = tlr.nextInt(10);
                String str;
                switch(findThis) {
                case 0: setState("0"); return;
                case 1: setState("1"); return;
                case 2: setState("2"); return;
                case 3: setState("3"); return;
                case 4: setState("4"); return;
                case 5: setState("5"); return;
                case 6: setState("6"); return;
                case 7: setState("7"); return;
                case 8: setState("8"); return;
                case 9: setState("9"); return;
                default: return;
                }
            }
        };
    }

    public static class ReferenceEquals extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }
        
        final Object[] ary;
        final TimeUnit toCmp = TimeUnit.SECONDS;
        
        public ReferenceEquals() {
            ary = new Object[TimeUnit.values().length];
            TimeUnit[] vals = TimeUnit.values();
            for(int i = 0; i < vals.length; ++i) {
                ary[i] = vals[i];
            }
        }

        public void run() {
            int mod = ary.length;
            int times = (int) getOps();
            for(int i = 0; i < times; ++i) {
                setState(toCmp == ary[i % mod] ? Boolean.TRUE : Boolean.FALSE);
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
            this.pairs = new Pair[num];
            for(int i = 0; i < num; ++i) {
                this.pairs[i] = new Pair(i, i + 1);
            }
        }

        public void run() {
            int mod = pairs.length;
            int times = (int) getOps();
            Pair mutable = new Pair(0, 0);
            boolean result = false;
            
            for(int i = 0; i < times; ++i) {
                mutable.one = i;
                mutable.two = i + 1;
                result = mutable == pairs[i % mod];
            }

            setState(result);
        }
    }

    public static RunnableState equality(final int num) {
        return new Equality(num);
    }

            
    public static void main(String[] args) {
        final long val = Long.parseLong(args[0]);
        final RunnableState rs = equality(100); //objects(100);
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
