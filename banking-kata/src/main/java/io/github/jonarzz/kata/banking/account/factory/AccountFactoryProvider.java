package io.github.jonarzz.kata.banking.account.factory;

import io.github.jonarzz.kata.banking.account.AccountFactory;
import io.github.jonarzz.kata.banking.account.csv.HtmlStatementPrintingCsvAccountFactory;
import io.github.jonarzz.kata.banking.account.csv.StringStatementPrintingCsvAccountFactory;
import io.github.jonarzz.kata.banking.account.memory.HtmlStatementPrintingInMemoryAccountFactory;
import io.github.jonarzz.kata.banking.account.memory.StringStatementPrintingInMemoryAccountFactory;

public class AccountFactoryProvider {

    private StorageType storageType;

    private AccountFactoryProvider(StorageType storageType) {
        this.storageType = storageType;
    }

    public static AccountFactoryProvider storedIn(StorageType storageType) {
        return new AccountFactoryProvider(storageType);
    }

    public AccountFactory<String> printing(StatementType statementType) {
        return switch (storageType) {
            case MEMORY -> switch (statementType) {
                case STRING -> new StringStatementPrintingInMemoryAccountFactory();
                case HTML   -> new HtmlStatementPrintingInMemoryAccountFactory();
            };
            case CSV -> switch (statementType) {
                case STRING -> new StringStatementPrintingCsvAccountFactory();
                case HTML   -> new HtmlStatementPrintingCsvAccountFactory();
            };
        };
    }

}
