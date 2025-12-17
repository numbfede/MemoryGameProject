import java.util.Scanner;
import java.util.Random;

class Constants {
    static final int MAX_NR_CELLS = 186;
}

class ConsoleInteractionUtils {
    Scanner scanner = new Scanner(System.in);

    int readIntegerInRange(int min, int max) {
        int input = 0;
        boolean correctInput = false;
        while (!correctInput) {
            System.out.println("Please enter a number between " + min + " and " + max + ":");
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input < min || input > max)
                    System.out.println("Error: number not in range.");
                else
                    correctInput = true;
            } else {
                System.out.println("Error: input is not a number.");
                emptyTheScanner();
            }
        }
        emptyTheScanner();
        return input;
    }

    String readStringAndEnsureIsNotEmptyOrWhiteSpaces() {
        String input = "";
        boolean correctInput = false;
        while (!correctInput) {
            System.out.println("Please enter a string:");
            input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println("Error: string is empty or contains only white spaces.");
            } else {
                correctInput = true;
            }
        }
        return input;
    }

    boolean isValidGridDimensions(int height, int width) {
        return height > 0 && width > 0 &&
               height * width <= Constants.MAX_NR_CELLS &&
               height * width % 2 == 0;
    }

    int[] getGridDimensions() {
        int height, width;
        do {
            System.out.println("Insert height:");
            height = readIntegerInRange(1, Constants.MAX_NR_CELLS);
            System.out.println("Insert width:");
            width = readIntegerInRange(1, Constants.MAX_NR_CELLS);
            if (!isValidGridDimensions(height, width)) {
                System.out.println("Invalid grid dimensions.");
            }
        } while (!isValidGridDimensions(height, width));
        return new int[] { height, width };
    }

    Coordinate getCoordinate(int gridHeight, int gridWidth) {
        System.out.println("Insert row:");
        int row = readIntegerInRange(1, gridHeight) - 1;
        System.out.println("Insert column:");
        int col = readIntegerInRange(1, gridWidth) - 1;
        return new Coordinate(row, col);
    }

    boolean askYesNo(String message) {
        String input;
        do {
            System.out.print(message + " [y/n]: ");
            input = scanner.nextLine().trim().toLowerCase();
        } while (!input.equals("y") && !input.equals("n"));
        return input.equals("y");
    }

    void closeScanner() {
        scanner.close();
    }

    void emptyTheScanner() {
        scanner.nextLine();
    }
}

class Coordinate {
    int row, col;

    Coordinate(int r, int c) {
        row = r;
        col = c;
    }
}

class Card {
    char symbol;
    boolean isFaceUp;

    Card(char s) {
        symbol = s;
        isFaceUp = false;
    }

    void print() {
        System.out.print(isFaceUp ? " " + symbol + " " : " ! ");
    }

    void flip() {
        isFaceUp = !isFaceUp;
    }
}

class Player {
    String name;
    int score;

    Player(String n) {
        name = n;
        score = 0;
    }
}

class Grid {
    Card[][] grid;

    Grid(int height, int width) {
        grid = new Card[height][width];
        int total = height * width;
        char symbol = 34;

        for (int i = 0; i < total; i += 2) {
            grid[i / width][i % width] = new Card(symbol);
            grid[(i + 1) / width][(i + 1) % width] = new Card(symbol);
            symbol++;
        }

        Random rand = new Random();
        for (int i = 0; i < total; i++) {
            int j = rand.nextInt(total);
            Card temp = grid[i / width][i % width];
            grid[i / width][i % width] = grid[j / width][j % width];
            grid[j / width][j % width] = temp;
        }
    }

    void print() {
        System.out.print("    ");
        for (int c = 0; c < grid[0].length; c++) {
            System.out.printf(" %2d ", c + 1);
        }
        System.out.println();

        for (int r = 0; r < grid.length; r++) {
            System.out.print("    ");
            for (int k = 0; k < grid[r].length * 4 + 1; k++) {
                System.out.print("-");
            }
            System.out.println();

            System.out.printf(" %2d |", r + 1);
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != null) grid[r][c].print();
                else System.out.print("   ");
                System.out.print("|");
            }
            System.out.println();
        }

        System.out.print("    ");
        for (int k = 0; k < grid[0].length * 4 + 1; k++) {
            System.out.print("-");
        }
        System.out.println();
    }
}

class Game {
    Player[] players;
    int numPlayers;
    Grid grid;
    int currentPlayerIndex;
    ConsoleInteractionUtils utils;

    Game(Player[] players, int numPlayers, Grid grid, ConsoleInteractionUtils utils) {
        this.players = players;
        this.numPlayers = numPlayers;
        this.grid = grid;
        this.utils = utils;
        currentPlayerIndex = 0;
    }

    void play() {
        boolean gameOver = false;

        while (!gameOver) {
            Player currentPlayer = players[currentPlayerIndex];
            System.out.println("Turn of " + currentPlayer.name);
            grid.print();

            Coordinate c1 = getValidCoordinate();
            grid.grid[c1.row][c1.col].flip();
            grid.print();

            Coordinate c2 = getValidCoordinate();
            grid.grid[c2.row][c2.col].flip();
            grid.print();

            if (grid.grid[c1.row][c1.col].symbol == grid.grid[c2.row][c2.col].symbol) {
                System.out.println("Match!");
                grid.grid[c1.row][c1.col] = null;
                grid.grid[c2.row][c2.col] = null;
                currentPlayer.score++;
            } else {
                System.out.println("No match.");
                grid.grid[c1.row][c1.col].flip();
                grid.grid[c2.row][c2.col].flip();
                currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
            }

            gameOver = true;
            for (int i = 0; i < grid.grid.length; i++) {
                for (int j = 0; j < grid.grid[i].length; j++) {
                    if (grid.grid[i][j] != null) {
                        gameOver = false;
                        break;
                    }
                }
            }
        }

        int maxScore = 0;
        for (Player p : players) {
            if (p.score > maxScore) maxScore = p.score;
        }

        System.out.println("Game over!");
        System.out.print("Winner(s): ");
        for (Player p : players) {
            if (p.score == maxScore) {
                System.out.print(p.name + " ");
            }
        }
        System.out.println("with " + maxScore + " pairs.");
    }

    Coordinate getValidCoordinate() {
        Coordinate coord;
        do {
            coord = utils.getCoordinate(grid.grid.length, grid.grid[0].length);
        } while (grid.grid[coord.row][coord.col] == null ||
                 grid.grid[coord.row][coord.col].isFaceUp);
        return coord;
    }
}

public class MemoryGameProject {

    static void printWelcome() {
        System.out.println("******************************");
        System.out.println("           MEMORY             ");
        System.out.println("******************************");
        System.out.println();
        System.out.println("Flip two cards per turn.");
        System.out.println("If they match, you score a point");
        System.out.println("and play again.");
        System.out.println("Most pairs wins!");
        System.out.println("----------------------------------");
    }

    public static void main(String[] args) {
        ConsoleInteractionUtils utils = new ConsoleInteractionUtils();

        printWelcome();

        if (!utils.askYesNo("Do you want to start the game?")) {
            System.out.println("Goodbye!");
            utils.closeScanner();
            return;
        }

        int numPlayers = utils.readIntegerInRange(2, 6);
        Player[] players = new Player[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            String name;
            boolean unique;
            do {
                System.out.println("Enter name for player " + (i + 1) + ":");
                name = utils.readStringAndEnsureIsNotEmptyOrWhiteSpaces();
                unique = true;
                for (int j = 0; j < i; j++) {
                    if (players[j].name.equalsIgnoreCase(name)) {
                        System.out.println("Error: name already used.");
                        unique = false;
                        break;
                    }
                }
            } while (!unique);
            players[i] = new Player(name);
        }

        int[] dims = utils.getGridDimensions();
        Grid grid = new Grid(dims[0], dims[1]);

        Game game = new Game(players, numPlayers, grid, utils);
        game.play();

        utils.closeScanner();
    }
}
