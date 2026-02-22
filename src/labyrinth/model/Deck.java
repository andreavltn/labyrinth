package labyrinth.model;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    private List<Integer> drawPile;
    private List<Integer> discardPile;

    public Deck() {
        this.drawPile = new ArrayList<Integer>();
        this.discardPile = new ArrayList<Integer>();
    }

    public List<Integer> getDrawPile() {
        return drawPile;
    }

    public List<Integer> getDiscardPile() {
        return discardPile;
    }
}
