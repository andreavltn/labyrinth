package labyrinth.model.strategy;

import labyrinth.model.MazeMove;
import labyrinth.model.strategy.simulation.GameSimulation;
import labyrinth.model.strategy.simulation.PlayerSimulation;
import labyrinth.model.strategy.simulation.TileSimulation;
import labyrinth.service.serialization.GameDTO;
import labyrinth.util.graph.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DefaultPlayStrategy implements PlayStrategy {

    // nombre de meilleurs candidats sélectionnés par Monte-Carlo
    private static final int CANDIDATES_FOR_MONTE_CARLO = 12;

    // profondeur maximale des simulations
    private static final int SIMULATION_DEPTH = 4;

    // nombre de coups aléatoires considérés pour chaque étape
    private static final int RANDOM_CHOICES = 8;

    // nombre de simulations effectuées pour chaque candidat
    private static final int SIMULATIONS_PER_CANDIDATE = 5;

    // poids des simulations dans le score final (alpha)
    private static final int SIMULATION_WEIGHT = 10;

    private final Random random = new Random(); // générateur de nombres aléatoires
    private GameSimulation game; // le jeu pour la simulation
    private PlayerSimulation initialPlayer; // le joueur courant pour la simulation

    @Override // calcule le prochain coup à jouer à partir d'une snapshot du jeu
    public Move getNextMove(GameDTO snapshot) {

        // initialise le jeu et le joueur courant
        this.game = new GameSimulation(snapshot);
        this.initialPlayer = game.getCurrentPlayer();

        return computeNextMove(); // délègue à computeNextMove();
    }

    // calcule le meilleur coup
    private Move computeNextMove() {

        // si le jeu est terminé, pas de prochain coup
        if (game.hasWon(initialPlayer)) return null;

        // calcule tous les coups valides avec leur score
        List<ScoredMove> moves = computeMoves();

        // si il n'y a pas de coup valide, pas de prochain coup
        if (moves.isEmpty()) return null;

        // première passe : récupère le meilleur score et les coups associés
        int firstPassBestScore = getBestScore(moves);
        List<ScoredMove> bestFirstPassMoves = getMovesWithScore(moves, firstPassBestScore);

        // deuxième passe : sélectionne les meilleurs candidats et ajuste leur score avec Monte-Carlo
        List<ScoredMove> adjustedMoves =
                moves.stream()
                        .sorted((a, b) -> Integer.compare(b.score(), a.score()))
                        .limit(CANDIDATES_FOR_MONTE_CARLO)
                        .map(m -> adjust(m, SIMULATION_DEPTH))
                        .toList();

        // récupère le meilleur score et les coups associés (après ajustement)
        int bestScore = getBestScore(adjustedMoves);
        List<ScoredMove> bestMoves = getMovesWithScore(adjustedMoves, bestScore);

        // reprend la première passe si aucun coup après ajustement
        if (bestMoves.isEmpty()) bestMoves = bestFirstPassMoves;

        // renvoie un des meilleurs coups aléatoirement
        return bestMoves.get(random.nextInt(bestMoves.size())).move();
    }

    // calcule le score
    private int getScore() {

        // si le jeu fait gagner le joueur courant
        if (game.hasWon(initialPlayer)) return 10000; // très bon score

        // si le jeu fait perdre le joueur courant
        if (game.isGameOver()) return -10000; // très mauvais score

        // récupère le trésor actuel du joueur courant, et calcule sa distance
        int treasures = initialPlayer.getDiscardPile().size();
        int distance = game.getDistanceToTarget(initialPlayer);

        // sinon score = 100*indiceTresor - distanceAuTresor
        return 100 * treasures - distance;
    }

    // renvoie le meilleur score parmi une liste de coups avec score
    private int getBestScore(List<ScoredMove> moves) {
        return moves.stream()
                .mapToInt(ScoredMove::score)
                .max()
                .orElse(Integer.MIN_VALUE);
    }

    // renvoie tous les coups ayant exactement le score choisi, parmi la liste
    private List<ScoredMove> getMovesWithScore(List<ScoredMove> moves, int score) {
        return moves.stream()
                .filter(m -> m.score() == score)
                .toList();
    }

    // énumère tous les coups possibles, les simule, et renvoie leurs scores
    private List<ScoredMove> computeMoves() {

        // dimensions du jeu
        int height = game.getMaze().getTiles().getHeight();
        int width = game.getMaze().getTiles().getWidth();

        // liste des coups scorés
        List<ScoredMove> scoredMoves = new ArrayList<>();

        // pour chaque coup valide
        for (MazeMove mazeMove : game.getValidMazeMoves()) {

            // pour chaque rotation de la tuile extra
            for (int rotation = 0; rotation < 4; rotation++) {
                game.modifyMaze(rotation, mazeMove);

                // calcule les chemins possibles
                Paths<TileSimulation> paths = game.computePaths();

                // pour chaque tuile du plateau
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        TileSimulation tile = game.getMaze().getTiles().get(x, y);

                        // ignore les tuiles non accessibles
                        if (!paths.isAccessible(tile)) continue;

                        // génère le déplacement, le joue, et calcule le score ensuite
                        Move move = new Move(rotation, mazeMove, tile.getPosition());
                        game.movePlayer(tile.getPosition());
                        int score = getScore();

                        // enregistre le coup scoré
                        scoredMoves.add(new ScoredMove(move, score));

                        // annule le déplacement du joueur
                        game.undoLastPlayerMove();
                    }
                }
                // annule le déplacement du plateau
                game.undoLastMazeModification();
            }
        }
        return scoredMoves;
    }

    //
    private ScoredMove getRandomMove() {

        // récupère les coups valides, et en choisit un au hasard
        List<MazeMove> mazeMoves = game.getValidMazeMoves();
        MazeMove mazeMove = mazeMoves.get(random.nextInt(mazeMoves.size()));

        // génère une rotation aléatoire et l'applique
        int rotation = random.nextInt(4);
        game.modifyMaze(rotation, mazeMove);

        // récupère les chemins accessibles, leurs tuiles d'arrivée, et récupère la meilleure d'entre elles
        Paths<TileSimulation> paths = game.computePaths();
        List<TileSimulation> accessibleTiles = getAccessibleTiles(paths);
        TileSimulation destination = getBestDestination(accessibleTiles, game.getCurrentPlayer());

        // génère le mouvement associé
        Move move = new Move(rotation, mazeMove, destination.getPosition());

        // applique le déplacement de joueur, et calcule le score
        game.movePlayer(destination);
        int score = getScore();

        // annule les déplacements
        game.undoLastPlayerMove();
        game.undoLastMazeModification();

        // renvoie le coup scoré
        return new ScoredMove(move, score);
    }

    // renvoie la liste des tuiles accessibles
    private List<TileSimulation> getAccessibleTiles(Paths<TileSimulation> paths) {

        // dimensions du plateau
        int height = game.getMaze().getTiles().getHeight();
        int width = game.getMaze().getTiles().getWidth();

        // liste des tuiles accessibles
        List<TileSimulation> accessibleTiles = new ArrayList<>();

        // pour chaque tuile du plateau
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TileSimulation tile = game.getMaze().getTiles().get(x, y);

                // si elle est accessible, l'ajoute a la liste
                if (paths.isAccessible(tile)) {
                    accessibleTiles.add(tile);
                }
            }
        }
        return accessibleTiles; // renvoie la liste
    }

    // renvoie la tuile accessible la plus proche de l'objectif du joueur donné (de son trésor)
    // si égalité, renvoie une aléatoirement parmi les meilleures
    private TileSimulation getBestDestination(List<TileSimulation> tiles, PlayerSimulation player) {

        // récupère la distance de la tuile la plus proche de l'objectif
        int bestDistance = tiles.stream()
                .mapToInt(tile -> game.getDistanceToTarget(tile.getPosition(), player))
                .min()
                .orElse(Integer.MAX_VALUE);

        // récupère la/les meilleures tuiles
        List<TileSimulation> bestTiles = tiles.stream()
                .filter(tile -> game.getDistanceToTarget(tile.getPosition(), player) == bestDistance)
                .toList();

        // renvoie une aléatoire parmi les meilleures (si il y en a plusieurs)
        return bestTiles.get(random.nextInt(bestTiles.size()));
    }

    // renvoie le meilleur coup parmi nb coups aléatoires
    private Move getBestRandomMove(int nb) {
        List<ScoredMove> moves = new ArrayList<>();

        // génère nb coups
        for (int i = 0; i < nb; i++) {
            moves.add(getRandomMove());
        }

        // récupère le/les meilleurs coups
        int bestScore = getBestScore(moves);
        List<ScoredMove> bestMoves = getMovesWithScore(moves, bestScore);

        // renvoie un aléatoire parmi les meilleurs (si il y en a plusieurs)
        return bestMoves.get(random.nextInt(bestMoves.size())).move();
    }

    // simule au plus maxDepth coups aléatoires depuis l'état courant
    // renvoie le score
    private int simulate(int maxDepth) {

        // si on a atteint la profondeur max, ou qu'un joueur a gagné
        if (maxDepth <= 0 || game.isGameOver()) {
            return getScore(); // renvoie le score
        }

        // récupère le meilleur coup
        Move move = getBestRandomMove(RANDOM_CHOICES);

        // applique le coup
        game.apply(move);

        // resimule à profondeur n-1
        int score = simulate(maxDepth - 1);

        // annule le coup
        game.undoLastMove();

        // renvoie le score à la fin
        return score;
    }

    // ajuste le score d'un coup par une moyenne des simulations Monte-Carlo
    private ScoredMove adjust(ScoredMove scoredMove, int maxDepth) {

        // applique le coup
        game.apply(scoredMove.move());

        // somme les scores de simulation de Monte-Carlo
        int simulationScore = 0;
        for (int i = 0; i < SIMULATIONS_PER_CANDIDATE; i++) {
            simulationScore += simulate(maxDepth);
        }

        // annule le coup
        game.undoLastMove();

        // score moyen des simulations
        int averageSimulationScore = simulationScore/SIMULATIONS_PER_CANDIDATE;

        // pondération
        int weightedSimulationScore = averageSimulationScore/SIMULATION_WEIGHT;

        // combien les deux scores
        int newScore = scoredMove.score() + weightedSimulationScore;

        // renvoie le coup avec son nouveau score
        return new ScoredMove(scoredMove.move(), newScore);
    }
}
