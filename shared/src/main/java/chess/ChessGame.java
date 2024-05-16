//package chess;
//
//import java.util.ArrayList;
//import java.util.Collection;
////            printBoard(hypotheticalBoard);
////            System.out.println("Checking the hypothetical where king is at" + getKingPosition(getTeamTurn(), hypotheticalBoard).getColumn() +", "+getKingPosition(getTeamTurn(), hypotheticalBoard).getRow());
////            System.out.println("Is king in check after move? " + kingInCheck(getKingPosition(getTeamTurn(), hypotheticalBoard), hypotheticalBoard));
////            System.out.println("King is at: " + getKingPosition(getTeamTurn(), hypotheticalBoard).getColumn() + ", "+getKingPosition(getTeamTurn(), hypotheticalBoard).getRow());
///**
// * For a class that can manage a chess game, making moves on a board
// * <p>
// * Note: You can add to this class, but you may not alter
// * signature of the existing methods.
// */
//public class ChessGame {
//
//    private ChessBoard board;
//    private TeamColor teamTurn;
//    private boolean whiteKingHasMoved;
//    private boolean whiteRook1HasMoved;
//    private boolean whiteRook2HasMoved;
//    private boolean blackKingHasMoved;
//    private boolean blackRook1HasMoved;
//    private boolean blackRook2HasMoved;
//    public ChessGame() {
//        this.board = new ChessBoard();
//        this.teamTurn = TeamColor.WHITE;
//        this.board.resetBoard();
//        this.whiteKingHasMoved = false;
//        this.whiteRook1HasMoved = false;
//        this.whiteRook2HasMoved = false;
//        this.blackKingHasMoved = false;
//        this.blackRook1HasMoved = false;
//        this.blackRook2HasMoved = false;
//    }
//
//    /**
//     * @return Which team's turn it is
//     */
//    public TeamColor getTeamTurn() {
//        return teamTurn;
//    }
//
//    /**
//     * Set's which teams turn it is
//     *
//     * @param team the team whose turn it is
//     */
//    public void setTeamTurn(TeamColor team) {
//        teamTurn = team;
//    }
//
//    /**
//     * Enum identifying the 2 possible teams in a chess game
//     */
//    public enum TeamColor {
//        WHITE,
//        BLACK
//    }
//
//    /**
//     * Gets a valid moves for a piece at the given location
//     *
//     * @param startPosition the piece to get valid moves for
//     * @return Set of valid moves for requested piece, or null if no piece at
//     * startPosition
//     */
//    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
//        if (startPosition == null) {
//            return null;
//        }
//
//        TeamColor origionalTurn = this.teamTurn;
//        ChessPiece currentPiece = board.getPiece(startPosition);
//        this.teamTurn = currentPiece.getTeamColor();
//        Collection<ChessMove> validMoves = new ArrayList<>();
//
//        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING || currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
//            addCastling(validMoves, currentPiece, startPosition);
//            System.out.println(validMoves);
//        }
//
//        ChessBoard hypotheticalBoard = copyChessBoard(board);
//        for (ChessMove move : currentPiece.pieceMoves(hypotheticalBoard, startPosition)) {
//            try {
//                checkMove(move);
//                validMoves.add(move);
//            } catch (InvalidMoveException e) {
//                System.out.println(e);
//            }
//        }
//        this.teamTurn = origionalTurn;
//        return validMoves;
//    }
//
//    public void addCastling(Collection<ChessMove> moves, ChessPiece piece, ChessPosition start) {
//        // validate whether the king and relevant rook have not moved, and the pathway is clear for castling
//        // Check for white pieces
//        boolean whiteKingAtOriginalPosition = board.getPiece(new ChessPosition(1, 5)) != null
//                && board.getPiece(new ChessPosition(1, 5)).getPieceType() == ChessPiece.PieceType.KING
//                && board.getPiece(new ChessPosition(1, 5)).getTeamColor() == ChessGame.TeamColor.WHITE;
//
//        boolean whiteRook1AtOriginalPosition = board.getPiece(new ChessPosition(1, 1)) != null
//                && board.getPiece(new ChessPosition(1, 1)).getPieceType() == ChessPiece.PieceType.ROOK
//                && board.getPiece(new ChessPosition(1, 1)).getTeamColor() == ChessGame.TeamColor.WHITE;
//
//        boolean whiteRook2AtOriginalPosition = board.getPiece(new ChessPosition(1, 8)) != null
//                && board.getPiece(new ChessPosition(1, 8)).getPieceType() == ChessPiece.PieceType.ROOK
//                && board.getPiece(new ChessPosition(1, 8)).getTeamColor() == ChessGame.TeamColor.WHITE;
//
//        // Check for black pieces
//        boolean blackKingAtOriginalPosition = board.getPiece(new ChessPosition(8, 5)) != null
//                && board.getPiece(new ChessPosition(8, 5)).getPieceType() == ChessPiece.PieceType.KING
//                && board.getPiece(new ChessPosition(8, 5)).getTeamColor() == ChessGame.TeamColor.BLACK;
//
//        boolean blackRook1AtOriginalPosition = board.getPiece(new ChessPosition(8, 1)) != null
//                && board.getPiece(new ChessPosition(8, 1)).getPieceType() == ChessPiece.PieceType.ROOK
//                && board.getPiece(new ChessPosition(8, 1)).getTeamColor() == ChessGame.TeamColor.BLACK;
//
//        boolean blackRook2AtOriginalPosition = board.getPiece(new ChessPosition(8, 8)) != null
//                && board.getPiece(new ChessPosition(8, 8)).getPieceType() == ChessPiece.PieceType.ROOK
//                && board.getPiece(new ChessPosition(8, 8)).getTeamColor() == ChessGame.TeamColor.BLACK;
//        System.out.println("Has black king moved? " + (!blackKingHasMoved && blackKingAtOriginalPosition));
//        if (!blackKingHasMoved && blackKingAtOriginalPosition) {
//            System.out.println("Black can castle and the black king is in original position");
//            // Kingside (Short) Castling
//            boolean blackKingSideCastling = board.getPiece(new ChessPosition(8, 7)) == null
//                    && board.getPiece(new ChessPosition(8, 6)) == null;
//
//            // Queenside (Long) Castling
//            boolean blackQueenSideCastling = board.getPiece(new ChessPosition(8, 2)) == null
//                    && board.getPiece(new ChessPosition(8, 3)) == null
//                    && board.getPiece(new ChessPosition(8, 4)) == null;
//
//
//            // Kingside (Short) Castling for black
//            ChessMove blackKingMovesKingSide = new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null);
//            ChessMove blackRookMovesKingSide = new ChessMove(new ChessPosition(8, 8), new ChessPosition(8, 6), null);
//
//            // Queenside (Long) Castling for black
//            ChessMove blackKingMovesQueenSide = new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null);
//            ChessMove blackRookMovesQueenSide = new ChessMove(new ChessPosition(8, 1), new ChessPosition(8, 4), null);
//            if (blackKingSideCastling && blackRook2AtOriginalPosition && !blackRook1HasMoved){
//                System.out.println("No intervening pieces on black kingside and the 2nd rook is in original position");
//                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
//                    System.out.println("add kingside castle to king's validMoves");
//                    moves.add(blackKingMovesKingSide);
//                } else {
//                    if (start.getRow() == 8 && start.getColumn() == 8){
//                        System.out.println("add kingside castle to rook 2's validMoves");
//                        moves.add(blackRookMovesKingSide);
//                    }
//                }
//            } if(blackQueenSideCastling && blackRook1AtOriginalPosition && !blackRook2HasMoved){
//                System.out.println("No intervening pieces on black queenside and the 1st rook is in original position");
//                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
//                    System.out.println("add queenside castle to king's validMoves");
//                    moves.add(blackKingMovesQueenSide);
//                } else{
//                    if (start.getRow() == 8 && start.getColumn() == 1){
//                        System.out.println("add queenside castle to rook 1's validMoves");
//                        moves.add(blackRookMovesQueenSide);
//                    }
//                }
//            }
//        }
//        System.out.println(!whiteKingHasMoved);
//        System.out.println("Has white king moved? " + (!whiteKingHasMoved && whiteKingAtOriginalPosition));
//        if (!whiteKingHasMoved && whiteKingAtOriginalPosition) {
//            System.out.println("White can castle and the white king is in original position");
//            // Kingside (Short) Castling
//            boolean whiteKingSideCastling = board.getPiece(new ChessPosition(1, 7)) == null
//                    && board.getPiece(new ChessPosition(1, 6)) == null;
//
//            // Queenside (Long) Castling
//            boolean whiteQueenSideCastling = board.getPiece(new ChessPosition(1, 2)) == null
//                    && board.getPiece(new ChessPosition(1, 3)) == null
//                    && board.getPiece(new ChessPosition(1, 4)) == null;
//
//            // Kingside (Short) Castling for white
//            ChessMove whiteKingMovesKingSide = new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null);
//            ChessMove whiteRookMovesKingSide = new ChessMove(new ChessPosition(1, 8), new ChessPosition(1, 6), null);
//
//            // Queenside (Long) Castling for white
//            ChessMove whiteKingMovesQueenSide = new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null);
//            ChessMove whiteRookMovesQueenSide = new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 4), null);
//
//            if (whiteKingSideCastling && whiteRook2AtOriginalPosition && !whiteRook2HasMoved){
//                System.out.println("No intervening pieces on white kingside and the 2nd rook is in original position");
//                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                    moves.add(whiteKingMovesKingSide);
//                } else{
//                    if (start.getRow() == 1 && start.getColumn() == 8){
//                        moves.add(whiteRookMovesKingSide);
//                    }
//                }
//            } if(whiteQueenSideCastling && whiteRook1AtOriginalPosition && !whiteRook1HasMoved){
//                System.out.println("No intervening pieces on white queenside and the 1st rook is in original position");
//                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                    moves.add(whiteKingMovesQueenSide);
//                } else{
//                    if (start.getRow() == 1 && start.getColumn() == 1){
//                        moves.add(whiteRookMovesQueenSide);
//                    }
//                }
//            }
//        }
//    }
//
//    public boolean kingInCheck(ChessPosition kingPosition, ChessBoard checkBoard) {
//        ChessPiece king = checkBoard.getPiece(kingPosition);
//        System.out.println("Is the king position actually occupied and is it actually a king " + (king != null && king.getPieceType() == ChessPiece.PieceType.KING));
//        if(king != null && king.getPieceType() == ChessPiece.PieceType.KING) {
//            ChessGame.TeamColor kingColor = king.getTeamColor();
//            ChessGame.TeamColor enemyColor = (kingColor == ChessGame.TeamColor.WHITE)? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
//
//            // Iterate the entire board
//            for(int row = 1; row <= 8; row++) {
//                for(int col = 1; col <= 8; col++) {
//                    ChessPosition currentPosition = new ChessPosition(row, col);
//                    ChessPiece currentPiece = checkBoard.getPiece(currentPosition);
//
//                    // Check if the spot has a piece of the enemy color.
//                    if(currentPiece != null && currentPiece.getTeamColor() == enemyColor) {
//                        // Check if this enemy piece has a move that can capture the king
//                        Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(checkBoard, currentPosition);
//                        for(ChessMove move: possibleMoves) {
//                            if(move.getEndPosition().equals(kingPosition)) {
//                                System.out.println(currentPiece.getPieceType() + "can take the king");
//                                return true; // King can be captured
//                            }
//                        }
//                        System.out.println(currentPiece.getPieceType() + "can't take the king");
//                    }
//                }
//            }
//        }
//        return false; // if we get here, King is not in check
//    }
//
//    public ChessBoard copyChessBoard(ChessBoard original) {
//        ChessBoard copy = new ChessBoard();
//        for (int i = 1; i <= 8; i++) {
//            for (int j = 1; j <= 8; j++) {
//                ChessPosition position = new ChessPosition(i,j);
//                if (original.getPiece(position) != null) {
//                    ChessPiece copyPiece = new ChessPiece(original.getPiece(position).getTeamColor(),
//                            original.getPiece(position).getPieceType());
//                    copy.addPiece(position, copyPiece);  // Deep copy
//                }
//            }
//        }
//        return copy;
//    }
//
//    public ChessPosition getKingPosition(ChessGame.TeamColor color, ChessBoard checkBoard) {
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition currentPosition = new ChessPosition(row, col);
//                ChessPiece currentPiece = checkBoard.getPiece(currentPosition);
//
//                if (currentPiece != null &&
//                        currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
//                        currentPiece.getTeamColor() == color) {
//
//                    return currentPosition;
//                }
//            }
//        }
//        return null;  // If a king of the given color is not found
//    }
//
//    public void updateBoard(ChessBoard hypotheticalBoard, ChessMove move, boolean enPassant, boolean real) {
//        ChessPiece piece = hypotheticalBoard.getPiece(move.getStartPosition());
//
//        hypotheticalBoard.removePiece(move.getStartPosition());
//        removePieceAtDestinationIfPresent(hypotheticalBoard, move.getEndPosition());
//
//        if (enPassant) {
//            // En passant capture
//            ChessPosition enPassantCapturePosition = new ChessPosition(
//                    move.getStartPosition().getRow(), // Same row as the current piece
//                    move.getEndPosition().getColumn() // But the column of the captured piece
//            );
//            hypotheticalBoard.removePiece(enPassantCapturePosition);
//        } else if (move.getPromotionPiece() != null) {
//            // Promotion
//            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
//        }
////        // Kingside (Short) Castling for white
////        ChessMove whiteKingMovesKingSide = new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null);
////        ChessMove whiteRookMovesKingSide = new ChessMove(new ChessPosition(1, 8), new ChessPosition(1, 6), null);
////
////        // Queenside (Long) Castling for white
////        ChessMove whiteKingMovesQueenSide = new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null);
////        ChessMove whiteRookMovesQueenSide = new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 4), null);
////
////        // Kingside (Short) Castling for black
////        ChessMove blackKingMovesKingSide = new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null);
////        ChessMove blackRookMovesKingSide = new ChessMove(new ChessPosition(8, 8), new ChessPosition(8, 6), null);
////
////        // Queenside (Long) Castling for black
////        ChessMove blackKingMovesQueenSide = new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null);
////        ChessMove blackRookMovesQueenSide = new ChessMove(new ChessPosition(8, 1), new ChessPosition(8, 4), null);
////
////        if(move.equals(whiteKingMovesKingSide)) {
////            updateBoard(hypotheticalBoard, whiteRookMovesKingSide, enPassant, real);
////        }
////        if(move.equals(whiteKingMovesQueenSide)) {
////            updateBoard(hypotheticalBoard, whiteRookMovesQueenSide, enPassant, real);
////        }
////        if(move.equals(blackKingMovesKingSide)) {
////            updateBoard(hypotheticalBoard, blackRookMovesKingSide, enPassant, real);
////        }
////        if(move.equals(blackKingMovesQueenSide)) {
////            updateBoard(hypotheticalBoard, blackRookMovesQueenSide, enPassant, real);
////        }
//
//        if(real){
//            if (piece.getPieceType() == ChessPiece.PieceType.KING){
//                if (piece.getTeamColor() == TeamColor.BLACK) {
//                    this.blackKingHasMoved = true;
//                }
//                else{
//                    this.whiteKingHasMoved = true;
//                }
//            }
////            if (move.getStartPosition().equals(new ChessPosition(1, 1))) {
////                this.whiteRook1HasMoved = true;
////            }
////            if (move.getStartPosition().equals(new ChessPosition(1, 8))) {
////                this.whiteRook2HasMoved = true;
////            }
////            if (move.getStartPosition().equals(new ChessPosition(8, 1))) {
////                this.blackRook1HasMoved = true;
////            }
////            if (move.getStartPosition().equals(new ChessPosition(8, 8))) {
////                this.blackRook2HasMoved = true;
////            }
//
//            hypotheticalBoard.addPiece(move.getEndPosition(), piece);
//        }
//    }
//
//
//
//    private void removePieceAtDestinationIfPresent(ChessBoard hypotheticalBoard, ChessPosition endPosition) {
//        if (hypotheticalBoard.getPiece(endPosition) != null) {
//            hypotheticalBoard.removePiece(endPosition);
//        }
//    }
//
//    public void checkMove(ChessMove move) throws InvalidMoveException {
//        if (board.getPiece(move.getStartPosition()) == null) {
//            throw new InvalidMoveException("No piece at given position " +
//                    move.getStartPosition());
//        }
//
//        ChessBoard hypotheticalBoard = copyChessBoard(this.board);
//        updateBoard(hypotheticalBoard, move, false, false);
//        ChessPiece piece = board.getPiece(move.getStartPosition());
//
//        if (piece.getTeamColor() != teamTurn) {
//            throw new InvalidMoveException("Can't move out of turn!");
//        }
//
//        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
//            throw new InvalidMoveException("Invalid move attempted from " +
//                    move.getStartPosition() + " to " + move.getEndPosition());
//        }
//
//        if (board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
//            throw new InvalidMoveException("Can't take a king ");
//        }
//
//        ChessPosition kingPos = getKingPosition(getTeamTurn(), this.board);
//        if (kingInCheck(kingPos, this.board)) {
//            if (kingInCheck(getKingPosition(getTeamTurn(), hypotheticalBoard), hypotheticalBoard)){
//                throw new InvalidMoveException("King is still in check after move from " +
//                        move.getStartPosition() + " to " + move.getEndPosition());
//            }
//        }
//
//        if (board.getPiece(move.getEndPosition()) != null &&
//                board.getPiece(move.getEndPosition()).getTeamColor() == piece.getTeamColor()) {
//            throw new InvalidMoveException("Cannot capture own piece at " +
//                    move.getEndPosition());
//        }
//
//        ChessPosition hypotheticalKingPos = getKingPosition(getTeamTurn(), hypotheticalBoard);
//        if (kingInCheck(hypotheticalKingPos, hypotheticalBoard)) {
//            throw new InvalidMoveException("Move Leaves King in Check");
//        }
//    }
//    /**
//     * Makes a move in a chess game
//     *
//     * @param move chess move to preform
//     * @throws InvalidMoveException if move is invalid
//     */
//    public void makeMove(ChessMove move) throws InvalidMoveException {
//        checkMove(move);
//        updateBoard(this.board, move, false, true);
//        this.teamTurn = this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
//    }
//
//
//    public void printBoard(ChessBoard board) {
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition currentPos = new ChessPosition(row, col);
//                if (board.getPiece(currentPos) != null) {
//                    System.out.println(board.getPiece(currentPos).getPieceType() +
//                            " at " + currentPos.getColumn() + "," + currentPos.getRow());
//                }
//            }
//        }
//        System.out.println("_________");
//    }
//
//
//    /**
//     * Determines if the given team is in check
//     *
//     * @param teamColor which team to check for check
//     * @return True if the specified team is in check
//     */
//    public boolean isInCheck(TeamColor teamColor) {
//        return kingInCheck(getKingPosition(teamColor, this.board), this.board);
//    }
//
//    /**
//     * Determines if the given team is in checkmate
//     *
//     * @param teamColor which team to check for checkmate
//     * @return True if the specified team is in checkmate
//     */
//    public boolean isInCheckmate(TeamColor teamColor) {
//        return (this.isInStalemate(teamColor) && this.isInCheck(teamColor));
//    }
//
//    /**
//     * Determines if the given team is in stalemate, which here is defined as having
//     * no valid moves
//     *
//     * @param teamColor which team to check for stalemate
//     * @return True if the specified team is in stalemate, otherwise false
//     */
//    public boolean isInStalemate(TeamColor teamColor) {
//        if (this.teamTurn != teamColor ){
//            return false;
//        }
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition currentPosition = new ChessPosition(row, col);
//                ChessPiece currentPiece = board.getPiece(currentPosition);
//
//                if (currentPiece != null && currentPiece.getTeamColor() == teamColor){
//                    System.out.println("Trying if "+currentPiece.getPieceType() + "can move");
//                    for (ChessMove move : currentPiece.pieceMoves(board, currentPosition)) {
//                        try {
//                            makeMove(move);
//                            System.out.println(currentPiece.getPieceType() + " can move " +
//                                    move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn());
//                            return false;
//                        } catch (InvalidMoveException e) {
//
//                        }
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Sets this game's chessboard with a given board
//     *
//     * @param board the new board to use
//     */
//    public void setBoard(ChessBoard board) {
//        this.board = copyChessBoard(board);
//    }
//
//    /**
//     * Gets the current chessboard
//     *
//     * @return the chessboard
//     */
//    public ChessBoard getBoard() {
//        return this.board;
//    }
//}
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
                System.out.println(e);
            }
        }
        this.teamTurn = origionalTurn;
        return validMoves;
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
    public void makeMove(ChessMove move) throws InvalidMoveException {
        checkMove(move);
        printBoard(board);
        updateBoard(this.board, move);
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
    public boolean isInStalemate(TeamColor teamColor) {
        return (isInStalemateHelper(teamColor) && !isInCheck(teamColor));
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