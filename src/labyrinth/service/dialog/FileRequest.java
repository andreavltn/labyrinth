package labyrinth.service.dialog;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FileRequest {

    private final String request;
    private final Mode mode;
    private final CompletableFuture<File> fileFuture;

    public FileRequest(String request, Mode mode) {
        this.request = request;
        this.mode = mode;
        this.fileFuture = new CompletableFuture<>();
    }

    public String getRequest() {
        return request;
    }

    public Mode getMode() {
        return mode;
    }

    public CompletionStage<File> getFile() {
        return fileFuture;
    }

    public void setFile(File file) {
        fileFuture.complete(file);
    }
}