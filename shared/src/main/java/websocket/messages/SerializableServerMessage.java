package websocket.messages;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

public class SerializableServerMessage extends ServerMessage {

    public SerializableServerMessage(ServerMessageType type) {
        super(type);
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
