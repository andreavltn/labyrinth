package labyrinth.model;

public class Slot {

    private int index;
    private Player player;
    private Game game;

    public Slot(Game game, int index) {
        this.game = game;
        this.index = index;
        this.player = null;
    }

    public int getIndex() {
        return this.index;
    }

    public Player getPlayer() {
        return this.player;
    }

    // ajouter un joueur au slot
    public void open() {
        if (game.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en état SETUP");
        }
        if (this.player != null) {
            throw new IllegalStateException("L'emplacement contient déjà un joueur");
        }
        this.player = new Player(this.index); // met à jour le joueur du slot
    }

    // retirer le joueur d'un slot
    public void close() {
        if (game.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en état SETUP");
        }
        if (this.player == null) {
            throw new IllegalStateException("L'emplacement ne contient pas de joueur");
        }
        this.player = null;
    }
}
