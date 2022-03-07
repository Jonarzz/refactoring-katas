package io.github.jonarzz.kata.bowling.game;

public interface Game {

    void roll(int knockedDownPins);

    int score();

}
