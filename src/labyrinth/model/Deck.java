package labyrinth.model;

import java.util.List;
import labyrinth.util.observable.ObservableList;
import labyrinth.util.observable.SimpleObservableList;

// les cartes trésor d'un joueur
public class Deck {

    private final SimpleObservableList<Integer> drawPile; // les trésors à trouver (pioche)
    private final SimpleObservableList<Integer> discardPile; // les trésors déjà trouvés (défausse)

    public Deck() {
        this.drawPile = new SimpleObservableList<>();
        this.discardPile = new SimpleObservableList<>();
    }

    public ObservableList<Integer> drawPileProperty(){
        return this.drawPile;
    }

    public List<Integer> getDrawPile(){
        return drawPile;
    }

    public ObservableList<Integer> discardPileProperty(){
        return this.discardPile;
    }

    public List<Integer> getDiscardPile(){
        return discardPile;
    }

    public void init(List<Integer> treasures){
        this.drawPile.clear(); // pioche vide
        this.drawPile.addAll(treasures); // y ajoute les trésors
        this.discardPile.clear(); // défausse vide
    }

    // place la première carte de la pioche dans la défausse (trésor trouvé)
    public void discard() {
        int treasure = this.drawPile.removeFirst(); // retire la première carte du paquet
        this.discardPile.add(treasure); // ajout à la défausse
    }
}
