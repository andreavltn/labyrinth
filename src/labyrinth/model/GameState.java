package labyrinth.model;

public enum GameState {
    SETUP, // le jeu est en cours d'initialisation'
    MAZE_MOVE, // le joueur courant est en train de placer sa tuile
    PLAYER_MOVE, // le joueur courant est en train de se déplacer
    TURN_END, // fin d'un tour, passage au joueur suivant
    GAME_OVER // fin du jeu
}
