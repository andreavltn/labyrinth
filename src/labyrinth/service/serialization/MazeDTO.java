package labyrinth.service.serialization;

import labyrinth.model.Maze;
import labyrinth.model.Tile;
import labyrinth.model.MazeMove;
import labyrinth.util.collection.ArrayGrid;
import labyrinth.util.collection.Grid;
import java.util.ArrayList;
import java.util.List;

public class MazeDTO {

    private final Grid<TileDTO> tiles;
    private final TileDTO extraTile;
    private static List<Integer> fixedRows;
    private static List<Integer> fixedColumns;
    private final MazeMove forbiddenMove;

    // constructeur à partir d'un labyrinthe
    public MazeDTO(Maze maze) {

        Grid<Tile> modelGrid = maze.getTiles(); // récupère les tuiles
        int w = modelGrid.getWidth(); // récupère largeur
        int h = modelGrid.getHeight(); // récupère hauteur

        // crée une nouvelle grille largeur*hauteur
        this.tiles = new ArrayGrid<>(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                this.tiles.set(x, y, new TileDTO(modelGrid.get(x, y))); // ajoute des TileDTO
            }
        }

        // tuile supplémentaire
        this.extraTile = new TileDTO(maze.getExtraTile());

        // indices des lignes et colonnes fixes
        this.fixedRows    = new ArrayList<>(maze.getFixedRows());
        this.fixedColumns = new ArrayList<>(maze.getFixedColumns());

        // mouvement interdit
        this.forbiddenMove = maze.getForbiddenMove();
    }

    public Grid<TileDTO> getTiles() {
        return tiles;
    }

    public TileDTO getExtraTile() {
        return extraTile;
    }

    public static List<Integer> getFixedRows() {
        return fixedRows;
    }

    public static List<Integer> getFixedColumns() {
        return fixedColumns;
    }

    public MazeMove getForbiddenMove() {
        return forbiddenMove;
    }
}