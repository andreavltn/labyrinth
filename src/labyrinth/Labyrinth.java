package labyrinth;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import labyrinth.model.Game;
import labyrinth.service.GameBootstrapper;
import labyrinth.service.Services;
import labyrinth.service.dialog.DefaultDialogService;
import labyrinth.service.dialog.DialogService;
import labyrinth.service.persistence.SqlPersistenceService;
import labyrinth.service.serialization.JsonSerializationService;
import labyrinth.service.serialization.SerializationService;
import labyrinth.view.DialogHandler;
import labyrinth.view.GameView;
import labyrinth.viewmodel.BaseGameViewModel;
import labyrinth.viewmodel.BaseMenuViewModel;

public class Labyrinth extends Application {

    @Override
    public void start(Stage stage) {

        // ======== SERVICES ======== //

        // service de sérialisation (convertir Game <-> Json)
        SerializationService serializationService = new JsonSerializationService();

        // service de dialogue (publier messages et requêtes de fichier)
        DialogService dialogService = new DefaultDialogService();

        // service de persistance (sauvegarde automatique)
        SqlPersistenceService persistenceService = new SqlPersistenceService(serializationService);

        // objet Services : rassemble les trois services
        Services services = new Services(serializationService, dialogService, persistenceService);

        // gestionnaire de dialogue
        new DialogHandler(services.getDialogService(), stage);

        // ======== JEU ET VIEWMODEL  ======== //

        // création du jeu
        GameBootstrapper bootstrapper = new GameBootstrapper(services);
        Game game = bootstrapper.bootstrap();

        // viewmodel
        BaseGameViewModel gameViewModel = new BaseGameViewModel(game, services);

        stage.titleProperty().bind(gameViewModel.titleProperty());

        // création de la vue principale
        GameView gameView = new GameView(gameViewModel);

        // création du menu
        MenuBar menuBar = new MenuBar();

        // bouton du menu "Jeu"
        Menu menuGame = new Menu("Jeu");

        // menu "Jeu"
        MenuItem itemNewGame = new MenuItem("Nouvelle partie"); // bouton "Nouvelle partie"
        MenuItem itemExit = new MenuItem("Quitter"); // bouton "Quitter"
        MenuItem itemSave = new MenuItem("Sauvegarder");  // bouton "Sauvegarder"
        MenuItem itemLoad = new MenuItem("Charger"); // bouton "Charger"
        menuGame.getItems().addAll(itemNewGame, itemExit, itemSave, itemLoad); // ajout au sous-menu

        // bouton du menu "Aide"
        Menu menuHelp = new Menu("Aide");
        MenuItem itemAbout = new MenuItem("À propos"); // bouton "A-propos"
        menuHelp.getItems().add(itemAbout); // ajout au sous-menu

        menuBar.getMenus().addAll(menuGame, menuHelp); // ajout des boutons à la barre Menu

        // branchement des actions aux boutons
        BaseMenuViewModel menuViewModel = new BaseMenuViewModel(gameViewModel, services);
        itemNewGame.setOnAction(e -> menuViewModel.newGame()); // lancer une partie
        itemSave.setOnAction(e -> menuViewModel.save()); // sauvegarder les données
        itemLoad.setOnAction(e -> menuViewModel.load()); // charger les données
        itemExit.setOnAction(e -> menuViewModel.exit()); // quitter le jeu
        itemAbout.setOnAction(e -> menuViewModel.about()); // afficher les infos

        // assemble le tout dans un borderPane
        BorderPane root = new BorderPane();
        root.setTop(menuBar); // le menu en haut
        root.setCenter(gameView); // le jeu au centre
        root.setStyle("-fx-background-color: #3d5a8a;"); // change la couleur du fond

        // crée la scène et l'affiche
        stage.setScene(new Scene(root, 1200, 700));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}