package labyrinth.service.serialization;

import labyrinth.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameDTO {

    private final GameState state;
    private final int currentPlayerIndex; // -1 si pas de joueurCourant
    private final List<PlayerDTO> players;
    private final MazeDTO maze;
    private final UUID uuid;

    // constructeur à partir d'un jeu
    public GameDTO(Game game) {

        this.state = game.getState(); // récupère l'état du jeu
        this.uuid = game.getUuid(); // récupère l'uuid du jeu

        Player cp = game.getCurrentPlayer(); // récupère le joueur courant
        if (cp != null){ // si il n'est pas nul
            this.currentPlayerIndex = cp.getIndex(); // récupère son indice
        } else { // sinon -1
            currentPlayerIndex = -1;
        }


        this.players = new ArrayList<>(4); // nouvelle liste des joueurs
        for (int i = 0; i < 4; i++) { // pour chaque slot
            Player p = game.getSlot(i).getPlayer(); // récupère le joueur du slot
            if (p != null){ // si le joueur existe
                players.add(new PlayerDTO(p)); // ajoute un playerDTO du joueur
            } else { // sinon null
                players.add(null);
            }
        }

        // si le labyrinthe existe
        if(game.getMaze() != null){
            this.maze = new MazeDTO(game.getMaze()); // récupère le labyrinthe
        } else { // sinon null
            this.maze = null;
        }
    }

    public GameState getState() {
        return this.state;
    }

    public List<PlayerDTO> getPlayers() {
        return this.players;
    }

    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    public MazeDTO getMaze() {
        return this.maze;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}