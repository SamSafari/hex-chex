package com.hexchex.engine.board;

import com.hexchex.engine.pieces.*;

public abstract class Cell {

    private final int row;
    private final int col;
    private final int cellID;

    public Cell(final int row, final int col, int cellID) {
        this.row = row;
        this.col = col;
        this.cellID = cellID;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public int getCellID() {
        return cellID;
    }

    public static final class EmptyCell extends Cell {

        public EmptyCell(final int row, final int col, final int cellID) {
            super(row, col, cellID);
        }

        public EmptyCell(Cell cell) {
            super(cell.row, cell.col, cell.getCellID());
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "[ ]";
        }

    }

    public static final class OccupiedCell extends Cell {

        private final Piece piece;

        public OccupiedCell(final int row, final int col, final int cellID, final Piece piece) {
            super(row, col, cellID);
            this.piece = piece;
        }

        public OccupiedCell(Cell cell, Piece piece) {
            super(cell.row, cell.col, cell.getCellID());
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
            return "[" + piece.getTeam().getAbbr() + "]";
        }

    }

}
