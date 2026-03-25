package labyrinth.view;

import javafx.scene.layout.BorderPane;
import labyrinth.viewmodel.GameViewModel;

// la vue du jeu dans son ensemble
public class GameView extends BorderPane {

    public GameView(GameViewModel viewModel) {

        // affichage de l'état du jeu + joueur courant
        javafx.scene.control.Label debugLabel = new javafx.scene.control.Label("État: SETUP | Joueur courant: x");
        debugLabel.setStyle("-fx-text-fill: white; -fx-padding: 5;");
        setLeft(debugLabel); // en haut à gauche

        // mise à jour de l'état du jeu
        viewModel.stateProperty().addListener((obs, old, newState) -> {
            int playerId = viewModel.currentPlayerIdProperty().getValue();
            debugLabel.setText("État: " + newState + " | Joueur courant : " + (playerId + 1));
        });

        // mise à jour du joueur courant
        viewModel.currentPlayerIdProperty().addListener((obs, old, playerId) -> {
            String state = viewModel.stateProperty().getValue().toString();
            debugLabel.setText("État: " + state + " | Joueur courant: " + (playerId + 1));
        });

        // la vue principale au centre
        MainView mainView = new MainView(viewModel);
        setCenter(mainView);

        // panneau des joueurs à droite
        PlayersView playersView = new PlayersView(viewModel);
        setRight(playersView);
    }
}