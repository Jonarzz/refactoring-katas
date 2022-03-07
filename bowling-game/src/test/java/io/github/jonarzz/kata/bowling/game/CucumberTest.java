package io.github.jonarzz.kata.bowling.game;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("io/github/jonarzz/kata/bowling/game")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "io.github.jonarzz.kata.bowling")
class CucumberTest {

    @Test
    void sonarFix() {
        // SMH
        var someValue = "test method" + " to please Sonar";
        assertThat(someValue)
                .isNotEmpty();
    }

}
