package labyrinth.model;

public record None() implements Content{

    @Override
    public boolean isStartingPositionFor(int index) {
        return false; // une tuile vide n'est jamais position de départ pour un joueur
    }
}
