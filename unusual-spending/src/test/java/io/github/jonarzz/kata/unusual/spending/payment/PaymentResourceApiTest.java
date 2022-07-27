package io.github.jonarzz.kata.unusual.spending.payment;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(IsolatedTestProfile.class)
class PaymentResourceApiTest {

    // data loaded by Liquibase (see: data/payment.csv in test resources)

    @Test
    void getAllPaymentsForUserId() {
        var response = sendGraphQlRequest(
                """
                        {
                          "query": "{
                            userPayments (userId: 2) {
                              cost {
                                amount
                                currency {
                                  alphaCode
                                }
                              }
                            }
                          }"
                        }""");

        assertPayments(response)
                .allSatisfy(result -> assertThat(result)
                        .asInstanceOf(MAP)
                        .containsOnlyKeys("cost"))
                .extracting("cost.amount", "cost.currency.alphaCode")
                .containsExactly(
                        tuple(22.71f, "USD"),
                        tuple(133.99f, "USD")
                );
    }

    @Test
    void getAllPaymentsForUserIdThatDoesNotExist() {
        var response = sendGraphQlRequest(
                """
                        {
                          "query": "{
                            userPayments (userId: 123) {
                              cost {
                                amount
                              }
                            }
                          }"
                        }""");

        assertPayments(response)
                .isEmpty();
    }

    @Test
    void getPaymentsForUserIdBetweenDates() {
        var response = sendGraphQlRequest(
                """
                        {
                          "variables": {
                            "userId": 1,
                            "from": "2022-05-04T00:00:00+02",
                            "to": "2022-05-11T00:00:00+02"
                          },
                          "query": "query GetUserPaymentsBetweenDates($userId: BigInteger!, $from: DateTime, $to: DateTime) {
                            userPayments (userId: $userId, from: $from, to: $to) {
                              description
                              category {
                                name
                              }
                              cost {
                                amount
                                currency {
                                  alphaCode
                                }
                              }
                            }
                          }"
                        }""");

        assertPayments(response)
                .extracting("cost.amount", "cost.currency.alphaCode", "category.name", "description")
                .containsExactly(
                        tuple(11.99f, "USD", "groceries", "Other test description")
                );
    }

    @Test
    void tryToGetAllPaymentsWithoutUserId() {
        var response = sendGraphQlRequest(
                """
                        {
                          "query": "{
                            userPayments {
                              cost {
                                amount
                              }
                            }
                          }"
                        }""");

        assertErrors(response)
                .extracting("message")
                .containsExactly("User ID is required");
    }

    private Response sendGraphQlRequest(String jsonBody) {
        return given()
                    .contentType(JSON)
                    .body(jsonBody)
                .when()
                    .post("/graphql")
                .then()
                    .extract()
                    .response();
    }

    private ListAssert<Object> assertPayments(Response response) {
        return assertJsonPath(response, "data.userPayments");
    }

    private ListAssert<Object> assertErrors(Response response) {
        return assertJsonPath(response, "errors");
    }

    private ListAssert<Object> assertJsonPath(Response response, String errors) {
        var responseBody = response.getBody();
        return assertThat(responseBody.jsonPath()
                                      .getList(errors))
                .as(responseBody::prettyPrint);
    }

}