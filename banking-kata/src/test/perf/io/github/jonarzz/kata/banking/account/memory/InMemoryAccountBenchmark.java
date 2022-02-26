package io.github.jonarzz.kata.banking.account.memory;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@Fork(1)
@Warmup(iterations = 5, time = 10)
@Measurement(iterations = 5, time = 10)
public class InMemoryAccountBenchmark {

    public static void main(String[] args) throws IOException {
        Main.main(new String[] {"-rf", "json"});
    }

    @Benchmark
    public void singleThread(Blackhole blackhole) {
        var random = new SecureRandom();
        var account = new InMemoryAccount();
        for (int i = 0; i < 100; i++) {
            var depositedAmount = random.nextInt(1, 10_000);
            var withdrawnAmount = random.nextInt(depositedAmount);
            account.deposit(depositedAmount);
            account.withdraw(withdrawnAmount);
        }
        blackhole.consume(account.printStatement());
    }

    @Benchmark
    public void multipleThreads(Blackhole blackhole) throws InterruptedException {
        var random = new SecureRandom();
        var account = new InMemoryAccount();
        var threadPool = Executors.newFixedThreadPool(4);
        var tasksCount = 100;
        var latch = new CountDownLatch(tasksCount);

        for (var i = 1; i <= tasksCount; i++) {
            threadPool.execute(() -> {
                var depositedAmount = random.nextInt(1, 10_000);
                var withdrawnAmount = random.nextInt(depositedAmount);
                account.deposit(depositedAmount);
                account.withdraw(withdrawnAmount);
            });
            latch.countDown();
        }
        latch.await();
        threadPool.shutdownNow();

        blackhole.consume(account.printStatement());
    }

}
