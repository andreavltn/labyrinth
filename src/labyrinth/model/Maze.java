package labyrinth.model;

import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;

import java.util.*;

public class Maze {

    private final Grid<Tile> grid; // la grille de tuiles du jeu
    private Tile extraTile; // la tuile supplémentaire
    private final Set<Integer> fixedRows; // indices des lignes fixes
    private final Set<Integer> fixedColumns; // indices des colonnes fixes
    private MazeMove forbiddenMove; // le mouvement interdit du tour en cours

    public Maze(Grid<Tile> tiles, Tile extraTile, Set<Integer> fixedRows, Set<Integer> fixedColumns) {

        // NullPointerException si au moins 1 paramètre est null
        if (tiles == null || extraTile == null || fixedRows == null || fixedColumns == null) {
            throw new NullPointerException("Aucun paramètre ne peut être null");
        }

        // NullPointerException si une tuile de la grille est null
        for (int y = 0; y < tiles.getHeight(); y++) { // pour chaque coordonnées
            for (int x = 0; x < tiles.getWidth(); x++) {
                if (tiles.get(x, y) == null) { // si la tuile est null
                    throw new NullPointerException("La tuile en (" + x + ", " + y + ") est null");
                }
            }
        }

        // IllegalArgumentException si une instance de Tile apparait plus d'une fois
        Set<Tile> seen = new HashSet<>();
        seen.add(extraTile); // ajout de la tuile supplémentaire car elle en fait partie aussi
        for (int y = 0; y < tiles.getHeight(); y++) { // pour chaque coordonnées
            for (int x = 0; x < tiles.getWidth(); x++) {
                Tile t = tiles.get(x, y); // récupère la tuile
                if (seen.contains(t)) { // si la tuile fait partie des tuiles déjà vues
                    throw new IllegalArgumentException("Tuile en doublon détectée"); // tuile doublon
                }
                seen.add(t); // on l'ajoute aux tuiles déjà vues
            }
        }

        // IllegalArgumentException si chaque joueur n'a pas exactement une position de départ
        for (int id = 0; id < 4; id++) { // pour chaque joueur
            int count = 0;
            for (int y = 0; y < tiles.getHeight(); y++) { // pour chaque coordonnées
                for (int x = 0; x < tiles.getWidth(); x++) {
                    Content content = tiles.get(x, y).getContent(); // récupère le contenu de la tuile
                    if (content.isStartingPositionFor(id)) { // si la tuile est la position de départ du joueur
                        count++; // incrémente le compteur
                    }
                }
            }
            if (count != 1) { // si on a trouvé plus d'une position de départ pour un joueur
                throw new IllegalArgumentException("Le joueur " + id + " a " + count + " positions de départ");
            }
        }

        // IllegalArgumentException si chaque trésor n'est pas présent sur exactement une tuile
        Map<Integer, Integer> treasureCount = new HashMap<>(); // map idTrésor -> nbFoisVu
        for (int y = 0; y < tiles.getHeight(); y++) { // pour chaque coordonnées
            for (int x = 0; x < tiles.getWidth(); x++) {
                Content content = tiles.get(x, y).getContent(); // récupère le contenu de la tuile
                switch (content) {
                    case Treasure tr -> { // si c'est un trésor
                        if (treasureCount.containsKey(tr.id())) { // si il fait déjà partie de la liste
                            treasureCount.put(tr.id(), treasureCount.get(tr.id()) + 1); // ajoute 1 à son nbFoisVu
                        } else {
                            treasureCount.put(tr.id(), 1); // sinon l'ajoute à la map, nbFoisVu = 1
                        }
                    }
                    default -> {} // sinon ce n'est pas un trésor, ne fait rien
                }
            }
        }

        // pour chaque indice de trésor de la map
        for (int id : treasureCount.keySet()) {
            int count = treasureCount.get(id); // récupère le nbFoisVu
            if (count != 1) { // si un trésor n'a pas été vu exactement 1 fois
                throw new IllegalArgumentException("Le trésor " + id + " apparaît " + count + " fois");
            }
        }

        // sinon tout va bien
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
        return Set.copyOf(this.fixedRows); // immutable
    }

    public Set<Integer> getFixedColumns() {
        return Set.copyOf(this.fixedColumns); // immutable
    }

    public Tile getExtraTile() {
        return this.extraTile;
    }

    public Tile getTile(Player player) {

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Tile tile = grid.get(x, y);
                if (tile.getContent().isStartingPositionFor(player.getIndex())) {
                    return tile;
                }

            }
        }
        return null;
    }

    public Tile getStartingTile(Player player){

        // pour chaque tuile du jeu
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.get(x, y); // récupère la tuile
                Content contenu = tile.getContent(); // récupère son contenu

                // si le contenu de la tuile est la position de départ du joueur recherché
                if(contenu.isStartingPositionFor(player.getIndex())){
                    return tile; // alors la renvoie
                }
            }
        }
        return null;
    }

    public MazeMove getForbiddenMove() {
        return this.forbiddenMove;
    }

    // un déplacement du jeu (le joueur pose une tuile sur un slot)
    public void move(MazeMove move) {

        // vérification mouvement interdit
        if (move.equals(forbiddenMove)) {
            throw new IllegalArgumentException("Ce mouvement est interdit");
        }

        // récupère les infos du mouvement
        Direction direction = move.direction();
        int index = move.index();

        // déplacement
        switch (direction) {

            case DOWN -> { // si on déplace vers le bas
                Tile exitTile = grid.get(index, grid.getHeight() - 1); // récupère la tuile tout en bas de la col
                for (int y = grid.getHeight() - 1; y > 0; y--) { // pour chaque tuile de la colonne (
                    grid.set(index, y, grid.get(index, y - 1)); // déplace d'une case vers le bas
                }
                grid.set(index, 0, extraTile); // place la tuile en haut de la col
                this.extraTile = exitTile; // met à jour la tuile supplémentaire
            }

            case UP -> {
                // idem ensuite dans toutes les directions
                Tile exitTile = grid.get(index, 0);
                for (int y = 0; y < grid.getHeight() - 1; y++) {
                    grid.set(index, y, grid.get(index, y + 1));
                }
                grid.set(index, grid.getHeight() - 1, extraTile);
                extraTile = exitTile;
            }

            case RIGHT -> {
                Tile exitTile = grid.get(grid.getWidth() - 1, index);
                for (int x = grid.getWidth() - 1; x > 0; x--) {
                    grid.set(x, index, grid.get(x - 1, index));
                }
                grid.set(0, index, extraTile);
                extraTile = exitTile;
            }

            case LEFT -> {
                Tile exitTile = grid.get(0, index);
                for (int x = 0; x < grid.getWidth() - 1; x++) {
                    grid.set(x, index, grid.get(x + 1, index));
                }
                grid.set(grid.getWidth() - 1, index, extraTile);
                extraTile = exitTile;
            }
        }

        // stocke le mouvement interdit pour le prochain tour
        this.forbiddenMove = move.getOpposite();
    }

}
