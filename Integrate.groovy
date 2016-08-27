import java.util.function.DoubleUnaryOperator;
import groovy.transform.CompileStatic;

class Integrate {

    final int steps;
    final double lower;
    final double upper;
    
    public Integrate(int steps, final double lower, final double upper) {
        this.steps = steps;
        this.lower = lower;
        this.upper = upper;
    }

    @CompileStatic
    public double getDelta() {
        return Math.abs(upper - lower) / (double) steps;
    }

    public RunnableState slowRun(final DoubleUnaryOperator function) {
        Integrate THIS = this;
        
        return new RunnableState() {
            double _state;
            public Object getState() { _state; }
            public void run() {
                _state = THIS.slow(function);
            }
        }
    }

    public double slow(final DoubleUnaryOperator function) {
        double d = delta;
        int count = 0;
        double total = 0.0d;
        double xValue = lower;
        
        while(count < steps) {
            total += (d * function.applyAsDouble(xValue));
            ++count;
            xValue += d;
        }

        return total;
    }

    public RunnableState fastRun(final DoubleUnaryOperator function) {
        Integrate THIS = this;
        
        return new RunnableState() {
            double _state;
            public Object getState() { _state; }
            public void run() {
                _state = THIS.fast(function);
            }
        }
    }

    @CompileStatic
    public double fast(final DoubleUnaryOperator function) {
        double d = delta;
        int count = 0;
        double total = 0.0d;
        double xValue = lower;
        
        while(count < steps) {
            total += (d * function.applyAsDouble(xValue));
            ++count;
            xValue += d;
        }

        return total;
    }
}
