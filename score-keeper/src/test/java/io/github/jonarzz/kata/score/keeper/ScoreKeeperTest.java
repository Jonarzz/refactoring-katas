package io.github.jonarzz.kata.score.keeper;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.of;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

@DisplayName("Score keeper tests")
class ScoreKeeperTest {

    ScoreKeeper scoreKeeper;

    @BeforeEach
    void setUp() {
        scoreKeeper = SimpleScoreKeeper.withMaxDigits(3);
    }

    @Test
    @DisplayName("Initial score")
    void initialScore() {
        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo("000:000");
    }

    @ParameterizedTest(name = "expected score = {1}")
    @MethodSource("runPointsScoreSource")
    @DisplayName("Single point acquisition")
    void singlePointAcquisition(Consumer<ScoreKeeper> scorePoints, String expectedScore) {
        scorePoints.accept(scoreKeeper);

        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo(expectedScore);
    }

    static Stream<Arguments> runPointsScoreSource() {
        Consumer<ScoreKeeper> scoreA1 = ScoreKeeper::scoreTeamA1;
        Consumer<ScoreKeeper> scoreA2 = ScoreKeeper::scoreTeamA2;
        Consumer<ScoreKeeper> scoreA3 = ScoreKeeper::scoreTeamA3;
        Consumer<ScoreKeeper> scoreB1 = ScoreKeeper::scoreTeamB1;
        Consumer<ScoreKeeper> scoreB2 = ScoreKeeper::scoreTeamB2;
        Consumer<ScoreKeeper> scoreB3 = ScoreKeeper::scoreTeamB3;
        return Stream.of(
                of(scoreA1, "001:000"),
                of(scoreA2, "002:000"),
                of(scoreA3, "003:000"),
                of(scoreB1, "000:001"),
                of(scoreB2, "000:002"),
                of(scoreB3, "000:003")
        );
    }

    @ParameterizedTest(name = "{0} times")
    @ValueSource(ints = {2, 3, 5, 10, 15, 25, 50, 77, 99, 100, 101, 250, 999})
    @DisplayName("Team A scores 1 point multiple times")
    void teamAScoresOnePointMultipleTimes(int times) {
        for (int i = 0; i < times; i++) {
            scoreKeeper.scoreTeamA1();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .containsSequence(
                        "%03d".formatted(times),
                        ":000"
                );
    }

    @ParameterizedTest(name = "{0} times")
    @ValueSource(ints = {2, 3, 5, 10, 15, 25, 50, 77, 99, 100, 101, 250, 999})
    @DisplayName("Team B scores 1 point multiple times")
    void teamBScoresOnePointMultipleTimes(int times) {
        for (int i = 0; i < times; i++) {
            scoreKeeper.scoreTeamB1();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .containsSequence(
                        "000:",
                        "%03d".formatted(times)
                );
    }

    @ParameterizedTest(name = "{0} times")
    @CsvSource({
            "2,   004:000",
            "11,  022:000",
            "53,  106:000",
            "99,  198:000",
            "473, 946:000"
    })
    @DisplayName("Team A scores 2 points multiple times")
    void teamAScoresTwoPointsMultipleTimes(int times, String expectedScore) {
        for (int i = 0; i < times; i++) {
            scoreKeeper.scoreTeamA2();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo(expectedScore);
    }

    @ParameterizedTest(name = "{0} times")
    @CsvSource({
            "2,   000:004",
            "11,  000:022",
            "53,  000:106",
            "99,  000:198",
            "473, 000:946"
    })
    @DisplayName("Team B scores 2 points multiple times")
    void teamBScoresTwoPointsMultipleTimes(int times, String expectedScore) {
        for (int i = 0; i < times; i++) {
            scoreKeeper.scoreTeamB2();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo(expectedScore);
    }

    @ParameterizedTest(name = "{0} times")
    @CsvSource({
            "2,   006:000",
            "11,  033:000",
            "53,  159:000",
            "99,  297:000",
            "312, 936:000"
    })
    @DisplayName("Team A scores 3 points multiple times")
    void teamAScoresThreePointsMultipleTimes(int times, String expectedScore) {
        for (int i = 0; i < times; i++) {
            scoreKeeper.scoreTeamA3();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo(expectedScore);
    }

    @ParameterizedTest(name = "{0} times")
    @CsvSource({
            "2,   000:006",
            "11,  000:033",
            "53,  000:159",
            "99,  000:297",
            "312, 000:936"
    })
    @DisplayName("Team B scores 3 points multiple times")
    void teamBScoresThreePointsMultipleTimes(int times, String expectedScore) {
        for (int i = 0; i < times; i++) {
            scoreKeeper.scoreTeamB3();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo(expectedScore);
    }

    @Test
    @DisplayName("Both teams score more than 999 points")
    void bothTeamsScoreMoreThan999Points() {
        for (int i = 0; i < 2000; i++) {
            scoreKeeper.scoreTeamA1();
            scoreKeeper.scoreTeamB1();
        }

        var score = scoreKeeper.getScore();

        assertThat(score)
                .isEqualTo("999:999");
    }

    @Test
    @DisplayName("Various points scored by both teams with score verification after each")
    void variousPointsScoredByBothTeams_scoreVerifiedAfterEach() {
        assertAll(
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamA3, "003:000"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamB2, "003:002"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamA1, "004:002"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamB3, "004:005"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamA1, "005:005"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamA3, "008:005"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamA2, "010:005"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamB1, "010:006"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamB1, "010:007"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamB3, "010:010"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamA1, "011:010"),
                () -> scorePointsAndAssert(scoreKeeper::scoreTeamB2, "011:012")
        );
    }

    void scorePointsAndAssert(Runnable scorePoints, String expectedScore) {
        scorePoints.run();
        assertThat(scoreKeeper.getScore())
                .isEqualTo(expectedScore);
    }

    @RepeatedTest(50)
    @DisplayName("Multithreaded score modification")
    void multithreadedScoreModification() throws InterruptedException {
        var threadPool = Executors.newFixedThreadPool(8);
        var runner = new ScoringRunner(threadPool, 150);

        var aTeamLatch = runner.run(scoreKeeper::scoreTeamA1, scoreKeeper::scoreTeamA2, scoreKeeper::scoreTeamA3);
        var bTeamLatch = runner.run(scoreKeeper::scoreTeamB1, scoreKeeper::scoreTeamB2, scoreKeeper::scoreTeamB3);
        aTeamLatch.await();
        bTeamLatch.await();
        threadPool.shutdownNow();

        assertThat(scoreKeeper.getScore())
                // 150 * (1 + 2 + 3)
                .isEqualTo("900:900");
    }

    private static class ScoringRunner {

        private ExecutorService threadPool;
        private int nTimes;

        ScoringRunner(ExecutorService threadPool, int nTimes) {
            this.threadPool = threadPool;
            this.nTimes = nTimes;
        }

        CountDownLatch run(Runnable... scoreModifications) {
            var modifications = Collections.nCopies(nTimes, Arrays.asList(scoreModifications))
                                           .stream()
                                           .flatMap(Collection::stream)
                                           .collect(toList());
            var latch = new CountDownLatch(modifications.size());
            shuffle(modifications);
            modifications.forEach(modification -> threadPool.submit(() -> {
                modification.run();
                latch.countDown();
            }));
            return latch;
        }
    }

}