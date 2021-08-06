package minesweeper;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("How many mines do you want on the field? ");
        int mines = scanner.nextInt();
        scanner.nextLine();

        Minesweeper minesweeper = new Minesweeper(9, mines);

        int row, col;
        String[] coordinatesAndAction;
        while (!minesweeper.isGameOver()) {
            try {
                System.out.println("Set/unset mines marks or claim a cell as free:");
                coordinatesAndAction = scanner.nextLine().split("\\s+");
                row = Integer.parseInt(coordinatesAndAction[0]);
                col = Integer.parseInt(coordinatesAndAction[1]);
                minesweeper.handleAction(coordinatesAndAction[2].toUpperCase(), row, col);
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

}
