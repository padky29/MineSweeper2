import java.util.*;

public class Game {

    private int availableFlags = 0;

    boolean cheatMode = false;

    int bombLostR;
    int bombLostC;

    private final Board board;
    private final int difficulty;

    static int gameCount = 0;

    static List<Player> players = new ArrayList<>();
    static List<Score> scores = new ArrayList<>();
    List<String> commandHistory = new ArrayList<>();

    public Game(Board board, int difficulty) {
        this.board = board;
        this.difficulty = difficulty;
    }

    public void gameLoop() {
        board.newBoard(difficulty);
        availableFlags = board.getBombCount();

        long startTime = System.currentTimeMillis();
        Scanner sc = new Scanner(System.in);

        Player currentPlayer = getPlayerForGame(sc);

        while (true) {
            renderGameStatus(startTime, currentPlayer);

            String command = getUserCommand(sc);
            if (!processCommand(command, currentPlayer)) {
                System.out.println("Invalid command. Use /open <row> <col> (e.g., /open A 3).");
            }

            if (checkWin(board.getBoard())) {
                handleWin(startTime, currentPlayer, board.getBoard());
            }
        }
    }

    private Player getPlayerForGame(Scanner sc) {
        System.out.print("Enter name: ");
        String playerName = sc.nextLine();
        if (playerName.isEmpty()) {
            playerName = "Anonymous " + gameCount++;
        }

        Player currentPlayer = getPlayerByName(playerName);
        if (currentPlayer == null) {
            currentPlayer = new Player(playerName);
            players.add(currentPlayer);
        }
        System.out.println("Welcome " + currentPlayer.getName());
        return currentPlayer;
    }

    private void renderGameStatus(long startTime, Player currentPlayer) {
        renderRevealedGame(board.getBoard());
        long elapsedTime = System.currentTimeMillis() - startTime;
        String formattedTime = Menu.formatTime(elapsedTime / 1000);
        System.out.printf("\nAvailable flags: %d\nElapsed time: %s\n[Type /help for assistance]\nCommand> ", availableFlags, formattedTime);
    }

    private String getUserCommand(Scanner sc) {
        return sc.nextLine();
    }

    private boolean processCommand(String command, Player currentPlayer) {
        String[] commandParts = command.split(" ");
        switch (commandParts[0].toLowerCase()) {
            case "/open":
                if (commandParts.length == 3)
                    openCell(commandParts[1], commandParts[2], board.getBoard());
                break;
            case "/flag":
                if (commandParts.length == 3) flagCell(commandParts[1], commandParts[2], board.getBoard());
                break;
            case "/help":
                showHelp();
                break;
            case "/cheat":
                cheatToggle(board.getBoard());
                break;
            case "/hint":
                provideHint(board.getBoard());
                break;
            case "/quit":
                if (confirmQuit()) {
                    Menu.gameMenu();
                    return true;
                }
                break;
            case "/mywins":
                showMyWins(currentPlayer);
                break;
            case "/history":
                showCommandHistory();
                break;
            case "/top":
                Menu.showLastGames();
                break;
            case "/wins":
                showAllWins();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showHelp() {
        System.out.println("""
                /open  - <row> <column> - Opens the cell at the specified row/column coordinates, e.g., /open A 2.
                /flag  - <row> <column> - Flags the cell at the specified row/column coordinates. If a flag already exists on the cell, it will be removed.
                /hint  - Suggests a random cell that does not contain a mine. The hint is given in the form of board coordinates.
                /cheat - Toggles cheat mode, revealing the mines every time the board is displayed.
                /quit  - Ends the game and returns to the main menu. A game ended this way will not be added to the list of victories.
                """);
    }

    private boolean confirmQuit() {
        Scanner confirmation = new Scanner(System.in);
        System.out.println("Are you sure you want to quit? All progress will be lost. Confirm with: /quit");
        String confirmationInput = confirmation.nextLine();
        return confirmationInput.equalsIgnoreCase("/quit");
    }

    private void handleWin(long startTime, Player currentPlayer, Cell[][] game) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        Score score = new Score(elapsedTime / 1000, currentPlayer);
        currentPlayer.setScore(score);
        scores.add(score);
        Menu.recordGame(score);
        renderRevealedGame(game);
        System.out.println("You won!");
        Menu.gameMenu();
    }

    private void showMyWins(Player currentPlayer) {
        System.out.println("Your game history:");
        scores.stream()
                .filter(score -> score.getPlayerName().equals(currentPlayer.getName()))
                .forEach(System.out::println);
    }

    private void showCommandHistory() {
        for (int i = commandHistory.size() - 1; i >= 0; i--) {
            System.out.println(commandHistory.get(i));
        }
    }

    private void showAllWins() {
        System.out.println("Wins:");
        scores.sort(Comparator.comparing(Score::getPlayerName));
        scores.forEach(System.out::println);
    }

    public boolean checkWin(Cell[][] game) {
        int totalCells = game.length * game[0].length;
        int bombCount = 0;
        int revealedCount = 0;

        for (Cell[] cells : game) {
            for (int j = 0; j < game[0].length; j++) {
                if (cells[j].isBomb()) {
                    bombCount++;
                } else if (cells[j].isRevealed()) {
                    revealedCount++;
                }
            }
        }

        return (revealedCount == totalCells - bombCount);
    }

    public void cheatToggle(Cell[][] game) {
        int rows = game.length;
        int cols = game[0].length;

        cheatMode = !cheatMode;  // Alterna o estado do cheatMode

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (game[i][j].isBomb()) {
                    game[i][j].setRevealed(cheatMode);
                }
            }
        }

        System.out.println("Cheat mode " + (cheatMode ? "activated" : "deactivated") + ".");
    }

    public void openCell(String row, String col, Cell[][] game) {
        int rowNum = letterToNumber(row);

        if (!isNumeric(col)) {
            System.out.println("Invalid move: column input is not a valid number.");
            return;
        }

        int colNum = Integer.parseInt(col);

        // Check for invalid input
        if (rowNum == 404 || colNum < 0 || colNum >= game[0].length || game[rowNum][colNum].isRevealed() || game[rowNum][colNum].isFlagged()) {
            System.out.println("Invalid move: cell already revealed or flagged.");
            return;
        }

        // If it's a bomb, end the game
        if (game[rowNum][colNum].isBomb()) {
            System.out.println("You hit a bomb! Game over!");
            for (Cell[] cells : game) {
                for (int j = 0; j < game[0].length; j++) {
                    cells[j].setRevealed(true);
                }
            }
            bombLostR = rowNum;
            bombLostC = colNum;
            renderRevealedGame(game);
            Menu.gameMenu();
        }

        // Reveal cells
        revealCells(rowNum, colNum, game);
    }

    public void flagCell(String row, String col, Cell[][] game) {
        int rowNum = letterToNumber(row);
        if (!isNumeric(col)) {
            System.out.println("Invalid move: column input is not a valid number.");
            return;
        }
        int colNum = Integer.parseInt(col);

        // Check for invalid input
        if (rowNum == 404 || colNum < 0 || game[rowNum][colNum].isRevealed()) {
            System.out.println("Invalid move or cell already revealed.");
            return;
        }

        if (availableFlags > 0) {
            // Toggle flag on or off
            if (game[rowNum][colNum].isFlagged()) {
                game[rowNum][colNum].setFlagged(false);
                availableFlags++;

            } else {
                game[rowNum][colNum].setFlagged(true);
                availableFlags--;
            }
        } else {
            System.out.println("No more flags available!");
        }
    }

    public void revealCells(int row, int col, Cell[][] game) {
        if (row < 0 || row >= game.length || col < 0 || col >= game[0].length || game[row][col].isRevealed()) {
            return;
        }
        game[row][col].setRevealed(true);

        if (!game[row][col].isRevealed()) {
            return;
        }

        if (game[row][col].getAdjacentBombs() > 0) {
            return;
        }

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) { // Skip the current cell
                    revealCells(row + i, col + j, game);
                }
            }
        }
    }


    public void renderRevealedGame(Cell[][] game) {
        System.out.print("  "); // Top-left corner padding for column labels
        for (int col = 0; col < game[0].length; col++) {
            System.out.print(" " + col + " "); // Column labels
        }
        System.out.println();

        for (int i = 0; i < game.length; i++) {
            System.out.print((char) ('A' + i) + " "); // Row label
            for (int j = 0; j < game[0].length; j++) {
                if (game[i][j].isFlagged()) {
                    System.out.print(" F "); // Flagged cell
                } else if (game[i][j].isRevealed()) {
                    if (game[i][j].isBomb()) {
                        if (i == bombLostR && j == bombLostC) {
                            System.out.print("BAM"); // Triggered bomb
                        } else {
                            System.out.print(" o "); // Bomb cell
                        }
                    } else if (game[i][j].getAdjacentBombs() == 0) {
                        System.out.print(" □ ");
                    } else {
                        System.out.print(" " + game[i][j].getAdjacentBombs() + " "); // Revealed non-bomb cell
                    }
                } else {
                    System.out.print(" ■ "); // Hidden cell
                }
            }
            System.out.println();
        }
    }

    public void provideHint(Cell[][] game) {
        Random rd = new Random();
        int row, col;

        while (true) {
            row = rd.nextInt(game.length);
            col = rd.nextInt(game[0].length);

            if (!game[row][col].isBomb() && !game[row][col].isRevealed()) {
                System.out.printf("Hint: Try opening the cell at %s%d.%n", (char) ('A' + row), col);
                break;
            }
        }
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
}
