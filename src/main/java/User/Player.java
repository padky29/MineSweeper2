package User;

import Game.Menu;

import java.util.List;

public class Player {
    private String name;
    private Score score;
    private List<Long> gameDurations; // Each game's playtime (score in seconds)
    private Inventory inventory; // User.Inventory associated with the player

    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory();
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

    public Inventory getInventory() {
        return inventory;
    }

}
