//import it.unimi.dsi.fastutil.ints.IntArrayList;
import groovy.transform.CompileStatic;
import java.util.concurrent.TimeUnit;
@CompileStatic
public class GroovyStuff {

    public static class ForRunnableState extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }

        final int width;

        ForRunnableState(final int width) {
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

    public static RunnableState arraysForLoop(final int width) {
        return new ForRunnableState(width);
    }

    public static class WhileRunnableState extends RunnableState {
        @Override public boolean getDoesIteration() { return true; }

        final int width;

        WhileRunnableState(final int width) {
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

    public static RunnableState arraysWhileLoop(final int width) {
        return new WhileRunnableState(width);
    }

    // public static class FastUtilRunnableState extends RunnableState {
    //     @Override public boolean getDoesIteration() { return true; }

    //     final int width;

    //     public FastUtilRunnableState(final int width) {
    //         this.width = width;
    //     }
        
    //     public void run() {
    //         IntArrayList ary = new IntArrayList(width);
    //         ary.size(width);
            
    //         int times = (int) getOps();
    //         int i = 0;
    //         while(i < times) {
    //             ++i;
    //             int index = (int) (i % width);
    //             ary.set(index, ary.get(index) + 1);
    //         }
            
    //         setState(ary.get(width - 1))
    //     }
    // }

    // public static RunnableState fastUtil(final int width) {
    //     return new FastUtilRunnableState(width);
    // }

    public static void main(String[] args) {
        final long val = Long.parseLong(args[0]);
        final RunnableState rs = arraysWhileLoop(100);
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
