package labyrinth.service.serialization;

import labyrinth.model.Player;
import java.util.UUID;

public class PlayerDTO {

    private final int index;
    private final DeckDTO deck;
    private final UUID uuid;

    // constructeur à partir d'un joueur
    public PlayerDTO(Player player) {
        this.index = player.getIndex();
        this.deck  = new DeckDTO(player.getDeck());
        this.uuid  = player.getUuid();
    }

    public int getIndex() {
        return index;
    }

    public DeckDTO getDeck() {
        return deck;
    }

    public UUID getUuid() {
        return uuid;
    }
}