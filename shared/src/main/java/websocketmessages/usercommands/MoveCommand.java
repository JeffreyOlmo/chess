package websocketmessages.usercommands;

import chess.ChessMove;

import static websocketmessages.usercommands.UserGameCommand.CommandType.MAKE_MOVE;

public class MoveCommand extends GameCommand {
    public final ChessMove move;

    public MoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(MAKE_MOVE, authToken, gameID);
        this.move = move;
    }
}
