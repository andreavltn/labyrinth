package labyrinth.view;

import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import labyrinth.viewmodel.PlayerViewModel;
import labyrinth.viewmodel.GameViewModel;
import java.util.List;

// représente la boite contenant les 4 joueurs ensemble
public class PlayersView extends VBox {


    public PlayersView(GameViewModel viewModel) {

        setMaxHeight(USE_PREF_SIZE); // force la boite a s'adapter a la taille de son contenu
        setStyle("-fx-border-color: white; -fx-border-width: 2;"); // bordure blanche autour de la boite

        List<PlayerViewModel> players = viewModel.getPlayersViewModel();

        // pour chaque joueur
        for (int i = 0; i < players.size(); i++) {
            getChildren().add(new PlayerView(players.get(i), i)); // ajoute la boite du joueur

            // ajoute un séparateur en dessous, sauf pour le dernier
            if (i < players.size() - 1) {
                getChildren().add(new Separator());
            }
        }
    }
}
