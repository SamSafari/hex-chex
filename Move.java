public class Move {

    private Board board;
    private Piece piece;
    private DIRECTIONS direction;

    public Move(Piece piece, Board board, DIRECTIONS direction) {
        this.piece = piece;
        this.board = board;
        this.direction = direction;
    }

    /**
     * Moves a piece one space in the specified direction on the specified board. Will replace enemy pieces moved onto
     * with the piece being moved.
     * @throws IllegalMoveException if the Cell in the specified direction is an invalid place to move (i.e. it is off
     *                              the board or occupied by a piece of the same team)
     */
    public void move() throws IllegalMoveException {
        int currentRow = piece.getRow();
        int currentCol = piece.getColumn();
        Cell currentCell = board.getBoard()[currentRow][currentCol];
        Cell newCell = null;
        switch (direction) {
            case N:
                newCell = board.getBoard()[currentRow - 1][currentCol];
            case S:
                newCell = board.getBoard()[currentRow + 1][currentCol];
            case NE:
                newCell = board.getBoard()[currentRow - 1][currentCol + 1];
            case NW:
                newCell = board.getBoard()[currentRow - 1][currentCol - 1];
            case SE:
                newCell = board.getBoard()[currentRow + 1][currentCol + 1];
            case SW:
                newCell = board.getBoard()[currentRow + 1][currentCol - 1];
        }

        if (newCell == null) {
            throw new IllegalMoveException("You cannot move that direction!");
        }

        if (!newCell.isOccupied()) {
            executeMove(currentCell, newCell);
        }

        if (newCell.isOccupied()) {
            if (newCell.getPiece().getTeam() == this.piece.getTeam()) {

                throw new IllegalMoveException("You cannot move to a cell occupied by one of your pieces!");

            } else {

                assert newCell.getPiece().getTeam() != this.piece.getTeam();
                newCell.getPiece().getTeam().getPieces().remove(newCell.getPiece());

                executeMove(currentCell, newCell);

            }
        }

    }

    /**
     * Helper method to move a piece from one cell to another and change the states of the Cell objects from
     * EmptyCell to OccupiedCell, and vice versa.
     * @param currentCell the original cell the piece is moving from (will be converted to an EmptyCell)
     * @param newCell the new cell the piece is moving to (will be converted to an OccupiedCell regardless of whether
     *                it was before)
     */
    private void executeMove(Cell currentCell, Cell newCell) {
        piece.setRow(newCell.getRow());
        piece.setColumn(newCell.getColumn());

        currentCell = new Cell.EmptyCell(currentCell.getRow(), currentCell.getColumn());
        newCell = new Cell.OccupiedCell(newCell.getRow(), newCell.getColumn(), piece);

        board.getBoard()[currentCell.getRow()][currentCell.getColumn()] = currentCell;
        board.getBoard()[newCell.getRow()][newCell.getColumn()] = newCell;
    }

}

