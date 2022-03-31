package io.github.jonarzz.kata.bowling.game.frame;

import static io.github.jonarzz.kata.bowling.game.frame.GameFrames.ROLLS_PER_STANDARD_FRAME;

import java.util.List;

class LastFrame extends ScoredFrame {

    @Override
    boolean shouldAccept(Roll roll) {
        if (rolls.size() == ROLLS_PER_STANDARD_FRAME) {
            return knockedDownPinsTotal() >= 10;
        }
        return rolls.size() < ROLLS_PER_STANDARD_FRAME;
    }

    @Override
    public int pointsTotal(List<Frame> followingFrames) {
        return knockedDownPinsTotal();
    }

}
