package chess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        this.board.resetBoard();
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null) {
            return null;
        }

        ChessPiece currentPiece = board.getPiece(startPosition);
        if (currentPiece == null || currentPiece.getTeamColor() != teamTurn) {
            return new ArrayList<>();
        }

        return currentPiece.pieceMoves(board, startPosition);
    }

    public boolean isKingInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }

        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == opponentColor) {
                    if (currentPiece.pieceMoves(board, currentPosition).contains(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);

                if (currentPiece != null &&
                        currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        currentPiece.getTeamColor() == teamColor) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("No piece at given position " + move.getStartPosition());
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Can't move out of turn!");
        }

        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move attempted from " +
                    move.getStartPosition() + " to " + move.getEndPosition());
        }

        if (board.getPiece(move.getEndPosition()) != null &&
                board.getPiece(move.getEndPosition()).getTeamColor() == piece.getTeamColor()) {
            throw new InvalidMoveException("Cannot capture own piece at " + move.getEndPosition());
        }

        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);

        if (isKingInCheck(teamTurn)) {
            // Undo the move if it leaves the king in check
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), piece);
            throw new InvalidMoveException("Move leaves the king in check");
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return isKingInCheck(teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && isInStalemate(teamColor, true);
    }

    public boolean isInStalemate(TeamColor teamColor, boolean checkmateCheck) {
        if (!checkmateCheck) {
            if (teamColor != teamTurn) {
                return false;
            }
        }


        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    if (!validMoves(currentPosition).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static ChessGame fromJson(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                .create();
        return gson.fromJson(json, ChessGame.class);
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessGame.class, new ChessGameSerializer())
                .create();
        return gson.toJson(this);
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return this.board;
    }
}