import groovy.transform.CompileStatic;
import java.util.function.DoubleUnaryOperator;
import java.util.concurrent.atomic.AtomicInteger;

@CompileStatic
trait IntegralUnaryFunction {
    abstract double integrate(int steps, double start, double end);

    public double delta(final int steps, final double start, final double end) {
        return Math.abs(end - start) / (double) steps;
    }

    static class Runner extends RunnableState {
        final IntegralUnaryFunction iuf;
        final int steps;
        final double start;
        final double end;
        
        Runner(final IntegralUnaryFunction iuf, final int steps, final double start, final double end) {
            this.iuf = iuf;
            this.steps = steps;
            this.start = start;
            this.end = end;
        }

        double _state;

        public Object getState() { return _state; }

        public void run() {
            _state = iuf.integrate(steps, start, end);
        }
    }

    public RunnableState runner(final int steps, final double start, final double end) {
        return new Runner(this, steps, start, end);
    }
}

class SlowRectangular implements IntegralUnaryFunction {

    final DoubleUnaryOperator op;
    
    SlowRectangular(final DoubleUnaryOperator op) {
        this.op = op;
    }
    
    double integrate(final int steps, final double start, final double end) {
        double d = delta(steps, start, end);
        int count = 0;
        double total = 0.0d;
        double xValue = start;
        
        while(count < steps) {
            total += (d * op.applyAsDouble(xValue));
            ++count;
            xValue += d;
        }

        return total;
    }
}

@CompileStatic
class FastRectangular implements IntegralUnaryFunction {

    final DoubleUnaryOperator op;
    
    FastRectangular(final DoubleUnaryOperator op) {
        this.op = op;
    }
    
    double integrate(final int steps, final double start, final double end) {
        double d = delta(steps, start, end);
        int count = 0;
        double total = 0.0d;
        double xValue = start;
        
        while(count < steps) {
            total += (d * op.applyAsDouble(xValue));
            ++count;
            xValue += d;
        }

        return total;
    }
}

@CompileStatic
class CompiledRectangular {
    static final String variable = 'tR0nhGaGDcoG3kTK5';
    static final AtomicInteger counter = new AtomicInteger();
    
    //expectes GStrings that look like this: "${x} * ${x}"
    static IntegralUnaryFunction compile(final GString gstr) {
        String toSub = gstr.strings.join(variable);
        GroovyClassLoader gcl = CompiledRectangular.classLoader as GroovyClassLoader;
        String function = """
@groovy.transform.CompileStatic
class CR_${counter.andIncrement} implements IntegralUnaryFunction {

     double integrate(final int steps, final double start, final double end) {
         double d = delta(steps, start, end);
         int count = 0;
         double total = 0.0d;
         double ${variable} = start;
        
         while(count < steps) {
             total += (d * (${toSub}));
             ++count;
             ${variable} += d;
         }

         return total;
    }
}""";

    println(function);
    return gcl.parseClass(function).newInstance() as IntegralUnaryFunction;
    }
}
