package chess;

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
                                return true; // King can be captured
                            }
                        }
                    }
                }
            }
        }
        return false; // if we get here, King is not in check
    }

    public ChessBoard copyChessBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
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

    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);

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

        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move attempted from " +
                    move.getStartPosition() + " to " + move.getEndPosition());
        }

        if (board.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.KING ||
                kingInCheck(move.getEndPosition(), this.board)) {
            throw new InvalidMoveException("King can't move into check from " +
                    move.getStartPosition() + " to " + move.getEndPosition());
        }

        ChessPosition kingPos = getKingPosition(getTeamTurn());
        if (kingInCheck(kingPos, this.board)) {
            System.out.println("This team's king is in check at the start");
            ChessBoard hypotheticalBoard = copyChessBoard(this.board);
            //make the hypothetical move
            hypotheticalBoard.removePiece(move.getStartPosition());
            if (hypotheticalBoard.getPiece(move.getEndPosition()) != null) {
                hypotheticalBoard.removePiece(move.getEndPosition());
            }
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            hypotheticalBoard.addPiece(move.getEndPosition(), promotedPiece);

            if (kingInCheck(kingPos, hypotheticalBoard)){
                System.out.println("This team's king is still in check after the move");
                throw new InvalidMoveException("King is still in check after move from " +
                        move.getStartPosition() + " to " + move.getEndPosition());
            }
        }

        if (board.getPiece(move.getEndPosition()) != null &&
                board.getPiece(move.getEndPosition()).getTeamColor() == piece.getTeamColor()) {
            throw new InvalidMoveException("Cannot capture own piece at " +
                    move.getEndPosition());
        }

        if (move.getPromotionPiece() == null) {
            board.removePiece(move.getStartPosition());

            if (board.getPiece(move.getEndPosition()) != null) {
                board.removePiece(move.getEndPosition());
            }

            board.addPiece(move.getEndPosition(), piece);
        } else {
            board.removePiece(move.getStartPosition());
            if (board.getPiece(move.getEndPosition()) != null) {
                board.removePiece(move.getEndPosition());
            }
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPiece);
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return kingInCheck(getKingPosition(teamColor), this.board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
//    public boolean isInCheckmate(TeamColor teamColor) {
//        if (!kingInCheck(getKingPosition(teamColor), this.board)){
//            return false;
//        }
//        noValidMoves = false;
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition currentPosition = new ChessPosition(row, col);
//                ChessPiece currentPiece = board.getPiece(currentPosition);
//                ChessMove move = new ChessMove(currentPosition)
//                if (makeMove)
//            }
//        }
//        return (kingInCheck(getKingPosition(teamColor), this.board) ||
//    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
