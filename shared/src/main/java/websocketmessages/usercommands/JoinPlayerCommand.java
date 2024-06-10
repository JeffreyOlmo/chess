package websocketmessages.usercommands;

import chess.ChessGame;

import static websocketmessages.usercommands.UserGameCommand.CommandType.JOIN_PLAYER;

public class JoinPlayerCommand extends GameCommand {
    public final ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(JOIN_PLAYER, authToken, gameID);
        this.playerColor = playerColor;
    }
}
