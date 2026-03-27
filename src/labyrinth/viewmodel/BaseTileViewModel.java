package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.util.observable.ChangeListener;
import labyrinth.util.observable.InvalidationListener;

import java.util.List;

public class BaseTileViewModel implements TileViewModel {

    private final labyrinth.util.observable.ObservableValue<Tile> tile;
    private final SimpleObjectProperty<TileKind> kind;
    private final SimpleObjectProperty<Direction> direction;
    private final SimpleObjectProperty<List<Integer>> playersId;
    private final SimpleObjectProperty<Integer> startingPosition;
    private final SimpleBooleanProperty treasure;
    private final SimpleObjectProperty<Integer> currentPlayerId;

    private Tile currentTile;
    private ChangeListener<Direction> orientationListener;
    private InvalidationListener playersListener;

    public BaseTileViewModel(labyrinth.util.observable.ObservableValue<Tile> tile, int currentPlayerId) {
        this.tile = tile;

        this.kind = new SimpleObjectProperty<>();
        this.direction = new SimpleObjectProperty<>();
        this.playersId = new SimpleObjectProperty<>(List.of());
        this.startingPosition = new SimpleObjectProperty<>(-1);
        this.treasure = new SimpleBooleanProperty(false);
        this.currentPlayerId = new SimpleObjectProperty<>(currentPlayerId);

        // initialise avec la tuile actuelle
        bindTile(tile.getValue());

        // quand la tuile de cette case change, on rebinde tout
        this.tile.addListener((obs, oldTile, newTile) -> bindTile(newTile));
    }

    private void bindTile(Tile tile) {

        // enlève les anciens listeners si besoin
        if (currentTile != null) {
            currentTile.orientationProperty().removeListener(orientationListener);
            currentTile.playersProperty().removeListener(playersListener);
        }

        this.currentTile = tile;

        // adapte l'orientation observable du modèle vers javaFX
        this.kind.set(tile.getKind());
        this.direction.set(tile.getOrientation());

        // index si position de départ, -1 sinon
        this.startingPosition.set(
                switch (tile.getContent()) {
                    case StartingPosition s -> s.index();
                    default -> -1;
                }
        );

        // true si c'est un trésor, false sinon
        this.treasure.set(
                switch (tile.getContent()) {
                    case Treasure t -> true;
                    default -> false;
                }
        );

        updatePlayersId();

        // suit l'orientation de la tuile courante
        orientationListener = (obs, oldDir, newDir) -> direction.set(newDir);
        tile.orientationProperty().addListener(orientationListener);

        // suit la liste des joueurs de la tuile courante
        playersListener = obs -> updatePlayersId();
        tile.playersProperty().addListener(playersListener);
    }

    private void updatePlayersId() {
        playersId.set(
                currentTile.getPlayers().stream().map(Player::getIndex).toList()
        );
    }

    @Override // propriété observable
    public ObservableValue<TileKind> kindProperty() {
        return kind;
    }

    @Override // renvoie le type de la tuile
    public TileKind getKind() {
        return kind.get();
    }

    @Override // propriété observable
    public ObservableValue<Direction> directionProperty() {
        return direction;
    }

    @Override // renvoie la direction de la tuile
    public Direction getDirection() {
        return direction.get();
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