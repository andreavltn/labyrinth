package labyrinth.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import labyrinth.model.Game;
import labyrinth.model.GameState;
import labyrinth.model.Player;
import labyrinth.service.dialog.DialogService;
import labyrinth.service.dialog.FileRequest;
import labyrinth.service.dialog.Message;
import labyrinth.service.dialog.Mode;

import java.io.File;

public class DialogHandler {

    private final Window owner;
    private final ProfilesManagerWindow profilesManagerWindow;

    // IMPORTANT :
    // référence directe vers la partie
    private final Game game;

    public DialogHandler(DialogService dialogService, Window owner, ProfilesManagerWindow profilesManagerWindow, Game game) {

        this.owner = owner;
        this.profilesManagerWindow = profilesManagerWindow;
        this.game = game;

        // gestion des messages
        dialogService.messageProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> handleMessage(newValue));
            }
        });

        // gestion des fichiers
        dialogService.fileRequestProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> handleFileRequest(newValue));
            }
        });

        // gestion des profils
        dialogService.profilManagerRequestProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> profilesManagerWindow.show());
            }
        });

        // gestion de la fin de tour
        game.stateProperty().addListener((obs, old, newState) -> {
            if (newState == GameState.TURN_END) {
                Player current = game.getCurrentPlayer();
                if (current != null && !current.isBot()) {
                    Platform.runLater(() -> {
                        if (game.getState() == GameState.TURN_END) {
                            game.endTurn();
                        }
                    });
                }
            }
        });
    }

    // gestion d'un message
    private void handleMessage(Message message) {

        Alert alert;

        // erreur
        if (message.isError()) {

            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
        }

        // information
        else {

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
        }

        alert.initOwner(owner);
        alert.setHeaderText(null);
        alert.setContentText(message.getMessage());
        alert.showAndWait();
    }

    // gestion des fichiers
    private void handleFileRequest(FileRequest fileRequest) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle(fileRequest.getRequest());

        File file;

        // ouverture
        if (fileRequest.getMode() == Mode.OPEN) {
            file = chooser.showOpenDialog(owner);
        }

        // sauvegarde
        else {
            file = chooser.showSaveDialog(owner);
        }

        fileRequest.setFile(file);
    }
}