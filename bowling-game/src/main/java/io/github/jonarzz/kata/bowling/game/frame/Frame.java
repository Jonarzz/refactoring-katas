package io.github.jonarzz.kata.bowling.game.frame;

import java.util.List;
import java.util.Optional;

interface Frame {

    boolean offerRoll(Roll roll);

    int pointsTotal(List<Frame> followingFrames);

    Roll firstRoll();

    Optional<Roll> secondRoll();

}
