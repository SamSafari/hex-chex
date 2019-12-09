package com.hexchex.engine.board;

import com.hexchex.engine.pieces.*;

import java.io.Serializable;

/**
 * Have a row and col position and are either OccupiedCells or EmptyCells. OccupiedCells
 * must store a Piece
 */
public abstract class Cell implements Serializable {

    private final int row;
    private final int col;

    public Cell(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public static final class EmptyCell extends Cell {

        public EmptyCell(final int row, final int col) {
            super(row, col);
        }

        public EmptyCell(Cell cell) {
            super(cell.row, cell.col);
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

        public OccupiedCell(final int row, final int col, final Piece piece) {
            super(row, col);
            this.piece = piece;
        }

        public OccupiedCell(Cell cell, Piece piece) {
            super(cell.row, cell.col);
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
