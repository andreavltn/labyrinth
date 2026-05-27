package labyrinth.model;

import labyrinth.service.serialization.PlayerDTO;

import java.util.UUID;

public class Player {

    private final int index;
    private final UUID uuid;
    private final boolean isBot;
    private final Deck deck;

    public Player(int index, UUID uuid, boolean isBot) {

        this.index = index;
        this.uuid = uuid;
        this.isBot = isBot;
        this.deck = new Deck();
    }

    public Player(int index, boolean isBot) {
        this(index, null, isBot);
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

    public Deck getDeck() {
        return deck;
    }

    // hydrate le joueur à partir d'un DTO
    public void updateFrom(PlayerDTO dto) {
        this.deck.updateFrom(dto.getDeck());
    }
}