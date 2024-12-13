import java.util.Random;

public class Board {
    private int[][] board;

    private int bombCount = 2;

    private int size = 0;

    /**
     * Initializes a new Minesweeper game.
     * The board is a 9x9 grid, and 10 bombs are placed randomly.
     * The number of neighboring bombs for each cell is calculated.
     */
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
                System.out.println("Invalid dificulty");
                break;
        }
        board = new int[size][size];
        placeBombs(board, bombCount); // Number of bombs that will be placed.
        calculateNeighborCounts(board); //Calculates the bombs arounds each cell before the game starts.
    }

    public int[][] getBoard() {
        return board;
    }

    public int getBombCount(){
        return bombCount;
    }

    public int getSize(){
        return size;
    }

    /**
     * Places a specified number of bombs randomly on the game board.
     *
     * @param game      the game board containing bomb and number data
     * @param bombCount the number of bombs to place on the board
     */
    public static void placeBombs(int[][] game, int bombCount) {
        Random rd = new Random();
        int bombsPlaced = 0;

        while (bombsPlaced < bombCount) {
            int row = rd.nextInt(9);
            int col = rd.nextInt(9);

            if (game[row][col] != 9) {  // 9 represents a bomb
                game[row][col] = 9;
                bombsPlaced++;
            }
        }
    }

    /**
     * Calculates and updates the number of bombs surrounding each cell on the game board.
     *
     * @param game the game board containing bomb and number data
     */
    public void calculateNeighborCounts(int[][] game) {
        int rows = game.length;
        int cols = game[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (game[i][j] == 9) {
                    incrementNeighbors(game, i, j);
                }
            }
        }
    }

    /**
     * Increments the bomb count for all valid neighboring cells of a given cell.
     *
     * @param game the game board containing bomb and number data
     * @param row  the row index of the cell
     * @param col  the column index of the cell
     */
    public void incrementNeighbors(int[][] game, int row, int col) {
        int rows = game.length;
        int cols = game[0].length;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols && game[i][j] != 9) {
                    game[i][j]++;
                }
            }
        }
    }
}
