package labyrinth.service.dialog;

import labyrinth.util.observable.ObservableValue;
import labyrinth.util.observable.SimpleObservableValue;

public class DefaultDialogService implements DialogService {

    private final SimpleObservableValue<Message> message;
    private final SimpleObservableValue<FileRequest> fileRequest;

    public DefaultDialogService() {
        this.message = new SimpleObservableValue<>(null);
        this.fileRequest = new SimpleObservableValue<>(null);
    }

    @Override
    public void publishMessage(String msg) {
        this.message.setValue(new Message(msg, false));
    }

    @Override
    public void publishError(String msg) {
        this.message.setValue(new Message(msg, true));
    }

    @Override
    public FileRequest requestOpenFile(String request) {
        FileRequest req = new FileRequest(request, Mode.OPEN);
        this.fileRequest.setValue(req);
        return req;
    }

    @Override
    public FileRequest requestSaveFile(String request) {
        FileRequest req = new FileRequest(request, Mode.SAVE);
        this.fileRequest.setValue(req);
        return req;
    }

    @Override
    public ObservableValue<Message> messageProperty() {
        return this.message.asReadOnly();
    }

    @Override
    public ObservableValue<FileRequest> fileRequestProperty() {
        return this.fileRequest.asReadOnly();
    }
}