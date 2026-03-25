package labyrinth.view;

import javafx.scene.layout.BorderPane;
import labyrinth.viewmodel.GameViewModel;

// la vue du jeu dans son ensemble
public class GameView extends BorderPane {

    public GameView(GameViewModel viewModel) {

        // la vue principale au centre
        MainView mainView = new MainView(viewModel);
        setCenter(mainView);

        // panneau des joueurs à droite
        PlayersView playersView = new PlayersView(viewModel);
        setRight(playersView);
    }
}