package labyrinth.model;

import labyrinth.util.observable.ObservableValue;
import labyrinth.util.observable.SimpleObservableValue;

import java.util.UUID;

public class Slot {

    private final Game game;
    private final int index;
    private final SimpleObservableValue<Player> player;

    public Slot(Game game, int index) {
        this.game = game;
        this.index = index;
        this.player = new SimpleObservableValue<>(null);
    }

    public int getIndex() {
        return index;
    }

    public ObservableValue<Player> playerProperty() {
        return player.asReadOnly();
    }

    public Player getPlayer() {
        return player.getValue();
    }

    public boolean isOpen() {
        return getPlayer() == null;
    }

    private void checkSlotOpenable() {

        if (game.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en mode SETUP");
        }

        if (!isOpen()) {
            throw new IllegalStateException("Le slot est déjà occupé");
        }
    }

    public void open() {
        open(false);
    }

    public void open(boolean isBot) {
        checkSlotOpenable();

        Player newPlayer = new Player(this.index, isBot);
        this.player.setValue(newPlayer);
    }

    public void open(UUID uuid) {
        open(uuid, false);
    }

    public void open(UUID uuid, boolean isBot) {
        checkSlotOpenable();

        Player newPlayer = new Player(this.index, uuid, isBot);
        this.player.setValue(newPlayer);
    }

    public void close() {

        if (game.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en mode SETUP");
        }

        this.player.setValue(null);
    }
}