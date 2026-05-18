package labyrinth.service.dialog;

import labyrinth.util.observable.ObservableValue;

public interface DialogService {
    void publishMessage(String message);
    void publishError(String message);
    FileRequest requestOpenFile(String request);
    FileRequest requestSaveFile(String request);
    ObservableValue<Message> messageProperty();
    ObservableValue<FileRequest> fileRequestProperty();
    void requestShowProfilesManager();
    ObservableValue<ProfilesManagerRequest> profilManagerRequestProperty();
}