package labyrinth.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import labyrinth.service.dialog.DialogService;
import labyrinth.service.dialog.FileRequest;
import labyrinth.service.dialog.Message;
import labyrinth.service.dialog.Mode;

import java.io.File;

public class DialogHandler {

    private final Window owner; // la fenêtre principale

    public DialogHandler(DialogService dialogService, Window owner) {
        this.owner = owner;

        // s'abonne aux messages (publishMessage/publishError)
        dialogService.messageProperty().addListener((obs, old, message) -> {
            if (message != null) {
                Platform.runLater(() -> handleMessage(message));
            }
        });

        // s'abonne aux requêtes de fichier (requestOpenFile/requestSaveFile)
        dialogService.fileRequestProperty().addListener((obs, old, fileRequest) -> {
            if (fileRequest != null) {
                Platform.runLater(() -> handleFileRequest(fileRequest));
            }
        });
    }

    // gestion d'un message (boite de dialogue)
    private void handleMessage(Message message) {
        Alert alert;

        // si c'est une erreur
        if (message.isError()) {
            // Alerte rouge "erreur"
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
        } else { // sinon c'est une information
            // Alerte bleue "information"
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
        }

        alert.initOwner(owner); // rattache la boîte à la fenêtre principale
        alert.setHeaderText(null); // pas de sous-titre

        alert.setContentText(message.getMessage()); // texte du message
        alert.showAndWait(); // affiche l'alerte, attend que l'utilisateur clique sur OK
    }

    // gestion d'une demande de fichier (sélecteur de fichier)
    private void handleFileRequest(FileRequest fileRequest) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileRequest.getRequest()); // titre de la fenêtre

        File file;

        // si c'est en mode lecture
        if (fileRequest.getMode() == Mode.OPEN) {
            // fenêtre "Ouvrir un fichier"
            file = fileChooser.showOpenDialog(owner);
        } else { // sinon c'est en mode écriture
            // fenêtre "Sauvegarder un fichier"
            file = fileChooser.showSaveDialog(owner);
        }

        fileRequest.setFile(file); // complète le CompletableFuture
    }
}