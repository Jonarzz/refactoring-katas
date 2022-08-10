package io.github.jonarzz.kata.unusual.spending.payment;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(IsolatedTestProfile.class)
class PaymentResourceApiTest {

    // data loaded by Liquibase (see: data/payment.csv in test resources)

    @Test
    void getAllPaymentsForUsername() {
        var response = sendGraphQlRequest(
                """
                        {
                          "query": "{
                            userPayments (username: \\"test_user_2\\") {
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
    void getAllPaymentsForUsernameThatDoesNotExist() {
        var response = sendGraphQlRequest(
                """
                        {
                          "query": "{
                            userPayments (username: \\"i do not exist\\") {
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
    void getPaymentsForUsernameBetweenDates() {
        var response = sendGraphQlRequest(
                """
                        {
                          "variables": {
                            "username": "test_user_1",
                            "from": "2022-05-04T00:00:00+02",
                            "to": "2022-05-11T00:00:00+02"
                          },
                          "query": "query GetUserPaymentsBetweenDates($username: String!, $from: DateTime, $to: DateTime) {
                            userPayments (username: $username, from: $from, to: $to) {
                              id
                              timestamp
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
                .extracting("id", "timestamp", "description",
                            "category.name", "cost.amount", "cost.currency.alphaCode")
                .containsExactly(
                        tuple("2a3dff5e-74ac-4da4-a867-9d12dcd23cd3", "2022-05-04T07:11:33Z", "Other test description",
                              "groceries", 11.99f, "USD")
                );
    }

    @Test
    void tryToGetAllPaymentsWithoutUsername() {
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
                .containsExactly("Username is required");
    }

    @Test
    void getPaymentDetailsById() {
        var response = sendGraphQlRequest(
                """
                        {
                          "variables": {
                            "paymentId": "c1c3856a-0e7e-42c1-a61b-e7670c538318"
                          },
                          "query": "query GetPaymentDetails($paymentId: String!) {
                            paymentDetails (paymentId: $paymentId) {
                              timestamp
                              description
                              category {
                                name
                              }
                            }
                          }"
                        }""");

        assertPaymentDetails(response)
                .extracting("timestamp", "description", "category.name")
                .containsExactly(
                        "2022-05-01T12:00:01Z", "Some test description", "travel"
                );
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
        return assertJsonPathList(response, "data.userPayments");
    }

    private AbstractObjectAssert<?, Object> assertPaymentDetails(Response response) {
        return assertJsonPath(response)
                .extracting(jsonPath -> jsonPath.get("data.paymentDetails"));
    }

    private ListAssert<Object> assertErrors(Response response) {
        return assertJsonPathList(response, "errors");
    }

    private ObjectAssert<JsonPath> assertJsonPath(Response response) {
        var responseBody = response.getBody();
        return assertThat(responseBody.jsonPath())
                .as(responseBody::prettyPrint);
    }

    private ListAssert<Object> assertJsonPathList(Response response, String path) {
        return assertJsonPath(response)
                .extracting(jsonPath -> jsonPath.getList(path),
                            list(Object.class));
    }

}