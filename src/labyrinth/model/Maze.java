package labyrinth.model;

import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;

import java.util.*;

public class Maze {

    private final Grid<Tile> grid;
    private final Tile extraTile;
    private final Set<Integer> fixedRows;
    private final Set<Integer> fixedColumns;

    public Maze(Grid<Tile> tiles, Tile extraTile, Set<Integer> fixedRows, Set<Integer> fixedColumns) {
        this.grid = tiles;
        this.extraTile = extraTile;
        this.fixedRows = new HashSet<>(fixedRows);
        this.fixedColumns = new HashSet<>(fixedColumns);
    }

    public Grid<Tile> getTiles() {
        return this.grid;
    }

    public Position getPosition(Tile tile){

        for (int y = 0; y < grid.getHeight(); y++) { // parcours de chaque tuile de la grille
            for (int x = 0; x < grid.getWidth(); x++) {

                if (grid.get(x, y).equals(tile)) { // si la tuile (x,y) correspond à celle en paramètre
                    return new Position(x, y); // renvoie sa position
                }
            }
        }

        return null;
    }

    public Set<Integer> getFixedRows() {
        return Set.copyOf(fixedRows); // immutable
    }

    public Set<Integer> getFixedColumns() {
        return Set.copyOf(fixedColumns); // immutable
    }

    public Tile getExtraTile() {
        return this.extraTile;
    }

}
