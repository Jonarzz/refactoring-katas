package io.github.jonarzz.kata.bowling.game.frame;

import static io.github.jonarzz.kata.bowling.game.frame.GameFrames.ROLLS_PER_STANDARD_FRAME;

import java.util.ArrayList;
import java.util.List;

class StandardFrame extends ScoredFrame {

    @Override
    public int pointsTotal(List<Frame> followingFrames) {
        var knockedDownPins = knockedDownPinsTotal();
        if (knockedDownPins < Roll.MAX_PINS) {
            return knockedDownPins;
        }
        var scoredRolls = new ArrayList<>(rolls);
        var nextFrame = firstElement(followingFrames);
        scoredRolls.add(nextFrame.firstRoll());
        if (firstRoll().isStrike()) {
            nextFrame.secondRoll()
                     .ifPresentOrElse(scoredRolls::add,
                                      () -> secondElement(followingFrames)
                                              .map(Frame::firstRoll)
                                              .ifPresent(scoredRolls::add));
        }
        return scoredRolls.stream()
                          .mapToInt(Roll::knockedDownPins)
                          .sum();
    }

    @Override
    boolean shouldAccept(Roll roll) {
        if (rolls.size() >= ROLLS_PER_STANDARD_FRAME) {
            return false;
        }
        var knockedDownPins = knockedDownPinsTotal();
        if (knockedDownPins == Roll.MAX_PINS) {
            return false;
        }
        if (knockedDownPins + roll.knockedDownPins() > Roll.MAX_PINS) {
            throw new IllegalArgumentException("Max pins that can be knocked down in one frame is " + Roll.MAX_PINS);
        }
        return true;
    }

}
