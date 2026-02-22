package labyrinth.model;

public sealed interface Content permits None, Treasure, StartingPosition{

    boolean isStartingPositionFor(int index);
}
