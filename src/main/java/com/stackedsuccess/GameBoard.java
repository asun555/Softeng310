package com.stackedsuccess;

import com.stackedsuccess.controllers.GameScreenController;
import com.stackedsuccess.tetriminos.*;

// This class defines the game board and functionality to check board state
public class GameBoard {
    private static final int DEFAULT_BOARD_WIDTH = 10;
    private static final int DEFAULT_BOARD_HEIGHT = 20;

    private final int[][] board;
    private Tetrimino currentTetrimino;
    private int frameCount;

    public GameBoard() {
        board = new int[DEFAULT_BOARD_HEIGHT][DEFAULT_BOARD_WIDTH];
        initializeBoard();
    }

    public GameBoard(int width, int height) {
        board = new int[width][height];
        initializeBoard();
    }

    /** Setup initial tetrimino pieces and board metrics. */
    private void initializeBoard() {
        currentTetrimino = TetriminoFactory.createRandomTetrimino();
        frameCount = 0;
    }

    // TODO: Change logic of how speed of current tetrimino is calculated based on frame rate.
    /** Update the state of the board. */
    public void update() {
        frameCount++;
        // Stagger automatic tetrimino movement based on frame count
        if (frameCount % 100 == 0) {
            if (!checkCollision(currentTetrimino.xPos, currentTetrimino.yPos + 1)) {
                moveDown();
            } else {
                placeTetrimino(currentTetrimino);
                clearFullRows();
                currentTetrimino = TetriminoFactory.createRandomTetrimino();
            }
        }
        printBoard();
    }

    // TODO: Refactor the movement functions to tetrimino object to separate movement from game board.
    /** Move current tetrimino downwards by one cell. */
    public void moveDown() {
        if (!checkCollision(currentTetrimino.xPos, currentTetrimino.yPos+1)) currentTetrimino.yPos++;;
    }

    /** Move current tetrimino left by one cell. */
    public void moveLeft() {
        if (!checkCollision(currentTetrimino.xPos-1, currentTetrimino.yPos)) currentTetrimino.xPos--;;
    }

    /** Move current tetrimino right by one cell. */
    public void moveRight() {
        if (!checkCollision(currentTetrimino.xPos+1, currentTetrimino.yPos)) currentTetrimino.xPos++;;
    }

    public void hardDrop() {
        while (!checkCollision(currentTetrimino.xPos, currentTetrimino.yPos+1)) currentTetrimino.yPos++;
    }

    /** Rotate current tetrimino clockwise. */
    public void rotateClockwise() {
        currentTetrimino.rotateClockwise();
    }

    /** Rotate current tetrimino counter-clockwise. */
    public void rotateCounterClockwise() {
        currentTetrimino.rotateCounterClockwise();
    }

    /**
     * Appends new tetrimino to the game board.
     *
     * @param tetrimino the tetrimino to place on the game board.
     */
    private void placeTetrimino(Tetrimino tetrimino) {
        int[][] layout = tetrimino.getTetriminoLayout();

        for (int layoutY = 0; layoutY < tetrimino.getHeight(); layoutY++) {
            for (int layoutX = 0; layoutX < tetrimino.getWidth(); layoutX++) {
                if (layout[layoutY][layoutX] != 0) {
                    board[tetrimino.yPos + layoutY][tetrimino.xPos + layoutX] = layout[layoutY][layoutX];
                }
            }
        }
    }

    /** Clears full rows and moves rows above downwards. */
    private void clearFullRows() {
        for (int y = 0; y < board.length; y++) {
            if (isRowFull(y, board[y])) {
                shiftRowsDown(y);
            }
        }
    }

    /**
     * Moves rows above certain row downwards and creates empty line at top of game board.
     *
     * @param fromYAxis the start y-axis for moving subsequent rows downward
     */
    private void shiftRowsDown(int fromYAxis) {
        for (int y = fromYAxis; y > 0; y--) {
            System.arraycopy(board[y - 1], 0, board[y], 0, board[0].length);
        }

        for (int x = 0; x < board[0].length; x++) board[0][x] = 0;
    }

    // TODO: Remove when visuals are ported to JavaFX.
    /** Debug utility to help separate game board from JavaFX elements. */
    private void printBoard() {
        int[][] layout = currentTetrimino.getTetriminoLayout();
        System.out.println("===| " + frameCount + " |===");
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                if ((y >= currentTetrimino.yPos && x >= currentTetrimino.xPos && y < currentTetrimino.yPos + currentTetrimino.getHeight() && x < currentTetrimino.xPos + currentTetrimino.getWidth()) && layout[y - currentTetrimino.yPos][x - currentTetrimino.xPos] != 0) {
                    System.out.print("█" + " ");
                } else if (board[y][x] != 0) {
                    System.out.print("█" + " ");
                } else {
                    System.out.print(0 + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Checks if current tetrimino will collide with borders or existing cells.
     *
     * @param x the x position to start check for collision
     * @param y the y position to start check for collision
     * @return true if current tetrimino will collide with border or existing cells
     */
    private boolean checkCollision(int x, int y) {
        int[][] layout = currentTetrimino.getTetriminoLayout();
        int newX, newY;

        for (int layoutY = 0; layoutY < currentTetrimino.getHeight(); layoutY++) {
            for (int layoutX = 0; layoutX < currentTetrimino.getWidth(); layoutX++) {
                if (layout[layoutY][layoutX] != 0) {
                    newX = x + layoutX;
                    newY = y + layoutY;

                    // Check for out of bound collisions
                    if (isOutOfBounds(newX, newY)) return true;

                    // Check for existing tetrimino cells
                    if (isCellOccupied(newX, newY)) return true;
                }
            }
        }
       return false;
    }

    /**
     * Checks if coordinates are outside the bounds of the game board.
     *
     * @param x the x position to check
     * @param y the y position to check
     * @return true if the coordinates are out of bounds
     */
    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= board[0].length || y < 0 || y >= board.length;
    }

    /**
     * Check if coordinates are occupied in the game board.
     *
     * @param x the x position to check
     * @param y the y position to check
     * @return true if cell is occupied in game board
     */
    private boolean isCellOccupied(int x, int y) {
        return board[y][x] != 0;
    }


    /**
     * Check if the contents within a row are full of tetrimino cells.
     *
     * @param rowY the y level or row number of given row
     * @param row the row to check
     * @return whether the row is full or not
     */
    private boolean isRowFull(int rowY, int[] row) {
        for (int x = 0; x < row.length; x++) {
            if (!isCellOccupied(x, rowY)) return false;
        }
        return true;
    }

}
