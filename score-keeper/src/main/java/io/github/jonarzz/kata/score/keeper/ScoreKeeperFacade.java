package io.github.jonarzz.kata.score.keeper;

public class ScoreKeeperFacade implements ScoreKeeper {

    // TODO refactor xD

    private static final String SCORE_DELIMITER = ":";

    private int aTeamScore;
    private int bTeamScore;

    @Override
    public void scoreTeamA1() {
        aTeamScore += 1;
    }

    @Override
    public void scoreTeamA2() {
        aTeamScore += 2;
    }

    @Override
    public void scoreTeamA3() {
        aTeamScore += 3;
    }

    @Override
    public void scoreTeamB1() {
        bTeamScore += 1;
    }

    @Override
    public void scoreTeamB2() {
        bTeamScore += 2;
    }

    @Override
    public void scoreTeamB3() {
        bTeamScore += 3;
    }

    @Override
    public String getScore() {
        aTeamScore = trimmed(aTeamScore);
        bTeamScore = trimmed(bTeamScore);
        return formatScore(aTeamScore) + SCORE_DELIMITER + formatScore(bTeamScore);
    }

    private static int trimmed(int score) {
        return Math.min(score, 999);
    }

    private static String formatScore(int score) {
        return "%03d".formatted(score);
    }

}
