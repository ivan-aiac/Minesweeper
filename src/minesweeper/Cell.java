package minesweeper;

public class Cell {

    private char content;
    private char displayContent;
    private boolean revealed;

    public Cell() {
        displayContent = Minesweeper.EMPTY_TOKEN;
        content = Minesweeper.EMPTY_TOKEN;
        revealed = false;
    }

    public char getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(char displayContent) {
        this.displayContent = displayContent;
    }

    public char getContent() {
        return content;
    }

    public void setContent(char content) {
        this.content = content;
    }

    public void increaseCount() {
        content++;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void reveal() {
        displayContent = content;
        revealed = true;
    }
}
