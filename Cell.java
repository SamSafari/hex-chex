public abstract class Cell {

    private final int row, col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public static final class EmptyCell extends Cell {

        public EmptyCell(final int row, final int col) {
            super(row, col);
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        //TODO
        /*@Override
        public String toString() {
            return "[" + getRow() + "," + getCol() + "]";
        }*/
        @Override
        public String toString() {
            return "[ ]";
        }

    }

    public static final class OccupiedCell extends Cell {

        private final Piece piece;

        public OccupiedCell(final int row, final int col, final Piece piece) {
            super(row, col);
            this.piece = piece;
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.piece;
        }

        @Override
        public String toString() {
            return "[X]";
        }

    }

}
