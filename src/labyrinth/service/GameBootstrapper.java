package labyrinth.service;

import labyrinth.model.Game;

public class GameBootstrapper {

    private final Services services;

    public GameBootstrapper(Services services) {
        this.services = services;
    }

    // crée un jeu (initialisé + sauvegarde automatique)
    public Game bootstrap() {

        // crée un nouveau jeu
        Game game = new Game();

        // initialise avec la dernière sauvegarde de la bdd
        GameInitializer initializer = new GameInitializer(services.getPersistenceService());
        initializer.init(game);

        // y attache la sauvegarde automatique
        SaveService saveService = new SaveService(services.getPersistenceService());
        saveService.attach(game);

        return game; // renvoie le jeu
    }
}