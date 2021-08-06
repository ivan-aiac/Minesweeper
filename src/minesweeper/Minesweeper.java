package minesweeper;

import java.util.*;

public class Minesweeper {

    public static final char EMPTY_TOKEN = '.';
    public static final char MARK_TOKEN = '*';
    public static final char MINE_TOKEN = 'X';
    public static final char REVEALED_TOKEN = '/';

    private static final String ACTION_MINE = "MINE";
    private static final String ACTION_FREE = "FREE";

    private final int size;
    private final int mines;
    private final Cell[][] mineField;
    private final List<Position> minePositions;
    private final Queue<Position> queue;
    private int safeCells;
    private int minesFound;
    private int markedCells;
    private boolean gameOver;
    private boolean firstExplored;

    public Minesweeper(int size, int mines) {
        this.size = size;
        this.mines = mines;
        this.mineField = new Cell[size][size];
        minePositions = new ArrayList<>();
        minesFound = 0;
        markedCells = 0;
        safeCells = size * size - mines;
        gameOver = false;
        firstExplored = false;
        queue = new ArrayDeque<>();
        // Initialize Cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mineField[i][j] = new Cell();
            }
        }
        printBoard();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void handleAction(String action, int row, int col) {
        Position position = new Position(col - 1, row - 1);
        if (isPositionOutOfBounds(position)) {
            throw new RuntimeException(String.format("Row: %d and Col: %d out of bounds.", position.getRow() + 1, position.getCol() + 1));
        }
        if (isFieldReady(action, position)) {
            if (ACTION_MINE.equals(action)) {
                markCell(position);
            } else {
                freeCell(position);
            }
            printBoard();
            if (gameOver) {
                System.out.println("You stepped on a mine and failed!");
            }
            if (safeCells == 0 || (minesFound == mines && markedCells == 0)) {
                gameOver = true;
                System.out.println("Congratulations! You found all the mines!");
            }
        }
    }

    private void freeCell(Position position) {
        Cell cell = getCellAt(position);
        if (cell.getContent() == MINE_TOKEN) {
            gameOver = true;
            revealMines();
        } else if (cell.getContent() == EMPTY_TOKEN) {
            revealEmptyCells(position);
        } else {
            if (cell.getDisplayContent() == MARK_TOKEN) {
                markedCells--;
            }
            cell.reveal();
            safeCells--;
        }
    }

    private void revealEmptyCells(Position position) {
        if (isPositionOutOfBounds(position) || getCellAt(position).isRevealed()) {
            return;
        }
        Cell cell = getCellAt(position);
        if (cell.getDisplayContent() == MARK_TOKEN) {
            markedCells--;
        }
        if (cell.getContent() != EMPTY_TOKEN) {
            safeCells--;
            cell.reveal();
        } else {
            safeCells--;
            cell.setContent(REVEALED_TOKEN);
            cell.reveal();
            for (CellPosition cp: CellPosition.values()) {
                revealEmptyCells(Position.sum(position, cp.getPosition()));
            }
        }
    }

    private void revealMines() {
        minePositions.forEach(position -> getCellAt(position).reveal());
    }

    private void markCell(Position position) {
        Cell cell = getCellAt(position);
        if (cell.getDisplayContent() == MARK_TOKEN) {
            if (cell.getContent() == MINE_TOKEN) {
                minesFound--;
            } else {
                markedCells--;
            }
            cell.setDisplayContent(EMPTY_TOKEN);
        } else {
            if (cell.getContent() == MINE_TOKEN) {
                minesFound++;
            } else {
                markedCells++;
            }
            cell.setDisplayContent(MARK_TOKEN);
        }
    }

    private void printBoard() {
        StringBuilder board = new StringBuilder();
        board.append(" |");
        for (int i = 1; i <= size; i++) {
            board.append(i);
        }
        board.append("|\n");
        board.append(String.format("-|%s|\n", "-".repeat(size)));
        for (int row = 0; row < size; row++) {
            board.append(row + 1).append('|');
            for (int col = 0; col < size; col++){
                board.append(mineField[row][col].getDisplayContent());
            }
            board.append("|\n");
        }
        board.append(String.format("-|%s|\n", "-".repeat(size)));
        System.out.println(board);
    }

    private boolean isFieldReady(String action, Position position) {
        if (firstExplored) {
            return true;
        } else {
            Cell cell;
            if (ACTION_FREE.equals(action)) {
                firstExplored = true;
                prepareField(position);
                while (!queue.isEmpty()) {
                    cell = getCellAt(queue.poll());
                    if (cell.getDisplayContent() == MARK_TOKEN) {
                        if (cell.getContent() == MINE_TOKEN) {
                            minesFound++;
                        } else {
                            markedCells++;
                        }
                    }
                }
            } else {
                cell = getCellAt(position);
                queue.offer(position);
                if (cell.getDisplayContent() == EMPTY_TOKEN) {
                    cell.setDisplayContent(MARK_TOKEN);
                } else {
                    cell.setDisplayContent(EMPTY_TOKEN);
                }
                printBoard();
            }
        }
        return firstExplored;
    }

    private void prepareField(Position playerPosition) {
        // Bury mines and set hints
        Random random = new Random();
        Position minePosition;
        int mines = this.mines;
        Cell cell;
        while (mines > 0) {
            minePosition = new Position(random.nextInt(size), random.nextInt(size));
            if (canPlaceMineAtCell(minePosition, playerPosition)) {
                cell = getCellAt(minePosition);
                cell.setContent(MINE_TOKEN);
                minePositions.add(minePosition);
                mines--;
                // Set hints check cells around
                for (CellPosition cellPosition: CellPosition.values()) {
                    increaseMineCount(Position.sum(minePosition, cellPosition.getPosition()));
                }
            }
        }
    }

    private void increaseMineCount(Position position) {
        if (!isPositionOutOfBounds(position)) {
            Cell cell = getCellAt(position);
            if (cell.getContent() == EMPTY_TOKEN) {
                cell.setContent('1');
            } else if (cell.getContent() != MINE_TOKEN){
                cell.increaseCount();
            }
        }
    }

    private boolean canPlaceMineAtCell(Position minePosition, Position playerPosition) {
        Cell cell = getCellAt(minePosition);
        // Is cell already a mine
        if (cell.getContent() == MINE_TOKEN) {
            return false;
        }
        // First explore is always EMPTY cell so have to check cells around
        for (CellPosition cellPosition: CellPosition.values()) {
            if (playerPosition.equals(Position.sum(minePosition, cellPosition.getPosition()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isPositionOutOfBounds(Position p) {
        return p.getRow() < 0 || p.getRow() >= size || p.getCol() < 0 || p.getCol() >= size;
    }

    private Cell getCellAt(Position position) {
        return mineField[position.getRow()][position.getCol()];
    }

}
