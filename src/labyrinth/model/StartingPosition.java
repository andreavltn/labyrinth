package labyrinth.model;

public record StartingPosition(int index) implements Content{

    @Override
    public boolean isStartingPositionFor(int index) {
        return this.index() == index; // si l'index correspond
    }
}
