package labyrinth.model;

import labyrinth.util.collection.Grid;
import labyrinth.util.graph.Paths;
import labyrinth.util.graph.Graph;
import labyrinth.util.graph.MazeToGraphAdapter;
import labyrinth.util.observable.ObservableValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import labyrinth.util.observable.SimpleObservableValue;

public class Game {

    private Maze maze; // le labyrinthe
    private final List<Slot> slots; // les slots de jeu
    private final List<Player> players; // les joueurs
    private final SimpleObservableValue<GameState> state;  // l'état courant du jeu
    private final SimpleObservableValue<Player> currentPlayer; // le joueur courant
    private final Random random; // générateur aléatoire
    private final SimpleObservableValue<Paths<Tile>> paths; // les chemins accessibles par le joueur courant

    public Game() {
        this.slots = new ArrayList<>(); // liste de 4 slots
        for (int i = 0; i < 4; i++) {
            this.slots.add(new Slot(this, i)); // ajout des slots
        }
        this.players = new ArrayList<>(); // liste des joueurs
        this.state = new SimpleObservableValue<>(GameState.SETUP);// on est en SETUP au depart
        this.currentPlayer = new SimpleObservableValue<>(null); // pas de joueur courant
        this.maze = null; // pas de labyrinthe
        this.random = new Random(); // pour l'aléatoire
        this.paths = new SimpleObservableValue<>(null); // pas de chemins pour l'instant
    }

    public Slot getSlot(int index){
        return this.slots.get(index);
    }

    public Maze getMaze(){
        return this.maze;
    }

    public Tile getTile(Player player){
        if (this.maze == null) {
            return null;
        } else {
            return this.maze.getTile(player);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public ObservableValue<GameState> stateProperty(){
        return this.state.asReadOnly();
    }

    public GameState getState() {
        return this.state.getValue();
    }

    public ObservableValue<Player> currentPlayerProperty(){
        return this.currentPlayer.asReadOnly();
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer.getValue();
    }

    // initialise le jeu (SETUP)
    public void init() {

        // si on n'est pas au SETUP, erreur
        if (this.getState() != GameState.SETUP) {
            throw new IllegalStateException("Le jeu n'est pas en état SETUP");
        }

        // récupère les joueurs depuis les slots ouverts
        this.players.clear(); // réinitialise les joueurs
        for (Slot slot : slots) { // pour chaque slot
            if (slot.getPlayer() != null) { // si il contient un joueur
                this.players.add(slot.getPlayer()); // ajoute le joueur à la partie
            }
        }

        // construction du labyrinthe
        this.maze = MazeBuilder.buildStandardMaze();

        // récupère tous les id des trésors présents dans le labyrinthe
        List<Integer> treasures = new ArrayList<>(); // réinitialise les trésors
        Grid<Tile> grid = maze.getTiles(); // récupère la grille du jeu
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Content content = grid.get(x, y).getContent(); // récupère le contenu de la tuile
                switch (content) {
                    case Treasure t -> treasures.add(t.id()); // si c'est un trésor, l'ajoute
                    default -> {} // sinon ne fait rien
                }
            }
        }

        // initialise la main de chaque joueur
        for (Player player : players) {
            Collections.shuffle(treasures);
            player.getDeck().init(new ArrayList<>(treasures));
        }

        // place les joueurs sur leur SP
        for (Player player : players) {
            Tile startTile = maze.getStartingTile(player);
            startTile.addPlayer(player);
        }

        // choisit un premier joueur au hasard
        Player randomPlayer = players.get(random.nextInt(players.size()));
        this.currentPlayer.setValue(randomPlayer);

        // calcule les chemins du joueur courant
        computePaths();

        this.state.setValue(GameState.MAZE_MOVE); // passe à l'état suivant
    }

    // remettre le jeu à l'état initial
    public void reset() {
        this.state.setValue(GameState.SETUP); // mode SETUP
        this.maze = null; // pas de labyrinthe
        this.currentPlayer.setValue(null); // pas de joueur courant
        this.paths.setValue(null); // pas de chemins

        for (Player player : players) { // réinitialise le deck de chaque joueur (vide)
            player.getDeck().init(new ArrayList<>());
        }
    }

    // fait tourner la tuile supplémentaire
    public void rotateExtraTile(int nbTimes) {
        if (this.getState() != GameState.MAZE_MOVE) { // si on n'est pas au bon état
            throw new IllegalStateException("Le jeu n'est pas en état MAZE_MOVE");
        }
        Tile extraTile = this.maze.getExtraTile(); // récupère la tuile supplémentaire
        extraTile.rotate(nbTimes); // la fait tourner
    }

    // éxécute le mouvement (MAZE_MOVE)
    public void moveMaze(MazeMove move) {
        if (this.getState() != GameState.MAZE_MOVE) { // si on n'est pas au bon état
            throw new IllegalStateException("Le jeu n'est pas en état MAZE_MOVE");
        }
        maze.move(move); // exécute le déplacement
        computePaths(); // recalcule les chemins accessibles
        this.state.setValue(GameState.PLAYER_MOVE); // passe l'état au déplacement du joueur
    }

    // déplacer le joueur vers une tuile (PLAYER_MOVE)
    public void movePlayer(Tile tile) {

        if (this.getState() != GameState.PLAYER_MOVE) { // si on n'est pas au bon état du jeu
            throw new IllegalStateException("Le jeu n'est pas en état PLAYER_MOVE!"); // erreur
        }

        if (!paths.getValue().isAccessible(tile)) { // si la tuile n'est pas accessible par un chemi,
            throw new IllegalArgumentException("La tuile n'est pas accessible!"); // erreur
        }

        // retire le joueur de sa tuile actuelle
        Tile currentTile = maze.getTile(currentPlayer.getValue());
        currentTile.removePlayer(currentPlayer.getValue());

        // place le joueur sur la nouvelle tuile
        tile.addPlayer(currentPlayer.getValue());

        // défausse la première carte de sa pioche si il se retrouve sur le trésor qu'il cherche
        Deck deck = currentPlayer.getValue().getDeck(); // récupère le deck du joueur
        List<Integer> drawPile = deck.getDrawPile(); // récupère la pioche associée
        if (!drawPile.isEmpty()) { // si il lui reste des trésors à trouver
            Content content = tile.getContent(); // récupère le contenu de la tuile courante
            switch (content) {
                case Treasure t -> { // si c'est un trésor
                    if (t.id() == drawPile.getFirst()) { // si c'est le trésor recherché (première carte de la pioche)
                        deck.discard(); // alors on défausse la carte
                    }
                }
                default -> {} // sinon on ne fait rien
            }
        }

        // vérifie si le joueur a gagné
        if (hasWon(currentPlayer.getValue())) {
            this.state.setValue(GameState.GAME_OVER); // si il a gagné, fin du jeu
        } else {
            this.state.setValue(GameState.TURN_END); // sinon le tour se termine
        }
    }

    // méthode privée : vérifier si un joueur a gagné la partie dans la situation actuelle
    // pour gagner : un joueur doit avoir trouvé tous ses trésors + être revenu sur sa tuile de départ
    private boolean hasWon(Player player) {
        Deck deck = player.getDeck(); // récupère le deck du joueur
        Tile startingTile = maze.getStartingTile(player); // récupère sa tuile de départ

        if (deck.getDrawPile().isEmpty()) { // si sa pioche est vide
            Tile currentTile = maze.getTile(player); // récupère sa tuile actuelle

            // si sa tuile courante est aussi sa tuile de départ, alors il gagné, sinon non
            return currentTile.equals(startingTile); // alors il a gagné
        }
        return false; // sinon il n'a pas encore gagné
    }

    // la fin d'un tour (END_TURN)
    public void endTurn() {
        if (this.getState() != GameState.TURN_END) { // si on n'est pas au bon état
            throw new IllegalStateException("Le jeu n'est pas en état TURN_END");
        }

        // passe au joueur suivant
        int nextIndex = (players.indexOf(currentPlayer.getValue()) + 1) % players.size(); // calcule index+1 modulo 4
        this.currentPlayer.setValue(players.get(nextIndex)); // met à jour le joueur courant
        computePaths(); // recalcule les chemins accessibles

        this.state.setValue(GameState.MAZE_MOVE); // le joueur courant devra faire un mouvement ensuite (état MAZE_MOVE)
    }

    public ObservableValue<Paths<Tile>> pathsProperty(){
        return this.paths.asReadOnly();
    }

    public Paths<Tile> getPaths() {
        return this.paths.getValue();
    }

    // calcule l'ensemble des chemins accessibles du joueur courant
    private void computePaths() {
        Graph<Tile> graph = MazeToGraphAdapter.getGraph(maze); // convertit le labyrinthe en graphe
        Tile currentTile = maze.getTile(currentPlayer.getValue()); // récupère la tuile du joueur courant
        this.paths.setValue(new Paths<>(graph, currentTile)); // calcule l'ensemble des chemins depuis cette tuile
    }
}
