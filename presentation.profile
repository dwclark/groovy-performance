:clear
:purge all
:load RunnableState.groovy
:load TimeIt.groovy
:load Integrate.groovy
:load Functions.groovy
:import static TimeIt.*
:import static Functions.*
sqIntegrate = new Integrate(1_000, 0.0d, 1.0d)
