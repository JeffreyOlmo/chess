package chess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        if (startPosition == null) {
            return null;
        }

        TeamColor origionalTurn = this.teamTurn;
        ChessPiece currentPiece = board.getPiece(startPosition);
        this.teamTurn = currentPiece.getTeamColor();
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessBoard hypotheticalBoard = copyChessBoard(board);
        for (ChessMove move : currentPiece.pieceMoves(hypotheticalBoard, startPosition)) {
            try {
                checkMove(move);
                validMoves.add(move);
            } catch (InvalidMoveException e) {
            }
        }
        this.teamTurn = origionalTurn;
        return validMoves;
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

    public void updateBoard(ChessBoard hypotheticalBoard, ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
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
    }

    public void checkMove(ChessMove move) throws InvalidMoveException {
        ChessBoard hypotheticalBoard = copyChessBoard(this.board);
        updateBoard(hypotheticalBoard, move);
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("No piece at given position " +
                    move.getStartPosition());
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Can't move out of turn!");
        }

        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move attempted from " +
                    move.getStartPosition() + " to " + move.getEndPosition());
        }

        if (board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
            throw new InvalidMoveException("Can't take a king ");
        }

        ChessPosition kingPos = getKingPosition(getTeamTurn(), this.board);
        if (kingInCheck(kingPos, this.board)) {
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

        ChessPosition hypotheticalKingPos = getKingPosition(getTeamTurn(), hypotheticalBoard);
        if (kingInCheck(hypotheticalKingPos, hypotheticalBoard)) {
            throw new InvalidMoveException("Move Leaves King in Check");
        }
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move, boolean isTest) throws InvalidMoveException {
        checkMove(move);
        printBoard(board);
        updateBoard(this.board, move);
        if (!isTest) {
            System.out.println("turn before: " + this.teamTurn);
            this.teamTurn = this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
            System.out.println("turn after: " + this.teamTurn);
        }
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
        return (this.isInStalemateHelper(teamColor) && this.isInCheck(teamColor));
    }




    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemateHelper(TeamColor teamColor) {
        if (this.teamTurn != teamColor){
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor){
                    for (ChessMove move : currentPiece.pieceMoves(board, currentPosition)) {
                        try {
                            System.out.println("\n Test move");
                            makeMove(move, true);
                            return false;
                        } catch (InvalidMoveException e) {

                        }
                    }
                }
            }
        }
        return true;
    }
    public boolean isInStalemate(TeamColor teamColor) {
        return (isInStalemateHelper(teamColor) && !isInCheck(teamColor));
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