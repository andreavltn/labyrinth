package labyrinth.model;

import labyrinth.service.serialization.PlayerDTO;
import java.util.UUID;

public class Player {

    private final int index;
    private Deck deck;
    private final UUID uuid;
    private final boolean isBot;

    public Player(int index){
        this(index, null, false);
    }

    public Player(int index, boolean isBot){
        this(index, null, isBot);
    }

    public Player(int index, UUID uuid) {
        this(index, uuid, false);
    }

    public Player(int index, UUID uuid, boolean isBot) {
        this.index = index;
        this.deck = new Deck();
        this.uuid = uuid;
        this.isBot = isBot;
    }

    public int getIndex(){
        return this.index;
    }

    public Deck getDeck(){
        return this.deck;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isBot() {
        return isBot;
    }

    public void updateFrom(PlayerDTO dto) {
        this.deck.updateFrom(dto.getDeck());
    }
}