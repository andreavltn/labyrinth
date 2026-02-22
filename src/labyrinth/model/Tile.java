package labyrinth.model;

import java.util.*;

public class Tile {

    private final TileKind kind; // le type de tuile (coude, couloir, T)
    private final Content content; // le contenu de la tuile (labyrinth.model.None/labyrinth.model.Treasure/labyrinth.model.StartingPosition)
    private final Direction orientation; // l'orientation de la tuile (Up/Down/Left/Right)
    private List<Player> players;

    public Tile(TileKind kind) { // à partir d'un type, contenu labyrinth.model.None(), orientation Up par défaut
        this.kind = kind;
        this.content = new None();
        this.orientation = Direction.UP;
        this.players = new ArrayList<>();
    }

    public Tile(TileKind kind, Content content) { // idem avec le contenu choisi
        this.kind = kind;
        this.content = content;
        this.orientation = Direction.UP;
        this.players = new ArrayList<>();
    }

    public Tile(TileKind kind, Content content, Direction orientation) { // idem avec tous les paramètres choisis
        this.kind = kind;
        this.content = content;
        this.orientation = orientation;
        this.players = new ArrayList<>();
    }

    public TileKind getKind(){
        return this.kind;
    }

    public Content getContent(){
        return this.content;
    }

    public Direction getOrientation(){
        return this.orientation;
    }

    public Set<Direction> getExits(){
        Set<Direction> result = new HashSet<>(); // nouveau set

        for (Direction dir : kind.getExits()) { // pour chaque orientation de sortie de la tuile
            Direction newDir = dir.getRotated(orientation.ordinal()); // applique la rotation de la tuile
            result.add(newDir); // ajoute au set l'orientation après rotation
        }

        return result;
    }

    public List<Player> getPlayers(){
        return this.players;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }
}
