package chess;

import java.util.*;

public class ChessBoard {

    public Map<ChessPosition, ChessPiece> board;

    public ChessBoard() {
        this.board = new HashMap<>();
        //resetBoard();
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
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // Setup pieces for BLACK
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }

    private static final int BLACK = 0;
    private static final int RED = 1;
    private static final int GREEN = 2;
    private static final int YELLOW = 3;
    private static final int BLUE = 4;
    private static final int MAGENTA = 5;
    private static final int CYAN = 6;
    private static final int WHITE = 7;

    private static final String COLOR_RESET = "\u001b[0m";

    private static String color(int foreground, int background) {
        return String.format("\u001b[3%d;4%dm", foreground, background);
    }

    private static String color(int foreground) {
        return String.format("\u001b[1;3%dm", foreground);
    }

    private static final String BORDER = color(BLACK, YELLOW);

    private static final String BOARD_BLACK = color(WHITE, BLACK);
    private static final String BOARD_WHITE = color(BLACK, WHITE);
    private static final String BOARD_HIGHLIGHT = color(GREEN, MAGENTA);

    private static final String BLACK_PIECE = color(RED);
    private static final String WHITE_PIECE = color(GREEN);

    private static final Map<ChessPiece.PieceType, String> PIECE_MAP = Map.of(
            ChessPiece.PieceType.KING, "K",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.KNIGHT, "N",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.PAWN, "P"
    );

    @Override
    public String toString() {
        return toString(ChessGame.TeamColor.WHITE, null);
    }

    public String toString(ChessGame.TeamColor playerColor, Collection<ChessPosition> highlights) {
        var sb = new StringBuilder();
        var currentSquare = BOARD_WHITE;
        var rows = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
        var columns = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        var columnsLetters = "    a  b  c  d  e  f  g  h    ";
        if (playerColor == ChessGame.TeamColor.BLACK) {
            columnsLetters = "    h  g  f  e  d  c  b  a    ";
            rows = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            columns = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
        }
        sb.append(BORDER).append(columnsLetters).append(COLOR_RESET).append("\n");
        for (var i : rows) {
            var row = " " + (i + 1) + " ";
            sb.append(BORDER).append(row).append(COLOR_RESET);
            for (var j : columns) {
                var squareColor = currentSquare;
                if (highlights != null && highlights.contains(new ChessPosition(i + 1, j + 1))) {
                    squareColor = BOARD_HIGHLIGHT;
                }
                var piece = board.get(new ChessPosition(i + 1, j + 1));
                if (piece != null) {
                    var color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PIECE : BLACK_PIECE;
                    var p = PIECE_MAP.get(piece.getPieceType());
                    sb.append(squareColor).append(color).append(" ").append(p).append(" ").append(COLOR_RESET);
                } else {
                    sb.append(squareColor).append("   ").append(COLOR_RESET);
                }
                currentSquare = currentSquare.equals(BOARD_BLACK) ? BOARD_WHITE : BOARD_BLACK;
            }
            sb.append(BORDER).append(row).append(COLOR_RESET);
            sb.append('\n');
            currentSquare = currentSquare.equals(BOARD_BLACK) ? BOARD_WHITE : BOARD_BLACK;
        }
        sb.append(BORDER).append(columnsLetters).append(COLOR_RESET).append("\n");
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }
}