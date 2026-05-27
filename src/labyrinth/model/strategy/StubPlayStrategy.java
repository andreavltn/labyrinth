package labyrinth.model.strategy;

import labyrinth.model.Direction;
import labyrinth.model.MazeMove;
import labyrinth.service.serialization.GameDTO;
import labyrinth.service.serialization.TileDTO;
import labyrinth.util.collection.Grid;
import labyrinth.util.collection.Position;

public class StubPlayStrategy
        implements PlayStrategy {

    @Override
    public Move getNextMove(
            GameDTO snapshot
    ) {

        MazeMove forbiddenMove =
                snapshot.getMaze()
                        .getForbiddenMove();

        MazeMove chosenMove = null;

        for (int i = 1;
             i < 6;
             i += 2) {

            MazeMove candidate =
                    new MazeMove(
                            Direction.UP,
                            i
                    );

            if (!candidate.equals(
                    forbiddenMove
            )) {

                chosenMove =
                        candidate;

                break;
            }
        }

        Position currentPosition =
                findCurrentPlayerPosition(
                        snapshot
                );

        return new Move(
                0,
                chosenMove,
                currentPosition
        );
    }

    private Position
    findCurrentPlayerPosition(
            GameDTO snapshot
    ) {

        int currentPlayer =
                snapshot
                        .getCurrentPlayerIndex();

        Grid<TileDTO> tiles =
                snapshot.getMaze()
                        .getTiles();

        for (int y = 0;
             y < tiles.getHeight();
             y++) {

            for (int x = 0;
                 x < tiles.getWidth();
                 x++) {

                TileDTO tile =
                        tiles.get(x, y);

                if (tile.getPlayers()
                        .contains(
                                currentPlayer
                        )) {

                    return new Position(
                            x,
                            y
                    );
                }
            }
        }

        return new Position(0, 0);
    }
}