package labyrinth.model;

public record None() implements Content{

    public boolean isStartingPosition() {
        return false;
    }
}
