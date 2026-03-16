package labyrinth;

import labyrinth.model.*;
import labyrinth.util.MazeFormatter;

public class Main {

    public static void main(String[] args) {

        // création du jeu
        Game game = new Game();

        // ouverture des slots
        game.getSlot(0).open();
        game.getSlot(1).open();
        game.getSlot(2).open();
        game.getSlot(3).open();

        // initialisation du jeu (création d'un labyrinthe standard 7x7) - SETUP
        game.init();

        System.out.println("Jeu créé!");
        System.out.println("Id du joueur courant : " + game.getCurrentPlayer().getIndex());
        System.out.println("Etat actuel du jeu: " + game.getState());

        // affichage du labyrinthe
        MazeFormatter formatter = new MazeFormatter();
        System.out.println(formatter.format(game.getMaze()));

        // le joueur 0 joue un coup (pousse la colonne 1 vers le bas) - MAZE_MOVE
        game.moveMaze(new MazeMove(Direction.DOWN, 1));
        System.out.println("Le joueur a joué un coup!");
        System.out.println("Etat actuel du jeu: " + game.getState());
        System.out.println(formatter.format(game.getMaze()));

        // déplacement du joueur 0 sur la tuile (1,0) - PLAYER_MOVE
        Tile destination = game.getMaze().getTiles().get(1, 0);
        game.movePlayer(destination);
        System.out.println("Le joueur s'est déplacé!");
        System.out.println("Etat actuel du jeu : " + game.getState()); // TURN_END ou GAME_OVER
        System.out.println(formatter.format(game.getMaze()));

        // fin du tour - TURN-END
        game.endTurn();
        System.out.println("Le tour est terminé!");
        System.out.println("Id du joueur courant : " + game.getCurrentPlayer().getIndex());
        System.out.println("Etat actuel du jeu : " + game.getState());
        System.out.println(formatter.format(game.getMaze()));
    }
}