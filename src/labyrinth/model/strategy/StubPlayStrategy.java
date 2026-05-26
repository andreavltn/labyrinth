package labyrinth.model.strategy;

import labyrinth.model.Direction;
import labyrinth.service.serialization.GameDTO;
import labyrinth.service.serialization.PlayerDTO;

public class StubPlayStrategy implements PlayStrategy {

    @Override
    public Move getNextMove(GameDTO snapshot) {

        PlayerDTO currentPlayer = snapshot.getCurrentPlayer();

        Direction forbidden = snapshot.getMaze().getForbiddenDirection();

        Direction chosenDirection = Direction.UP;

        for (Direction direction : Direction.values()) {
            if (direction != forbidden) {
                chosenDirection = direction;
                break;
            }
        }

        return new Move(
                0,
                chosenDirection,
                currentPlayer.getIndex(),
                currentPlayer.getIndex()
        );
    }
}