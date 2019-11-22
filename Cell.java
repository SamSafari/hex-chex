public abstract class Cell {

    private final int row, column;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }


    public static final class EmptyCell extends Cell {

        public EmptyCell(final int row, final int column) {
            super(row, column);
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
        @Override
        public String toString() {
            return getRow() + "," + getColumn() + " "  ;
        }

    }

    public static final class OccupiedCell extends Cell {

        private final Piece piece;

        public OccupiedCell(final int row, final int column, final Piece piece) {
            super(row, column);
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

    }

}
