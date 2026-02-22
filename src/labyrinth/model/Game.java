package labyrinth.model;

import java.util.List;

public class Game {

    private Maze maze;
    private List<Slot> slots;

    public Game(){

    }

    public Slot getSlot(int index){
        return this.slots.get(index);
    }

    public Maze getMaze(){
        return this.maze;
    }

    public Tile getTile(Player player){
        return this.maze.getTile(player);
    }
}
