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
    private boolean hasMoved;

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
                break;
            case QUEEN:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
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
                    }
                }
                break;
            case BISHOP:
                for (int dx = -1; dx <= 1; dx += 2) {
                    for (int dy = -1; dy <= 1; dy += 2) {
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
                }
                break;
            case KNIGHT:
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
                break;
            case ROOK:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 || dy == 0) {
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
                    }
                }
                break;
            case PAWN:
                ChessPosition newPosition = this.pieceColor == ChessGame.TeamColor.WHITE ? myPosition.adjust(1, 0) : myPosition.adjust(-1, 0);
                System.out.println("New position under consideration: " + newPosition.getRow() + "," + newPosition.getColumn());
                System.out.println("Is the new position inside the board and unoccupied? " + (newPosition.inBoard() && board.getPiece(newPosition) == null));

                if (newPosition.inBoard() && board.getPiece(newPosition) == null) {
                    if (this.pieceColor == ChessGame.TeamColor.WHITE && newPosition.getRow() == 8
                            || this.pieceColor == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) {
                        for (ChessPiece.PieceType promotionPiece : ChessPiece.PieceType.values()) {
                            if (promotionPiece != PieceType.KING && promotionPiece != PieceType.PAWN){
                                validMoves.add(new ChessMove(myPosition, newPosition, promotionPiece));
                                System.out.println("Added promotion move: " + newPosition.getRow() + "," + newPosition.getColumn() + ", promoted to: " + promotionPiece.name());
                            }
                        }

                    } else {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                        System.out.println("Added: " + newPosition.getRow() + "," + newPosition.getColumn());
                    }

                    boolean isOriginalRow = (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)
                            || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7);
                    System.out.println("Is this the first move? " + isOriginalRow);

                    if (isOriginalRow) {
                        ChessPosition twoSquaresForward = this.pieceColor == ChessGame.TeamColor.WHITE ? myPosition.adjust(2, 0) : myPosition.adjust(-2, 0);
                        System.out.println("Is the en passant position inside the board and unoccupied? " + (twoSquaresForward.inBoard() && board.getPiece(twoSquaresForward) == null));
                        if (twoSquaresForward.inBoard() && board.getPiece(twoSquaresForward) == null) {
                            validMoves.add(new ChessMove(myPosition, twoSquaresForward, null));
                            System.out.println("Added: " + twoSquaresForward.getRow() + "," + twoSquaresForward.getColumn());
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
                                        System.out.println("Added capture promotion move: " + newPosition.getRow() + "," + newPosition.getColumn() + ", promoted to: " + promotionPiece.name());
                                    }

                                }
                            } else {
                                validMoves.add(new ChessMove(myPosition, takePosition, null));
                                System.out.println("Added: " + takePosition.getRow() + "," + takePosition.getColumn());
                            }
                        }
                    }
                }
                break;
        }
        return validMoves;
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
