#Groovy Performance

This repo contains all of the code necessary to run the samples from the groovy-performance demonstration. You do need to have `javac` on your command line path so that the groovy shell can auto compile the java samples. I have tested this on MacOS and Linux, however I don't have a Windows machine to test on. I'm guessing that a proper `bash` and/or `bash + cygwin` setup will run this without problems.

##How to run

Open a command prompt and `cd` to the directory where you have checked out the presentation code.
Execute the groovy shell using the `groovysh` command.
You are now in the groovy sheel, for a good tutorial on the groovy shell, check out [the groovy docs](http://groovy-lang.org/groovysh.html).
Type `:load profile` in the groovy shell, this will load all code and compile the java code as well.
To see what is available at any time, type `:show variables`. The variables here are just pieces of code you can use to time executions. The names of the variables should give you a hint as to what is being tested.
Timing something is just a matter of executing `timer.time <iterations> <variable>` where variable is someting that implements `RunnableState`

##Details

This is a simple reloadable framework for timing JVM based code. Microbenchmaring is a tricky business and can give a lot of phony results. What we are really after when benchmarking code are obvious differences. Execution times of code can vary widely between runs so what we are really looking for are obvious and repeatable results. As an example let's suppose you have 3 pieces of code that do the same thing. Let's also suppose that when benchmarking them we get these results:

| Code ID  | Time To Execute |
|:--------:| ---------------:|
| A        | 1.5 seconds     |
| B        | 1.4 seconds     |
| C        | 100 millis      |

Given those results, is B faster than A? Probably not. Even if it is "faster" you probably shouldn't bother swapping B for A. However, C is clearly faster than B or A and should probably replace them in a performance sensitive application.

There are lots of caveats to doing this kind of testing and lots of pitfalls. First, the JVM will hotspot compile your code after it has executed a certain number of times. The default for modern JVM's is 10,000 executions. You can force the JVM hotspot compilation to kick in by warming up your code propertly *before* you execute it. To do this the timer provides a `warmUp` method.

The other problem that commonly happens is that the JVM is sometimes smart enough to optimize your function out of existence. If you have a pure function with no side effects and you do not consume the return value the JVM will sometimes convert your function into a no-op. If you start seeing too-good-to-be-true results when timing your code, this may have happened. To guard against this, the `RunnableState` interface provides a `setState` method which the timer will consume. Once your computations are done, set that value and the timer will consume it, forcing the JVM to always run your code.
