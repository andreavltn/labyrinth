package labyrinth.model;

public class Player {

    private final int index;
    private Deck deck;

    public Player(int index){
        this.index = index;
    }

    public int getIndex(){
        return this.index;
    }

    public Deck getDeck(){
        return this.deck;
    }
}
