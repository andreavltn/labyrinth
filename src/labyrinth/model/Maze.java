package labyrinth.model;

import labyrinth.service.serialization.MazeDTO;
import labyrinth.service.serialization.TileDTO;
import labyrinth.util.collection.ArrayGrid;
import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;
import labyrinth.util.observable.ObservableValue;
import labyrinth.util.observable.SimpleObservableValue;

import java.util.*;

public class Maze {

    private final Grid<SimpleObservableValue<Tile>> grid;
    private final SimpleObservableValue<Tile> extraTile;
    private final Set<Integer> fixedRows;
    private final Set<Integer> fixedColumns;
    private final SimpleObservableValue<MazeMove> forbiddenMove;

    public Maze(
            Grid<Tile> tiles,
            Tile extraTile,
            Set<Integer> fixedRows,
            Set<Integer> fixedColumns
    ) {

        if (tiles == null || extraTile == null || fixedRows == null || fixedColumns == null) {
            throw new NullPointerException("Aucun paramètre ne peut être null");
        }

        for (int y = 0; y < tiles.getHeight(); y++) {
            for (int x = 0; x < tiles.getWidth(); x++) {
                if (tiles.get(x, y) == null) {
                    throw new NullPointerException("La tuile en (" + x + ", " + y + ") est null");
                }
            }
        }

        Set<Tile> seen = new HashSet<>();
        seen.add(extraTile);

        for (int y = 0; y < tiles.getHeight(); y++) {
            for (int x = 0; x < tiles.getWidth(); x++) {

                Tile t = tiles.get(x, y);

                if (seen.contains(t)) {
                    throw new IllegalArgumentException("Tuile en doublon détectée");
                }

                seen.add(t);
            }
        }

        for (int id = 0; id < 4; id++) {

            int count = 0;

            for (int y = 0; y < tiles.getHeight(); y++) {
                for (int x = 0; x < tiles.getWidth(); x++) {

                    Content content = tiles.get(x, y).getContent();

                    if (content.isStartingPositionFor(id)) {
                        count++;
                    }
                }
            }

            if (count != 1) {
                throw new IllegalArgumentException("Le joueur " + id + " a " + count + " positions de départ");
            }
        }

        Map<Integer, Integer> treasureCount = new HashMap<>();

        for (int y = 0; y < tiles.getHeight(); y++) {
            for (int x = 0; x < tiles.getWidth(); x++) {

                Content content = tiles.get(x, y).getContent();

                switch (content) {

                    case Treasure tr -> {
                        treasureCount.put(tr.id(), treasureCount.getOrDefault(tr.id(), 0) + 1);
                    }

                    default -> {
                    }
                }
            }
        }

        for (int id : treasureCount.keySet()) {

            int count = treasureCount.get(id);

            if (count != 1) {
                throw new IllegalArgumentException("Le trésor " + id + " apparaît " + count + " fois");
            }
        }

        this.grid = new ArrayGrid<>(tiles.getWidth(), tiles.getHeight());

        for (int y = 0; y < tiles.getHeight(); y++) {
            for (int x = 0; x < tiles.getWidth(); x++) {

                this.grid.set(x, y, new SimpleObservableValue<>(tiles.get(x, y)));
            }
        }

        this.extraTile = new SimpleObservableValue<>(extraTile);
        this.forbiddenMove = new SimpleObservableValue<>(null);
        this.fixedRows = new HashSet<>(fixedRows);
        this.fixedColumns = new HashSet<>(fixedColumns);
    }

    public Grid<ObservableValue<Tile>> getObservableTiles() {
        return grid.map(SimpleObservableValue::asReadOnly);
    }

    public Grid<Tile> getTiles() {
        return grid.map(SimpleObservableValue::getValue);
    }

    public Position getPosition(Tile tile) {

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {

                Tile tileXY = grid.get(x, y).getValue();

                if (tileXY.equals(tile)) {
                    return new Position(x, y);
                }
            }
        }

        return null;
    }

    public Set<Integer> getFixedRows() {
        return Set.copyOf(this.fixedRows);
    }

    public Set<Integer> getFixedColumns() {
        return Set.copyOf(this.fixedColumns);
    }

    public ObservableValue<Tile> extraTileProperty() {
        return this.extraTile.asReadOnly();
    }

    public Tile getExtraTile() {
        return this.extraTile.getValue();
    }

    public Tile getTile(Player player) {

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {

                Tile tile = grid.get(x, y).getValue();

                if (tile.getPlayers().contains(player)) {
                    return tile;
                }
            }
        }

        return null;
    }

    public Tile getStartingTile(Player player) {

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {

                Tile tile = grid.get(x, y).getValue();
                Content contenu = tile.getContent();

                if (contenu.isStartingPositionFor(player.getIndex())) {
                    return tile;
                }
            }
        }

        return null;
    }

    public ObservableValue<MazeMove> forbiddenMoveProperty() {
        return this.forbiddenMove.asReadOnly();
    }

    public MazeMove getForbiddenMove() {
        return this.forbiddenMove.getValue();
    }

    // un déplacement du jeu (le joueur pose une tuile sur un slot)
    public void move(MazeMove move) {

        if (move.equals(forbiddenMove.getValue())) {
            throw new IllegalArgumentException("Ce mouvement est interdit");
        }

        Direction direction = move.direction();
        int index = move.index();

        Tile insertedTile = extraTile.getValue();

        switch (direction) {

            case DOWN -> {

                Tile exitTile = grid.get(index, grid.getHeight() - 1).getValue();

                List<Player> movedPlayers = new ArrayList<>(exitTile.getPlayers());

                for (int y = grid.getHeight() - 1; y > 0; y--) {
                    grid.get(index, y).setValue(grid.get(index, y - 1).getValue());
                }

                grid.get(index, 0).setValue(insertedTile);
                this.extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {
                    exitTile.removePlayer(player);
                    insertedTile.addPlayer(player);
                }
            }

            case UP -> {

                Tile exitTile = grid.get(index, 0).getValue();

                List<Player> movedPlayers = new ArrayList<>(exitTile.getPlayers());

                for (int y = 0; y < grid.getHeight() - 1; y++) {
                    grid.get(index, y).setValue(grid.get(index, y + 1).getValue());
                }

                grid.get(index, grid.getHeight() - 1).setValue(insertedTile);
                extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {
                    exitTile.removePlayer(player);
                    insertedTile.addPlayer(player);
                }
            }

            case RIGHT -> {

                Tile exitTile = grid.get(grid.getWidth() - 1, index).getValue();

                List<Player> movedPlayers = new ArrayList<>(exitTile.getPlayers());

                for (int x = grid.getWidth() - 1; x > 0; x--) {
                    grid.get(x, index).setValue(grid.get(x - 1, index).getValue());
                }

                grid.get(0, index).setValue(insertedTile);
                extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {
                    exitTile.removePlayer(player);
                    insertedTile.addPlayer(player);
                }
            }

            case LEFT -> {

                Tile exitTile = grid.get(0, index).getValue();

                List<Player> movedPlayers = new ArrayList<>(exitTile.getPlayers());

                for (int x = 0; x < grid.getWidth() - 1; x++) {
                    grid.get(x, index).setValue(grid.get(x + 1, index).getValue());
                }

                grid.get(grid.getWidth() - 1, index).setValue(insertedTile);
                extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {
                    exitTile.removePlayer(player);
                    insertedTile.addPlayer(player);
                }
            }
        }

        this.forbiddenMove.setValue(move.getOpposite());
    }

    public static Maze from(MazeDTO dto, List<Player> players) {

        Grid<TileDTO> dtoCells = dto.getTiles();

        int w = dtoCells.getWidth();
        int h = dtoCells.getHeight();

        Grid<Tile> grid = new ArrayGrid<>(w, h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                TileDTO tileDTO = dtoCells.get(x, y);

                if (tileDTO == null) {
                    throw new IllegalStateException("TileDTO null en (" + x + ", " + y + ")");
                }

                grid.set(x, y, Tile.from(tileDTO, players));
            }
        }

        Tile extraTile = Tile.from(dto.getExtraTile(), players);

        Set<Integer> fixedRows = dto.getFixedRows() == null ? new HashSet<>() : new HashSet<>(dto.getFixedRows());
        Set<Integer> fixedColumns = dto.getFixedColumns() == null ? new HashSet<>() : new HashSet<>(dto.getFixedColumns());

        Maze maze = new Maze(grid, extraTile, fixedRows, fixedColumns);

        if (dto.getForbiddenMove() != null) {
            maze.setForbiddenMove(dto.getForbiddenMove());
        }

        return maze;
    }

    private void setForbiddenMove(MazeMove move) {
        this.forbiddenMove.setValue(move);
    }
}