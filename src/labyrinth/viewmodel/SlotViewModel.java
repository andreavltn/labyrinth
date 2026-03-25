package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;
import labyrinth.model.MazeMove;

// représente un slot au bord du labyrinthe
public interface SlotViewModel {
    MazeMove getMazeMove(); // retourne le mouvement associé au slot
    ObservableValue<Boolean> magnetizedProperty(); // true si le slot est aimanté
    void magnetize(); // aimante le slot (la tuile libre s'affiche dessus)
    void demagnetize(); // désaimante le slot (la tuile libre disparaît)
    ObservableValue<Boolean> forbiddenProperty(); // true si le slot est interdit
    void moveMaze(); // joue le coup associé au slot
    ObservableValue<Boolean> activeProperty(); // true si l'aimantation est active (pendant MAZE_MOVE)
}