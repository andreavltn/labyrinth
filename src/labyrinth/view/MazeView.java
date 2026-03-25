package labyrinth.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Rectangle;
import labyrinth.viewmodel.MazeViewModel;
import labyrinth.model.GameState;

// la vue du labyrinthe + la tuile supplémentaire
public class MazeView extends StackPane {

    public MazeView(MazeViewModel viewModel) {

        // COUCHE FIXE : plateau + tuile fixe

        // plateau de jeu
        BoardView boardView = new BoardView(viewModel);

        // tuile fixe
        TileView extraTileFixed = new TileView(viewModel.getExtraTileViewModel());

        // enveloppée dans une boite
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.BOTTOM_LEFT); // en bas à gauche
        bottomBox.getChildren().add(extraTileFixed); // ajoute la tuile à la boite
        BorderPane.setMargin(bottomBox, new Insets(10)); // un peu de margin autour

        // plateau au centre, tuile fixe en bas à gauche
        BorderPane fixLayer = new BorderPane();
        fixLayer.setCenter(boardView); // plateau
        fixLayer.setBottom(bottomBox); // extraTile

        // COUCHE MOBILE : la tuile suit les mouvements de la souris

        // tuile mobile qui suit le mouvement de la souris
        Pane mobileLayer = new Pane();
        TileView mobileTile = new TileView(viewModel.getExtraTileViewModel());
        mobileTile.setVisible(false); // au départ la tuile est invisble
        mobileLayer.getChildren().add(mobileTile); // ajout au Pane
        mobileLayer.setMouseTransparent(true); // capte les événements souris

        // quand la souris bouge, on déplace la tuile mobile
        setOnMouseMoved(e -> {
            // affichée seulement quand on est en MAZE_MOVE
            if (viewModel.stateProperty().getValue() == GameState.MAZE_MOVE) {
                mobileTile.setVisible(true);
                mobileTile.relocate(e.getX() - 30, e.getY() - 30); // centre la tuile sur le curseur
            }
        });

        // si la souris quitte la zone du jeu, fait disparaitre la tuile
        setOnMouseExited(e -> {
            mobileTile.setVisible(false); // elle devient invisible
        });

        // cache la tuile mobile si on n'est plus en MAZE_MOVE
        viewModel.stateProperty().addListener((obs, old, newState) -> {
            if (newState != GameState.MAZE_MOVE) {
                mobileTile.setVisible(false);
            }
        });

        // empeche la tuile de dépasser du composant
        Rectangle clip = new Rectangle(); // rectangle vide
        clip.widthProperty().bind(mobileLayer.widthProperty()); // récupère la largeur et hauteur du layer
        clip.heightProperty().bind(mobileLayer.heightProperty());
        mobileLayer.setClip(clip); // applique le rectangle comme "zone de découpe"

        setFocusTraversable(true); // le pane peut recevoir le focus clavier
        setOnMouseEntered(e -> requestFocus()); // demande le focus clavier dès le début

        // rotation gauche/droite avec A,E
        setOnKeyPressed((KeyEvent e) -> {
            switch (e.getCode()) {
                case A -> viewModel.rotateExtraTile(-1);
                case E -> viewModel.rotateExtraTile(1);
            }
        });

        // rotation aussi avec la molette
        setOnScroll((ScrollEvent e) -> {

            // delta > 0 = scroll vers le haut
            if (e.getDeltaY() > 0) {
                viewModel.rotateExtraTile(1);
            } else {
                viewModel.rotateExtraTile(-1);
            }
        });

        // empile les deux couches
        getChildren().addAll(fixLayer, mobileLayer);
    }
}