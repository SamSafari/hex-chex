package com.hexchex.engine.pieces;

import com.hexchex.engine.board.*;

public enum Direction {

    N(-2, 0), S(2, 0), NW(-1, -1), NE(-1, 1), SW(1, -1), SE(1, 1);

    private int row, col;

    Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Cell findNewCell(Cell startCell) {
        return new Cell.EmptyCell(startCell.row() + row, startCell.col() + col, -1);
    }

}
