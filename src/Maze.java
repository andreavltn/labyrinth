import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;

import java.util.*;

public class Maze {
    private Set<Integer> fixedRows;
    private Set<Integer> fixedColumns;
    private final Grid<Tile> grid;
    private final Tile extraTile;

    public Maze(Grid<Tile> tiles, Tile extraTile, Set<Integer> fixedRows, Set<Integer> fixedColumns, Grid<Tile> grid) {
        this.grid = grid;
        this.extraTile = extraTile;
    }

    public Grid<Tile> getTiles() {
        return this.grid;
    }

    public Position getPosition(Tile tile){
        return null;
    }

    public Set<Integer> getFixedRows() {
        return null;
    }

    public Set<Integer> getFixedColumns() {
        return null;
    }

    public Tile getExtraTile(){
        return this.extraTile;
    }

}
