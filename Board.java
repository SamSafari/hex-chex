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

    public Cell[][] generateBoard() {

        int cellNumberCounter = 0;
        board = new Cell[BOARD_HEIGHT][BOARD_WIDTH];

        for (int col = 0; col < BOARD_WIDTH; col++) {

            for (int row = 0; row < BOARD_HEIGHT; row++) {

                if (col % 2 == 0) {
                    if (row == 0) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(cellNumberCounter++);
                    }

                } else {
                    if (row == BOARD_HEIGHT - 1) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(cellNumberCounter++);
                    }
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

        Board board = new Board(1, 1);

        for(int i = 0; i < board.getBOARD_WIDTH(); i++)
        {
            for(int j = 0; j < board.getBOARD_HEIGHT(); j++)
            {
                System.out.print(board.generateBoard()[i][j]);
            }
            System.out.println();
        }

    }
}