package labyrinth;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import labyrinth.view.GameView;
import labyrinth.viewmodel.BaseGameViewModel;
import labyrinth.viewmodel.BaseMenuViewModel;

public class Labyrinth extends Application {

    @Override
    public void start(Stage stage) {

        // création du viewModel (stub pour l'instant)
        BaseGameViewModel gameViewModel = new BaseGameViewModel();

        stage.titleProperty().bind(gameViewModel.titleProperty());

        // création de la vue principale
        GameView gameView = new GameView(gameViewModel);

        // création du menu
        MenuBar menuBar = new MenuBar();

        // bouton "Jeu"
        Menu menuGame = new Menu("Jeu");

        MenuItem itemNewGame = new MenuItem("Nouvelle partie"); // bouton "Nouvelle partie"
        MenuItem itemExit = new MenuItem("Quitter"); // bouton "Quitter"
        menuGame.getItems().addAll(itemNewGame, itemExit); // ajout au sous-menu

        // bouton "Aide"
        Menu menuHelp = new Menu("Aide");
        MenuItem itemAbout = new MenuItem("À propos"); // bouton "A-propos"
        menuHelp.getItems().add(itemAbout); // ajout au sous-menu

        menuBar.getMenus().addAll(menuGame, menuHelp); // ajout des boutons à la barre Menu

        // menu des actions
        BaseMenuViewModel menuViewModel = new BaseMenuViewModel(gameViewModel);
        itemNewGame.setOnAction(e -> menuViewModel.newGame()); // lancer une partie
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