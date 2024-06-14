package chess;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import java.lang.reflect.Type;

import com.google.gson.JsonParseException;

public class ChessGameDeserializer implements JsonDeserializer<ChessGame> {

    @Override
    public ChessGame deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize the team's turn
        String teamTurn = jsonObject.get("teamTurn").getAsString();

        // Deserialize the board
        JsonObject boardObj = jsonObject.getAsJsonObject("board").getAsJsonObject("board");
        ChessBoard chessBoard = new ChessBoard();

        for (String key : boardObj.keySet()) {
            JsonObject pieceObj = boardObj.getAsJsonObject(key);
            String typeStr = pieceObj.get("type").getAsString();
            String color = pieceObj.get("pieceColor").getAsString();
            boolean hasMoved = pieceObj.get("hasMoved").getAsBoolean();

            ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(typeStr.toUpperCase());
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color.toUpperCase());
            ChessPiece chessPiece = new ChessPiece(teamColor, pieceType);

            chessBoard.addPiece(key, chessPiece); // Use string key directly
        }

        ChessGame chessGame = new ChessGame();
        chessGame.setBoard(chessBoard);
        chessGame.setTeamTurn(ChessGame.TeamColor.valueOf(teamTurn.toUpperCase()));

        return chessGame;
    }
}
