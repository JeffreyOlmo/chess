package websocketmessages.servermessages;


public class NotificationMessage extends SerializableServerMessage {
    public String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
