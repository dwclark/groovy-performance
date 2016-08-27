import java.util.function.DoubleUnaryOperator;
import groovy.transform.CompileStatic;

class Functions {

    abstract static class One implements DoubleUnaryOperator {
        @Override
        public String toString() {
            return "x^2";
        }        
    }

    final static class SlowOne extends One {
        public double applyAsDouble(double x) {
            return Math.pow(x, 2);
        }
    }

    @CompileStatic
    final static class FastOne extends One {
        public double applyAsDouble(double x) {
            return Math.pow(x, 2);
        }
    }

    public static One slowOne = new SlowOne();
    public static One fastOne = new FastOne();
}
