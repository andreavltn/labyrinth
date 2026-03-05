package labyrinth.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import labyrinth.util.collection.ArrayGrid;
import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;
import java.util.Set;

public class ModelTest {

    // Direction.getRotated (tester les rotations des tuiles)
    @Test
    void getRotatedZero() {
        // vérifie que 0 rotations donne la même direction
        assertEquals(Direction.UP, Direction.UP.getRotated(0));
        assertEquals(Direction.RIGHT, Direction.RIGHT.getRotated(0));
    }

    @Test
    void getRotatedUnPositif() {
        // vérifie le cycle UP -> RIGHT -> DOWN -> LEFT -> UP
        assertEquals(Direction.RIGHT, Direction.UP.getRotated(1));
        assertEquals(Direction.DOWN, Direction.RIGHT.getRotated(1));
        assertEquals(Direction.LEFT, Direction.DOWN.getRotated(1));
        assertEquals(Direction.UP, Direction.LEFT.getRotated(1));
    }

    @Test
    void getRotatedUnNegatif() {
        // vérifie que la rotation inverse fonctionne (cycle inverse
        assertEquals(Direction.LEFT, Direction.UP.getRotated(-1));
        assertEquals(Direction.UP, Direction.RIGHT.getRotated(-1));
    }

    @Test
    void getRotatedRotationComplete() {
        // vérifie que 4 rotations donne la même direction
        assertEquals(Direction.UP, Direction.UP.getRotated(4));
        assertEquals(Direction.RIGHT, Direction.RIGHT.getRotated(4));
    }

    @Test
    void getRotatedDirectionOrdinal() {
        // vérifie que la rotation par ordinal fonctionne
        assertEquals(Direction.UP.getRotated(1), Direction.UP.getRotated(Direction.RIGHT));
        assertEquals(Direction.UP.getRotated(2), Direction.UP.getRotated(Direction.DOWN));
    }



    // Tile.getExits (tester les sorties des tuiles)
    @Test
    void getExitsCorridorDefault() {
        Tile tile = new Tile(TileKind.CORRIDOR);
        // corridor -> par défaut up et right
        assertEquals(Set.of(Direction.UP, Direction.DOWN), tile.getExits());
    }

    @Test
    void getExitsCorridorRotatedRight() {
        // corridor tourné à droite (1 cran) -> left et right
        Tile tile = new Tile(TileKind.CORRIDOR, new None(), Direction.RIGHT);
        assertEquals(Set.of(Direction.RIGHT, Direction.LEFT), tile.getExits());
    }

    @Test
    void getExitsElbowDefault() {
        Tile tile = new Tile(TileKind.ELBOW);
        // elbow par défaut -> par défaut up et right
        assertEquals(Set.of(Direction.UP, Direction.RIGHT), tile.getExits());
    }

    @Test
    void getExitsElbowRotatedDown() {
        // elbow tourné en bas (2 crans) -> down et left
        Tile tile = new Tile(TileKind.ELBOW, new None(), Direction.DOWN);
        assertEquals(Set.of(Direction.DOWN, Direction.LEFT), tile.getExits());
    }

    @Test
    void getExitsTCrossDefault() {
        Tile tile = new Tile(TileKind.T_CROSS);
        // TCross par défaut -> up, left et right
        assertEquals(Set.of(Direction.UP, Direction.LEFT, Direction.RIGHT), tile.getExits());
    }

    @Test
    void getExitsTCrossRotatedRight() {
        // TCross tourné à droite (1 cran) -> up, right et down
        Tile tile = new Tile(TileKind.T_CROSS, new None(), Direction.RIGHT);
        assertEquals(Set.of(Direction.RIGHT, Direction.UP, Direction.DOWN), tile.getExits());
    }



    // Maze.getPosition (tester les positions des tuiles)
    @Test
    void getPositionTile() {
        // tester si la position d'une tuile existante est renvoyée correctement
        Grid<Tile> grid = new ArrayGrid<>(3, 2);
        Tile tile = new Tile(TileKind.ELBOW);
        grid.set(1, 0, tile);
        grid.set(0, 0, new Tile(TileKind.CORRIDOR));
        grid.set(2, 1, new Tile(TileKind.T_CROSS));

        Maze maze = new Maze(grid, new Tile(TileKind.T_CROSS), Set.of(), Set.of());

        assertEquals(new Position(1, 0), maze.getPosition(tile));
    }

    @Test
    void getPositionMissingTile() {
        // tester si la position d'une tuile non existante (absente) renvoie null
        Grid<Tile> grid = new ArrayGrid<>(2, 2);
        grid.set(0, 0, new Tile(TileKind.CORRIDOR));
        grid.set(1, 0, new Tile(TileKind.ELBOW));
        grid.set(0, 1, new Tile(TileKind.T_CROSS));
        grid.set(1, 1, new Tile(TileKind.CORRIDOR));

        Maze maze = new Maze(grid, new Tile(TileKind.T_CROSS), Set.of(), Set.of());
        Tile missingTile = new Tile(TileKind.ELBOW); // pas dans la grille

        assertNull(maze.getPosition(missingTile));
    }

    @Test
    void getPositionExtraTile() {
        // tester si la position de la tuile supplémentaire est bien nulle
        Grid<Tile> grid = new ArrayGrid<>(2, 2);
        Tile extraTile = new Tile(TileKind.T_CROSS);
        grid.set(0, 0, new Tile(TileKind.CORRIDOR));
        grid.set(1, 0, new Tile(TileKind.ELBOW));
        grid.set(0, 1, new Tile(TileKind.T_CROSS));
        grid.set(1, 1, new Tile(TileKind.CORRIDOR));

        Maze maze = new Maze(grid, extraTile, Set.of(), Set.of());

        // l'extraTile n'est pas dans la grille
        assertNull(maze.getPosition(extraTile));
    }
}