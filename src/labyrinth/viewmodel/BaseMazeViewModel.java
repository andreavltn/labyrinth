package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.util.adapter.FxAdapter;
import labyrinth.util.collection.ArrayGrid;
import labyrinth.util.collection.Grid;

import java.util.ArrayList;
import java.util.List;

public class BaseMazeViewModel implements MazeViewModel {

    private final Game game;
    private final Grid<BaseCellViewModel> cells;
    private final List<SlotViewModel> slots = new ArrayList<>();
    private final BaseTileViewModel extraTileViewModel;
    private final ObservableValue<GameState> state;

    public BaseMazeViewModel(Game game) {

        this.game = game;

        // récupère les données du jeu
        Maze maze = game.getMaze();
        Grid<Tile> grid = maze.getTiles();

        int currentPlayerId = game.getCurrentPlayer().getIndex();

        // crée les cellules
        cells = new ArrayGrid<>(grid.getWidth(), grid.getHeight());
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                cells.set(x, y, new BaseCellViewModel(grid.get(x, y), game, currentPlayerId));
            }
        }

        // tuile supplémentaire
        this.extraTileViewModel = new BaseTileViewModel(maze.getExtraTile(), currentPlayerId);

        // ajoute les slots sur les colonnes impaires (1 3 5)
        for (int x = 1; x < grid.getWidth(); x += 2) {
            slots.add(new BaseSlotViewModel(new MazeMove(Direction.DOWN, x), game));
            slots.add(new BaseSlotViewModel(new MazeMove(Direction.UP, x), game));
        }

        // ajoute les slots sur les lignes impaires (1 3 5)
        for (int y = 1; y < grid.getHeight(); y += 2) {
            slots.add(new BaseSlotViewModel(new MazeMove(Direction.RIGHT, y), game));
            slots.add(new BaseSlotViewModel(new MazeMove(Direction.LEFT, y), game));
        }

        // adapte l'état
        this.state = FxAdapter.adapt(game.stateProperty());

        // écoute les changements d'état pour les cellules et les slots
        state.addListener((obs, old, newState) -> {

            // si c'est la fin du tour
            if (newState == GameState.TURN_END) {
                game.endTurn(); // fin de tour
                return; // redéclenche le listener
            }

            // slotsActive = true si on est en MAZE_MOVE, false sinon
            boolean slotsActive = newState == GameState.MAZE_MOVE;

            // met à jour l'état du slot en fonction du booléen
            for (SlotViewModel slot : slots) {
                ((BaseSlotViewModel) slot).setActive(slotsActive);
            }

            // cellsActive = true si on est en PLAYER_MOVE, false sinon
            boolean cellsActive = newState == GameState.PLAYER_MOVE;

            // met à jour l'état des cellules en fonction du booléen
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    cells.get(x, y).setActive(cellsActive);
                }
            }

            // met à jour le currentPlayerId de toutes les cellules
            if (game.getCurrentPlayer() != null) { // si il y a un joueur courant
                int cpId = game.getCurrentPlayer().getIndex(); // récupère son id
                for (int y = 0; y < grid.getHeight(); y++) {
                    for (int x = 0; x < grid.getWidth(); x++) {
                        cells.get(x, y).setCurrentPlayerId(cpId);
                    }
                }
            }
        });

        // état initial au lancement du jeu
        GameState initialState = game.getState();

        // tous les slots sont actifs en MAZE_MOVE
        boolean slotsActive = initialState == GameState.MAZE_MOVE;
        for (SlotViewModel slot : slots) {
            ((BaseSlotViewModel) slot).setActive(slotsActive);
        }

        // toutes les cellules sont actives en PLAYER_MOVE
        boolean cellsActive = initialState == GameState.PLAYER_MOVE;
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                cells.get(x, y).setActive(cellsActive);
            }
        }
    }

    @Override // renvoie la grille des cellules du plateau
    public Grid<CellViewModel> getCellViewModels() {
        Grid<CellViewModel> grid = new ArrayGrid<>(cells.getWidth(), cells.getHeight());
        for (int y = 0; y < cells.getHeight(); y++) {
            for (int x = 0; x < cells.getWidth(); x++) {
                grid.set(x, y, cells.get(x, y));
            }
        }
        return grid;
    }

    @Override // renvoie les slots
    public List<SlotViewModel> getSlotViewModels() {
        return slots;
    }

    @Override // renvoie la tuile supplémentaire
    public TileViewModel getExtraTileViewModel() {
        return extraTileViewModel;
    }

    @Override // renvoie l'état du jeu
    public ObservableValue<GameState> stateProperty() {
        return state;
    }

    @Override // fait tout tourner la tuile supplémentaire nbTimes fois (vers la droite)
    public void rotateExtraTile(int nbTimes) {
        game.rotateExtraTile(nbTimes);
    }
}