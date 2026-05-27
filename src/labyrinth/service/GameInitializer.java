package labyrinth.service;

import labyrinth.model.Game;
import labyrinth.service.persistence.SqlPersistenceService;
import labyrinth.service.serialization.GameDTO;
import javafx.application.Platform;
import labyrinth.model.GameState;

public class GameInitializer {

    private final SqlPersistenceService persistenceService;

    public GameInitializer(SqlPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    // initialise le jeu via les données de la bdd
    public void init(Game game) {
        GameDTO dto = persistenceService.loadLatestGame(); // charge la dernière partie sauvegardée
        if (dto != null) { // si il y avait une partie sauvegardée
            game.updateFrom(dto); // hydrate le jeu avec les données

            // si le joueur courant est un bot, déclencher son tour
            if (game.getCurrentPlayer() != null
                    && game.getCurrentPlayer().isBot()
                    && game.getState() == GameState.MAZE_MOVE) {
                Platform.runLater(() -> game.startTurn());
            }
        }
        // sinon ne fait rien (bdd vide)
    }
}