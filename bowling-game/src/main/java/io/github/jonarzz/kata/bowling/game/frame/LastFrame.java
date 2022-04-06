package io.github.jonarzz.kata.bowling.game.frame;

import static io.github.jonarzz.kata.bowling.game.frame.FrameFactory.ROLLS_PER_STANDARD_FRAME;

import java.util.List;

class LastFrame extends ScoredFrame {

    @Override
    boolean shouldAccept(Roll roll) {
        var rollsCount = rolls.size();
        if (rollsCount == ROLLS_PER_STANDARD_FRAME) {
            return knockedDownPinsTotal() >= 10;
        }
        return rollsCount < ROLLS_PER_STANDARD_FRAME;
    }

    @Override
    public int pointsTotal(List<Frame> followingFrames) {
        return knockedDownPinsTotal();
    }

}
