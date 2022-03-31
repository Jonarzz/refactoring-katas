package io.github.jonarzz.kata.bowling.game.frame;

import java.util.List;
import java.util.Optional;

class NoOpFrame implements Frame {

    @Override
    public boolean offerRoll(Roll roll) {
        return true;
    }

    @Override
    public int pointsTotal(List<Frame> followingFrames) {
        return 0;
    }

    @Override
    public Roll firstRoll() {
        return null;
    }

    @Override
    public Optional<Roll> secondRoll() {
        return Optional.empty();
    }

}
