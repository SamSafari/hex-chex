public class Board {

    private Cell[][] board;
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;

    public int BOARD_WIDTH() {
        return BOARD_WIDTH;
    }

    public int BOARD_HEIGHT() {
        return BOARD_HEIGHT;
    }

    /**
     * Generates the game board which appears as staggered columns of cells to mimic
     * a hexagonal cell structure.
     * @return a staggered board of EmptyCell objects with appropriate row and column values
     */
    public Cell[][] generateBoard() {

        board = new Cell[BOARD_HEIGHT][BOARD_WIDTH];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {

                if (col % 2 == 0) {
                    if (row == 0) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(row, col);
                    }

                } else if (row == BOARD_HEIGHT - 1) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(row, col);
                    }
                }
            }

        return board;
    }

    public Board(int BOARD_WIDTH, int BOARD_HEIGHT) {
        this.BOARD_WIDTH = BOARD_WIDTH;
        this.BOARD_HEIGHT = BOARD_HEIGHT;
        board = generateBoard();
    }

    public Board() {
        this(8, 8);
        board = generateBoard();
    }

    public Cell[][] getBoard() {
        return board;
    }

    @Override
    public String toString() {
        String output = "";
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {

                if (board[row][col] == null) {
                    output += "   ";
                } else {
                    output += board[row][col];
                }

            }
            output += "\n";
        }
        return output;
    }


}

class Main {
    public static void main(String[] args) {

        Board board = new Board(8, 8);

        for (int i = 0; i < board.BOARD_WIDTH(); i++) {

            for (int j = 0; j < board.BOARD_HEIGHT(); j++) {

                if (board.getBoard()[i][j] == null) {
                    System.out.print("     ");
                } else {
                    System.out.print(board.getBoard()[i][j]);
                }

            }
            System.out.println();
        }

    }
}