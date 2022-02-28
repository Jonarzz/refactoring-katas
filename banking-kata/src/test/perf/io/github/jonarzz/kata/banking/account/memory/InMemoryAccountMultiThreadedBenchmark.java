package io.github.jonarzz.kata.banking.account.memory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Fork(1)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 3)
public class InMemoryAccountMultiThreadedBenchmark extends OperationPairExecutionBasedAccountBenchmark {

    private ExecutorService executorService;
    private CountDownLatch latch;

    @Setup(Level.Iteration)
    public void setUpTrial() {
        executorService = Executors.newFixedThreadPool(4);
        latch = new CountDownLatch(OPERATION_PAIRS_COUNT);
    }

    @TearDown(Level.Iteration)
    public void cleanUpTrial() {
        executorService.shutdownNow();
    }

    @Benchmark
    public void run(Blackhole blackhole) throws InterruptedException {
        var account = new InMemoryAccount();
        for (var i = 0; i < OPERATION_PAIRS_COUNT; i++) {
            var depositedAmount = depositedAmounts.get(i);
            var withdrawnAmount = withdrawnAmounts.get(i);
            executorService.execute(() -> {
                account.deposit(depositedAmount);
                account.withdraw(withdrawnAmount);
            });
            latch.countDown();
        }
        latch.await();
        blackhole.consume(account.printStatement());
    }

}
