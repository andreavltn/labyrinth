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

public class DefaultPlayStrategy
        implements PlayStrategy {

    private static final int CANDIDATES_FOR_MONTE_CARLO = 12;
    private static final int SIMULATION_DEPTH = 4;
    private static final int RANDOM_CHOICES = 8;
    private static final int SIMULATIONS_PER_CANDIDATE = 5;
    private static final int SIMULATION_WEIGHT = 10;

    private final Random random =
            new Random();

    private GameSimulation game;

    private PlayerSimulation initialPlayer;

    @Override
    public Move getNextMove(
            GameDTO snapshot
    ) {

        this.game =
                new GameSimulation(snapshot);

        this.initialPlayer =
                game.getCurrentPlayer();

        return computeNextMove();
    }

    private Move computeNextMove() {

        if (game.hasWon(initialPlayer)) {
            return null;
        }

        List<ScoredMove> moves =
                computeMoves();

        if (moves.isEmpty()) {
            return null;
        }

        int firstPassBestScore =
                getBestScore(moves);

        List<ScoredMove> bestFirstPassMoves =
                getMovesWithScore(
                        moves,
                        firstPassBestScore
                );

        List<ScoredMove> adjustedMoves =
                moves.stream()
                        .sorted((a, b) -> Integer.compare(
                                b.score(),
                                a.score()
                        ))
                        .limit(CANDIDATES_FOR_MONTE_CARLO)
                        .map(m -> adjust(m, SIMULATION_DEPTH))
                        .toList();

        int bestScore =
                getBestScore(adjustedMoves);

        List<ScoredMove> bestMoves =
                getMovesWithScore(
                        adjustedMoves,
                        bestScore
                );

        if (bestMoves.isEmpty()) {
            bestMoves = bestFirstPassMoves;
        }

        return bestMoves.get(
                random.nextInt(
                        bestMoves.size()
                )
        ).move();
    }

    private int getScore() {

        if (game.hasWon(initialPlayer)) {
            return 10000;
        }

        if (game.isGameOver()) {
            return -10000;
        }

        int treasures =
                initialPlayer
                        .getDiscardPile()
                        .size();

        int distance =
                game.getDistanceToTarget(
                        initialPlayer
                );

        return 100 * treasures
                - distance;
    }

    private int getBestScore(
            List<ScoredMove> moves
    ) {

        return moves.stream()
                .mapToInt(ScoredMove::score)
                .max()
                .orElse(Integer.MIN_VALUE);
    }

    private List<ScoredMove> getMovesWithScore(
            List<ScoredMove> moves,
            int score
    ) {

        return moves.stream()
                .filter(m -> m.score() == score)
                .toList();
    }

    private List<ScoredMove> computeMoves() {

        List<ScoredMove> scoredMoves =
                new ArrayList<>();

        for (MazeMove mazeMove :
                game.getValidMazeMoves()) {

            for (int rotation = 0;
                 rotation < 4;
                 rotation++) {

                game.modifyMaze(
                        rotation,
                        mazeMove
                );

                Paths<TileSimulation> paths =
                        game.computePaths();

                for (int y = 0;
                     y < game.getMaze()
                             .getTiles()
                             .getHeight();
                     y++) {

                    for (int x = 0;
                         x < game.getMaze()
                                 .getTiles()
                                 .getWidth();
                         x++) {

                        TileSimulation tile =
                                game.getMaze()
                                        .getTiles()
                                        .get(x, y);

                        if (!paths.isAccessible(tile)) {
                            continue;
                        }

                        Move move =
                                new Move(
                                        rotation,
                                        mazeMove,
                                        tile.getPosition()
                                );

                        game.movePlayer(
                                tile.getPosition()
                        );

                        int score =
                                getScore();

                        scoredMoves.add(
                                new ScoredMove(
                                        move,
                                        score
                                )
                        );

                        game.undoLastPlayerMove();
                    }
                }

                game.undoLastMazeModification();
            }
        }

        return scoredMoves;
    }

    private ScoredMove getRandomMove() {

        List<MazeMove> mazeMoves =
                game.getValidMazeMoves();

        MazeMove mazeMove =
                mazeMoves.get(
                        random.nextInt(
                                mazeMoves.size()
                        )
                );

        int rotation =
                random.nextInt(4);

        game.modifyMaze(
                rotation,
                mazeMove
        );

        Paths<TileSimulation> paths =
                game.computePaths();

        List<TileSimulation> accessibleTiles =
                getAccessibleTiles(paths);

        TileSimulation destination =
                getBestDestination(
                        accessibleTiles,
                        game.getCurrentPlayer()
                );

        Move move =
                new Move(
                        rotation,
                        mazeMove,
                        destination.getPosition()
                );

        game.movePlayer(destination);

        int score =
                getScore();

        game.undoLastPlayerMove();
        game.undoLastMazeModification();

        return new ScoredMove(
                move,
                score
        );
    }

    private List<TileSimulation> getAccessibleTiles(
            Paths<TileSimulation> paths
    ) {

        List<TileSimulation> accessibleTiles =
                new ArrayList<>();

        for (int y = 0;
             y < game.getMaze()
                     .getTiles()
                     .getHeight();
             y++) {

            for (int x = 0;
                 x < game.getMaze()
                         .getTiles()
                         .getWidth();
                 x++) {

                TileSimulation tile =
                        game.getMaze()
                                .getTiles()
                                .get(x, y);

                if (paths.isAccessible(tile)) {
                    accessibleTiles.add(tile);
                }
            }
        }

        return accessibleTiles;
    }

    private TileSimulation getBestDestination(
            List<TileSimulation> tiles,
            PlayerSimulation player
    ) {

        int bestDistance =
                tiles.stream()
                        .mapToInt(tile -> game.getDistanceToTarget(
                                tile.getPosition(),
                                player
                        ))
                        .min()
                        .orElse(Integer.MAX_VALUE);

        List<TileSimulation> bestTiles =
                tiles.stream()
                        .filter(tile -> game.getDistanceToTarget(
                                tile.getPosition(),
                                player
                        ) == bestDistance)
                        .toList();

        return bestTiles.get(
                random.nextInt(
                        bestTiles.size()
                )
        );
    }

    private Move getBestRandomMove(
            int nb
    ) {

        List<ScoredMove> moves =
                new ArrayList<>();

        for (int i = 0;
             i < nb;
             i++) {

            moves.add(getRandomMove());
        }

        int bestScore =
                getBestScore(moves);

        List<ScoredMove> bestMoves =
                getMovesWithScore(
                        moves,
                        bestScore
                );

        return bestMoves.get(
                random.nextInt(
                        bestMoves.size()
                )
        ).move();
    }

    private int simulate(
            int maxDepth
    ) {

        if (maxDepth <= 0
                || game.isGameOver()) {

            return getScore();
        }

        Move move =
                getBestRandomMove(RANDOM_CHOICES);

        game.apply(move);

        int score =
                simulate(maxDepth - 1);

        game.undoLastMove();

        return score;
    }

    private ScoredMove adjust(
            ScoredMove scoredMove,
            int maxDepth
    ) {

        game.apply(
                scoredMove.move()
        );

        int simulationScore = 0;

        for (int i = 0;
             i < SIMULATIONS_PER_CANDIDATE;
             i++) {

            simulationScore +=
                    simulate(maxDepth);
        }

        game.undoLastMove();

        return new ScoredMove(
                scoredMove.move(),
                scoredMove.score()
                        + simulationScore
                        / (SIMULATION_WEIGHT
                        * SIMULATIONS_PER_CANDIDATE)
        );
    }
}
