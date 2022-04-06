package io.github.jonarzz.kata.bowling.game;

import io.github.jonarzz.kata.bowling.game.frame.Frame;
import io.github.jonarzz.kata.bowling.game.frame.FrameFactory;
import io.github.jonarzz.kata.bowling.game.frame.Roll;

import java.util.ArrayList;
import java.util.List;

public class DefaultGame implements Game {

    public static final int MAX_FRAMES = 10;

    private final FrameFactory frameFactory = new FrameFactory();

    private final List<Frame> frames = new ArrayList<>(MAX_FRAMES);

    private int rollsCount;

    @Override
    public void roll(int knockedDownPins) {
        Frame frame;
        Roll roll;
        do {
            roll = Roll.number(++rollsCount)
                       .knockDown(knockedDownPins);
            frame = frameFactory.nextFrame(roll)
                                .orElseGet(this::getCurrentFrame);
        } while (!frame.offerRoll(roll));

        if (frames.isEmpty() || frame != getCurrentFrame()) {
            frames.add(frame);
        }
    }

    @Override
    public int score() throws IncompleteGameException {
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

    private Frame getCurrentFrame() {
        return frames.get(frames.size() - 1);
    }

}
