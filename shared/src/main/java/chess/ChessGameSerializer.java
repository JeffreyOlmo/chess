package chess;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;

public class ChessGameSerializer implements JsonSerializer<ChessGame> {
    public JsonElement serialize(final ChessGame chessGame, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        // Serialize the board
        JsonObject boardObject = new JsonObject();
        Map<ChessPosition, ChessPiece> board = chessGame.getBoard().board;

        for (Map.Entry<ChessPosition, ChessPiece> entry : board.entrySet()) {
            ChessPiece piece = entry.getValue();
            if (piece != null) {
                // Maintain the format as in the ChessPlacement class's toString method
                String pieceToJson = String.format("%s:%s", piece.getPieceType(), piece.getTeamColor());
                boardObject.addProperty(entry.getKey().toString(), pieceToJson);
            }
        }
        result.add("board", boardObject);

        // Serialize the current team's turn
        result.addProperty("teamTurn", chessGame.getTeamTurn().toString());

        // Include additional fields you'd like to serialize

        return result;
    }
}