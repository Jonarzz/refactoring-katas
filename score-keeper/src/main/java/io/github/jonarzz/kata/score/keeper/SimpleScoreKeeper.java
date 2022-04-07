package io.github.jonarzz.kata.score.keeper;

public class SimpleScoreKeeper implements ScoreKeeper {

    private static final String SCORE_DELIMITER = ":";

    private final Score aTeamScore;
    private final Score bTeamScore;

    private SimpleScoreKeeper(int scoreDigits) {
        aTeamScore = Score.withMaxDigits(scoreDigits);
        bTeamScore = Score.withMaxDigits(scoreDigits);
    }

    public static SimpleScoreKeeper withMaxDigits(int scoreDigits) {
        return new SimpleScoreKeeper(scoreDigits);
    }

    @Override
    public void scoreTeamA1() {
        aTeamScore.add(1);
    }

    @Override
    public void scoreTeamA2() {
        aTeamScore.add(2);
    }

    @Override
    public void scoreTeamA3() {
        aTeamScore.add(3);
    }

    @Override
    public void scoreTeamB1() {
        bTeamScore.add(1);
    }

    @Override
    public void scoreTeamB2() {
        bTeamScore.add(2);
    }

    @Override
    public void scoreTeamB3() {
        bTeamScore.add(3);
    }

    @Override
    public String getScore() {
        return aTeamScore + SCORE_DELIMITER + bTeamScore;
    }

}
