package labyrinth.viewmodel;

import javafx.beans.value.ObservableValue;

import java.util.UUID;

// représente un emplacement joueur (dans le menu à droite)
public interface PlayerViewModel {
    ObservableValue<Boolean> activeProperty(); // true si l'emplacement est occupé
    void addPlayer(); // rend le slot actif (joueur humain)
    void addPlayer(UUID uuid); // idem mais avec uuid
    void addBot(); // idem (joueur bot)
    void removePlayer(); // rend l'emplacement inactif
    ObservableValue<Boolean> lockedProperty(); // true si l'emplacement est verrouillé (après SETUP)
}