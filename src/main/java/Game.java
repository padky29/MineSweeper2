import java.util.*;

public class Game {

    private int availableFlags = 0;

    private final Board board;
    private final int difficulty;

    static int gameCount = 0;

    // Added data structure to store player names
    static List<Player> players = new ArrayList<>();

    static List<Score> scores = new ArrayList<>();

    List<String> commandHistory = new ArrayList<>();

    public Game(Board board, int difficulty) {
        this.board = board;
        this.difficulty = difficulty;
    }

    public void gameLoop() {
        board.newBoard(difficulty);
        int size = board.getSize();
        boolean[][] revealed = new boolean[size][size]; // Array to track revealed cells
        boolean[][] flagged = new boolean[size][size]; // Array to track flagged cells
        availableFlags = board.getBombCount();

        long startTime = System.currentTimeMillis();
        String formattedTime;

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter name: ");
        String playerName = sc.nextLine();

        if (playerName.isEmpty()) {
            playerName = "Anonymous " + gameCount;
            gameCount++;
        }

        Player currentPlayer = getPlayerByName(playerName);

        if (currentPlayer != null) {
            System.out.println("Welcome back " + currentPlayer.getName());
        } else {
            currentPlayer = new Player(playerName);
            System.out.println("Welcome " + currentPlayer.getName());
            players.add(currentPlayer);
        }

        while (true) {
            System.out.println("\n");

            // Render the updated board
            renderRevealedGame(board.getBoard(), revealed, flagged);

            long elapsedTime = System.currentTimeMillis() - startTime;

            long totalSeconds = elapsedTime / 1000;

            formattedTime = Menu.formatTime(totalSeconds);

            if (checkWin(board.getBoard(), revealed)) {
                Score score = new Score(totalSeconds, currentPlayer);
                currentPlayer.setScore(score);
                scores.add(score);

                Menu.recordGame(score);
                System.out.println("You won!");
                Menu.gameMenu();
            }


            System.out.printf("\nAvailable flags: %d\nElapsed time: %s\n[Type /help for assistance]\nCommand> ", availableFlags, formattedTime);
            String command = sc.nextLine();

            String[] commandParts = command.split(" ");
            commandHistory.add(command);
            if (commandParts[0].equalsIgnoreCase("/open") && commandParts.length == 3) {
                openCell(commandParts[1], commandParts[2], board.getBoard(), revealed, flagged);
            } else if (commandParts[0].equalsIgnoreCase("/flag") && commandParts.length == 3) {
                flagCell(commandParts[1], commandParts[2], flagged, revealed);
            } else if (commandParts[0].equalsIgnoreCase("/help") && commandParts.length == 1) {
                System.out.println("""
                        /open  - <row> <column> - Opens the cell at the specified row/column coordinates, e.g., /open A 2.
                        /flag  - <row> <column> - Flags the cell at the specified row/column coordinates. If a flag already exists on the cell, it will be removed.
                        /hint  - Suggests a random cell that does not contain a mine. The hint is given in the form of board coordinates.
                        /cheat - Toggles cheat mode, revealing the mines every time the board is displayed.
                        /quit  - Ends the game and returns to the main menu. A game ended this way will not be added to the list of victories.
                        """);
            } else if (commandParts[0].equalsIgnoreCase("/cheat") && commandParts.length == 1) {
                cheatRevealCells(board.getBoard(), revealed);
            } else if (commandParts[0].equalsIgnoreCase("/hint") && commandParts.length == 1) {
                provideHint(board.getBoard(), revealed);
            } else if (commandParts[0].equalsIgnoreCase("/quit") && commandParts.length == 1) {
                System.out.println("Game ended. Returning to the main menu...");
                Menu.gameMenu();
            } else if(commandParts[0].equalsIgnoreCase("/mywins") && commandParts.length == 1){
                System.out.println("Your game history:");
                if (scores.isEmpty()) {
                    System.out.println("No games played yet.");
                } else {
                    for (Score score : scores) {
                        if (score.getPlayerName().equals(currentPlayer.getName())) {
                            System.out.println(score);
                        }
                    }
                }
            }else if (commandParts[0].equalsIgnoreCase("/history") && commandParts.length == 1) {
                for (int i = commandHistory.size() - 1; i >= 0; i--) {
                    System.out.println(commandHistory.get(i));
                }
            }else if(commandParts[0].equalsIgnoreCase("/top") && commandParts.length == 1){
                System.out.println("Top 3 games:");
                Menu.showLastGames();
            }else if (commandParts[0].equalsIgnoreCase("/wins") && commandParts.length == 1){
                System.out.println("Wins:");
                scores.sort(Comparator.comparing(Score::getPlayerName)); // Sort by time
                for (Score score : scores) {
                    System.out.println(score.toString());
                }
            }else {
                System.out.println("Invalid command. Use /open <row> <col> (e.g., /open A 3).");
                commandHistory.remove(commandHistory.getLast());
            }
        }
    }

    /**
     * Checks if the player has won by ensuring all non-bomb cells are revealed.
     *
     * @param game     the game board containing bomb and number data
     * @param revealed a 2D array indicating which cells are revealed
     * @return true if the player has revealed all non-bomb cells; false otherwise
     */
    public boolean checkWin(int[][] game, boolean[][] revealed) {
        int totalCells = game.length * game[0].length;
        int bombCount = 0;
        int revealedCount = 0;

        for (int i = 0; i < game.length; i++) {
            for (int j = 0; j < game[0].length; j++) {
                if (game[i][j] == 9) {
                    bombCount++;
                } else if (revealed[i][j]) {
                    revealedCount++;
                }
            }
        }

        return (revealedCount == totalCells - bombCount);
    }

    /**
     * Reveals all cells containing bombs (cheat mode).
     *
     * @param game     The current game board.
     * @param revealed A 2D boolean array tracking revealed cells.
     */
    public void cheatRevealCells(int[][] game, boolean[][] revealed) {
        int rows = game.length;
        int cols = game[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (game[i][j] == 9) {
                    revealed[i][j] = true;
                }
            }
        }
    }

    /**
     * Opens a specific cell on the game board.
     * If the cell contains a bomb, the game is lost. If not, the surrounding cells are revealed recursively.
     *
     * @param row      The row (letter) of the cell to open.
     * @param col      The column (number) of the cell to open.
     * @param game     The game board.
     * @param revealed The revealed cells tracker.
     * @param flagged  The flagged cells tracker.
     */
    public void openCell(String row, String col, int[][] game, boolean[][] revealed, boolean[][] flagged) {
        int rowNum = letterToNumber(row);

        if (!isNumeric(col)) {
            System.out.println("Invalid move: column input is not a valid number.");
            return;
        }

        int colNum = Integer.parseInt(col);

        // Check for invalid input
        if (rowNum == 404 || colNum < 0 || colNum >= game[0].length || revealed[rowNum][colNum] || flagged[rowNum][colNum]) {
            System.out.println("Invalid move: cell already revealed or flagged.");
            return;
        }

        // If it's a bomb, end the game
        if (game[rowNum][colNum] == 9) {
            System.out.println("You hit a bomb! Game over!");
            cheatRevealCells(game, revealed);
            game[rowNum][colNum] = 99;
            renderRevealedGame(game, revealed, flagged);
            Menu.gameMenu();
        }

        // Reveal cells
        revealCells(rowNum, colNum, game, revealed);
    }

    /**
     * Toggles a flag on a cell or removes it. Prevents flagging of already revealed cells.
     *
     * @param row      the row label (e.g., "A", "B", etc.)
     * @param col      the column index as a string
     * @param flagged  a 2D array indicating which cells are flagged
     * @param revealed a 2D array indicating which cells are revealed
     */
    public void flagCell(String row, String col, boolean[][] flagged, boolean[][] revealed) {
        int rowNum = letterToNumber(row);
        if (!isNumeric(col)) {
            System.out.println("Invalid move: column input is not a valid number.");
            return;
        }
        int colNum = Integer.parseInt(col);

        // Check for invalid input
        if (rowNum == 404 || colNum < 0 || colNum >= flagged[0].length || revealed[rowNum][colNum]) {
            System.out.println("Invalid move or cell already revealed.");
            return;
        }

        if (availableFlags > 0) {
            // Toggle flag on or off
            if (flagged[rowNum][colNum]) {
                flagged[rowNum][colNum] = false;
                availableFlags++;

            } else {
                flagged[rowNum][colNum] = true;
                availableFlags--;
            }
        } else {
            System.out.println("No more flags available!");
        }
    }

    /**
     * Recursively reveals cells on the game board starting from the specified cell.
     *
     * @param row      the row index of the cell
     * @param col      the column index of the cell
     * @param game     the game board containing bomb and number data
     * @param revealed a 2D array indicating which cells are revealed
     */
    public void revealCells(int row, int col, int[][] game, boolean[][] revealed) {
        // Only reveal unrevealed cells
        if (row < 0 || row >= game.length || col < 0 || col >= game[0].length || revealed[row][col]) {
            return;
        }

        // Mark the cell as revealed
        revealed[row][col] = true;

        // Stop recursion if the cell is not 0
        if (game[row][col] != 0) {
            return;
        }

        // Reveal neighbors around the 0
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) { // Skip the current cell
                    revealCells(row + i, col + j, game, revealed);
                }
            }
        }
    }

    /**
     * Renders the current state of the game board with revealed cells, flagged cells, and hidden cells.
     *
     * @param game     the game board containing bomb and number data
     * @param revealed a 2D array indicating which cells are revealed
     * @param flagged  a 2D array indicating which cells are flagged
     */
    public void renderRevealedGame(int[][] game, boolean[][] revealed, boolean[][] flagged) {
        System.out.print("  "); // Top-left corner padding for column labels
        for (int col = 0; col < game[0].length; col++) {
            System.out.print(" " + col + " "); // Column labels
        }
        System.out.println();

        for (int i = 0; i < game.length; i++) {
            System.out.print((char) ('A' + i) + " "); // Row labels
            for (int j = 0; j < game[0].length; j++) {
                if (flagged[i][j]) {
                    // Print flagged cells
                    System.out.print(" F ");
                } else if (revealed[i][j]) {
                    // Print the actual cell value if revealed
                    if (game[i][j] == 0) {
                        System.out.print(" □ ");
                    } else if (game[i][j] == 9) {
                        System.out.print(" o ");
                    } else if (game[i][j] == 99) {
                        System.out.print("BAM");
                    } else {
                        System.out.print(" " + game[i][j] + " ");
                    }
                } else {
                    // Print unrevealed cells as a block
                    System.out.print(" ■ ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Provides a hint by randomly selecting an unrevealed, non-bomb cell and suggesting it to the player.
     *
     * @param game     the game board containing bomb and number data
     * @param revealed a 2D array indicating which cells are revealed
     */
    public void provideHint(int[][] game, boolean[][] revealed) {
        Random rd = new Random();
        int row, col;

        while (true) {
            // Gera uma célula aleatória
            row = rd.nextInt(game.length);
            col = rd.nextInt(game[0].length);

            // Verifica se a célula não contém mina e não foi revelada
            if (game[row][col] != 9 && !revealed[row][col]) {
                System.out.printf("Hint: Try opening the cell at %s%d.%n", (char) ('A' + row), col);
                break;
            }
        }
    }

    /**
     * Converts a row label (e.g., "A", "B", etc.) to its corresponding row index.
     *
     * @param letter the row label
     * @return the row index, or 404 if the letter is invalid
     */
    public int letterToNumber(String letter) {
        return switch (letter.toUpperCase()) {
            case "A" -> 0;
            case "B" -> 1;
            case "C" -> 2;
            case "D" -> 3;
            case "E" -> 4;
            case "F" -> 5;
            case "G" -> 6;
            case "H" -> 7;
            case "I" -> 8;
            case "K" -> 9;
            case "L" -> 10;
            case "M" -> 11;
            case "N" -> 12;
            default -> {
                System.out.println("Invalid letter");
                yield 404;
            }
        };
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public Player getPlayerByName(String playerName) {
        if (!players.isEmpty()) {
            for (Player player : Game.players) {
                if (player.getName().equals(playerName)) {
                    return player;
                }
            }
        }
        return null;
    }
}
