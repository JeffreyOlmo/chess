package ui.facade;
import model.GameData;
import java.util.Arrays;

public class Response {
    private GameData[] games;

    public GameData[] getGames() {
        return games;
    }


    @Override
    public String toString() {
        return "Response{" +
                "games=" + Arrays.toString(games) +
                '}';
    }
}

