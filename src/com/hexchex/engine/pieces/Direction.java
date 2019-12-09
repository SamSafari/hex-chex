package com.hexchex.engine.pieces;

import com.hexchex.engine.board.*;

public enum Direction {

    /**
     * All the valid directions and distances that a Piece can travel in one move. Notice that the Cardinal directions
     * (N and S) increment by 2, and the InterCardinal directions (NW, NE, SW, SE) increment by 1. This is due to the
     * "double height" structure of the Board class' hexagonal coordinate system
     */
    N(-2, 0), S(2, 0), NW(-1, -1), NE(-1, 1), SW(1, -1), SE(1, 1);

    private int row, col;

    Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Based on a given startCell, calculates a Cell whose position is incremented by Direction
     * @param startCell cell moving from
     * @return a new EmptyCell with the position incremented by Direction
     */
    public Cell findNewCell(Cell startCell) {
        return new Cell.EmptyCell(startCell.row() + row, startCell.col() + col);
    }

}
