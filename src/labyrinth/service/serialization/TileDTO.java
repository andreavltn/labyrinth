package labyrinth.service.serialization;

import labyrinth.model.*;
import java.util.ArrayList;
import java.util.List;

public class TileDTO {

    private final TileKind kind;
    private final Content content;
    private final Direction orientation;
    private final List<Integer> players;

    // constructeur à partir d'une tuile
    public TileDTO(Tile tile) {
        this.kind = tile.getKind();
        this.orientation = tile.getOrientation();

        Content c = tile.getContent(); // récupère le contenu de la tuile

        // en fonction de son type
        switch(c){
            case None n -> this.content = new None(); // vide -> None()
            default -> this.content = c; // non-vide -> contenu
        }

        // ajoute les joueurs
        this.players = new ArrayList<>();
        for (Player p : tile.getPlayers()) {
            this.players.add(p.getIndex());
        }
    }

    public TileKind getKind() {
        return kind;
    }

    public Content getContent() {
        return content;
    }

    public Direction getOrientation() {
        return orientation;
    }

    public List<Integer> getPlayers() {
        return players;
    }
}