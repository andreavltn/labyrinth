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

    private final Grid<SimpleObservableValue<Tile>> grid; // la grille de tuiles du jeu

    private final SimpleObservableValue<Tile> extraTile; // la tuile supplémentaire

    private final Set<Integer> fixedRows; // indices des lignes fixes

    private final Set<Integer> fixedColumns; // indices des colonnes fixes

    private final SimpleObservableValue<MazeMove> forbiddenMove; // le mouvement interdit du tour en cours

    public Maze(
            Grid<Tile> tiles,
            Tile extraTile,
            Set<Integer> fixedRows,
            Set<Integer> fixedColumns
    ) {

        // NullPointerException si au moins 1 paramètre est null
        if (tiles == null
                || extraTile == null
                || fixedRows == null
                || fixedColumns == null) {

            throw new NullPointerException(
                    "Aucun paramètre ne peut être null"
            );
        }

        // NullPointerException si une tuile de la grille est null
        for (int y = 0;
             y < tiles.getHeight();
             y++) {

            for (int x = 0;
                 x < tiles.getWidth();
                 x++) {

                if (tiles.get(x, y) == null) {

                    throw new NullPointerException(
                            "La tuile en ("
                                    + x
                                    + ", "
                                    + y
                                    + ") est null"
                    );
                }
            }
        }

        // IllegalArgumentException si une instance de Tile apparait plus d'une fois
        Set<Tile> seen = new HashSet<>();

        seen.add(extraTile); // ajout de la tuile supplémentaire car elle en fait partie aussi

        for (int y = 0;
             y < tiles.getHeight();
             y++) {

            for (int x = 0;
                 x < tiles.getWidth();
                 x++) {

                Tile t = tiles.get(x, y); // récupère la tuile

                if (seen.contains(t)) { // si la tuile fait partie des tuiles déjà vues

                    throw new IllegalArgumentException(
                            "Tuile en doublon détectée"
                    );
                }

                seen.add(t); // on l'ajoute aux tuiles déjà vues
            }
        }

        // IllegalArgumentException si chaque joueur n'a pas exactement une position de départ
        for (int id = 0;
             id < 4;
             id++) {

            int count = 0;

            for (int y = 0;
                 y < tiles.getHeight();
                 y++) {

                for (int x = 0;
                     x < tiles.getWidth();
                     x++) {

                    Content content =
                            tiles.get(x, y)
                                    .getContent();

                    // récupère le contenu de la tuile

                    if (content.isStartingPositionFor(id)) {

                        // si la tuile est la position de départ du joueur

                        count++; // incrémente le compteur
                    }
                }
            }

            if (count != 1) {

                // si on a trouvé plus d'une position de départ pour un joueur

                throw new IllegalArgumentException(
                        "Le joueur "
                                + id
                                + " a "
                                + count
                                + " positions de départ"
                );
            }
        }

        // IllegalArgumentException si chaque trésor n'est pas présent sur exactement une tuile
        Map<Integer, Integer> treasureCount =
                new HashMap<>();

        // map idTrésor -> nbFoisVu

        for (int y = 0;
             y < tiles.getHeight();
             y++) {

            for (int x = 0;
                 x < tiles.getWidth();
                 x++) {

                Content content =
                        tiles.get(x, y)
                                .getContent();

                // récupère le contenu de la tuile

                switch (content) {

                    case Treasure tr -> {

                        // si c'est un trésor

                        if (treasureCount.containsKey(tr.id())) {

                            // si il fait déjà partie de la liste

                            treasureCount.put(
                                    tr.id(),
                                    treasureCount.get(tr.id()) + 1
                            );

                            // ajoute 1 à son nbFoisVu
                        }

                        else {

                            treasureCount.put(
                                    tr.id(),
                                    1
                            );

                            // sinon l'ajoute à la map, nbFoisVu = 1
                        }
                    }

                    default -> {
                    } // sinon ce n'est pas un trésor, ne fait rien
                }
            }
        }

        // pour chaque indice de trésor de la map
        for (int id : treasureCount.keySet()) {

            int count =
                    treasureCount.get(id);

            // récupère le nbFoisVu

            if (count != 1) {

                // si un trésor n'a pas été vu exactement 1 fois

                throw new IllegalArgumentException(
                        "Le trésor "
                                + id
                                + " apparaît "
                                + count
                                + " fois"
                );
            }
        }

        // sinon tout va bien

        // initialisation de la grille observable
        this.grid =
                new ArrayGrid<>(
                        tiles.getWidth(),
                        tiles.getHeight()
                );

        for (int y = 0;
             y < tiles.getHeight();
             y++) {

            for (int x = 0;
                 x < tiles.getWidth();
                 x++) {

                this.grid.set(
                        x,
                        y,
                        new SimpleObservableValue<>(
                                tiles.get(x, y)
                        )
                );
            }
        }

        this.extraTile =
                new SimpleObservableValue<>(
                        extraTile
                );

        this.forbiddenMove =
                new SimpleObservableValue<>(
                        null
                );

        this.fixedRows =
                new HashSet<>(fixedRows);

        this.fixedColumns =
                new HashSet<>(fixedColumns);
    }

    public Grid<ObservableValue<Tile>>
    getObservableTiles() {

        return grid.map(
                SimpleObservableValue::asReadOnly
        );
    }

    public Grid<Tile> getTiles() {

        return grid.map(
                SimpleObservableValue::getValue
        );
    }

    public Position getPosition(
            Tile tile
    ) {

        for (int y = 0;
             y < grid.getHeight();
             y++) {

            // parcours de chaque tuile de la grille

            for (int x = 0;
                 x < grid.getWidth();
                 x++) {

                Tile tileXY =
                        grid.get(x, y)
                                .getValue();

                if (tileXY.equals(tile)) {

                    // si la tuile (x,y) correspond à celle en paramètre

                    return new Position(x, y);

                    // renvoie sa position
                }
            }
        }

        return null;
    }

    public Set<Integer> getFixedRows() {

        return Set.copyOf(this.fixedRows);

        // immutable
    }

    public Set<Integer> getFixedColumns() {

        return Set.copyOf(this.fixedColumns);

        // immutable
    }

    public ObservableValue<Tile>
    extraTileProperty() {

        return this.extraTile.asReadOnly();
    }

    public Tile getExtraTile() {

        return this.extraTile.getValue();
    }

    public Tile getTile(
            Player player
    ) {

        for (int y = 0;
             y < grid.getHeight();
             y++) {

            for (int x = 0;
                 x < grid.getWidth();
                 x++) {

                Tile tile =
                        grid.get(x, y)
                                .getValue();

                if (tile.getPlayers()
                        .contains(player)) {

                    return tile;
                }
            }
        }

        return null;
    }

    public Tile getStartingTile(
            Player player
    ) {

        // pour chaque tuile du jeu
        for (int x = 0;
             x < grid.getWidth();
             x++) {

            for (int y = 0;
                 y < grid.getHeight();
                 y++) {

                Tile tile =
                        grid.get(x, y)
                                .getValue();

                // récupère la tuile

                Content contenu =
                        tile.getContent();

                // récupère son contenu

                // si le contenu de la tuile est la position de départ du joueur recherché
                if (contenu.isStartingPositionFor(
                        player.getIndex()
                )) {

                    return tile;

                    // alors la renvoie
                }
            }
        }

        return null;
    }

    public ObservableValue<MazeMove>
    forbiddenMoveProperty() {

        return this.forbiddenMove.asReadOnly();
    }

    public MazeMove getForbiddenMove() {

        return this.forbiddenMove.getValue();
    }

    // un déplacement du jeu (le joueur pose une tuile sur un slot)
    public void move(
            MazeMove move
    ) {

        // vérification mouvement interdit
        if (move.equals(
                forbiddenMove.getValue()
        )) {

            throw new IllegalArgumentException(
                    "Ce mouvement est interdit"
            );
        }

        // récupère les infos du mouvement
        Direction direction =
                move.direction();

        int index =
                move.index();

        // garde une référence vers la tuile qu'on insère
        Tile insertedTile =
                extraTile.getValue();

        // déplacement
        switch (direction) {

            case DOWN -> {

                // si on déplace vers le bas

                Tile exitTile =
                        grid.get(
                                index,
                                grid.getHeight() - 1
                        ).getValue();

                // récupère la tuile tout en bas de la col

                // les joueurs présents sur la tuile expulsée réapparaissent sur la tuile insérée
                List<Player> movedPlayers =
                        new ArrayList<>(
                                exitTile.getPlayers()
                        );

                for (int y = grid.getHeight() - 1;
                     y > 0;
                     y--) {

                    // pour chaque tuile de la colonne

                    grid.get(index, y)
                            .setValue(
                                    grid.get(index, y - 1)
                                            .getValue()
                            );

                    // déplace d'une case vers le bas
                }

                grid.get(index, 0)
                        .setValue(insertedTile);

                // place la tuile en haut de la col

                this.extraTile.setValue(exitTile);

                // met à jour la tuile supplémentaire

                for (Player player : movedPlayers) {

                    exitTile.removePlayer(player);

                    insertedTile.addPlayer(player);
                }
            }

            case UP -> {

                // idem ensuite dans toutes les directions

                Tile exitTile =
                        grid.get(index, 0)
                                .getValue();

                List<Player> movedPlayers =
                        new ArrayList<>(
                                exitTile.getPlayers()
                        );

                for (int y = 0;
                     y < grid.getHeight() - 1;
                     y++) {

                    grid.get(index, y)
                            .setValue(
                                    grid.get(index, y + 1)
                                            .getValue()
                            );
                }

                grid.get(
                        index,
                        grid.getHeight() - 1
                ).setValue(insertedTile);

                extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {

                    exitTile.removePlayer(player);

                    insertedTile.addPlayer(player);
                }
            }

            case RIGHT -> {

                Tile exitTile =
                        grid.get(
                                grid.getWidth() - 1,
                                index
                        ).getValue();

                List<Player> movedPlayers =
                        new ArrayList<>(
                                exitTile.getPlayers()
                        );

                for (int x = grid.getWidth() - 1;
                     x > 0;
                     x--) {

                    grid.get(x, index)
                            .setValue(
                                    grid.get(x - 1, index)
                                            .getValue()
                            );
                }

                grid.get(0, index)
                        .setValue(insertedTile);

                extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {

                    exitTile.removePlayer(player);

                    insertedTile.addPlayer(player);
                }
            }

            case LEFT -> {

                Tile exitTile =
                        grid.get(0, index)
                                .getValue();

                List<Player> movedPlayers =
                        new ArrayList<>(
                                exitTile.getPlayers()
                        );

                for (int x = 0;
                     x < grid.getWidth() - 1;
                     x++) {

                    grid.get(x, index)
                            .setValue(
                                    grid.get(x + 1, index)
                                            .getValue()
                            );
                }

                grid.get(
                        grid.getWidth() - 1,
                        index
                ).setValue(insertedTile);

                extraTile.setValue(exitTile);

                for (Player player : movedPlayers) {

                    exitTile.removePlayer(player);

                    insertedTile.addPlayer(player);
                }
            }
        }

        // stocke le mouvement interdit pour le prochain tour
        this.forbiddenMove.setValue(
                move.getOpposite()
        );
    }

    // recrée une instance de Maze depuis les données du DTO
    public static Maze from(
            MazeDTO dto,
            List<Player> players
    ) {

        Grid<TileDTO> dtoCells =
                dto.getTiles();

        // récupère les tuiles du DTO

        int w =
                dtoCells.getWidth();

        // récupère la largeur de la grille

        int h =
                dtoCells.getHeight();

        // et la hauteur

        // reconstruction de la grille de tuiles
        Grid<Tile> grid =
                new ArrayGrid<>(w, h);

        // création d'une grille vide

        for (int y = 0;
             y < h;
             y++) {

            for (int x = 0;
                 x < w;
                 x++) {

                TileDTO tileDTO =
                        dtoCells.get(x, y);

                if (tileDTO == null) {

                    throw new IllegalStateException(
                            "TileDTO null en ("
                                    + x
                                    + ", "
                                    + y
                                    + ")"
                    );
                }

                // ajout des tuiles via Tile.from(TileDTO, players)

                grid.set(
                        x,
                        y,
                        Tile.from(
                                tileDTO,
                                players
                        )
                );
            }
        }

        // recrée aussi la tuile supplémentaire via la même méthode
        Tile extraTile =
                Tile.from(
                        dto.getExtraTile(),
                        players
                );

        // recrée les indices des lignes/colonnes fixes
        Set<Integer> fixedRows =
                dto.getFixedRows() == null
                        ? new HashSet<>()
                        : new HashSet<>(
                        dto.getFixedRows()
                );

        Set<Integer> fixedColumns =
                dto.getFixedColumns() == null
                        ? new HashSet<>()
                        : new HashSet<>(
                        dto.getFixedColumns()
                );

        // création du Maze
        Maze maze =
                new Maze(
                        grid,
                        extraTile,
                        fixedRows,
                        fixedColumns
                );

        // récupère le mouvement interdit
        if (dto.getForbiddenMove()
                != null) {

            // si il n'est pas nul

            maze.setForbiddenMove(
                    dto.getForbiddenMove()
            );

            // l'ajoute au nouveau maze
        }

        return maze;

        // renvoie le nouveau maze
    }

    // change le mouvement interdit, utilisé uniquement pour la méthode
    // Maze.from() juste au dessus, c'est une méthode privée
    private void setForbiddenMove(
            MazeMove move
    ) {

        this.forbiddenMove.setValue(move);
    }
}