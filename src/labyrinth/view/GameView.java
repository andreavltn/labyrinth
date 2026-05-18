package labyrinth.view;

import javafx.scene.layout.BorderPane;
import labyrinth.viewmodel.GameViewModel;

// la vue du jeu dans son ensemble
public class GameView extends BorderPane {

    public GameView(GameViewModel viewModel) {

        // récupère le joueur courant initial du jeu
        Integer initialPlayerId = viewModel.currentPlayerIdProperty().getValue();

        // choix du texte du joueur courant (affichage)
        String currentPlayerIdText;
        if(initialPlayerId == null) { // si il n'y a pas de joueur courant
            currentPlayerIdText = "X"; // affiche X
        } else { // sinon
            currentPlayerIdText = String.valueOf(initialPlayerId + 1); // affiche son numéro de joueur
        }

        // affichage de l'état du jeu + joueur courant
        javafx.scene.control.Label debugLabel =
                new javafx.scene.control.Label("État: SETUP | Joueur courant: " + currentPlayerIdText);
        debugLabel.setStyle("-fx-text-fill: white; -fx-padding: 5;"); // style
        setLeft(debugLabel); // en haut à gauche

        // mise à jour de l'état du jeu
        viewModel.stateProperty().addListener((obs, old, newState) -> {
            Integer playerId = viewModel.currentPlayerIdProperty().getValue();
            if (playerId == null) { // si il n'y a pas de joueur courant
                debugLabel.setText("État: " + newState + " | Joueur courant: X");
            } else { // sinon il y en a un
                debugLabel.setText("État: " + newState + " | Joueur courant: " + (playerId + 1));
            }
        });

        // mise à jour du joueur courant
        viewModel.currentPlayerIdProperty().addListener((obs, old, playerId) -> {
            String state = viewModel.stateProperty().getValue().toString();
            if (playerId == null) { // si il n'y a pas de joueur courant
                debugLabel.setText("État: " + state + " | Joueur courant: X");
            } else { // sinon il y en a un
                debugLabel.setText("État: " + state + " | Joueur courant: " + (playerId + 1));
            }
        });

        // la vue principale au centre
        MainView mainView = new MainView(viewModel);
        setCenter(mainView);

        // panneau des joueurs à droite
        PlayersView playersView = new PlayersView(viewModel);
        setRight(playersView);
    }
}