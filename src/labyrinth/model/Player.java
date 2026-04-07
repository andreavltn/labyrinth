package labyrinth.model;

import labyrinth.service.serialization.PlayerDTO;

public class Player {

    private final int index;
    private Deck deck;

    public Player(int index){
        this.index = index;
        this.deck = new Deck();
    }

    public int getIndex(){
        return this.index;
    }

    public Deck getDeck(){
        return this.deck;
    }

    // hydrate le Player à partir d'un PlayerDTO
    public void updateFrom(PlayerDTO dto) {
        this.deck.updateFrom(dto.getDeck()); // appelle sur le deck directement
    }
}
