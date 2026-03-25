package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;
import labyrinth.model.GameState;
import java.util.List;

// représente le jeu dans sa globalité
public interface GameViewModel {
    ObservableValue<String> titleProperty(); // titre de la fenêtre
    MazeViewModel getMazeViewModel(); // renvoie le plateau de jeu (null pendant SETUP)
    ObservableValue<Integer> currentPlayerIdProperty(); // id du joueur dont c'est le tour
    List<PlayerViewModel> getPlayersViewModel(); // renvoie la liste des 4 emplacements de joueur
    ObservableValue<GameState> stateProperty(); // l'état actuel du jeu (i.e GameState)
    void startGame(); // démarre la partie (SETUP -> MOVE_MAZE)
}