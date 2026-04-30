package labyrinth.service.dialog;

public class Message {

    private final String message;
    private final boolean isError;

    public Message(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return isError;
    }
}