//clean up system
:clear
:purge all

//recompile java code
new File('.').eachFile { f -> if(f.name.endsWith('.class')) { f.delete(); }; }
new File('.').eachFile { f ->
    if(f.name.endsWith('.java')) {
        "javac ${f.canonicalPath}".execute();
    } 
}

//grab fast util
groovy.grape.Grape.grab(group: 'it.unimi.dsi', module: 'fastutil', version: '7.0.13')

:load TimeIt.groovy
timer = new TimeIt()
:load Groovy.groovy
forLoop = Groovy.forLoop(100)
timer.warmUp(forLoop)
whileLoop = Groovy.whileLoop(100)
timer.warmUp(whileLoop)
fastUtil = FastUtil.fastUtil(100)
timer.warmUp(30_000, fastUtil)
javaArrays = Java.arrays(100)
timer.warmUp(javaArrays)
objectArray = Groovy.objectArray(100);
timer.warmUp(objectArray);
javaObjectArray = Java.objects(100);
timer.warmUp(javaObjectArray)
groovySwitch = Groovy.groovySwitch();
timer.warmUp(groovySwitch);
javaSwitch = Java.javaSwitch()
timer.warmUp(javaSwitch);
groovyReferenceEquals = Groovy.referenceEquals()
timer.warmUp(groovyReferenceEquals)
javaReferenceEquals = Java.referenceEquals();
timer.warmUp(javaReferenceEquals);
groovyEquality = Groovy.equality(100)
timer.warmUp(groovyEquality)
javaEquality = Java.equality(100)
timer.warmUp(javaEquality)

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

