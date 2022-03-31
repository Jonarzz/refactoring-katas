package io.github.jonarzz.kata.bowling.game.frame;

import io.github.jonarzz.kata.bowling.game.Game;
import io.github.jonarzz.kata.bowling.game.IncompleteGameException;

public class DefaultGame implements Game {

    private GameFrames frames = new GameFrames();

    @Override
    public void roll(int knockedDownPins) {
        frames.nextRoll(knockedDownPins);
    }

    @Override
    public int score() throws IncompleteGameException {
        return frames.pointsTotal();
    }

}
