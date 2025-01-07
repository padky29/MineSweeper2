package Player;

import Game.*;

public class Score {
    private final long time;
    private final Player player;

    public Score(long time, Player player) {
        this.time = time;
        this.player = player;
    }

    public String toString() {
        return player.getName() + ": " + Menu.formatTime(time);
    }

    public long getTime() {
        return time;
    }

    public String getPlayerName(){
        return player.getName();
    }

}