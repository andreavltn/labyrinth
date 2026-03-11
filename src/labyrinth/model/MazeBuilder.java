package labyrinth.model;

import labyrinth.util.collection.ArrayGrid;
import labyrinth.util.collection.Grid;

import java.util.*;

public class MazeBuilder {

    private final Grid<Tile> grid;
    private final List<Tile> freeTiles;
    private final Set<Integer> fixedRows;
    private final Set<Integer> fixedColumns;
    private final Random random;

    public MazeBuilder(int width, int height) {
        this.grid = new ArrayGrid<>(width, height);
        this.freeTiles = new ArrayList<>();
        this.fixedRows = new HashSet<>();
        this.fixedColumns = new HashSet<>();
        this.random = new Random();
    }

    public MazeBuilder addFixedTile(int x, int y, Tile tile) {
        grid.set(x, y, tile);
        fixedRows.add(y);
        fixedColumns.add(x);
        return this;
    }

    public MazeBuilder addFreeTile(Tile tile) {
        freeTiles.add(tile);
        return this;
    }

    public Maze build() {

        List<Tile> tiles = new ArrayList<>(freeTiles);

        Collections.shuffle(tiles);

        List<Tile> randomizedTiles = new ArrayList<>();

        for (Tile tile : tiles) {

            Direction orientation =
                    Direction.UP.getRotated(random.nextInt(4));

            randomizedTiles.add(
                    new Tile(
                            tile.getKind(),
                            tile.getContent(),
                            orientation
                    )
            );
        }

        tiles = randomizedTiles;

        int width = grid.getWidth();
        int height = grid.getHeight();

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (grid.get(x, y) == null) {
                    grid.set(x, y, tiles.get(index));
                    index++;
                }
            }
        }

        Tile extraTile = tiles.get(index);

        return new Maze(grid, extraTile, fixedRows, fixedColumns);
    }

    public static Maze buildStandardMaze() {

        MazeBuilder builder = new MazeBuilder(7, 7);

        /*
         * ========================
         * COINS (positions joueurs)
         * ========================
         */

        builder.addFixedTile(0, 0,
                new Tile(TileKind.ELBOW,
                        new StartingPosition(0),
                        Direction.RIGHT));

        builder.addFixedTile(6, 0,
                new Tile(TileKind.ELBOW,
                        new StartingPosition(1),
                        Direction.DOWN));

        builder.addFixedTile(0, 6,
                new Tile(TileKind.ELBOW,
                        new StartingPosition(2),
                        Direction.UP));

        builder.addFixedTile(6, 6,
                new Tile(TileKind.ELBOW,
                        new StartingPosition(3),
                        Direction.LEFT));

        /*
         * ========================
         * TUILES FIXES AVEC TRÉSORS
         * ========================
         */

        int treasureId = 0;

        int[][] positions = {
                {2,0},{4,0},
                {0,2},{2,2},{4,2},{6,2},
                {0,4},{2,4},{4,4},{6,4},
                {2,6},{4,6}
        };

        for (int[] pos : positions) {

            builder.addFixedTile(
                    pos[0],
                    pos[1],
                    new Tile(
                            TileKind.T_CROSS,
                            new Treasure(treasureId++),
                            Direction.UP
                    )
            );
        }

        /*
         * ========================
         * TUILES LIBRES
         * ========================
         */

        for (int i = 0; i < 13; i++) {
            builder.addFreeTile(
                    new Tile(TileKind.CORRIDOR, new None())
            );
        }

        for (int i = 0; i < 9; i++) {
            builder.addFreeTile(
                    new Tile(TileKind.ELBOW, new None())
            );
        }

        for (int i = 0; i < 6; i++) {
            builder.addFreeTile(
                    new Tile(TileKind.ELBOW,
                            new Treasure(treasureId++))
            );
        }

        for (int i = 0; i < 6; i++) {
            builder.addFreeTile(
                    new Tile(TileKind.T_CROSS,
                            new Treasure(treasureId++))
            );
        }

        return builder.build();
    }
}