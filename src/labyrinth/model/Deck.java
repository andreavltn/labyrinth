package labyrinth.model;

import java.util.ArrayList;
import java.util.List;

// les cartes trésor d'un joueur
public class Deck {

    private List<Integer> drawPile; // les trésors à trouver (pioche)
    private List<Integer> discardPile; // les trésors déjà trouvés (défausse)

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

    public void init(List<Integer> treasures) {
        this.drawPile = new ArrayList<>(treasures); // pioche = une liste de trésors
        this.discardPile = new ArrayList<>(); // défausse = une liste vide
    }

    // place la première carte de la pioche dans la défausse (trésor trouvé)
    public void discard() {
        int treasure = this.drawPile.removeFirst(); // retire la première carte du paquet
        this.discardPile.add(treasure); // ajout à la défausse
    }
}
