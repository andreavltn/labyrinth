package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;

// représente un emplacement joueur (dans le menu à droite)
public interface PlayerViewModel {
    ObservableValue<Boolean> activeProperty(); // true si l'emplacement est occupé
    void addPlayer(); // rend l'emplacement actif (joueur humain)
    void addBot(); // idem (joueur bot)
    void removePlayer(); // rend l'emplacement inactif
    ObservableValue<Boolean> lockedProperty(); // true si l'emplacement est verrouillé (après SETUP)
}