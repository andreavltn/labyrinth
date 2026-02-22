package labyrinth.model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Maze maze;
    private List<Slot> slots;

    public Game(){
        this.slots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.slots.add(new Slot(this, i)); // 4 slots ouverts
        }
        this.maze = MazeBuilder.buildStandardMaze();
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
