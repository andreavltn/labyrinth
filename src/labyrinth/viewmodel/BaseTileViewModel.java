package labyrinth.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import labyrinth.model.*;
import labyrinth.util.adapter.FxAdapter;
import java.util.List;

public class BaseTileViewModel implements TileViewModel {

    private final Tile tile;
    private final ObservableValue<TileKind> kind;
    private final ObservableValue<Direction> direction;
    private final ObservableValue<List<Integer>> playersId;
    private final SimpleObjectProperty<Integer> startingPosition;
    private final SimpleBooleanProperty treasure;
    private final SimpleObjectProperty<Integer> currentPlayerId;

    public BaseTileViewModel(Tile tile, int currentPlayerId) {
        this.tile = tile;

        // adapte l'orientation observable du modèle vers javaFX
        this.kind = new SimpleObjectProperty<>(tile.getKind());
        this.direction = FxAdapter.adapt(tile.orientationProperty());

        // adapte la liste des joueurs (solution bricolée avec Bindings, j'ai pas tout compris)
        ObservableList<Player> playersList = FxAdapter.adapt(tile.playersProperty()); // adapte la liste en javaFX
        this.playersId = Bindings.createObjectBinding( // + transforme chaque joueur en son index
                () -> playersList.stream().map(Player::getIndex).toList(), playersList
        );

        // index si position de départ, -1 sinon
        this.startingPosition = new SimpleObjectProperty<>(
                switch (tile.getContent()) {
                    case StartingPosition s -> s.index();
                    default -> -1;
                }
        );

        // true si c'est un trésor, false sinon
        this.treasure = new SimpleBooleanProperty(
                switch (tile.getContent()) {
                    case Treasure t -> true;
                    default -> false;
                }
        );

        this.currentPlayerId = new SimpleObjectProperty<>(currentPlayerId);
    }

    @Override // propriété observable
    public ObservableValue<TileKind> kindProperty() {
        return kind;
    }

    @Override // renvoie le type de la tuile
    public TileKind getKind() {
        return tile.getKind();
    }

    @Override // propriété observable
    public ObservableValue<Direction> directionProperty() {
        return direction;
    }

    @Override // renvoie la direction de la tuile
    public Direction getDirection() {
        return tile.getOrientation();
    }

    @Override // renvoie l'indice du joueur duquel c'est la position de départ, false sinon
    public ObservableValue<Integer> startingPositionProperty() {
        return startingPosition;
    }

    @Override // renvoie true si c'est un trésor, false sinon
    public ObservableValue<Boolean> treasureProperty() {
        return treasure;
    }

    @Override // renvoie les id des joueurs présents sur la tuile
    public ObservableValue<List<Integer>> playersIdProperty() {
        return playersId;
    }

    @Override // renvoie l'id du joueur courant
    public ObservableValue<Integer> currentPlayerIdProperty() {
        return currentPlayerId;
    }

    // change l'id du joueur courant
    public void setCurrentPlayerId(int id) {
        currentPlayerId.set(id);
    }
}