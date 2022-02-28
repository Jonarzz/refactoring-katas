package io.github.jonarzz.kata.banking.account.memory;

import static java.util.stream.Collectors.toList;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
abstract class OperationPairExecutionBasedAccountBenchmark {

    static final int OPERATION_PAIRS_COUNT = 100;

    static final SecureRandom RANDOM = new SecureRandom();

    List<Integer> depositedAmounts;
    List<Integer> withdrawnAmounts;

    @Setup(Level.Iteration)
    public void setUpIteration() {
        depositedAmounts = IntStream.range(0, OPERATION_PAIRS_COUNT)
                                    .map(i -> RANDOM.nextInt(1, 10_000))
                                    .boxed()
                                    .collect(toList());
        withdrawnAmounts = depositedAmounts.stream()
                                           .map(RANDOM::nextInt)
                                           .collect(toList());
    }

}
