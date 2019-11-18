import com.sun.xml.internal.bind.v2.TODO;

import java.util.HashMap;
import java.util.Map;

public abstract class Cell {

    private final int cellNumber;
    
    /*private static final Map<Integer, EmptyCell> EMPTY_CELLS = generateEmptyCells();

    private static Map<Integer, EmptyCell> generateEmptyCells() {

        final Map<Integer, EmptyCell> emptyCellMap = new HashMap<>();

        for(int i = 0; i < 64; i++) {
            emptyCellMap.put(i, new EmptyCell(i));
        }

        return emptyCellMap;

    }

    public static Cell createCell(final int cellNumber, final Piece piece) {
        if(piece != null) {
            return new OccupiedCell(cellNumber, piece);
        } else {
            return EMPTY_CELLS.get(cellNumber);
        }
    }*/

    public Cell(final int cellNumber) {
        this.cellNumber = cellNumber;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public static final class EmptyCell extends Cell {

        public EmptyCell(final int cellNumber) {
            super(cellNumber);
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
            return "[  ]";
        }

    }

    public static final class OccupiedCell extends Cell {

        private final Piece piece;

        public OccupiedCell(final int cellNumber, final Piece piece) {
            super(cellNumber);
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
