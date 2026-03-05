package labyrinth.model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Maze maze;
    private List<Slot> slots;
    private final List<Player> players;

    public Game(){

        //Création des slots
        this.slots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.slots.add(new Slot(this, i)); // 4 slots ouverts
        }
        this.maze = MazeBuilder.buildStandardMaze();

        //Création des joueurs
        this.players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.players.add(new Player(i));
        }

        //Création du labyrinthe
        this.maze = MazeBuilder.buildStandardMaze();

        //Placement des joueurs sur leur position de départ
        for (Player player : players) {
            Tile startTile = maze.getTile(player);
            startTile.addPlayer(player);
        }
    }

    //Getters

    public Slot getSlot(int index){
        return this.slots.get(index);
    }

    public Maze getMaze(){
        return this.maze;
    }

    public Tile getTile(Player player){
        return this.maze.getTile(player);
    }

    public List<Player> getPlayers() {
        return players;
    }
}
