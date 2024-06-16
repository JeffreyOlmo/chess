package websocket.messages;

public class ErrorMessage extends SerializableServerMessage {
    public String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
