package labyrinth.model;

public class Slot {

    private int index;
    private Player player;
    private Game game;

    public Slot(Game game, int index) {
        this.game = game;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public Player getPlayer() {
        return this.player;
    }
}
