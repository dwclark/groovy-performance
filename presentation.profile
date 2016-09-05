:clear
:purge all
:load RunnableState.groovy
:load TimeIt.groovy
:load IntegrateFunction.groovy
:load Functions.groovy
:load Fibonacci.groovy
:load Grades.groovy
:import static TimeIt.*
:import static Functions.*

slowIntegral = new SlowRectangular(Functions.slowOne).runner(1_000, 0.0d, 1.0d);
fastIntegral = new FastRectangular(Functions.fastOne).runner(1_000, 0.0d, 1.0d);
fastComplexIntegral = new FastRectangular(Functions.fastComplex).runner(1_000, 0.0d, 1.0d);
x = 0.0d;
compiledIntegral = CompiledRectangular.compile("Math.pow(${x}, 2)").runner(1_000, 0.0d, 1.0d);
compiledComplexIntegral = CompiledRectangular.compile("$x * (($x * $x) + $x + 1.0d)").runner(1_000, 0.0d, 1.0d);

