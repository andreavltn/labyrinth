package labyrinth.model;

import labyrinth.service.serialization.PlayerDTO;
import java.util.UUID;

public class Player {

    private final int index;
    private Deck deck;
    private final UUID uuid;

    public Player(int index){
        this.index = index;
        this.deck = new Deck();
        this.uuid = null;
    }

    public Player(int index, UUID uuid) {
        this.index = index;
        this.deck = new Deck();
        this.uuid = uuid;
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

    // hydrate le Player à partir d'un PlayerDTO
    public void updateFrom(PlayerDTO dto) {
        this.deck.updateFrom(dto.getDeck()); // appelle sur le deck directement
    }
}
