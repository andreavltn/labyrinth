package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.util.adapter.FxAdapter;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.UUID;

public class BasePlayerViewModel implements PlayerViewModel {

    private final Slot slot;
    private final Game game;
    private final ObservableValue<Boolean> active;
    private final SimpleBooleanProperty locked = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<Integer> targetTreasure = new SimpleObjectProperty<>(-1);

    public BasePlayerViewModel(Slot slot, Game game) {

        this.slot = slot;
        this.game = game;

        // actif si le slot contient un joueur
        this.active = FxAdapter.adapt(slot.playerProperty()).map(p -> p != null);

        // verrouille hors setup
        game.stateProperty().addListener((obs, old, newState) -> {
            locked.set(newState != GameState.SETUP);
        });

        // met à jour le trésor cible
        game.stateProperty().addListener((obs, old, newState) -> {
            refreshTargetTreasure();
        });

        refreshTargetTreasure();
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

    @Override
    public ObservableValue<Integer> targetTreasureProperty() {
        return targetTreasure;
    }

    // raffraichit le numéro du trésor cible
    private void refreshTargetTreasure() {
        // récupère le joueur du slot
        Player player = slot.getPlayer();
        // si il n'existe pas, où qu'il n'a pas de trésor cible
        if (player == null || player.getDeck().getDrawPile().isEmpty()) {
            targetTreasure.set(-1); // -1
        } else { // sinon affiche l'indice du trésor cible
            targetTreasure.set(player.getDeck().getDrawPile().getFirst());
        }

        if (player != null) {
            // écoute les changements du deck du joueur courant
            player.getDeck().drawPileProperty().addListener(obs -> {
                // récupère sa pile
                List<Integer> pile = player.getDeck().getDrawPile();
                // met à jour l'indice du trésor cible
                targetTreasure.set(pile.isEmpty() ? -1 : pile.getFirst());
            });
        }
    }
}