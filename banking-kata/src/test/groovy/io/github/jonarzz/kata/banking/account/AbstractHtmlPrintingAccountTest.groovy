package io.github.jonarzz.kata.banking.account


import java.util.stream.Stream

import static java.util.stream.Collectors.joining

abstract class AbstractHtmlPrintingAccountTest extends AbstractValidatedAccountTest {

    def "Kata acceptance test"() {
        when: "deposit"
            testClock.set(createInstant(2015, 12, 24))
            account.deposit(500)
        and: "withdraw"
            testClock.set(createInstant(2016, 8, 23))
            account.withdraw(100)

        then: "print statement"
            account.printStatement() == removeIndent(
                    "<table>",
                    "  <thead>",
                    "    <tr>",
                    "      <th>Date</th>",
                    "      <th>Amount</th>",
                    "      <th>Balance</th>",
                    "    </tr>",
                    "  </thead>",
                    "  <tbody>",
                    "    <tr>",
                    "      <td>24.12.2015</td>",
                    "      <td>+500</td>",
                    "      <td>500</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>23.8.2016</td>",
                    "      <td>-100</td>",
                    "      <td>400</td>",
                    "    </tr>",
                    "  </tbody>",
                    "</table>"
            )
    }

    def "Statement without any operation"() {
        when:
            def statement = account.printStatement()

        then: "print statement"
            statement == removeIndent(
                    "<table>",
                    "  <thead>",
                    "    <tr>",
                    "      <th>Date</th>",
                    "      <th>Amount</th>",
                    "      <th>Balance</th>",
                    "    </tr>",
                    "  </thead>",
                    "</table>"
            )
    }

    def "Multiple depositions and withdrawals"() {
        given:
            testClock.set(createInstant(2020, 12, 21))

        when: "deposit and withdraw multiple times"
            account.deposit(500)
            account.withdraw(20)
            account.deposit(600)
            account.withdraw(900)
            account.deposit(100)
            account.withdraw(250)

        then: "print statement"
            account.printStatement() == removeIndent(
                    "<table>",
                    "  <thead>",
                    "    <tr>",
                    "      <th>Date</th>",
                    "      <th>Amount</th>",
                    "      <th>Balance</th>",
                    "    </tr>",
                    "  </thead>",
                    "  <tbody>",
                    "    <tr>",
                    "      <td>21.12.2020</td>",
                    "      <td>+500</td>",
                    "      <td>500</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>21.12.2020</td>",
                    "      <td>-20</td>",
                    "      <td>480</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>21.12.2020</td>",
                    "      <td>+600</td>",
                    "      <td>1080</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>21.12.2020</td>",
                    "      <td>-900</td>",
                    "      <td>180</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>21.12.2020</td>",
                    "      <td>+100</td>",
                    "      <td>280</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>21.12.2020</td>",
                    "      <td>-250</td>",
                    "      <td>30</td>",
                    "    </tr>",
                    "  </tbody>",
                    "</table>"
            )
    }

    def "Operations performed on various dates"() {
        when: "deposit and withdraw multiple times at various dates"
            testClock.set(createInstant(2019, 1, 1))
            account.deposit(100)
            testClock.set(createInstant(2019, 10, 1))
            account.withdraw(100)
            testClock.set(createInstant(2020, 2, 15))
            account.deposit(200)
            testClock.set(createInstant(2021, 7, 7))
            account.withdraw(200)

        then: "print statement"
            account.printStatement() == removeIndent(
                    "<table>",
                    "  <thead>",
                    "    <tr>",
                    "      <th>Date</th>",
                    "      <th>Amount</th>",
                    "      <th>Balance</th>",
                    "    </tr>",
                    "  </thead>",
                    "  <tbody>",
                    "    <tr>",
                    "      <td>1.1.2019</td>",
                    "      <td>+100</td>",
                    "      <td>100</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>1.10.2019</td>",
                    "      <td>-100</td>",
                    "      <td>0</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>15.2.2020</td>",
                    "      <td>+200</td>",
                    "      <td>200</td>",
                    "    </tr>",
                    "    <tr>",
                    "      <td>7.7.2021</td>",
                    "      <td>-200</td>",
                    "      <td>0</td>",
                    "    </tr>",
                    "  </tbody>",
                    "</table>"
            )
    }

    private static String removeIndent(String... htmlRows) {
        return Stream.of(htmlRows)
                .map(String::trim)
                .collect(joining())
    }

}