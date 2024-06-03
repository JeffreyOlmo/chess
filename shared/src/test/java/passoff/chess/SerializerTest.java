package passoff.chess;

import chess.ChessGame;
import chess.ChessGameSerializer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.junit.jupiter.api.Test;

public class SerializerTest {

    @Test
    public void testChessGameSerializer() {
        // Create a sample ChessGame object
        ChessGame chessGame = new ChessGame();

        // Use ChessGameSerializer to serialize it
        ChessGameSerializer serializer = new ChessGameSerializer();
        JsonElement jsonElement = serializer.serialize(chessGame, ChessGame.class, new JsonSerializationContext() {
            public JsonElement serialize(Object src) {
                return new Gson().toJsonTree(src);
            }

            public JsonElement serialize(Object src, java.lang.reflect.Type typeOfSrc) {
                return new Gson().toJsonTree(src, typeOfSrc);
            }
        });
    }
}

