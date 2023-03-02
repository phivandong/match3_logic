package match3;

import java.util.Scanner;
import java.util.Vector;

public class Match3Game {
    public Vector<Match3Player> players;
    public Match3Board board;
    public int currentPlayer;
    int[][] numberBoard;

    private final int verticalGem = 6;
    private final int horizontalGem = 7;
    private final int bombGem = 8;
    private final int thunderGem = 9;

    public Match3Game(int width, int height) {
        this.board = new Match3Board(width, height);
        this.currentPlayer = -1;
        this.players = new Vector<>();
    }

    public void addPlayer(Match3Player player) {
        players.add(player);
    }

    public void removePlayer(Match3Player player) {
        players.remove(player);
    }

    public void startGame() {
        // Initialize game components and start the game loop
        numberBoard = new int[board.height][board.width];
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
                numberBoard[i][j] = (int) Math.floor(Math.random() * 5);
            }
        }
        printBoard(numberBoard);

        this.currentPlayer = 0;

        scanBoard(numberBoard);

        int loop = 10;

        for (int l = 0; l < loop; l++) {
            System.out.println("Player " + (this.currentPlayer + 1) + " is playing");
            int turn = 2;
            while (turn > 0) {
                Match3Move move = new Match3Move();
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the move: ");

                move.direction = scanner.nextLine();
                move.x = scanner.nextInt();
                move.y = scanner.nextInt();

                if (makeMove(move)) {
                    switch (move.direction) {
                        case "up":
                            swap(numberBoard, move.x, move.y, move.x - 1, move.y);
                            if (thunderGemCheck(numberBoard, move.x, move.y)) {
                                createThunderGemOnTrigger(numberBoard, move.x, move.y);
                                turn--;
                                break;
                            } else if (verticalCheck(numberBoard, move.x, move.y) &&
                                    horizontalCheck(numberBoard, move.x, move.y)) {
                                createBombGemOnTrigger(numberBoard, move.x, move.y);
                                turn--;
                                break;
                            } else if (verticalGemCheck(numberBoard, move.x, move.y)) {
                                createVerticalGemOnTrigger(numberBoard, move.x, move.y);
                                turn--;
                                break;
                            } else if (horizontalGemCheck(numberBoard, move.x, move.y)) {
                                createHorizontalGemOnTrigger(numberBoard, move.x, move.y);
                                turn--;
                                break;
                            } else if (checkMatch(numberBoard, move.x, move.y)
                                    || checkMatch(numberBoard, move.x - 1, move.y)) {
                                turn--;
                                break;
                            } else {
                                swap(numberBoard, move.x, move.y, move.x - 1, move.y);
                            }
                            break;
                        case "down":
                            swap(numberBoard, move.x, move.y, move.x + 1, move.y);
                            if (checkMatch(numberBoard, move.x, move.y)
                                    || checkMatch(numberBoard, move.x + 1, move.y)) {
                                turn--;
                                break;
                            } else {
                                swap(numberBoard, move.x, move.y, move.x + 1, move.y);
                            }
                            break;
                        case "right":
                            swap(numberBoard, move.x, move.y, move.x, move.y + 1);
                            if (checkMatch(numberBoard, move.x, move.y)
                                    || checkMatch(numberBoard, move.x, move.y + 1)) {
                                turn--;
                                break;
                            } else {
                                swap(numberBoard, move.x, move.y, move.x, move.y + 1);
                            }
                            break;
                        case "left":
                            swap(numberBoard, move.x, move.y, move.x, move.y - 1);
                            if (checkMatch(numberBoard, move.x, move.y)
                                    || checkMatch(numberBoard, move.x, move.y - 1)) {
                                turn--;
                                break;
                            } else {
                                swap(numberBoard, move.x, move.y, move.x, move.y - 1);
                            }
                            break;
                    }
                    scanBoard(numberBoard);
                } else {
                    System.out.println("Error move");
                    printBoard(numberBoard);
                }
            }
            switchPlayer();
        }
        endGame();
    }

    private void swap(int[][] board, int x1, int y1, int x2, int y2) {
        int temp = board[x1][y1];
        board[x1][y1] = board[x2][y2];
        board[x2][y2] = temp;
    }

    private void printBoard(int[][] numberBoard) {
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
                System.out.print(numberBoard[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public void endGame() {
        int win = players.get(0).score > players.get(1).score ? 1 : 2;
        System.out.println("Player " + win + " wins");
    }

    public boolean makeMove(Match3Move move) {
        // Apply the move to the game board and update the player's score
        // Return true if the move is valid and false otherwise
        if (isValidMove(move)) {
            players.get(currentPlayer).chair++;
            return true;
        }
        return false;
    }

    public boolean isValidMove(Match3Move move) {
        // Check if the move is valid
        if (move.y == 0 && move.direction.equals("left")) {
            return false;
        } else if (move.y == board.width - 1 && move.direction.equals("right")) {
            return false;
        } else if (move.x == 0 && move.direction.equals("up")) {
            return false;
        } else if (move.x == board.height - 1 && move.direction.equals("down")) {
            return false;
        }
        return move.x < board.width && move.y < board.height;
    }

    private boolean checkMatch(int[][] numberBoard, int x, int y) {
        int d = 0;
        int k, h;

        // check hang
        if (x == 0) {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }
        } else {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k--;
            }
        }

        if (d >= 3) {
            return true;
        }

        // check cot
        d = 0;

        if (y == 0) {
            h = y;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }
        } else {
            h = y;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h--;
            }
        }

        if (d >= 3) {
            return true;
        }

        return false;
    }

    private boolean verticalCheck(int[][] numberBoard, int x, int y) {
        int d = 0;
        int h = y;

        if (y == 0) {
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }
        } else {
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h--;
            }
        }

        return d >= 3;
    }

    private boolean horizontalCheck(int[][] numberBoard, int x, int y) {
        int d = 0;
        int k;

        // check hang
        if (x == 0) {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }
        } else {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k--;
            }
        }

        return d >= 3;
    }

    private boolean thunderGemCheck(int[][] numberBoard, int x, int y) {
        int d = 0;
        int k, h;

        // check hang
        if (x == 0) {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }
        } else {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k--;
            }
        }

        if (d >= 5) {
            return true;
        }

        // check cot
        d = 0;

        if (y == 0) {
            h = y;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }
        } else {
            h = y;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h--;
            }
        }

        if (d >= 5) {
            return true;
        }

        return false;
    }

    private boolean checkLeftThunder(int[][] numberBoard, int x, int y) {
        if (y < 2) return false;
        return numberBoard[x][y] == numberBoard[x][y - 1] && numberBoard[x][y] == numberBoard[x][y - 2];
    }

    private boolean checkRightThunder(int[][] numberBoard, int x, int y) {
        if (y > board.width - 2) return false;
        return numberBoard[x][y] == numberBoard[x][y + 1] && numberBoard[x][y] == numberBoard[x][y + 2];
    }

    private boolean checkUpThunder(int[][] numberBoard, int x, int y) {
        if (x < 2) return false;
        return numberBoard[x][y] == numberBoard[x - 1][y] && numberBoard[x][y] == numberBoard[x - 2][y];
    }

    private boolean checkDownThunder(int[][] numberBoard, int x, int y) {
        if (x > board.height - 2) return false;
        return numberBoard[x][y] == numberBoard[x + 1][y] && numberBoard[x][y] == numberBoard[x + 2][y];
    }

    private boolean verticalGemCheck(int[][] numberBoard, int x, int y) {
        int d = 0;
        int h = y;

        if (y == 0) {
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }
        } else {
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                d++;
                h--;
            }
        }

        return d >= 4;
    }

    private boolean horizontalGemCheck(int[][] numberBoard, int x, int y) {
        int d = 0;
        int k;

        // check hang
        if (x == 0) {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }
        } else {
            k = x;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                d++;
                k--;
            }
        }

        return d >= 4;
    }

    private void createThunderGemOnTrigger(int[][] numberBoard, int x, int y) {
        int k, h;

        // check hang
        if (x == 0) {
            k = x + 1;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k++;
            }
        } else {
            k = x + 1;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k--;
            }
        }

        // check cot
        if (y == 0) {
            h = y + 1;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h++;
            }
        } else {
            h = y + 1;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h--;
            }
        }

        numberBoard[x][y] = thunderGem;
    }

    private void createVerticalGemOnTrigger(int[][] numberBoard, int x, int y) {
        int h;

        if (y == 0) {
            h = y + 1;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h++;
            }
        } else {
            h = y + 1;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h--;
            }
        }

        numberBoard[x][y] = verticalGem;
    }

    private void createHorizontalGemOnTrigger(int[][] numberBoard, int x, int y) {
        int k;

        if (x == 0) {
            k = x + 1;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k++;
            }
        } else {
            k = x + 1;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k--;
            }
        }

        numberBoard[x][y] = horizontalGem;
    }

    private void createBombGemOnTrigger(int[][] numberBoard, int x, int y) {
        int k, h;

        // check hang
        if (x == 0) {
            k = x + 1;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k++;
            }
        } else {
            k = x + 1;
            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k++;
            }

            k = x - 1;
            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
                numberBoard[k][y] = -1;
                k--;
            }
        }

        // check cot
        if (y == 0) {
            h = y + 1;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h++;
            }
        } else {
            h = y + 1;
            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h++;
            }

            h = y - 1;
            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
                numberBoard[x][h] = -1;
                h--;
            }
        }

        numberBoard[x][y] = bombGem;
    }

//    private void createThunderGemOnAuto(int[][] numberBoard, int x, int y) {
//        int k, h;
//
//        // check hang
//        k = x + 1;
//        while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
//            numberBoard[k][y] = -1;
//            k++;
//        }
//
//        // check cot
//        h = y + 1;
//        while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
//            numberBoard[x][h] = -1;
//            h++;
//        }
//
//        numberBoard[x][y] = thunderGem;
//    }
//
//    private void createVerticalGemOnAuto(int[][] numberBoard, int x, int y) {
//        int h;
//
//        h = y + 1;
//        while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
//            numberBoard[x][h] = -1;
//            h++;
//        }
//
//        numberBoard[x][y] = verticalGem;
//    }
//
//    private void createHorizontalGemOnAuto(int[][] numberBoard, int x, int y) {
//        int k;
//
//        k = x + 1;
//        while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
//            numberBoard[k][y] = -1;
//            k++;
//        }
//
//        numberBoard[x][y] = horizontalGem;
//    }
//
//    private void createBombGemOnAuto(int[][] numberBoard, int x, int y) {
//        int k, h;
//
//        // check hang
//        if (x == 0) {
//            k = x + 1;
//            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
//                numberBoard[k][y] = -1;
//                k++;
//            }
//        } else {
//            k = x + 1;
//            while (k < board.width && numberBoard[k][y] == numberBoard[x][y]) {
//                numberBoard[k][y] = -1;
//                k++;
//            }
//
//            k = x - 1;
//            while (k > -1 && numberBoard[k][y] == numberBoard[x][y]) {
//                numberBoard[k][y] = -1;
//                k--;
//            }
//        }
//
//        // check cot
//        if (y == 0) {
//            h = y + 1;
//            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
//                numberBoard[x][h] = -1;
//                h++;
//            }
//        } else {
//            h = y + 1;
//            while (h < board.height && numberBoard[x][h] == numberBoard[x][y]) {
//                numberBoard[x][h] = -1;
//                h++;
//            }
//
//            h = y - 1;
//            while (h > -1 && numberBoard[x][h] == numberBoard[x][y]) {
//                numberBoard[x][h] = -1;
//                h--;
//            }
//        }
//
//        numberBoard[x][y] = bombGem;
//    }

    private void matchGemOnAuto(int[][] numberBoard, int x, int y) {
        int stackX = 0;
        int[] stackYSaver = new int[board.width];
        for (int j = y; j < board.width; j++) {
            if (numberBoard[x][j] == numberBoard[x][y]) {
                stackX++;
                int stackY = 1;
                for (int i = x + 1; i < board.height; i++) {
                    if (numberBoard[i][j] == numberBoard[x][j]) {
                        stackY++;
                    } else {
                        if (stackY >= 3) {
                            break;
                        } else {
                            stackY = 1;
                            break;
                        }
                    }
                }
                stackYSaver[j] = stackY;
            } else {
                if (stackX >= 3) {
                    break;
                } else {
                    stackX = 1;
                    break;
                }
            }
        }

        int maxOfStackY = stackYSaver[y];
        for (int i = y + 1; i < stackX + y; i++) {
            if (stackYSaver[i] > maxOfStackY) {
                maxOfStackY = stackYSaver[i];
            }
        }

        if (stackX >= 5) {
            for (int j = y; j < stackX + y; j++) {
                numberBoard[x][j] = -1;
                if (stackYSaver[j] >= 3) {
                    for (int k = x; k < stackYSaver[j] + x; k++) {
                        numberBoard[k][j] = -1;
                    }
                }
            }
            numberBoard[x][y] = thunderGem;
        } else if (stackX >= 3 && maxOfStackY >= 5) {
            for (int j = y; j < stackX + y; j++) {
                numberBoard[x][j] = -1;
                if (stackYSaver[j] >= 3) {
                    for (int k = x; k < stackYSaver[j] + x; k++) {
                        numberBoard[k][j] = -1;
                    }
                }
            }
            numberBoard[x][y] = thunderGem;
        } else if (stackX >= 3 && maxOfStackY >= 3) {
            for (int j = y; j < stackX + y; j++) {
                numberBoard[x][j] = -1;
                if (stackYSaver[j] >= 3) {
                    for (int k = x; k < stackYSaver[j] + x; k++) {
                        numberBoard[k][j] = -1;
                    }
                }
            }
            numberBoard[x][y] = bombGem;
        } else if (stackX == 4) {
            for (int j = y; j < stackX + y; j++) {
                numberBoard[x][j] = -1;
            }
            numberBoard[x][y] = horizontalGem;
        } else if (stackX == 1 && stackYSaver[y] >= 5) {
            for (int k = x; k < stackYSaver[y] + x; k++) {
                numberBoard[k][y] = -1;
            }
            numberBoard[x][y] = thunderGem;
        } else if (stackX == 1 && stackYSaver[y] == 4) {
            for (int j = y; j < stackX + y; j++) {
                numberBoard[x][j] = -1;
            }
            numberBoard[x][y] = verticalGem;
        } else if (stackX == 3) {
            for (int j = y; j < stackX + y; j++) {
                numberBoard[x][j] = -1;
            }
        } else if (stackX == 1 && stackYSaver[y] == 3) {
            for (int i = x; i < stackYSaver[y] + x; i++) {
                numberBoard[i][y] = -1;
            }
        }
    }

    private boolean availableScan(int[][] numberBoard) {
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
                if (checkMatch(numberBoard, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void scanBoard(int[][] numberBoard) {
        System.out.println("Scan board");
//        boolean[][] check = new boolean[board.height][board.width];
//        int[][] specialBoard = new int[board.height][board.width];
        boolean exit = true;
        if (availableScan(numberBoard)) {
            for (int j = 0; j < board.width; j++) {
                for (int i = 0; i < board.height; i++) {
                    matchGemOnAuto(numberBoard, i, j);
                }
            }
            exit = false;
        }

//        for (int i = 0; i < board.height; i++) {
//            for (int j = 0; j < board.width; j++) {
//                if (check[i][j]) {
//                    numberBoard[i][j] = -1;
//                }
//            }
//        }
//
//        for (int i = 0; i < board.height; i++) {
//            for (int j = 0; j < board.width; j++) {
//                if (numberBoard[i][j] == -1 && specialBoard[i][j] > 5) {
//                    numberBoard[i][j] = specialBoard[i][j];
//                }
//            }
//        }

        printBoard(numberBoard);
        players.get(currentPlayer).score += countScore(numberBoard);
        fillEmpty(numberBoard);
        printBoard(numberBoard);
        System.out.println("Player " + (this.currentPlayer + 1) + " got " + players.get(currentPlayer).score);

        if (exit) {
            return;
        } else {
            scanBoard(numberBoard);
        }
    }

    private void fillEmpty(int[][] numberBoard) {
        System.out.println("Fill board");
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
                if (numberBoard[i][j] == -1) {
                    removeMatchedCandy(numberBoard, i, j);
                }
            }
        }
    }

    private void removeMatchedCandy(int[][] numberBoard, int x, int y) {
        if (numberBoard[0][y] == -1) {
            numberBoard[0][y] = (int) Math.floor(Math.random() * 5);
        }

        for (int i = x; i > 0; i--) {
            numberBoard[i][y] = numberBoard[i - 1][y];
            if (numberBoard[i][y] == -1) {
                i++;
            }
        }
        numberBoard[0][y] = (int) Math.floor(Math.random() * 5);
    }

    private int countScore(int[][] numberBoard) {
        int score = 0;
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
                if (numberBoard[i][j] < 0 || numberBoard[i][j] > 5) {
                    score++;
                }
            }
        }
        return score;
    }

    //----------------------------------------------------------------------------------
    // METHOD OF SPECIAL GEM
    //----------------------------------------------------------------------------------

    private void explodeVertical(int[][] numberBoard, int x, int y) {
        for (int i = 0; i < board.height; i++) {
            if (numberBoard[i][y] == horizontalGem) {
                explodeHorizontal(numberBoard, x, y);
            } else if (numberBoard[i][y] == bombGem) {
                explodeBomb(numberBoard, x, y);
            }
            numberBoard[i][y] = -1;
        }
    }

    private void explodeHorizontal(int[][] numberBoard, int x, int y) {
        for (int j = 0; j < board.width; j++) {
            if (numberBoard[x][j] == verticalGem) {
                explodeVertical(numberBoard, x, y);
            } else if (numberBoard[x][j] == bombGem) {
                explodeBomb(numberBoard, x, y);
            }
            numberBoard[x][j] = -1;
        }
    }

    private void explodeBomb(int[][] numberBoard, int x, int y) {
        numberBoard[x][y] = -1;
        for (int i = x - 1; i < x + 1; i++) {
            for (int j = y - 1; j < y + 1; j++) {
                if (numberBoard[i][j] == verticalGem) {
                    explodeVertical(numberBoard, x, y);
                } else if (numberBoard[i][j] == horizontalGem) {
                    explodeHorizontal(numberBoard, x, y);
                } else if (numberBoard[i][j] == bombGem) {
                    explodeBomb(numberBoard, x, y);
                }
                numberBoard[i][j] = -1;
            }
        }
    }

    private void explodeBigBomb(int[][] numberBoard, int x, int y) {
        numberBoard[x][y] = -1;
        for (int i = x - 2; i < x + 2; i++) {
            for (int j = y - 2; j < y + 2; j++) {
                if (numberBoard[i][j] == verticalGem) {
                    explodeVertical(numberBoard, x, y);
                } else if (numberBoard[i][j] == horizontalGem) {
                    explodeHorizontal(numberBoard, x, y);
                } else if (numberBoard[i][j] == bombGem) {
                    explodeBomb(numberBoard, x, y);
                }
                numberBoard[i][j] = -1;
            }
        }
    }

    private void explodeTwoVH(int[][] numberBoard, int x, int y) {
        explodeVertical(numberBoard, x, y);
        explodeHorizontal(numberBoard, x, y);
    }

    private void switchPlayer() {
        this.currentPlayer = this.currentPlayer == 0 ? 1 : 0;
    }

    public boolean isGameOver() {
        return false;
    }
}
