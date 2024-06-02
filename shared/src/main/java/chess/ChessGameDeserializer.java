package chess;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import java.lang.reflect.Type;

import com.google.gson.JsonParseException;

class ChessGameDeserializer implements JsonDeserializer<ChessGame> {

    @Override
    public ChessGame deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize the team's turn
        String teamTurn = jsonObject.get("teamTurn").getAsString();

        // Deserialize the board
        JsonObject boardObj = jsonObject.getAsJsonObject("board");
        ChessBoard chessBoard = new ChessBoard();

        for (String key : boardObj.keySet()) {
            // Convert the key back into a ChessPosition
            int row = Character.getNumericValue(key.charAt(0));
            int col = Character.getNumericValue(key.charAt(1));
            ChessPosition chessPosition = new ChessPosition(row, col);

            // Convert the value back into a ChessPiece
            String pieceString = boardObj.get(key).getAsString();
            String[] splitPieceString = pieceString.split(":");
            ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(splitPieceString[0]);
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(splitPieceString[1]);
            ChessPiece chessPiece = new ChessPiece(teamColor, pieceType);

            // Add the piece back to the board at the appropriate position
            chessBoard.addPiece(chessPosition, chessPiece);
        }
        // Create the new ChessGame instance
        ChessGame chessGame = new ChessGame();
        chessGame.setBoard(chessBoard);
        chessGame.setTeamTurn(ChessGame.TeamColor.valueOf(teamTurn));

        // Include additional fields you'd like to deserialize

        return chessGame;
    }
}