package minesweeper;

public enum CellPosition {
    UPPER_LEFT(new Position(-1, -1)),
    UPPER_MIDDLE(new Position(-1, 0)),
    UPPER_RIGHT(new Position(-1, 1)),
    CENTER_LEFT(new Position(0, -1)),
    CENTER_MIDDLE(new Position(0, 0)),
    CENTER_RIGHT(new Position(0, 1)),
    LOWER_LEFT(new Position(1, -1)),
    LOWER_MIDDLE(new Position(1, 0)),
    LOWER_RIGHT(new Position(1, 1));

    private final Position position;

    CellPosition(Position position) {
        this.position = position;
    }

    Position getPosition() {
        return position;
    }
}
