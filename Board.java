public class Board {

    private Cell[][] board = generateBoard();
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;

    public int getBOARD_WIDTH() {
        return BOARD_WIDTH;
    }

    public int getBOARD_HEIGHT() {
        return BOARD_HEIGHT;
    }

    /**
     * Generates the game board which appears as staggered columns of cells to mimic
     * a hexagonal cell structure.
     * @return a staggered board of EmptyCell objects with appropriate row and column values
     */
    public Cell[][] generateBoard() {

        board = new Cell[BOARD_HEIGHT][BOARD_WIDTH];
        for (int colIndex = 0; colIndex < BOARD_WIDTH; colIndex++) {

            for (int rowIndex = 0; rowIndex < BOARD_HEIGHT; rowIndex++) {

                if (colIndex % 2 == 0) {
                    if (rowIndex == 0) {
                        board[rowIndex][colIndex] = null;
                    } else {
                        board[rowIndex][colIndex] = new Cell.EmptyCell(rowIndex, colIndex);
                    }

                } else if (rowIndex == BOARD_HEIGHT - 1) {
                        board[rowIndex][colIndex] = null;
                    } else {
                        board[rowIndex][colIndex] = new Cell.EmptyCell(rowIndex, colIndex);
                    }
                }
            }

        return board;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public Board(int BOARD_WIDTH, int BOARD_HEIGHT) {
        this.BOARD_WIDTH = BOARD_WIDTH;
        this.BOARD_HEIGHT = BOARD_HEIGHT;
    }

    public Board() {
        this(8, 8);
    }




}

class Main {
    public static void main(String[] args) {

        Board board = new Board(8, 8);

        for(int i = 0; i < board.getBOARD_WIDTH(); i++) {

            for(int j = 0; j < board.getBOARD_HEIGHT(); j++) {

                System.out.print(board.generateBoard()[i][j]);

            }
            System.out.println();
        }

    }
}