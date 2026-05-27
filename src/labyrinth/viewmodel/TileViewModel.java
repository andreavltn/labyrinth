package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;
import labyrinth.model.Direction;
import labyrinth.model.TileKind;
import java.util.List;

// représente une tuile du labyrinthe
public interface TileViewModel {
    ObservableValue<TileKind> kindProperty(); // propriété observable du type de tuile
    TileKind getKind(); // renvoie le type de la tuile
    ObservableValue<Direction> directionProperty(); // // propriété observable de la direction de la tuile
    Direction getDirection(); // renvoie la direction de la tuile
    ObservableValue<Integer> startingPositionProperty(); // position de départ d'un joueur (-1 si aucune)
    ObservableValue<Integer> treasureIdProperty(); // id (-1 si pas de trésor)
    ObservableValue<List<Integer>> playersIdProperty(); // liste des ids des joueurs sur la tuile
    ObservableValue<Integer> currentPlayerIdProperty(); // id du joueur courant (dont c'est le tour)
}