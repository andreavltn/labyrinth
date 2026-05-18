package labyrinth.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import labyrinth.model.Game;
import labyrinth.model.Player;
import labyrinth.service.persistence.SqlPersistenceService;
import labyrinth.service.persistence.UserProfile;
import java.util.UUID;

public class BaseUserProfilesViewModel implements UserProfilesViewModel {

    private final SqlPersistenceService persistenceService;
    private final Game game;

    // liste observable de tous les profils de la bdd
    private final ObservableList<UserProfile> userProfiles = FXCollections.observableArrayList();

    // liste observable des profils sélectionnés de la partie en cours
    private final ObservableList<UserProfile> selectedUserProfiles = FXCollections.observableArrayList();

    public BaseUserProfilesViewModel(SqlPersistenceService persistenceService, Game game) {
        this.persistenceService = persistenceService;
        this.game = game;

        // charge tous les profils
        refreshProfiles();

        // si un profil change en bdd : recharger les profils
        // (changer = ajout/suppression/renommage)
        persistenceService.addHandler(playerId -> refreshProfiles());

        // pour chaque slot
        for (int i = 0; i < 4; i++) {
            // si le joueur associé change (ajout ou suppression)
            game.getSlot(i).playerProperty().addListener((obs, old, player) -> {
                refreshSelectedProfiles(); // recharge les profils sélectionnés
            });
        }

        // charge les profils sélectionnés
        refreshSelectedProfiles();
    }

    // recharge tous les profils depuis la BDD
    private void refreshProfiles() {
        userProfiles.setAll(persistenceService.getUserProfiles()); // récupère la liste des profils
        refreshSelectedProfiles(); // recharge aussi les profils sélectionnés
    }

    // recharge les profils sélectionnés à partir des joueurs dans les slots
    private void refreshSelectedProfiles() {

        // vide la liste des joueurs sélectionnés
        selectedUserProfiles.clear();

        for (int i = 0; i < 4; i++) { // pour chaque slot
            Player player = game.getSlot(i).getPlayer(); // récupère le joueur associé
            if (player != null) { // si le joueur existe
                UUID uuid = player.getUuid(); // récupère son uuid
                if (uuid != null) { // si l'uuid existe
                    UserProfile profile = persistenceService.getUserProfile(uuid); // récupère son profil
                    if (profile != null) { // si le profil existe
                        selectedUserProfiles.add(profile); // l'ajoute aux profils sélectionnés
                    }
                }
            }
        }
    }

    @Override
    public ObservableList<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    @Override
    public ObservableList<UserProfile> getSelectedUserProfiles() {
        return selectedUserProfiles;
    }

    @Override // crée un profil utilisateur
    public void createUserProfile(String userName) {
        persistenceService.createUserProfile(userName);
    }

    @Override // renomme un profil utilisateur
    public void renameUserProfile(UserProfile userProfile, String userName) {
        persistenceService.setUserName(userProfile.uuid(), userName);
    }

    @Override // supprime un profil utilisateur
    public void deleteUserProfile(UserProfile userProfile) {
        persistenceService.deleteUserProfile(userProfile.uuid());
    }
}