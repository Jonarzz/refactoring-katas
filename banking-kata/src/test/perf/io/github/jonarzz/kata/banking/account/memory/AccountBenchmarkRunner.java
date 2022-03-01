package io.github.jonarzz.kata.banking.account.memory;

import io.github.jonarzz.kata.banking.account.Account;
import io.github.jonarzz.kata.banking.account.AccountFactory;
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
import java.security.SecureRandom;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class AccountBenchmarkRunner {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AccountFactory<String> ACCOUNT_FACTORY = new StringStatementPrintingInMemoryAccountFactory();

    private Account<String> account;

    private int depositedAmount;
    private int withdrawnAmount;

    public static void main(String[] args) throws IOException {
        Main.main(new String[] {"-rf", "json"});
    }

    @Setup(Level.Iteration)
    public void setUpIteration() {
        account = ACCOUNT_FACTORY.createAccount();
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        depositedAmount = RANDOM.nextInt(1, 10_000);
        withdrawnAmount = RANDOM.nextInt(depositedAmount);
    }

    @Benchmark
    @Threads(1)
    public void singleThread(Blackhole blackhole) {
        runAllActions(blackhole);
    }

    @Benchmark
    @Threads(4)
    public void multipleThreads(Blackhole blackhole) {
        runAllActions(blackhole);
    }

    private void runAllActions(Blackhole blackhole) {
        account.deposit(depositedAmount);
        account.withdraw(withdrawnAmount);
        blackhole.consume(account.printStatement());
    }

}
