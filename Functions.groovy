import java.util.function.DoubleUnaryOperator;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;
import groovy.transform.CompileStatic;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

class Functions {

    final static class SlowOne implements DoubleUnaryOperator {
        public double applyAsDouble(double x) {
            return Math.pow(x, 2);
        }
    }

    @CompileStatic
    final static class FastOne implements DoubleUnaryOperator {
        public double applyAsDouble(double x) {
            return Math.pow(x, 2);
        }
    }

    @CompileStatic
    final static class FastComplex implements DoubleUnaryOperator {
        public double applyAsDouble(double x) {
            return x * ((x * x) + x + 1.0d);
        }
    }

    @TypeChecked
    final static class TypeCheckedOne implements DoubleUnaryOperator {
        public double applyAsDouble(double x) {
            return Math.pow(x, 2);
        }
    }

    public static final DoubleUnaryOperator slowOne = new SlowOne();
    public static final DoubleUnaryOperator fastOne = new FastOne();
    public static final DoubleUnaryOperator fastComplex = new FastComplex();
    public static final DoubleUnaryOperator typeCheckedOne = new TypeCheckedOne();

    //NOTE, these methods are not compiled static, but their outputs are
    public static DoubleUnaryOperator compile(final GString functionBody) {
        String variable = 'tR0nhGaGDcoG3kTK5';
        String finalBody =  functionBody.strings.join(variable);
        
        String function = """
@groovy.transform.CompileStatic
class Func_${System.currentTimeMillis()} implements java.util.function.DoubleUnaryOperator {
    public double applyAsDouble(double ${variable}) {
        ${finalBody}
    }
}""";

        GroovyClassLoader gcl = Functions.classLoader as GroovyClassLoader;
        return gcl.parseClass(function).newInstance() as DoubleUnaryOperator;
    }

    public static DoubleUnaryOperator fromScript(final Reader reader) {
        ImportCustomizer ic = new ImportCustomizer();
        ic.addImport('java.util.function.DoubleUnaryOperator');
        ASTTransformationCustomizer ast = new ASTTransformationCustomizer(CompileStatic);
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.addCompilationCustomizers(ic, ast);
        GroovyShell shell = new GroovyShell(Functions.classLoader, cc);
        return (DoubleUnaryOperator) shell.evaluate(reader);
    }

    public static DoubleUnaryOperator fromScript(final File file) {
        file.withReader { Reader r -> return fromScript(r); };
    }
}
