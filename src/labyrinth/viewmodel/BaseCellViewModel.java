package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;

public class BaseCellViewModel implements CellViewModel {

    private final Tile tile;
    private final BaseTileViewModel tileViewModel;
    private final SimpleBooleanProperty active = new SimpleBooleanProperty(false);
    private final Game game;

    public BaseCellViewModel(Tile tile, Game game, int currentPlayerId) {
        this.tile = tile;
        this.game = game;
        this.tileViewModel = new BaseTileViewModel(tile, currentPlayerId);
    }

    @Override // renvoie la tuile associée à la cellule
    public TileViewModel getTileViewModel() {
        return tileViewModel;
    }

    @Override // true si on peut cliquer sur la cellule
    public ObservableValue<Boolean> activeProperty() {
        return active;
    }

    @Override // appelée quand le joueur clique sur la cellule pour y déplacer son pion
    public void movePlayer() {
        game.movePlayer(tile);
    }

    public void setActive(boolean value) {
        active.set(value);
    }

    public void setCurrentPlayerId(int id) {
        tileViewModel.setCurrentPlayerId(id);
    }
}