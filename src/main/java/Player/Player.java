package Player;

import java.util.List;
import Game.*;

public class Player {
    private String name;
    private Score score;
    private List<Long> gameDurations; // Each game's playtime (score in seconds)

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public String scoreToString(Score score){
        return getName() + ": " + Menu.formatTime(score.getTime());
    }

    public List<Long> getGameDurations() {
        return gameDurations;
    }

    public void addGameDuration(long duration) {
        this.gameDurations.add(duration);
    }

}
