package labyrinth.model;

public record Treasure(int id) implements Content{

    @Override
    public boolean isStartingPositionFor(int index){
        return false; // un trésor n'est jamais point de départ d'un joueur
    }

}
