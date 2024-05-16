package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
    }


    public boolean kingInCheck(ChessPosition kingPosition, ChessBoard checkBoard) {
        ChessPiece king = checkBoard.getPiece(kingPosition);
        System.out.println("Is the king position actually occupied and is it actually a king " + (king != null && king.getPieceType() == ChessPiece.PieceType.KING));
        if(king != null && king.getPieceType() == ChessPiece.PieceType.KING) {
            ChessGame.TeamColor kingColor = king.getTeamColor();
            ChessGame.TeamColor enemyColor = (kingColor == ChessGame.TeamColor.WHITE)? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

            // Iterate the entire board
            for(int row = 1; row <= 8; row++) {
                for(int col = 1; col <= 8; col++) {
                    ChessPosition currentPosition = new ChessPosition(row, col);
                    ChessPiece currentPiece = checkBoard.getPiece(currentPosition);

                    // Check if the spot has a piece of the enemy color.
                    if(currentPiece != null && currentPiece.getTeamColor() == enemyColor) {
                        // Check if this enemy piece has a move that can capture the king
                        Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(checkBoard, currentPosition);
                        for(ChessMove move: possibleMoves) {
                            if(move.getEndPosition().equals(kingPosition)) {
                                System.out.println(currentPiece.getPieceType() + "can take the king");
                                return true; // King can be captured
                            }
                        }
                        System.out.println(currentPiece.getPieceType() + "can't take the king");
                    }
                }
            }
        }
        return false; // if we get here, King is not in check
    }

    public ChessBoard copyChessBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if (original.getPiece(position) != null) {
                    ChessPiece copyPiece = new ChessPiece(original.getPiece(position).getTeamColor(),
                            original.getPiece(position).getPieceType());
                    copy.addPiece(position, copyPiece);  // Deep copy
                }
            }
        }
        return copy;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color, ChessBoard checkBoard) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = checkBoard.getPiece(currentPosition);

                if (currentPiece != null &&
                        currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        currentPiece.getTeamColor() == color) {

                    return currentPosition;
                }
            }
        }
        return null;  // If a king of the given color is not found
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece at given position " +
                    move.getStartPosition());
        }

        if (board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Can't move out of turn!");
        }

        ChessPiece piece = board.getPiece(move.getStartPosition());
        System.out.println("This move is " + piece.getPieceType() +
                " moving to " + move.getEndPosition().getRow() + " , " + move.getEndPosition().getColumn());
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move attempted from " +
                    move.getStartPosition() + " to " + move.getEndPosition());
        }

        if (board.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.KING) {
            ChessBoard hypotheticalBoard = copyChessBoard(this.board);
            //make the hypothetical move
            if (move.getPromotionPiece() == null) {
                hypotheticalBoard.removePiece(move.getStartPosition());

                if (hypotheticalBoard.getPiece(move.getEndPosition()) != null) {
                    hypotheticalBoard.removePiece(move.getEndPosition());
                }

                hypotheticalBoard.addPiece(move.getEndPosition(), piece);

            } else {
                hypotheticalBoard.removePiece(move.getStartPosition());
                if (hypotheticalBoard.getPiece(move.getEndPosition()) != null) {
                    hypotheticalBoard.removePiece(move.getEndPosition());
                }
                ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                hypotheticalBoard.addPiece(move.getEndPosition(), promotedPiece);
            }
            if (kingInCheck(move.getEndPosition(), hypotheticalBoard)) {
                throw new InvalidMoveException("King can't move into check from " +
                        move.getStartPosition() + " to " + move.getEndPosition());
            }
        }

        if (board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
            throw new InvalidMoveException("Can't take a king ");
        }

        ChessPosition kingPos = getKingPosition(getTeamTurn(), this.board);
        System.out.println(kingInCheck(kingPos, this.board));
        if (kingInCheck(kingPos, this.board)) {
            ChessBoard hypotheticalBoard = copyChessBoard(this.board);
            //make the hypothetical move
            if (move.getPromotionPiece() == null) {
                hypotheticalBoard.removePiece(move.getStartPosition());

                if (hypotheticalBoard.getPiece(move.getEndPosition()) != null) {
                    hypotheticalBoard.removePiece(move.getEndPosition());
                }

                hypotheticalBoard.addPiece(move.getEndPosition(), piece);

            } else {
                hypotheticalBoard.removePiece(move.getStartPosition());
                if (hypotheticalBoard.getPiece(move.getEndPosition()) != null) {
                    hypotheticalBoard.removePiece(move.getEndPosition());
                }
                ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                hypotheticalBoard.addPiece(move.getEndPosition(), promotedPiece);
            }

            //end real version

            printBoard(hypotheticalBoard);
            System.out.println("Checking the hypothetical where king is at" + getKingPosition(getTeamTurn(), hypotheticalBoard).getColumn() +", "+getKingPosition(getTeamTurn(), hypotheticalBoard).getRow());
            System.out.println("Is king in check after move? " + kingInCheck(getKingPosition(getTeamTurn(), hypotheticalBoard), hypotheticalBoard));
            System.out.println("King is at: " + getKingPosition(getTeamTurn(), hypotheticalBoard).getColumn() + ", "+getKingPosition(getTeamTurn(), hypotheticalBoard).getRow());
            if (kingInCheck(getKingPosition(getTeamTurn(), hypotheticalBoard), hypotheticalBoard)){
                throw new InvalidMoveException("King is still in check after move from " +
                        move.getStartPosition() + " to " + move.getEndPosition());
            }
        }

        if (board.getPiece(move.getEndPosition()) != null &&
                board.getPiece(move.getEndPosition()).getTeamColor() == piece.getTeamColor()) {
            throw new InvalidMoveException("Cannot capture own piece at " +
                    move.getEndPosition());
        }
        printBoard(board);
        if (move.getPromotionPiece() == null) {
            board.removePiece(move.getStartPosition());

            if (board.getPiece(move.getEndPosition()) != null) {
                board.removePiece(move.getEndPosition());
            }

            board.addPiece(move.getEndPosition(), piece);
            printBoard(board);

        } else {
            board.removePiece(move.getStartPosition());
            if (board.getPiece(move.getEndPosition()) != null) {
                board.removePiece(move.getEndPosition());
            }
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPiece);
        }
        this.teamTurn = this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public void printBoard(ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPos = new ChessPosition(row, col);
                if (board.getPiece(currentPos) != null) {
                    System.out.println(board.getPiece(currentPos).getPieceType() +
                            " at " + currentPos.getColumn() + "," + currentPos.getRow());
                }
            }
        }
        System.out.println("_________");
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return kingInCheck(getKingPosition(teamColor, this.board), this.board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (this.isInStalemate(teamColor) && this.isInCheck(teamColor));
    }




    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (this.teamTurn != teamColor ){
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor){
                    System.out.println("Trying if "+currentPiece.getPieceType() + "can move");
                    for (ChessMove move : currentPiece.pieceMoves(board, currentPosition)) {
                        try {
                            makeMove(move);
                            System.out.println(currentPiece.getPieceType() + " can move " +
                                    move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn());
                            return false;
                        } catch (InvalidMoveException e) {

                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = copyChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
