package io.github.jonarzz.kata.score.keeper;

import static java.util.Collections.shuffle;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 3, time = 3)
public class ScoreKeeperBenchmarkRunner {

    private ScoreKeeper scoreKeeper;
    private List<Runnable> operations;

    public static void main(String[] args) throws IOException {
        Main.main(new String[] {"-rf", "json"});
    }

    @Setup(Level.Iteration)
    public void setUpIteration() {
        scoreKeeper = SimpleScoreKeeper.withMaxDigits(3);
        operations = createOperations();
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        shuffle(operations);
    }

    @Benchmark
    @Threads(1)
    public void singleThread(Blackhole blackhole) {
        runAllActions(blackhole);
    }

    @Benchmark
    @Threads(4)
    public void fourThreads(Blackhole blackhole) {
        runAllActions(blackhole);
    }

    @Benchmark
    @Threads(8)
    public void eightThreads(Blackhole blackhole) {
        runAllActions(blackhole);
    }

    private void runAllActions(Blackhole blackhole) {
        operations.forEach(Runnable::run);
        blackhole.consume(scoreKeeper.getScore());
    }

    private List<Runnable> createOperations() {
        return Arrays.asList(
                scoreKeeper::scoreTeamA1,
                scoreKeeper::scoreTeamA2,
                scoreKeeper::scoreTeamA3,
                scoreKeeper::scoreTeamB1,
                scoreKeeper::scoreTeamB2,
                scoreKeeper::scoreTeamB3
        );
    }

}
