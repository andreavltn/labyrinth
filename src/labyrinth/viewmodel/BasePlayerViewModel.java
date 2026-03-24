package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.util.adapter.FxAdapter;

public class BasePlayerViewModel implements PlayerViewModel {

    private final Slot slot;
    private final Game game;
    private final ObservableValue<Boolean> active;
    private final SimpleBooleanProperty locked = new SimpleBooleanProperty(false);

    public BasePlayerViewModel(Slot slot, Game game) {
        this.slot = slot;
        this.game = game;

        // actif si le slot contient un joueur, sinon inactif (+ adaptation vers javaFX)
        this.active = FxAdapter.adapt(slot.playerProperty()).map(p -> p != null);

        // dès qu'on quitte l'état SETUP, verrouille le slot
        game.stateProperty().addListener((obs, old, newState) -> {
            locked.set(newState != GameState.SETUP);
        });
    }

    @Override // renvoie true si le slot est actif, false sinon
    public ObservableValue<Boolean> activeProperty() {
        return active;
    }

    @Override // ajoute un joueur humain
    public void addPlayer() {
        if (!locked.get()) { // seulement si le slot n'est pas verrouillé
            slot.open();
        }
    }

    @Override // ajoute un bot
    public void addBot() {
        addPlayer(); // pour l'instant ajoute un joueur humain
    }

    @Override // retire un joueur du slot
    public void removePlayer() {
        if (!locked.get()) { // seulement si le slot n'est pas verrouillé
            slot.close();
        }
    }

    @Override // renvoie true si le slot est verrouillé, false sinon
    public ObservableValue<Boolean> lockedProperty() {
        return locked;
    }
}