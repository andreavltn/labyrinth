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

    // ouvre un slot avec un joueur humain
    public void open() {
        open(false);
    }

    // ouvre un slot avec un bot ou un humain
    public void open(boolean isBot) {
        checkSlotOpenable();
        Player newPlayer = new Player(this.index, isBot);
        this.player.setValue(newPlayer);
    }

    // ouvre un slot avec un uuid
    public void open(UUID uuid) {
        open(uuid, false);
    }

    // ouvre un slot avec un uuid et un type bot/humain
    public void open(UUID uuid, boolean isBot) {
        checkSlotOpenable();
        Player newPlayer = new Player(this.index, uuid, isBot);
        this.player.setValue(newPlayer);
    }

    // retirer le joueur d'un slot
    public void close() {
        checkSlotClosable();
        this.player.setValue(null);
    }

    private void checkSlotOpenable(){

        if (game.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en état SETUP");
        }

        if (this.player.getValue() != null) {
            throw new IllegalStateException("L'emplacement contient déjà un joueur");
        }
    }

    private void checkSlotClosable(){

        if (game.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en état SETUP");
        }

        if (this.player.getValue() == null) {
            throw new IllegalStateException("L'emplacement ne contient pas de joueur");
        }
    }
}