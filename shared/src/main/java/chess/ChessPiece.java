package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    public boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor(){
        return this.pieceColor;
    }

    public PieceType getPieceType(){
        return this.type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        switch (this.type) {
            case KING:
                kingMoves(board, myPosition, validMoves);
                break;
            case QUEEN:
                queenMoves(board, myPosition, validMoves);
                break;
            case BISHOP:
                bishopMoves(board, myPosition, validMoves);
                break;
            case KNIGHT:
                knightMoves(board, myPosition, validMoves);
                break;
            case ROOK:
                rookMoves(board, myPosition, validMoves);
                break;
            case PAWN:
                ChessPosition newPosition = this.pieceColor == ChessGame.TeamColor.WHITE ? myPosition.adjust(1, 0) : myPosition.adjust(-1, 0);

                if (newPosition.inBoard() && board.getPiece(newPosition) == null) {
                    if (this.pieceColor == ChessGame.TeamColor.WHITE && newPosition.getRow() == 8
                            || this.pieceColor == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) {
                        for (ChessPiece.PieceType promotionPiece : ChessPiece.PieceType.values()) {
                            if (promotionPiece != PieceType.KING && promotionPiece != PieceType.PAWN){
                                validMoves.add(new ChessMove(myPosition, newPosition, promotionPiece));
                            }
                        }

                    } else {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }

                    boolean isOriginalRow = (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)
                            || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7);

                    if (isOriginalRow) {
                        ChessPosition twoSquaresForward = this.pieceColor == ChessGame.TeamColor.WHITE ? myPosition.adjust(2, 0) : myPosition.adjust(-2, 0);
                        if (twoSquaresForward.inBoard() && board.getPiece(twoSquaresForward) == null) {
                            validMoves.add(new ChessMove(myPosition, twoSquaresForward, null));
                        }
                    }
                }

                ChessPosition[] takePositions = {
                        this.pieceColor == ChessGame.TeamColor.WHITE ? myPosition.adjust(1, -1) : myPosition.adjust(-1, -1),
                        this.pieceColor == ChessGame.TeamColor.WHITE ? myPosition.adjust(1, 1) : myPosition.adjust(-1, 1)
                };

                for (ChessPosition takePosition : takePositions) {
                    if (takePosition.inBoard()) {
                        ChessPiece potentialTarget = board.getPiece(takePosition);
                        if (potentialTarget != null && potentialTarget.getTeamColor() != this.pieceColor) {
                            // Checking if the new position is a promotion rank.
                            if (this.pieceColor == ChessGame.TeamColor.WHITE && takePosition.getRow() == 8
                                    || this.pieceColor == ChessGame.TeamColor.BLACK && takePosition.getRow() == 1) {
                                for (ChessPiece.PieceType promotionPiece : ChessPiece.PieceType.values()) {
                                    if (promotionPiece != PieceType.KING && promotionPiece != PieceType.PAWN){
                                        validMoves.add(new ChessMove(myPosition, takePosition, promotionPiece));
                                    }

                                }
                            } else {
                                validMoves.add(new ChessMove(myPosition, takePosition, null));
                            }
                        }
                    }
                }
                break;
        }
        return validMoves;
    }

    public void kingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                ChessPosition newPosition = myPosition.adjust(dx, dy);
                if (newPosition.inBoard()) {
                    ChessPiece otherPiece = board.getPiece(newPosition);
                    if (otherPiece != null) {
                        if (otherPiece.getTeamColor() != this.pieceColor) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }
    }

    public void queenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    incrementPosition(board, myPosition, validMoves, dx, dy);
                }
            }
        }
    }

    public void bishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                incrementPosition(board, myPosition, validMoves, dx, dy);
            }
        }
    }

    public void knightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (Math.abs(dx) != Math.abs(dy) && Math.abs(dx) + Math.abs(dy) == 3) {
                    ChessPosition newPosition = myPosition.adjust(dx, dy);
                    if (newPosition.inBoard()) {
                        if (board.getPiece(newPosition) != null) {
                            if (board.getPiece(newPosition).getTeamColor() != this.pieceColor) {
                                validMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                            continue;
                        } else {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }
        }
    }

    public void rookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves){
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 || dy == 0) {
                    incrementPosition(board, myPosition, validMoves, dx, dy);
                }
            }
        }
    }

    private void incrementPosition(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int dx, int dy) {
        ChessPosition newPosition = myPosition.adjust(dx, dy);
        while (newPosition.inBoard()) {
            ChessPiece otherPiece = board.getPiece(newPosition);
            if (otherPiece != null) {
                if (otherPiece.getTeamColor() != this.pieceColor) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(myPosition, newPosition, null));
            newPosition = newPosition.adjust(dx, dy);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return hasMoved == that.hasMoved && pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, hasMoved);
    }
}
