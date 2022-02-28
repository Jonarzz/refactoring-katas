package io.github.jonarzz.kata.banking.account.memory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Fork(1)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 3, time = 3)
public class InMemoryAccountSingleThreadedBenchmark extends OperationPairExecutionBasedAccountBenchmark {

    @Benchmark
    public void run(Blackhole blackhole) {
        var account = new InMemoryAccount();
        for (int i = 0; i < OPERATION_PAIRS_COUNT; i++) {
            account.deposit(depositedAmounts.get(i));
            account.withdraw(withdrawnAmounts.get(i));
        }
        blackhole.consume(account.printStatement());
    }

}
