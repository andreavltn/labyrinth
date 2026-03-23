package labyrinth.util.graph;

import labyrinth.model.Maze;
import labyrinth.model.Tile;
import labyrinth.model.Direction;
import labyrinth.util.collection.Grid;

public class MazeToGraphAdapter {

    // convertit un labyrinthe en graphe de tuiles
    public static Graph<Tile> getGraph(Maze maze) {

        Graph<Tile> graph = new Graph<>(); // nouveau graphe
        Grid<Tile> grid = maze.getTiles(); // récupère la grille des tuiles

        // ajoute toutes les tuiles en tant que noeuds du graphe
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                graph.addNode(grid.get(x, y));
            }
        }

        // puis pour chaque tuile
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Tile tile = grid.get(x, y);

                // pour chaque direction de sortie de la tuile
                for (Direction direction : tile.getExits()) {

                    // calcule les coordonnées du voisin dans la direction
                    int voisinX = x + getDx(direction);
                    int voisinY = y + getDy(direction);

                    // si la tuile voisine est bien sur le plateau (existe)
                    if (voisinX >= 0 && voisinX < grid.getWidth() && voisinY >= 0 && voisinY < grid.getHeight()) {
                        Tile neighbor = grid.get(voisinX, voisinY); // la récupère

                        // si la tuile voisine a bien une sortie dans l'autre direction
                        // c'est à dire qu'on peut aller de A a B
                        if (neighbor.getExits().contains(direction.getOpposite())) {
                            graph.addEdge(tile, neighbor); // ajoute une arête A -> B
                        }
                    }
                }
            }
        }

        return graph; // renvoie le graphe complet
    }

    // fonction utilitaire qui renvoie le déplacement en X en fonction d'une direction (de sortie)
    private static int getDx(Direction direction) {
        int value;
        switch (direction) {
            case LEFT -> value = -1; // à gauche on se déplace de -1 en x
            case RIGHT -> value = 1; // à droite +1
            default -> value = 0; // sinon rien
        };
        return value;
    }

    // fonction utilitaire qui renvoie le déplacement en Y en fonction d'une direction (de sortie)
    private static int getDy(Direction direction) {
        int value;
        switch (direction) {
            case UP -> value = -1; // en haut on se déplace de -1 en y
            case DOWN -> value = 1; // en bas +1
            default -> value = 0; // sinon rien
        };
        return value;
    }
}