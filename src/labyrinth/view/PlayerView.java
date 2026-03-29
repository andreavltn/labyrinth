package labyrinth.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import labyrinth.viewmodel.PlayerViewModel;

// représente un seul joueur
public class PlayerView extends VBox {

    public PlayerView(PlayerViewModel viewModel, int id) {

        setSpacing(10); // 10px d'espace entre chaque élément du Vbox (chaque Joueur)

        // padding intérieur
        setStyle("-fx-padding: 10;");

        // image par défault d'un joueur
        ImageView playerImage = new ImageView();
        playerImage.setFitWidth(40); // taille 40x40
        playerImage.setFitHeight(40);
        playerImage.setImage(new Image("/img/default-user.png")); // image par défaut

        // label avec l'id du joueur + son statut
        boolean isActive = Boolean.TRUE.equals(viewModel.activeProperty().getValue());
        Label label = new Label("Joueur " + (id + 1) + (isActive ? " | Actif" : " | Inactif"));

        // met à jour le statut quand l'état change
        viewModel.activeProperty().addListener((obs, old, active) -> {
            boolean activeNow = Boolean.TRUE.equals(active);
            label.setText("Joueur " + (id + 1) + (activeNow ? " | Actif" : " | Inactif")); // actif/inactif
        });

        // 10px d'espace entre l'image et le nom + statut
        HBox firstLine = new HBox(10);
        firstLine.setAlignment(Pos.CENTER_LEFT); // centré à gauche
        firstLine.getChildren().addAll(playerImage, label);

        // bouton pour ajouter un joueur
        Button btnAdd = new Button("Ajouter");
        btnAdd.setOnAction(e -> viewModel.addPlayer());

        // bouton pour retirer un joueur
        Button btnRemove = new Button("Retirer");
        btnRemove.setOnAction(e -> viewModel.removePlayer());

        // met à jour l'état des boutons
        Runnable refreshButtons = () -> {
            boolean active = Boolean.TRUE.equals(viewModel.activeProperty().getValue());
            boolean locked = Boolean.TRUE.equals(viewModel.lockedProperty().getValue());

            btnAdd.setDisable(locked || active);
            btnRemove.setDisable(locked || !active);
        };

        // valeur de base
        refreshButtons.run();

        // met à jour si le slot devient actif/inactif
        viewModel.activeProperty().addListener((obs, old, active) -> {
            refreshButtons.run();
        });

        // met à jour si verrouillé
        viewModel.lockedProperty().addListener((obs, old, locked) -> {
            refreshButtons.run();
        });

        // boite des boutons
        HBox buttons = new HBox(10); // boite horizontale
        buttons.getChildren().addAll(btnAdd, btnRemove); // avec les 2 boutons
        buttons.setAlignment(Pos.CENTER); // centré

        // textes couleur blanc
        label.setStyle("-fx-text-fill: white;");

        getChildren().addAll(firstLine, buttons); // empile les 2 boites verticalement
    }
}