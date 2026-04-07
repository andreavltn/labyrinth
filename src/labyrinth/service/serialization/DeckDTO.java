package labyrinth.service.serialization;

import labyrinth.model.Deck;
import java.util.ArrayList;
import java.util.List;

public class DeckDTO {

    private final List<Integer> drawPile;
    private final List<Integer> discardPile;

    // constructeur à partir d'un deck
    public DeckDTO(Deck deck) {
        this.drawPile = new ArrayList<>(deck.getDrawPile());
        this.discardPile = new ArrayList<>(deck.getDiscardPile());
    }

    public List<Integer> getDrawPile() {
        return drawPile;
    }

    public List<Integer> getDiscardPile() {
        return discardPile;
    }
}