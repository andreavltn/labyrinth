package labyrinth.model;

import java.util.*;

public class Tile {

    private final TileKind kind; // le type de tuile (coude, couloir, T)
    private final Content content; // le contenu de la tuile (labyrinth.model.None/labyrinth.model.Treasure/labyrinth.model.StartingPosition)
    private Direction orientation; // l'orientation de la tuile (Up/Down/Left/Right)
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

    // renvoie la liste des sorties de la tuile
    public Set<Direction> getExits(){
        Set<Direction> result = new HashSet<>(); // nouveau set

        for (Direction dir : kind.getExits()) { // pour chaque orientation de sortie de la tuile
            Direction newDir = dir.getRotated(orientation.ordinal()); // applique la rotation de la tuile
            result.add(newDir); // ajoute au set l'orientation après rotation
        }

        return result;
    }

    // renvoie la liste des joueurs présents sur la tuile
    public List<Player> getPlayers(){
        return this.players;
    }

    // ajoute un joueur sur la tuile
    public void addPlayer(Player player){
        this.players.add(player);
    }

    // tourne la tuile de nbTimes rotations
    public void rotate(int nbTimes) {
        this.orientation = this.orientation.getRotated(nbTimes);
    }

    // retire le joueur "player" de la tuile
    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}
