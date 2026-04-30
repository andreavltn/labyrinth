package labyrinth.view;

import javafx.application.Platform;
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

            // si on quitte l'état setup
            if (old == GameState.SETUP && newState != GameState.SETUP) {
                updateDisplay(viewModel); // met à jour la vue du jeu
            }

            // si on réinitialise le jeu
            else if (newState == GameState.SETUP){
                Platform.runLater(() -> {
                    getChildren().clear(); // retire le bouton Start
                    getChildren().add(btnStart); // re-affiche le bouton start
                });
            }
        });

        // si chargement d'une partie (depuis un fichier)
        viewModel.mazeViewModelProperty().addListener((obs, oldVM, newVM) -> {
            if (newVM != null) {
                updateDisplay(viewModel);
            }
        });
    }

    // méthode pour mettre à jour l'affichage du labyrinthe
    private void updateDisplay(GameViewModel viewModel) {
        Platform.runLater(() -> {
            if (viewModel.getMazeViewModel() != null) {
                MazeView mazeView = new MazeView(viewModel.getMazeViewModel()); // crée le plateau
                getChildren().clear(); // retire le bouton Start
                getChildren().add(mazeView); // affiche le jeu
                mazeView.requestFocus(); // forcer le focus
            }
        });
    }
}