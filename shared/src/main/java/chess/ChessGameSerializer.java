package chess;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;

public class ChessGameSerializer implements JsonSerializer<ChessGame> {
    @Override
    public JsonElement serialize(final ChessGame chessGame, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        // Serialize the board
        JsonObject boardObject = new JsonObject();
        Map<String, ChessPiece> board = chessGame.getBoard().getBoard();

        for (Map.Entry<String, ChessPiece> entry : board.entrySet()) {
            ChessPiece piece = entry.getValue();
            if (piece != null) {
                // Serialize each piece
                JsonObject pieceObject = new JsonObject();
                pieceObject.addProperty("pieceColor", piece.getTeamColor().toString());
                pieceObject.addProperty("type", piece.getPieceType().toString());
                pieceObject.addProperty("hasMoved", piece.hasMoved);

                boardObject.add(entry.getKey(), pieceObject); // Use string keys directly
            }
        }
        JsonObject boardWrapper = new JsonObject();
        boardWrapper.add("board", boardObject);
        result.add("board", boardWrapper);

        // Serialize the current team's turn
        result.addProperty("teamTurn", chessGame.getTeamTurn().toString());

        // Include additional fields you'd like to serialize

        return result;
    }
}
