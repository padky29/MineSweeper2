import java.util.Random;

public class Board {
    private Cell[][] board;

    private int bombCount = 2;

    private int size = 0;

    public void newBoard(int difficulty) {
        switch (difficulty) {
            case 1:
                size = 9;
                bombCount = 10;
                break;
            case 2:
                size = 10;
                bombCount = 16;
                break;
            case 3:
                size = 12;
                bombCount = 24;
                break;
            case 29:
                size = 9;
                bombCount = 1;
                break;
            default:
                System.out.println("Invalid difficulty");
                break;
        }
        board = new Cell[size][size];
        // Initialize each cell in the board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = new Cell(false);
            }
        }
        placeBombs(board, bombCount, size);
        calculateNeighborCounts(board);
    }

    public Cell[][] getBoard() {
        return board;
    }

    public int getBombCount(){
        return bombCount;
    }

    public int getSize(){
        return size;
    }

    public static void placeBombs(Cell[][] game, int bombCount, int size) {
        Random rd = new Random();
        int bombsPlaced = 0;

        while (bombsPlaced < bombCount) {
            int row = rd.nextInt(size);
            int col = rd.nextInt(size);

            if (!game[row][col].isBomb()) {
                game[row][col].setBomb(true);
                bombsPlaced++;
            }
        }
    }

    public void calculateNeighborCounts(Cell[][] game) {
        int rows = game.length;
        int cols = game[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (game[i][j].isBomb()) {
                    incrementNeighbors(game, i, j);
                }
            }
        }
    }

    public void incrementNeighbors(Cell[][] game, int row, int col) {
        int rows = game.length;
        int cols = game[0].length;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols && !game[i][j].isBomb()) {
                    game[i][j].setAdjacentBombs(game[i][j].getAdjacentBombs() + 1);
                }
            }
        }
    }
}
