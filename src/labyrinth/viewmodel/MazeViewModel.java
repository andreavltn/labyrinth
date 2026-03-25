package labyrinth.viewmodel;

import labyrinth.util.collection.Grid;
import java.util.List;
import labyrinth.model.GameState;
import javafx.beans.value.ObservableValue;

// représente le plateau du jeu (le labyrinthe)
public interface MazeViewModel {
    Grid<CellViewModel> getCellViewModels(); // renvoie la grille des cellules
    List<SlotViewModel> getSlotViewModels(); // renvoie la liste des slots
    TileViewModel getExtraTileViewModel(); // renvoie la tuile supplémentaire
    ObservableValue<GameState> stateProperty(); // l'état du jeu (i.e GameState)
    void rotateExtraTile(int nbTimes); // tourne la tuile supplémentaire nbTimes fois (vers la droite)
}