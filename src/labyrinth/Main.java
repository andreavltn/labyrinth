package labyrinth;

import labyrinth.model.Game;

public class Main {

    public static void main(String[] args) {

        Game game = new Game();

        System.out.println("Game created");

        for(var player : game.getPlayers()){
            System.out.println("Player " + player.getIndex() + " ready");
        }

    }
}