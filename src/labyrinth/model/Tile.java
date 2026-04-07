package labyrinth.model;

import labyrinth.service.serialization.TileDTO;
import labyrinth.util.observable.ObservableValue;
import labyrinth.util.observable.ObservableList;
import labyrinth.util.observable.SimpleObservableList;
import labyrinth.util.observable.SimpleObservableValue;
import java.util.*;

public class Tile {

    private final TileKind kind; // le type de tuile (coude, couloir, T)
    private final Content content; // le contenu de la tuile (labyrinth.model.None/labyrinth.model.Treasure/labyrinth.model.StartingPosition)
    private final SimpleObservableValue<Direction> orientation; // l'orientation de la tuile (Up/Down/Left/Right)
    private final SimpleObservableList<Player> players;

    public Tile(TileKind kind){ // à partir d'un type, contenu labyrinth.model.None(), orientation Up par défaut
        this.kind = kind;
        this.content = new None();
        this.orientation = new SimpleObservableValue<>(Direction.UP);
        this.players = new SimpleObservableList<>();
    }

    public Tile(TileKind kind, Content content){ // idem avec le contenu choisi
        this.kind = kind;
        this.content = content;
        this.orientation = new SimpleObservableValue<>(Direction.UP);
        this.players = new SimpleObservableList<>();
    }

    public Tile(TileKind kind, Content content, Direction orientation){ // idem avec tous les paramètres choisis
        this.kind = kind;
        this.content = content;
        this.orientation = new SimpleObservableValue<>(orientation);
        this.players = new SimpleObservableList<>();
    }

    public TileKind getKind(){
        return this.kind;
    }

    public Content getContent(){
        return this.content;
    }

    public ObservableValue<Direction> orientationProperty(){
        return this.orientation.asReadOnly();
    }

    public Direction getOrientation(){
        return this.orientation.getValue();
    }

    // renvoie la liste des sorties de la tuile
    public Set<Direction> getExits(){
        Set<Direction> result = new HashSet<>(); // nouveau set

        for (Direction dir : kind.getExits()) { // pour chaque orientation de sortie de la tuile
            int ordinal = orientation.getValue().ordinal(); // récupère l'ordinal de l'orientation
            Direction newDir = dir.getRotated(ordinal); // applique la rotation de la tuile
            result.add(newDir); // ajoute au set l'orientation après rotation
        }

        return result;
    }

    public ObservableList<Player> playersProperty(){
        return this.players;
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
    public void rotate(int nbTimes){
        Direction newDir = this.orientation.getValue().getRotated(nbTimes);
        this.orientation.setValue(newDir);
    }

    // retire le joueur "player" de la tuile
    public void removePlayer(Player player){
        this.players.remove(player);
    }

    // recrée une instance de Tile depuis les données du DTO
    public static Tile from(TileDTO dto, List<Player> players) {

        // recrée la tuile avec les données du DTO
        Tile tile = new Tile(dto.getKind(), dto.getContent(), dto.getOrientation());

        // pour chaque indice de joueur du DTO
        for (int index : dto.getPlayers()) {

            // parcours des joueurs de la tuile
            for (Player p : players) {
                if (p.getIndex() == index) { // si il y a correspondance d'indice
                    tile.addPlayer(p); // ajoute le joueur à la tuile
                    break;
                }
            }
        }

        return tile;
    }
}
