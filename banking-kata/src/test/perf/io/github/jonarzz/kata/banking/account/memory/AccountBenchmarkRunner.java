package io.github.jonarzz.kata.banking.account.memory;

import org.openjdk.jmh.Main;

import java.io.IOException;

public class AccountBenchmarkRunner {

    public static void main(String[] args) throws IOException {
        Main.main(new String[] {"-rf", "json"});
    }

}
