import java.util.function.DoubleUnaryOperator;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;

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

    @CompileStatic
    final static class FastComplex extends One {
        public double applyAsDouble(double x) {
            return x * ((x * x) + x + 1.0d);
        }
    }

    @TypeChecked
    final static class TypeCheckedOne extends One {
        public double applyAsDouble(double x) {
            return Math.pow(x, 2);
        }
    }

    public static final One slowOne = new SlowOne();
    public static final One fastOne = new FastOne();
    public static final One fastComplex = new FastComplex();
    public static final One typeCheckedOne = new TypeCheckedOne();
}
