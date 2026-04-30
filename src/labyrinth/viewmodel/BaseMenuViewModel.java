package labyrinth.viewmodel;

import labyrinth.service.Services;
import labyrinth.service.dialog.FileRequest;
import labyrinth.service.serialization.GameDTO;

import java.nio.file.Files;

public class BaseMenuViewModel implements MenuViewModel {

    private final BaseGameViewModel gameViewModel;
    private final Services services;

    public BaseMenuViewModel(BaseGameViewModel gameViewModel, Services services) {
        this.gameViewModel = gameViewModel;
        this.services = services;
    }

    @Override
    public void newGame() {
        // à implémenter plus tard
    }

    @Override
    public void exit() {
        System.exit(0); // quitte le jeu
    }

    @Override
    public void about() {
        // à implémenter plus tard
    }

    // charger une sauvegarde (depuis un fichier)
    public void load() {

        // affiche une fenêtre "Ouvrir un fichier"
        FileRequest req = services.getDialogService().requestOpenFile("Charger une sauvegarde");

        // quand l'utilisateur aura mis un fichier (ou cliqué sur Annuler) :
        req.getFile().thenAccept(file -> {

            // si l'utilisateur a appuyé sur Annuler
            if (file == null) {
                return; // rien
            }

            // sinon
            try {
                // lire le contenu du fichier (chaine de caractères)
                String json = Files.readString(file.toPath());

                // désérialiser la chaine de caractères en objet GameDTO utilisable
                GameDTO dto = services.getSerializationService().deserialize(json);

                // hydrater le jeu avec l'objet GameDTO
                gameViewModel.getGame().updateFrom(dto);

            } catch (Exception e) { // si erreur

                // affiche une alerte de type erreur (rouge)
                services.getDialogService().publishError("Erreur lors du chargement : " + e.getMessage());
            }
        });
    }

    // sauvegarder une partie (dans un fichier)
    public void save() {

        // affiche une fenêtre "Sauvegarder un fichier"
        FileRequest req = services.getDialogService().requestSaveFile("Sauvegarder la partie");

        // quand l'utilisateur aura mis un fichier (ou cliqué sur Annuler) :
        req.getFile().thenAccept(file -> {

            // si l'utilisateur a appuyé sur Annuler
            if (file == null) {
                return; // rien
            }

            // sinon
            try {
                // créer un objet GameDTO à partir des données du jeu
                GameDTO dto = new GameDTO(gameViewModel.getGame());

                // sérialiser l'objet en chaine de caractères
                String json = services.getSerializationService().serialize(dto);

                // écrire la chaine dans le fichier
                Files.writeString(file.toPath(), json);

            } catch (Exception e) { // si erreur
                // affiche une alerte de type erreur (rouge)
                services.getDialogService().publishError("Erreur lors de la sauvegarde : " + e.getMessage());
            }
        });
    }
}