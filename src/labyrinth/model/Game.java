package labyrinth.model;

import javafx.application.Platform;
import labyrinth.model.strategy.DefaultPlayStrategy;
import labyrinth.model.strategy.Move;
import labyrinth.model.strategy.PlayStrategy;
import labyrinth.service.serialization.GameDTO;
import labyrinth.service.serialization.PlayerDTO;
import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;
import labyrinth.util.graph.Graph;
import labyrinth.util.graph.MazeToGraphAdapter;
import labyrinth.util.graph.Paths;
import labyrinth.util.observable.ObservableValue;
import labyrinth.util.observable.SimpleObservableValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Game {

    private static final PlayStrategy STRATEGY =
            new DefaultPlayStrategy();

    private final SimpleObservableValue<Maze> maze;

    // le labyrinthe

    private final List<Slot> slots;

    // les slots de jeu

    private final List<Player> players;

    // les joueurs

    private final SimpleObservableValue<GameState> state;

    // l'état courant du jeu

    private final SimpleObservableValue<Player> currentPlayer;

    // le joueur courant

    private final Random random;

    // générateur aléatoire

    private final SimpleObservableValue<Paths<Tile>> paths;

    // les chemins accessibles par le joueur courant

    private final UUID uuid;

    // l'uuid de la partie

    public Game() {

        this.slots = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            this.slots.add(
                    new Slot(this, i)
            );
        }

        this.players =
                new ArrayList<>();

        this.state =
                new SimpleObservableValue<>(
                        GameState.SETUP
                );

        this.currentPlayer =
                new SimpleObservableValue<>(
                        null
                );

        this.maze =
                new SimpleObservableValue<>(
                        null
                );

        this.random =
                new Random();

        this.paths =
                new SimpleObservableValue<>(
                        null
                );

        this.uuid =
                UUID.randomUUID();
    }

    public List<Slot> getSlots() {
        return this.slots;
    }

    public Slot getSlot(int index) {
        return this.slots.get(index);
    }

    public Maze getMaze() {
        return this.maze.getValue();
    }

    public Tile getTile(Player player) {

        if (this.maze.getValue() == null) {
            return null;
        }

        return this.maze.getValue()
                .getTile(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public ObservableValue<GameState>
    stateProperty() {

        return this.state.asReadOnly();
    }

    public GameState getState() {
        return this.state.getValue();
    }

    public ObservableValue<Player>
    currentPlayerProperty() {

        return this.currentPlayer.asReadOnly();
    }

    public ObservableValue<Maze>
    mazeProperty() {

        return this.maze.asReadOnly();
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer.getValue();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    // initialise le jeu (SETUP)
    public void init() {

        // si on n'est pas au SETUP, erreur
        if (this.getState()
                != GameState.SETUP) {

            throw new IllegalStateException(
                    "Le jeu n'est pas en état SETUP"
            );
        }

        // récupère les joueurs depuis les slots ouverts
        this.players.clear();

        for (Slot slot : slots) {

            if (slot.getPlayer() != null) {

                this.players.add(
                        slot.getPlayer()
                );
            }
        }

        // construction du labyrinthe
        this.maze.setValue(
                MazeBuilder.buildStandardMaze()
        );

        // récupère tous les id des trésors présents dans le labyrinthe
        List<Integer> treasures =
                new ArrayList<>();

        Grid<Tile> grid =
                this.maze.getValue()
                        .getTiles();

        for (int y = 0;
             y < grid.getHeight();
             y++) {

            for (int x = 0;
                 x < grid.getWidth();
                 x++) {

                Content content =
                        grid.get(x, y)
                                .getContent();

                switch (content) {

                    case Treasure t ->
                            treasures.add(t.id());

                    default -> {
                    }
                }
            }
        }

        // initialise la main de chaque joueur
        for (Player player : players) {

            Collections.shuffle(treasures);

            player.getDeck().init(
                    new ArrayList<>(treasures)
            );
        }

        // place les joueurs sur leur SP
        for (Player player : players) {

            Tile startTile =
                    this.maze.getValue()
                            .getStartingTile(player);

            if (startTile != null) {

                startTile.addPlayer(player);
            }
        }

        // choisit un premier joueur au hasard
        Player randomPlayer =
                players.get(
                        random.nextInt(players.size())
                );

        this.currentPlayer.setValue(
                randomPlayer
        );

        // calcule les chemins du joueur courant
        computePaths();

        this.state.setValue(
                GameState.MAZE_MOVE
        );

        // si le premier joueur est un bot
        if (randomPlayer.isBot()) {

            Platform.runLater(
                    this::startTurn
            );
        }
    }

    // remettre le jeu à l'état initial
    public void reset() {

        this.state.setValue(
                GameState.SETUP
        );

        this.maze.setValue(null);

        this.currentPlayer.setValue(null);

        this.paths.setValue(null);

        // réinitialise le deck de chaque joueur
        for (Player player : players) {

            player.getDeck().init(
                    new ArrayList<>()
            );
        }

        // vide les slots
        for (Slot slot : slots) {

            if (slot.getPlayer() != null) {

                slot.close();
            }
        }

        // vide les joueurs
        this.players.clear();
    }

    // fait tourner la tuile supplémentaire
    public void rotateExtraTile(int nbTimes) {

        if (this.getState()
                != GameState.MAZE_MOVE) {

            throw new IllegalStateException(
                    "Le jeu n'est pas en état MAZE_MOVE"
            );
        }

        Tile extraTile =
                this.maze.getValue()
                        .getExtraTile();

        extraTile.rotate(nbTimes);
    }

    // éxécute le mouvement (MAZE_MOVE)
    public void moveMaze(MazeMove move) {

        if (this.getState()
                != GameState.MAZE_MOVE) {

            throw new IllegalStateException(
                    "Le jeu n'est pas en état MAZE_MOVE"
            );
        }

        this.maze.getValue()
                .move(move);

        computePaths();

        this.state.setValue(
                GameState.PLAYER_MOVE
        );
    }

    // déplacer le joueur vers une tuile (PLAYER_MOVE)
    public void movePlayer(Tile tile) {

        if (this.getState()
                != GameState.PLAYER_MOVE) {

            throw new IllegalStateException(
                    "Le jeu n'est pas en état PLAYER_MOVE!"
            );
        }

        if (tile == null) {

            throw new IllegalArgumentException(
                    "La tuile est null"
            );
        }

        if (this.paths.getValue() == null
                || !this.paths.getValue()
                .isAccessible(tile)) {

            throw new IllegalArgumentException(
                    "La tuile n'est pas accessible!"
            );
        }

        Player player =
                currentPlayer.getValue();

        // retire le joueur de sa tuile actuelle
        Tile currentTile =
                this.maze.getValue()
                        .getTile(player);

        currentTile.removePlayer(player);

        // place le joueur sur la nouvelle tuile
        tile.addPlayer(player);

        // gestion des trésors
        Deck deck =
                player.getDeck();

        List<Integer> drawPile =
                deck.getDrawPile();

        if (!drawPile.isEmpty()) {

            Content content =
                    tile.getContent();

            switch (content) {

                case Treasure t -> {

                    if (t.id()
                            == drawPile.getFirst()) {

                        deck.discard();
                    }
                }

                default -> {
                }
            }
        }

        // vérifie victoire
        if (hasWon(player)) {

            this.state.setValue(
                    GameState.GAME_OVER
            );
        }

        else {

            this.state.setValue(
                    GameState.TURN_END
            );
        }
    }

    // vérifier victoire
    private boolean hasWon(Player player) {

        Deck deck =
                player.getDeck();

        if (!deck.getDrawPile().isEmpty()) {

            return false;
        }

        Tile currentTile =
                this.maze.getValue()
                        .getTile(player);

        Tile startingTile =
                this.maze.getValue()
                        .getStartingTile(player);

        return currentTile.equals(startingTile);
    }

    // fin de tour
    public void endTurn() {

        if (this.getState()
                != GameState.TURN_END) {

            throw new IllegalStateException(
                    "Le jeu n'est pas en état TURN_END"
            );
        }

        int nextIndex =
                (
                        players.indexOf(
                                currentPlayer.getValue()
                        ) + 1
                ) % players.size();

        Player nextPlayer =
                players.get(nextIndex);

        this.currentPlayer.setValue(
                nextPlayer
        );

        computePaths();

        this.state.setValue(
                GameState.MAZE_MOVE
        );

        // IMPORTANT :
        // le prochain bot joue automatiquement
        // MAIS seulement après le clic OK
        if (this.getCurrentPlayer() != null
                && this.getCurrentPlayer().isBot()) {

            Platform.runLater(
                    this::startTurn
            );
        }
    }

    public ObservableValue<Paths<Tile>>
    pathsProperty() {

        return this.paths.asReadOnly();
    }

    public Paths<Tile> getPaths() {
        return this.paths.getValue();
    }

    // calcule les chemins accessibles
    private void computePaths() {

        if (maze.getValue() == null
                || currentPlayer.getValue() == null) {

            this.paths.setValue(null);

            return;
        }

        Graph<Tile> graph =
                MazeToGraphAdapter.getGraph(
                        maze.getValue()
                );

        Tile currentTile =
                this.maze.getValue()
                        .getTile(
                                currentPlayer.getValue()
                        );

        if (currentTile == null) {

            this.paths.setValue(null);

            return;
        }

        this.paths.setValue(
                new Paths<>(
                        graph,
                        currentTile
                )
        );
    }

    // hydrate le jeu
    public void updateFrom(GameDTO dto) {

        // réinitialisation
        this.players.clear();

        this.paths.setValue(null);

        // recréer joueurs
        List<PlayerDTO> playerDTOs =
                dto.getPlayers();

        for (int i = 0; i < 4; i++) {

            Slot slot =
                    getSlot(i);

            PlayerDTO pDto =
                    playerDTOs.get(i);

            if (pDto != null) {

                if (slot.getPlayer() == null) {

                    slot.open(
                            pDto.isBot()
                    );
                }

                slot.getPlayer()
                        .updateFrom(pDto);

                players.add(
                        slot.getPlayer()
                );
            }

            else {

                if (slot.getPlayer() != null) {

                    slot.close();
                }
            }
        }

        // recréer labyrinthe
        if (dto.getMaze() != null) {

            this.maze.setValue(
                    Maze.from(
                            dto.getMaze(),
                            players
                    )
            );
        }

        else {

            this.maze.setValue(null);
        }

        // joueur courant
        Player newCurrentPlayer =
                null;

        int cpIndex =
                dto.getCurrentPlayerIndex();

        if (cpIndex >= 0) {

            for (Player p : players) {

                if (p.getIndex() == cpIndex) {

                    newCurrentPlayer = p;

                    break;
                }
            }
        }

        this.currentPlayer.setValue(
                newCurrentPlayer
        );

        if (newCurrentPlayer != null
                && this.maze.getValue() != null) {

            computePaths();
        }

        // état
        this.state.setValue(
                dto.getState()
        );
    }

    // démarrage tour bot
    public void startTurn() {

        Player player =
                this.getCurrentPlayer();

        if (player == null) {
            return;
        }

        if (!player.isBot()) {
            return;
        }

        if (this.getState()
                != GameState.MAZE_MOVE) {

            return;
        }

        try {

            Move move =
                    STRATEGY.getNextMove(
                            new GameDTO(this)
                    );

            if (move == null) {
                return;
            }

            // rotation
            this.rotateExtraTile(
                    move.nbRotations()
            );

            // déplacement labyrinthe
            this.moveMaze(
                    move.mazeMove()
            );

            // destination
            Position p =
                    move.destination();

            Tile destinationTile =
                    null;

            if (p != null) {

                Grid<Tile> tiles =
                        this.getMaze()
                                .getTiles();

                if (
                        p.x() >= 0
                                && p.x() < tiles.getWidth()
                                && p.y() >= 0
                                && p.y() < tiles.getHeight()
                ) {

                    destinationTile =
                            tiles.get(
                                    p.x(),
                                    p.y()
                            );
                }
            }

            // fallback :
            // rester sur place
            if (
                    destinationTile == null
                            || this.getPaths() == null
                            || !this.getPaths()
                            .isAccessible(destinationTile)
            ) {

                destinationTile =
                        this.getTile(player);
            }

            // déplacement joueur
            this.movePlayer(destinationTile);
        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }
}
