package labyrinth;

import labyrinth.model.*;
import labyrinth.util.MazeFormatter;
import labyrinth.util.graph.Paths;

import java.util.Scanner;

// classe de test en lignes de commande
public class LabyrinthCLI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final MazeFormatter formatter = new MazeFormatter();

    public static void main(String[] args) {

        // nouvelle partie
        Game game = new Game();

        // demande le nombre de joueurs
        int nbPlayers = askNbPlayers();
        for (int i = 0; i < nbPlayers; i++) {
            game.getSlot(i).open(); // ouvre les slots
        }

        // initialisation du jeu
        game.init();
        System.out.println("Début de la partie.");

        // boucle de jeu, tant qu'on n'est pas en état GAME_OVER
        while (game.getState() != GameState.GAME_OVER) {

            // affiche l'état du jeu (les infos)
            displayState(game);

            // demande le nombre de rotations de la tuile supplémentaire
            int rotation = askRotation();
            game.rotateExtraTile(rotation);

            // demande où placer la tuile supplémentaire
            MazeMove move = askMazeMove(game);
            game.moveMaze(move);

            // affiche le labyrinthe
            System.out.println(formatter.format(game.getMaze()));

            // demande ou faire déplacer le pion du joueur
            Tile destination = askPlayerMove(game);
            game.movePlayer(destination);

            // affiche le labyrinthe
            System.out.println(formatter.format(game.getMaze()));

            // fin du tour, passe au joueur suivant
            if (game.getState() == GameState.TURN_END) {
                game.endTurn();
            }
        }

        // fin de partie
        System.out.println("Le joueur " + game.getCurrentPlayer().getIndex() + " a gagné!");
    }

    // affiche l'état actuel du jeu
    private static void displayState(Game game) {

        // affiche le joueur courant
        System.out.println("\nJoueur courant : " + game.getCurrentPlayer().getIndex());

        // affiche le labyrinthe
        System.out.println(formatter.format(game.getMaze()));

        // affiche la tuile supplémentaire
        System.out.println("Tuile supplémentaire :");
        System.out.println(formatter.format(game.getMaze().getExtraTile()));

        // affiche le trésor à trouver
        Deck deck = game.getCurrentPlayer().getDeck(); // récupère le deck du joueur courant
        System.out.println("Trésor à trouver : " + deck.getDrawPile().getFirst()); // récupère son trésor
    }

    // demande le nombre de joueurs
    private static int askNbPlayers() {

        int nbPlayers = -1;

        System.out.print("Choisir le nombre de joueurs (2-4) : ");

        while (nbPlayers < 2 || nbPlayers > 4) {

            try {
                nbPlayers = Integer.parseInt(scanner.nextLine().trim()); // récupère l'entier entré

                if (nbPlayers < 2 || nbPlayers > 4) { // si il n'est pas dans les bornes
                    System.out.print("\nNombre de joueurs invalide. Réessayez : "); // erreur
                }

            } catch (NumberFormatException e) { // si on n'entre pas un nombre
                System.out.println("Entrée invalide, entrez un nombre."); // erreur
            }
        }

        return nbPlayers;
    }

    // demande le nombre de rotations à appliquer sur la tuile supplémentaire
    private static int askRotation() {

        int nbRotations = -1;

        System.out.print("Rotation de la tuile supplémentaire (0-3) : ");

        while (nbRotations < 0 || nbRotations > 3) {

            try {
                nbRotations = Integer.parseInt(scanner.nextLine().trim()); // récupère l'entier entré

                if (nbRotations < 0 || nbRotations > 3) { // si il n'est pas dans les bornes
                    System.out.print("\nRotation invalide. Réessayez : "); // erreur
                }

            } catch (NumberFormatException e) { // si on n'entre pas un nombre
                System.out.println("Entrée invalide, entrez un nombre."); // erreur
            }
        }

        return nbRotations;
    }

    // demande un déplacement du labyrinthe
    private static MazeMove askMazeMove(Game game) {

        MazeMove move = null;
        MazeMove forbiddenMove = game.getMaze().getForbiddenMove(); // récupère le mouvement interdit

        System.out.print("Déplacement (<DIRECTION> <indice>) ex: DOWN 1 : ");

        while (move == null) {

            try {
                String[] parts = scanner.nextLine().trim().split(" "); // récupère l'entrée en un bloc

                // sépare la direction et l'index
                Direction direction = Direction.valueOf(parts[0].toUpperCase());
                int index = Integer.parseInt(parts[1]);

                move = new MazeMove(direction, index); // génère le mouvement associé

                if (move.equals(forbiddenMove)) { // si le mouvement est interdit
                    System.out.print("\nCe mouvement est interdit. Réessayez : "); // erreur
                    move = null; // reset le mouvement
                }

            } catch (Exception e) { // si le format est invalide
                System.out.print("\nFormat invalide. Exemple : DOWN 1 : "); // erreur
            }
        }

        return move;
    }

    // demande un déplacement du pion sur le plateau
    private static Tile askPlayerMove(Game game) {

        Tile tile = null;

        System.out.print("Déplacement du joueur (x y) : ");

        while (tile == null) {

            try {
                String[] parts = scanner.nextLine().trim().split(" "); // récupère l'entrée en un bloc

                // sépare le x et le y
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);

                tile = game.getMaze().getTiles().get(x, y); // récupère la tuile (x,y)

                if (!game.getPaths().isAccessible(tile)) { // si la tuile n'est pas accessible
                    System.out.print("\nLa tuile n'est pas accessible. Réessayez : "); // erreur
                    tile = null; // reset la tuile
                }

            } catch (Exception e) { // si le format est invalide
                System.out.print("\nFormat invalide. Exemple : 3 2 : "); // erreur
            }
        }

        return tile;
    }
}