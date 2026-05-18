package labyrinth.viewmodel;

// représente le menu du jeu
public interface MenuViewModel {
    void exit(); // quitter le jeu
    void newGame(); // lancer une partie
    void about(); // infos
    void showProfileManager(); // gestionnaire de profils
}