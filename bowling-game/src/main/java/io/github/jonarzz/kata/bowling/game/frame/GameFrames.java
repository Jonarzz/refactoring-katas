package io.github.jonarzz.kata.bowling.game.frame;

import io.github.jonarzz.kata.bowling.game.IncompleteGameException;

import java.util.ArrayList;
import java.util.List;

class GameFrames {

    static final int MAX_FRAMES = 10;
    static final int ROLLS_PER_STANDARD_FRAME = 2;
    static final int MAX_ROLLS_PER_LAST_FRAME = 3;

    private static final int LAST_STANDARD_FRAME_ROLL_NUMBER = (MAX_FRAMES - 1) * ROLLS_PER_STANDARD_FRAME;
    private static final int MAX_TOTAL_ROLLS = LAST_STANDARD_FRAME_ROLL_NUMBER + MAX_ROLLS_PER_LAST_FRAME;

    private final List<Frame> frames = new ArrayList<>(MAX_FRAMES);

    private int rollsCount;

    void nextRoll(int knockedDownPins) {
        Frame frame;
        Roll roll;
        do {
            roll = Roll.number(++rollsCount)
                       .knockDown(knockedDownPins);
            frame = calculateFrame(roll);
        } while (!frame.offerRoll(roll));

        if (frames.isEmpty() || frame != getCurrentFrame()) {
            frames.add(frame);
        }
    }

    int pointsTotal() throws IncompleteGameException {
        var framesCount = frames.size();
        if (framesCount < MAX_FRAMES) {
            throw new IncompleteGameException();
        }
        var points = 0;
        for (int index = 0; index < framesCount; index++) {
            points += frames.get(index)
                            .pointsTotal(frames.subList(index + 1,
                                                        framesCount));
        }
        return points;
    }

    private Frame calculateFrame(Roll current) {
        var currentRollNumber = current.number();
        if (currentRollNumber > MAX_TOTAL_ROLLS) {
            return new NoOpFrame();
        }
        if (currentRollNumber % ROLLS_PER_STANDARD_FRAME == 0 || currentRollNumber == MAX_TOTAL_ROLLS) {
            return getCurrentFrame();
        }
        if (currentRollNumber <= LAST_STANDARD_FRAME_ROLL_NUMBER) {
            return new StandardFrame();
        }
        return new LastFrame();
    }

    private Frame getCurrentFrame() {
        return frames.get(frames.size() - 1);
    }

}
