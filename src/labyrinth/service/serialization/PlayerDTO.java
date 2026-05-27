package labyrinth.service.serialization;

import labyrinth.model.Player;

import java.util.UUID;

public class PlayerDTO {

    private final int index;

    private final UUID uuid;

    private final boolean isBot;

    private final DeckDTO deck;

    public PlayerDTO(Player player) {

        this.index = player.getIndex();
        this.uuid = player.getUuid();
        this.isBot = player.isBot();

        this.deck =
                new DeckDTO(
                        player.getDeck()
                );
    }

    public int getIndex() {
        return index;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isBot() {
        return isBot;
    }

    public DeckDTO getDeck() {
        return deck;
    }
}