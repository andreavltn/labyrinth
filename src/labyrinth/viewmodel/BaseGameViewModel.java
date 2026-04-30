package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;
import labyrinth.service.Services;
import labyrinth.util.adapter.FxAdapter;
import java.util.ArrayList;
import java.util.List;

public class BaseGameViewModel implements GameViewModel {

    private final Game game;
    private final SimpleObjectProperty<String> title = new SimpleObjectProperty<>("Labyrinthe");
    private final ObservableValue<GameState> state;
    private final ObservableValue<Integer> currentPlayerId;
    private final List<PlayerViewModel> players = new ArrayList<>();
    private final SimpleBooleanProperty canStart = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<MazeViewModel> mazeViewModel = new SimpleObjectProperty<>(null);
    private final Services services;

    public BaseGameViewModel(Game game, Services services) {
        this.game = game;
        this.services = services;

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
            PlayerViewModel playerViewModel = new BasePlayerViewModel(game.getSlot(i), game);
            players.add(playerViewModel);

            // met à jour la possibilité de lancer quand un slot devient actif/inactif
            playerViewModel.activeProperty().addListener((obs, old, active) -> updateCanStart());
        }

        // met aussi à jour si l'état du jeu change
        this.state.addListener((obs, old, newState) -> updateCanStart());

        // initialisation directe si le labyrinthe existe
        if (game.getMaze() != null) {
            this.mazeViewModel.set(new BaseMazeViewModel(game, services));
        }

        // met à jour la vue du labyrinthe s'il change
        game.mazeProperty().addListener((obs, oldMaze, newMaze) -> {
            if (newMaze != null && game.getCurrentPlayer() != null) {
                this.mazeViewModel.setValue(new BaseMazeViewModel(game, services));
            } else {
                this.mazeViewModel.setValue(null);
            }
        });

        updateCanStart();
    }

    private void updateCanStart() {
        int activePlayers = 0;

        for (PlayerViewModel player : players) {
            if (Boolean.TRUE.equals(player.activeProperty().getValue())) {
                activePlayers++;
            }
        }

        // on peut démarrer seulement en SETUP avec au moins 2 joueurs
        canStart.set(game.getState() == GameState.SETUP && activePlayers >= 2);
    }

    @Override // renvoie le titre de la fenêtre
    public ObservableValue<String> titleProperty() {
        return title;
    }

    @Override
    public ObservableValue<MazeViewModel> mazeViewModelProperty() {
        return mazeViewModel;
    }

    @Override // renvoie le plateau du jeu
    public MazeViewModel getMazeViewModel() {
        return mazeViewModel.getValue();
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

    @Override // true si on peut démarrer la partie
    public ObservableValue<Boolean> canStartProperty() {
        return canStart;
    }

    @Override // démarre une partie avec les slots actuellement ouverts
    public void startGame() {

        // ne démarre pas si il n'y a pas assez de joueurs
        if (!canStart.get()) {
            return;
        }

        // initialise le jeu
        game.init();

        // crée le MazeViewModel une fois le jeu initialisé
        this.mazeViewModel.set(new BaseMazeViewModel(game, services));

        // message de début de partie
        if (services != null && services.getDialogService() != null) {
            int id = game.getCurrentPlayer().getIndex() + 1; // +1 car les indices commencent à 0
            services.getDialogService().publishMessage("La partie commence! C'est au joueur " + id + " de jouer.");
        }

        updateCanStart();
    }

    public Game getGame() {
        return game;
    }

    public Services getServices() {
        return services;
    }

    // force le rafraichissement de la vue du labyrinthe
    public void forceRefreshMazeViewModel() {
        if (game.getMaze() != null && game.getCurrentPlayer() != null) {
            this.mazeViewModel.setValue(new BaseMazeViewModel(game, services));
        }
    }
}