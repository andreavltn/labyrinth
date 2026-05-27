package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.util.adapter.FxAdapter;

import java.util.UUID;

public class BasePlayerViewModel implements PlayerViewModel {

    private final Slot slot;

    private final Game game;

    private final ObservableValue<Boolean> active;

    private final SimpleBooleanProperty locked =
            new SimpleBooleanProperty(false);

    public BasePlayerViewModel(
            Slot slot,
            Game game
    ) {

        this.slot = slot;
        this.game = game;

        // actif si le slot contient un joueur
        this.active =
                FxAdapter.adapt(
                        slot.playerProperty()
                ).map(p -> p != null);

        // verrouille hors setup
        game.stateProperty().addListener(
                (obs, old, newState) -> {

                    locked.set(
                            newState != GameState.SETUP
                    );
                }
        );
    }

    @Override
    public ObservableValue<Boolean>
    activeProperty() {

        return active;
    }

    @Override
    public void addPlayer() {

        if (!locked.get()) {
            slot.open(false);
        }
    }

    @Override
    public void addPlayer(
            UUID uuid
    ) {

        if (!locked.get()) {
            slot.open(uuid, false);
        }
    }

    @Override
    public void addBot() {

        if (!locked.get()) {
            slot.open(true);
        }
    }

    @Override
    public void removePlayer() {

        if (!locked.get()) {
            slot.close();
        }
    }

    @Override
    public ObservableValue<Boolean>
    lockedProperty() {

        return locked;
    }

    public ObservableValue<Player>
    playerProperty() {

        return FxAdapter.adapt(
                slot.playerProperty()
        );
    }
}