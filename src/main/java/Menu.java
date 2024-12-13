import java.util.*;


public class Menu {
    /**
     * The current count of played games.
     */
    private static final int HISTORY_LIMIT = 3;

    private static List<Score> scores = new ArrayList<>();

    public Menu() {
    }

    /**
     * Main game loop. The player can enter commands to interact with the game.
     * The loop continues until the game is over (either win or loss).
     */
    public static void gameMenu() {
        Scanner sc = new Scanner(System.in);

        System.out.println("MineSweeper Game\n----------------\n1 - New Game\n2 - Top 3 Wins\n3 - Exit Game");
        System.out.print("Option>");
        String option = sc.nextLine();

        switch (option) {
            case "1":
                Board board = new Board(); // Create board
                Game game = new Game(board, selectDifficulty());
                game.gameLoop();
                break;
            case "2":
                System.out.println("\n");
                showLastGames();
                System.out.println();
                gameMenu();
                break;
            case "3":
                System.out.println("Goodbye!");
                System.exit(0); // Exit the program
                break;
            default:
                System.out.println("Invalid command");
                gameMenu();
                break;
        }
    }

    public static void recordGame(Score score) {
        scores.add(score);
    }

    /**
     * Displays the results of the last 3 games.
     */
    public static void showLastGames() {
        if (scores.isEmpty()) {
            System.out.println("No games played yet.");
        } else if (scores.size() < HISTORY_LIMIT) {
            scores.sort(Comparator.comparingLong(Score::getTime)); // Sort by time
            for (Score score : scores) {
                System.out.println(score.toString());
            }
        } else {
            scores.sort(Comparator.comparingLong(Score::getTime)); // Sort by time
            for (int i = 0; i < HISTORY_LIMIT; i++) {
                System.out.println(scores.get(i).toString()); // Keep only top 3
            }
        }
    }

    static String formatTime(long time) {
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static int selectDifficulty() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Select difficulty:\n1 - Starter\n" +
                "2 - Pro\n" +
                "3 - Guru");
        String difficulty = sc.nextLine();

        while (!Game.isNumeric(difficulty)) {
            System.out.println("Invalid");
            difficulty = sc.nextLine();
        }

        int trueDifficulty = (Integer.parseInt(difficulty));

        while (trueDifficulty < 1 || trueDifficulty > 3 && trueDifficulty != 29) {
            System.out.println("Invalid");
            trueDifficulty = sc.nextInt();
        }

        return trueDifficulty;
    }

}
