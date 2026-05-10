package labyrinth.model;

import labyrinth.util.observable.ObservableValue;
import labyrinth.util.observable.SimpleObservableValue;
import java.util.UUID;

public class Slot {

    private final int index;
    private final SimpleObservableValue<Player> player;
    private final Game game;

    public Slot(Game game, int index){
        this.game = game;
        this.index = index;
        this.player = new SimpleObservableValue<>(null);
    }

    public int getIndex(){
        return this.index;
    }

    public ObservableValue<Player> playerProperty(){
        return this.player.asReadOnly();
    }

    public Player getPlayer(){
        return this.player.getValue();
    }

    // ajouter un joueur au slot
    public void open() {
        checkSlotOpenable(); // vérifie si le slot est ouvrable
        Player newPlayer = new Player(this.index); // crée un nouveau joueur
        this.player.setValue(newPlayer); // met à jour le joueur du slot
    }

    // idem mais avec un uuid
    public void open(UUID uuid) {
        checkSlotOpenable();
        Player newPlayer = new Player(this.index, uuid); // crée un nouveau joueur (avec uuid)
        this.player.setValue(newPlayer); // met à jour le joueur du slot
    }

    // retirer le joueur d'un slot
    public void close() {
        checkSlotClosable(); // vérifie si le slot est fermable
        this.player.setValue(null); // retire le joueur du slot
    }

    private void checkSlotOpenable(){
        if (game.getState() != GameState.SETUP) { // si on n'est pas au setup
            throw new IllegalStateException("Le jeu n'est pas en état SETUP"); // erreur
        }
        if (this.player.getValue() != null) { // si il y a déjà un joueur dans le slot
            throw new IllegalStateException("L'emplacement contient déjà un joueur"); // erreur
        }
    }

    private void checkSlotClosable(){
        if (game.getState() != GameState.SETUP) { // si on n'est pas au setup
            throw new IllegalStateException("Le jeu n'est pas en état SETUP"); // erreur
        }
        if (this.player.getValue() == null) { // si il n'y a pas de joueur dans le slot
            throw new IllegalStateException("L'emplacement ne contient pas de joueur"); // erreur
        }
    }
}
