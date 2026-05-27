package labyrinth.view;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import labyrinth.model.Direction;
import labyrinth.model.TileKind;
import labyrinth.viewmodel.TileViewModel;
import java.util.List;

// la vue d'une tuile
public class TileView extends StackPane {

    private final ImageView imageView = new ImageView(); // imageView est attribut d'une tuile

    public TileView(TileViewModel viewModel) {

        // taille fixe pour une tuile
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);

        // met l'image initiale
        updateImage(viewModel);

        // au changement du type de tuile, met à jour l'image
        viewModel.kindProperty().addListener((obs, oldVal, newVal) -> {
            updateImage(viewModel);
        });

        // au changement de directions, met à jour l'image
        viewModel.directionProperty().addListener((obs, oldVal, newVal) -> {
            updateImage(viewModel);
        });

        // label position de départ (-1 = d'aucun joueur, sinon 0-3)
        Label startLabel;

        // état de base (avant changement via listener)
        if (viewModel.startingPositionProperty().getValue() >= 0) {
            startLabel = new Label("P" + (viewModel.startingPositionProperty().getValue() + 1));
        } else {
            startLabel = new Label("");
        }

        // état si changement
        viewModel.startingPositionProperty().addListener((obs, old, id) -> {
            startLabel.setText(id >= 0 ? "P" + (id + 1) : ""); // PX où X = id+1 du joueur, sinon rien
        });

        // label trésor
        Label treasureLabel;

        // récupère l'id du trésor
        int initialId = viewModel.treasureIdProperty().getValue();

        // état de base (avant changement via listener)
        if(initialId >= 0) {
            treasureLabel = new Label("T" + (initialId + 1));
        } else {
            treasureLabel = new Label("");
        }

        // état si changement
        viewModel.treasureIdProperty().addListener((obs, old, id) -> {
            treasureLabel.setText(id >= 0 ? "T" + id : ""); // T[X] sinon rien
        });

        // image du joueur présent sur la tuile
        ImageView playerImage = new ImageView();
        playerImage.setFitWidth(30);
        playerImage.setFitHeight(30);

        // état de base
        updatePlayerImage(
                playerImage,
                viewModel.playersIdProperty().getValue(),
                viewModel.currentPlayerIdProperty().getValue()
        );

        // état si changement
        viewModel.playersIdProperty().addListener((obs, old, players) -> {
            updatePlayerImage(
                    playerImage,
                    players,
                    viewModel.currentPlayerIdProperty().getValue()
            );
        });

        // état si changement du joueur courant
        viewModel.currentPlayerIdProperty().addListener((obs, old, currentPlayerId) -> {
            updatePlayerImage(
                    playerImage,
                    viewModel.playersIdProperty().getValue(),
                    currentPlayerId
            );
        });

        // empile dans l'ordre : image -> position de départ -> trésor -> pion
        getChildren().addAll(imageView, startLabel, treasureLabel, playerImage);
    }

    // maj de l'image d'une tuile
    private void updateImage(TileViewModel viewModel) {

        // récupère les infos de la tuile
        TileKind kind = viewModel.getKind();
        Direction dir = viewModel.getDirection();

        // récupère l'image associée
        Image image = ImageManager.getInstance().getTileImage(kind,dir);

        // l'applique
        imageView.setImage(image);
    }

    // met à jour l'affichage de l'image (ou non) sur une tuile
    private void updatePlayerImage(ImageView imageView, List<Integer> players, Integer currentPlayerId) {
        if (players.isEmpty()) { // si il n'y a pas de joueur sur la tuile
            imageView.setImage(null); // pas d'image
            imageView.setStyle("");
        } else { // sinon il y a au moins un joueur sur la tuile
            int id;

            // si le joueur courant est sur la tuile, on l'affiche en priorité
            if (currentPlayerId != null && players.contains(currentPlayerId)) {
                id = currentPlayerId;
                imageView.setStyle("-fx-effect: dropshadow(gaussian, gold, 10, 0.8, 0, 0);");
            } else {
                id = players.getFirst(); // récupère l'id du premier joueur arrivé sur la tuile
                imageView.setStyle("");
            }

            imageView.setImage(new Image("/img/player" + (id + 1) + ".png")); // affiche son pion
        }
    }
}