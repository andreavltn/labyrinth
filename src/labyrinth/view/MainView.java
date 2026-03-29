package labyrinth.view;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import labyrinth.model.GameState;
import labyrinth.viewmodel.GameViewModel;

// la vue principale (bouton start ou le jeu suivant l'état du jeu)
public class MainView extends StackPane {

    public MainView(GameViewModel viewModel) {

        // bouton start affiché dans l'état SETUP
        Button btnStart = new Button("Start");
        btnStart.setOnAction(e -> viewModel.startGame()); // lance le jeu au clic
        btnStart.setDisable(!Boolean.TRUE.equals(viewModel.canStartProperty().getValue()));
        getChildren().add(btnStart);

        // met à jour l'activation du bouton start
        viewModel.canStartProperty().addListener((obs, old, canStart) -> {
            btnStart.setDisable(!Boolean.TRUE.equals(canStart));
        });

        // écoute les changements d'état
        viewModel.stateProperty().addListener((obs, old, newState) -> {

            if (old == GameState.SETUP) { // si l'ancien état était SETUP
                javafx.application.Platform.runLater(() -> {
                    MazeView mazeView = new MazeView(viewModel.getMazeViewModel()); // crée le plateau
                    getChildren().clear(); // retire le bouton Start
                    getChildren().add(mazeView); // affiche le plateau
                });
            }
        });
    }
}