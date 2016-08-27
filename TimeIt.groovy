import groovy.transform.CompileStatic;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

class TimeIt {

    static class Info {
        TimeUnit units = NANOSECONDS;
        int ops;
        long start;
        long end;

        public Info(final int ops) {
            this.ops = ops;
            this.start = System.nanoTime();
        }

        public Info stop() {
            this.end = System.nanoTime();
            return this;
        }

        public long getTotal() {
            return end - start;
        }

        public long getSeconds() {
            return units.toSeconds(total);
        }

        public long getMilliSeconds() {
            return units.toMillis(total);
        }

        public String getDisplayUnits() {
            return units.toString().toLowerCase();
        }

        public String getDisplayUnit() {
            String tmp = displayUnits;
            return tmp.substring(0, tmp.length() - 1);
        }

        private BigDecimal scale(double val) {
            BigDecimal bd = val;
            return bd.setScale(2, RoundingMode.UP);
        }
        
        @Override
        public String toString() {
            String ret = "Total Time: ${total} ${displayUnits}, ${scale(ops / total)} ops/${displayUnits}";
            
            if(milliSeconds) {
                ret += ", ${scale(ops / milliSeconds)} ops/ms"
            }

            if(seconds) {
                ret += ", ${scale(ops / seconds)} ops/sec";
            }
            
            return ret;
        }
    }

    public static Runnable warmUp(final Integer ops, final RunnableState r) {
        ops.times { r.run(); }
        println("Warm Up State: ${r.state}");
        return r;
    }

    public static Runnable warmUp(final RunnableState r) {
        return warmUp(15_000, r);
    }

    @CompileStatic
    public static Info time(final int warmUpCycles, final int total, final RunnableState r) {
        warmUp(warmUpCycles, r);
        int counter = 0;
        Info info = new Info(total);
        while(counter < total) {
            ++counter;
            r.run();
        }

        info.stop();
        println("State: ${r.state}");
        return info;
    }
    
    @CompileStatic
    public static Info time(final int total, final RunnableState r) {
        return time(15_000, total, r);
    }

    @CompileStatic
    public static Info time(final RunnableState r) {
        return time(15_000, 100_000, r);
    }
}
