package io.github.jonarzz.kata.fizz.buzz;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
public class FizzBuzzBenchmarkRunner {

    @Param({
            "io.github.jonarzz.kata.fizz.buzz.policy.PolicyBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.DivisibilityRuleBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.DumbArrayBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.DumbListBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.DumbMapBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.EagerModuloBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.InvertedNestedModuloBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.ModuloBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.NestedModuloBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.SimpleModuloBasedFizzBuzz",
            "io.github.jonarzz.kata.fizz.buzz.StringConcatenatingFizzBuzz"
    })
    private String fizzBuzzClassName;

    private FizzBuzz fizzBuzz;

    private List<Integer> inputs = IntStream.rangeClosed(1, 100)
                                            .boxed()
                                            .collect(toList());

    public static void main(String[] args) throws IOException {
        Main.main(new String[] {"-rf", "json"});
    }

    @Setup(Level.Trial)
    public void setUpIteration() throws Exception {
        fizzBuzz = (FizzBuzz) Class.forName(fizzBuzzClassName)
                                   .getDeclaredConstructor()
                                   .newInstance();
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        shuffle(inputs);
    }

    @Benchmark
    @Threads(1)
    public void run(Blackhole blackhole) {
        for (var input : inputs) {
            blackhole.consume(fizzBuzz.fizzbuzz(input));
        }
    }

}
