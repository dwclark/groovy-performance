import java.util.concurrent.TimeUnit;

public class JavaStuff {

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

    public static void main(String[] args) {
        final long val = Long.parseLong(args[0]);
        final RunnableState rs = arrays(100);
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
