package io.github.jonarzz.kata.bowling.game.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class ScoredFrame implements Frame {

    final List<Roll> rolls = new ArrayList<>();

    @Override
    public final boolean offerRoll(Roll roll) {
        if (!shouldAccept(roll)) {
            return false;
        }
        rolls.add(roll);
        return true;
    }

    @Override
    public final Roll firstRoll() {
        return firstElement(rolls);
    }

    @Override
    public final Optional<Roll> secondRoll() {
        return secondElement(rolls);
    }

    abstract boolean shouldAccept(Roll roll);

    final int knockedDownPinsTotal() {
        return rolls.stream()
                    .mapToInt(Roll::knockedDownPins)
                    .sum();
    }

    static <T> T firstElement(List<T> elements) {
        return elements.get(0);
    }

    static <T> Optional<T> secondElement(List<T> elements) {
        return elements.stream()
                       .skip(1)
                       .findFirst();
    }

}
