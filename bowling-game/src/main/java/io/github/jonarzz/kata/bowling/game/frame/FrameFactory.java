package io.github.jonarzz.kata.bowling.game.frame;

import static io.github.jonarzz.kata.bowling.game.DefaultGame.MAX_FRAMES;

import java.util.Optional;

public class FrameFactory {

    static final int ROLLS_PER_STANDARD_FRAME = 2;
    static final int MAX_ROLLS_PER_LAST_FRAME = 3;

    static final int MAX_PINS_PER_FRAME = 10;

    private static final int LAST_STANDARD_FRAME_ROLL_NUMBER = (MAX_FRAMES - 1) * ROLLS_PER_STANDARD_FRAME;
    private static final int MAX_TOTAL_ROLLS = LAST_STANDARD_FRAME_ROLL_NUMBER + MAX_ROLLS_PER_LAST_FRAME;

    public Optional<Frame> nextFrame(Roll current) {
        var currentRollNumber = current.number();
        if (isRollOutOfBounds(currentRollNumber)) {
            return Optional.of(new NoOpFrame());
        }
        if (isSecondRollInFrame(currentRollNumber) || isLastRoll(currentRollNumber)) {
            return Optional.empty();
        }
        if (isStandardFrameRoll(currentRollNumber)) {
            return Optional.of(new StandardFrame());
        }
        return Optional.of(new LastFrame());
    }

    private boolean isRollOutOfBounds(int currentRollNumber) {
        return currentRollNumber > MAX_TOTAL_ROLLS;
    }

    private boolean isSecondRollInFrame(int currentRollNumber) {
        return currentRollNumber % ROLLS_PER_STANDARD_FRAME == 0;
    }

    private boolean isLastRoll(int currentRollNumber) {
        return currentRollNumber == MAX_TOTAL_ROLLS;
    }

    private boolean isStandardFrameRoll(int currentRollNumber) {
        return currentRollNumber <= LAST_STANDARD_FRAME_ROLL_NUMBER;
    }

}
