package labyrinth.viewmodel;

import javafx.collections.ObservableList;
import labyrinth.service.persistence.UserProfile;

public interface UserProfilesViewModel {
  // list of all defined user (in DB)
  ObservableList<UserProfile> getUserProfiles();

  // list of users selected in the current game, they should not be removed from the DB
  ObservableList<UserProfile> getSelectedUserProfiles();

  void createUserProfile(String userName);

  void renameUserProfile(UserProfile userProfile, String userName);

  void deleteUserProfile(UserProfile userProfile);
}
