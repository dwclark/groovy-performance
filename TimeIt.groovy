import groovy.transform.CompileStatic;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

@CompileStatic
class TimeIt {

    static class Info {
        TimeUnit units = NANOSECONDS;
        long ops;
        long total;
        long start;
        
        public Info(final long ops) {
            this.ops = ops;
            this.start = System.nanoTime();
        }

        public Info(final long ops, final long total) {
            this.ops = ops;
            this.total = total;
        }

        public Info plus(Info info) {
            return new Info(ops + info.ops,
                            total + info.total);
        }

        public Info stop() {
            this.total = System.nanoTime() - start;
            return this;
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
            return "Total Time: ${total} ${displayUnits}, ${ops / milliSeconds} ops/ms";
        }
    }

    public void iteration(final RunnableState r) {
        long counter = 0L;
        while(counter < r.ops) {
            r.run();
        }
    }
    
    public TimeIt warmUp(final Integer ops, final RunnableState r) {
        r.ops = ops;
        Info info = new Info(ops);

        if(r.doesIteration) {
            r.run();
        }
        else {
            iteration(r);
        }
        
        info.stop();
        println("Warm Up State: ${r.state}");
        return this;
    }

    public TimeIt warmUp(final RunnableState r) {
        return warmUp(15_000, r);
    }

    public Info time(final long ops, final RunnableState r) {
        r.ops = ops;
        Info info = new Info(ops);
        
        if(r.doesIteration) {
            r.run();
        }
        else {
            iteration(r);
        }
        
        info.stop();
        println("Type: ${r.getClass()}, State: ${r.state}");
        return info;
    }

    public Info time(final RunnableState r) {
        return time(100_000L, r);
    }
}
