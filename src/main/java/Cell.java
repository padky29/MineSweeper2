public class Cell {
    private boolean revealed = false;
    private boolean flagged = false;
    private boolean isBomb = false;
    private int adjacentBombs = 0; // Only relevant if the cell is not a bomb

    public Cell(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean bomb) {
        this.isBomb = bomb;
    }

    public int getAdjacentBombs() {
        return adjacentBombs;
    }

    public void setAdjacentBombs(int adjacentBombs) {
        this.adjacentBombs = adjacentBombs;
    }
}
