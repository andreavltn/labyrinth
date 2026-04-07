package labyrinth.service.serialization;

import labyrinth.model.Player;

public class PlayerDTO {

    private final int index;
    private final DeckDTO deck;

    // constructeur à partir d'un joueur
    public PlayerDTO(Player player) {
        this.index = player.getIndex();
        this.deck  = new DeckDTO(player.getDeck());
    }

    public int getIndex() {
        return index;
    }

    public DeckDTO getDeck() {
        return deck;
    }
}