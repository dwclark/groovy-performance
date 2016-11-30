# Groovy Performance

This repo contains all of the code necessary to run the samples from the groovy-performance demonstration. You do need to have `javac` on your command line path so that the groovy shell can auto compile the java samples. I have tested this on MacOS and Linux, however I don't have a Windows machine to test on. I'm guessing that a proper `bash` and/or `bash + cygwin` setup will run this without problems.

## How to run

1. Open a command prompt and `cd` to the directory where you have checked out the presentation code.
2. Execute the groovy shell using the `groovysh` command.
3. You are now in the groovy shell, for a good tutorial on the groovy shell, check out [the groovy docs](http://groovy-lang.org/groovysh.html).
4. Type `:load profile` in the groovy shell, this will load all code and compile the java code as well.
5. To see what is available at any time, type `:show variables`. The variables here are just pieces of code you can use to time executions. The names of the variables should give you a hint as to what is being tested.
6. Timing something is just a matter of executing `timer.time <iterations> <variable>` where variable is someting that implements `RunnableState`

## Details

This is a simple reloadable framework for timing JVM based code. Microbenchmaring is a tricky business and can give a lot of phony results. What we are really after when benchmarking code are obvious differences. Execution times of code can vary widely between runs so what we are really looking for are obvious and repeatable results. As an example let's suppose you have 3 pieces of code that do the same thing. Let's also suppose that when benchmarking them we get these results:

| Code ID  | Time To Execute |
|:--------:| ---------------:|
| A        | 1.5 seconds     |
| B        | 1.4 seconds     |
| C        | 100 millis      |

Given those results, is B faster than A? Probably not. Even if it is "faster" you probably shouldn't bother swapping B for A. However, C is clearly faster than B or A and should probably replace them in a performance sensitive application.

There are lots of caveats to doing this kind of testing and lots of pitfalls. First, the JVM will hotspot compile your code after it has executed a certain number of times. The default for modern JVM's is 10,000 executions. You can force the JVM hotspot compilation to kick in by warming up your code propertly *before* you execute it. To do this the timer provides a `warmUp` method.

The other problem that commonly happens is that the JVM is sometimes smart enough to optimize your function out of existence. If you have a pure function with no side effects and you do not consume the return value the JVM will sometimes convert your function into a no-op. If you start seeing too-good-to-be-true results when timing your code, this may have happened. To guard against this, the `RunnableState` interface provides a `setState` method which the timer will consume. Once your computations are done, set that value and the timer will consume it, forcing the JVM to always run your code.

## Diagnosing Performance Issues

The `perf/PerformanceIssues.groovy` file is designed with several potential performance issues. Performance is always relative, so this class may or may not be perfomant enough in different scenarios. Here's some possible ways to diagnose performance issues.

First, we need to compile the script because most JVM/Groovy performance diagnostic tools work best when they can find the class files. So make sure you are in the source code directory for this project, then compile the `PerformanceIssues.groovy` files using this command: `groovyc perf/PerformanceIssues.groovy`.

### Using YourKit

Performance issues will run in a continuous loop to facilitate attaching a profiler. I launch the process using the following command: `java -cp <path_to_groovy_installation>/lib/groovy-2.4.7.jar:. perf.PerformanceIssues`. Do the following to profile the application:

1. Launch YourKit
2. In the upper left corner YourKit will list the Java processes running on your machine. Click on it to attach.
3. Click on the tool bar icon that says *Start CPU Profiling.
4. Wait for YourKit to gather data. Note, the time listed by YourKit is not clock time, but the total amount of CPU time consumed. So if you have 8 cores and you are using all of them you will accumulate approximately 8 seconds of data for every second of wall time.
5. Click the *capture snapshot* button.
6. Click *open*
7. Use the *Call Tree* and *Hot Spots* to locate suspicious looking code
8. Modify your code, recompile, and re-run.
9. Did you do any better, go back to step #2 and find out.

"Suspcious looking code" is very vague, but basically any line of code that you didn't expect to see or can't justify is grounds for suspicion. It may turn out that the code is needed, but you need to work by process of elimination to see if you can make the code faster. Examples of suspicious looking code would be code numeric code that spends lots of time doing string manipulation. A common source of groovy performance issues are the groovy "magic" methods that are injected into your code to make it, well, Groovy.

### Using JD-GUI

Since Groovy does inject code it sometimes can be hard to figure out what is actually being run by the JVM. By the way, this problem is not unique to Groovy. Java 8 makes heavy use of dynamic code generation to make reflection go faster and also injects lots of lambda related code. If you use a runtime instrumentation product such as AppDynamics, it will also inject lots of bytecode. Using Spring? It will inject a ton of bytecode using AspectJ. The point is that Groovy shoult *NOT* be avoided because of code injection.

Anyway, once you have identified problem lines using YourKit, open up JD-GUI and then use the file menu to open up the .class file for `PerformanceIssues.groovy`. The source code lines on the left should reflect the lines in your groovy code. Using these line numbers and your groovy file you should be able to locate the problem code and fix it.

You can also try IntelliJ's Bytecode Decompiler, but this is less useful than JD-GUI because IntelliJ doesn't seem to make an effort to make decompiled lines line up with the original lines in your code.

### Using JIT Watch

The JVM is an awesome piece of engineering. The Hotspot compiler does a lot of work and is a large reason why Groovy is as fast as it is. Sometimes getting insight into how the JVM optimizes your code is very helpful in knowing why it is slow and what you can do to help the JVM do its job. To use JIT Watch do the following:

1. You need to run `PerformanceIssues` with extra instrumentation. The magic incantation is this: `java -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation -XX:+TraceClassLoading -XX:+PrintAssembly -cp <path_to_groovy_installation>/lib/groovy-2.4.7.jar:. perf.PerformanceIssues`.
2. Once the JVM stops barfing instrumentation it's safe to kill the program.
3. Open up JIT Watch. You will probably have to download JIT Watch, compile it yourself, and run it using this command: `./gradlew clean build run`.
4. Click *Open Log*, navigate to source code directory, and open up the hotspot_pidxxxxx.log file.
5. Click *Start* (Yes this tool violates UI principles, cut them some slack, they are perf guys).
6. Expand the *perf* tree and then click on *PerformanceIssues*
7. Now highlight the method you want to investigate, it will be one of the methods in the `PerformanceIssues.groovy` file.
8. Click *TriView*
9. The Tri View shows your source code on the left, the bytecode in the middle, and assembly info on the left. The bytecode is what the JVM is actuall seeing.
10. Finally, click on *Chain*. This is short for call chain and will show you the call graph for the method you are investigating.

The call graph gives you information about code compilation and code inlining. In generally code that is both inlined and compiled will execute the fastest, while code that is neither will be the slowest. Code will generally not be inlined unless it is sufficiently short. Decisions about code compilation are more complicated, but in general well organized short methods will be more likely to be compiled and executed quickly.
