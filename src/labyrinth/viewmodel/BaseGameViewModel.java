package labyrinth.viewmodel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.util.adapter.FxAdapter;
import java.util.ArrayList;
import java.util.List;

public class BaseGameViewModel implements GameViewModel {

    private final Game game;
    private final SimpleObjectProperty<String> title = new SimpleObjectProperty<>("Labyrinthe");
    private final ObservableValue<GameState> state;
    private final ObservableValue<Integer> currentPlayerId;
    private final List<PlayerViewModel> players = new ArrayList<>();
    private BaseMazeViewModel mazeViewModel;

    public BaseGameViewModel() {
        this.game = new Game();

        // adapte les observables du modèle vers javaFX
        this.state = FxAdapter.adapt(game.stateProperty());
        this.currentPlayerId = FxAdapter.adapt(game.currentPlayerProperty()).map(p -> {
            // + transformation en entier :
            if (p == null) { // -1 si il n'y a pas de joueur courant
                return -1;
            } else { // indiceJoueur sinon
                return p.getIndex();
            }
        });

        // crée les 4 slots pour les joueurs
        for (int i = 0; i < 4; i++) {
            players.add(new BasePlayerViewModel(game.getSlot(i), game));
        }
    }

    @Override // renvoie le titre de la fenêtre
    public ObservableValue<String> titleProperty() {
        return title;
    }

    @Override // renvoie le plateau du jeu
    public MazeViewModel getMazeViewModel() {
        return mazeViewModel;
    }

    @Override // renvoie l'id du joueur courant (joueur dont c'est le tour)
    public ObservableValue<Integer> currentPlayerIdProperty() {
        return currentPlayerId;
    }

    @Override // renvoie la liste des 4 emplacements des joueurs
    public List<PlayerViewModel> getPlayersViewModel() {
        return players;
    }

    @Override // renvoie l'état actuel du jeu
    public ObservableValue<GameState> stateProperty() {
        return state;
    }

    @Override // démarre une partie (ouvre les slots + initialise le jeu)
    public void startGame() {

        // ouvre les slots qui ne sont pas encore ouverts (A FIX CAR C'EST PAS CE QU'ON VEUT)
        // POUR L'INSTANT CA FORCE 4 JOUEURS QUOI QU'IL ARRIVE)
        for (int i = 0; i < 4; i++) {
            try {
                game.getSlot(i).open(); // essaye de l'ouvrir
            } catch (Exception ignored) {
                // sinon ne fait rien
            }
        }

        // initialise le jeu
        game.init();

        // crée le MazeViewModel une fois le jeu initialisé
        this.mazeViewModel = new BaseMazeViewModel(game);
    }
}