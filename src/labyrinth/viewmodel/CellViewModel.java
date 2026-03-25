package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;

// représente une cellule du plateau (lien entre une cellule et la tuile qu'elle contient)
public interface CellViewModel {
    TileViewModel getTileViewModel(); // renvoie la tuile associée à la cellule
    ObservableValue<Boolean> activeProperty(); // true si on peut cliquer sur la cellule (pendant PLAYER_MOVE)
    void movePlayer(); // appelée quand le joueur clique sur la cellule pour y déplacer son pion
}