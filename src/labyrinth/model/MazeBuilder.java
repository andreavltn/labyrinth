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

    // place une tuile fixe en (x,y)
    public MazeBuilder addFixedTile(int x, int y, Tile tile) {
        grid.set(x, y, tile);
        fixedRows.add(y);
        fixedColumns.add(x);
        return this;
    }

    // place une tuile libre (position automatique)
    public MazeBuilder addFreeTile(Tile tile) {
        freeTiles.add(tile);
        return this;
    }

    public Maze build() {

        // copie de la liste des tuiles libres
        List<Tile> tiles = new ArrayList<>(freeTiles);

        // mélange aléatoire de l'ordre des tuiles libres
        Collections.shuffle(tiles);

        // crée une nouvelle liste pour les tuiles avec orientation aléatoire
        List<Tile> randomizedTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            int nbRotations = random.nextInt(4); // nombre de rotations aléatoire
            Direction orientation = Direction.UP.getRotated(nbRotations); // applique les rotations

            // ajout de la tuile à la liste
            randomizedTiles.add(new Tile(tile.getKind(), tile.getContent(), orientation));
        }

        // remplace les tuiles de base par les aléatoires
        tiles = randomizedTiles;

        int width = grid.getWidth();
        int height = grid.getHeight();
        int index = 0;

        for (int y = 0; y < height; y++) { // pour chaque coordonnées
            for (int x = 0; x < width; x++) {
                if (grid.get(x, y) == null) { // si aucune tuile n'a été posée
                    grid.set(x, y, tiles.get(index)); // ajoute la tuile
                    index++;
                }
            }
        }

        // la dernière tuile est la tuile supplémentaire
        Tile extraTile = tiles.get(index);

        return new Maze(grid, extraTile, fixedRows, fixedColumns);
    }

    // constructeur d'un labyrinthe par défaut
    public static Maze buildStandardMaze() {

        // taille 7x7
        MazeBuilder builder = new MazeBuilder(7, 7);

        // COINS (fixes)
        // haut gauche (position de départ du joueur id=0)
        builder.addFixedTile(0, 0, new Tile(TileKind.ELBOW, new StartingPosition(0), Direction.RIGHT));
        // haut droite (position de départ du joueur id=1)
        builder.addFixedTile(6, 0, new Tile(TileKind.ELBOW, new StartingPosition(1), Direction.DOWN));
        // bas gauche (position de départ du joueur id=2)
        builder.addFixedTile(0, 6, new Tile(TileKind.ELBOW, new StartingPosition(2), Direction.UP));
        // bas droite (position de départ du joueur id=3)
        builder.addFixedTile(6, 6, new Tile(TileKind.ELBOW, new StartingPosition(3), Direction.LEFT));

        // TRESORS (fixes)
        int treasureId = 0; // id du trésor qu'on est en train d'ajouter

        // positions des 12 trésors fixes
        int[][] positions = {
                {2,0},{4,0},
                {0,2},{2,2},{4,2},{6,2},
                {0,4},{2,4},{4,4},{6,4},
                {2,6},{4,6}
        };

        // pour chaque position
        for (int[] pos : positions) {
            Tile tr = new Tile(TileKind.T_CROSS, new Treasure(treasureId), Direction.UP); // création d'un trésor
            treasureId++; // incrémentation du treasureId
            builder.addFixedTile(pos[0], pos[1], tr); // ajout du trésor sur le labyrinthe
        }

        // TUILES LIBRES
        // 13 corridor vides
        for (int i = 0; i < 13; i++) {
            Tile cor = new Tile(TileKind.CORRIDOR, new None());
            builder.addFreeTile(cor);
        }

        // 9 coudes vides
        for (int i = 0; i < 9; i++) {
            Tile elb = new Tile(TileKind.ELBOW, new None());
            builder.addFreeTile(elb);
        }

        // 6 coudes avec trésor
        for (int i = 0; i < 6; i++) {
            Tile elbTR = new Tile(TileKind.ELBOW, new Treasure(treasureId));
            treasureId++; // incrémentation du treasureId
            builder.addFreeTile(elbTR);
        }

        // 6 intersections avec trésor
        for (int i = 0; i < 6; i++) {
            Tile tcrossTR = new Tile(TileKind.T_CROSS, new Treasure(treasureId));
            treasureId++; // incrémentation du treasureId
            builder.addFreeTile(tcrossTR);
        }

        return builder.build(); // retourne le labyrinthe
    }
}