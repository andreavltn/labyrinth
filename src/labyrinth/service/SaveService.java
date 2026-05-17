package labyrinth.service;

import labyrinth.model.Game;
import labyrinth.model.GameState;
import labyrinth.service.persistence.SqlPersistenceService;
import labyrinth.service.serialization.GameDTO;

public class SaveService {

    private final SqlPersistenceService persistenceService;

    public SaveService(SqlPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    // attache des écouteurs au jeu pour sauvegarder automatiquement
    public void attach(Game game) {

        // si un tour se termine
        game.stateProperty().addListener((obs, old, newState) -> {
            // (si le nouvel état est MAZE_MOVE depuis une fin de tour)
            // i.e un joueur vient de finir son tour
            if (newState == GameState.MAZE_MOVE && old == GameState.TURN_END) {
                GameDTO dto = new GameDTO(game); // crée le DTO
                persistenceService.saveGame(dto); // le sauvegarde dans la BDD
            }
        });

        // si la partie se termine
        game.stateProperty().addListener((obs, old, newState) -> {

            // (si l'état du jeu est GAME_OVER)
            if (newState == GameState.GAME_OVER) {
                // idem
                GameDTO dto = new GameDTO(game);
                persistenceService.saveGame(dto);
            }
        });
    }
}