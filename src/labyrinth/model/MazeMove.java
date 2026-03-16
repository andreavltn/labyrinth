package labyrinth.model;

// un mouvement du jeu
public record MazeMove(Direction direction, int index) {

    public MazeMove getOpposite() {
        return new MazeMove(direction.getOpposite(), index); // renvoie le mouvement opposé
    }
}