package chess;

import java.util.HashMap;
import java.util.Map;

public class ChessBoard {

    private Map<ChessPosition, ChessPiece> board;

    public ChessBoard() {
        this.board = new HashMap<>();
        resetBoard();
    }

    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    public ChessPiece getPiece(ChessPosition position) {
        return board.get(position);
    }

    public void removePiece(ChessPosition position) {
        board.remove(position);
    }

    public void resetBoard() {
        board.clear();

        // Setup pieces for WHITE
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessPiece.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // Setup pieces for BLACK
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessPiece.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return board.equals(that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board);
    }
}