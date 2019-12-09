package com.hexchex.engine.pieces;

import com.hexchex.engine.board.*;

import java.io.Serializable;

/**
 * Piece Objects have a Cell they occupy, a Team they represent, and a Board they play on
 */
public class Piece implements Serializable {

    private Cell position;
    private final Team team;
    private final Board board;

    public Piece(Cell position, Team team, Board board) {
        this.position = position;
        this.team = team;
        this.board = board;
    }

    public Piece(int row, int col, Team team, Board board) {
        this.position = board.getBoard()[row][col];
        this.team = team;
        this.board = board;
    }

    public Team getTeam() {
        return this.team;
    }

    public Cell getPosition() {
        return position;
    }

    private void setPosition(Cell position) {
        this.position = position;
    }

    /**
     * Moves this piece from its current cell to another cell.
     * @param endCell cell moving to
     * @return true if the move is successful, false otherwise
     */
    public boolean move(Cell endCell) {
        if(validateMove(endCell)) {
            executeMove(endCell);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method to move this piece from its current cell to another, changing the states of the Cell objects from
     * EmptyCell to OccupiedCell, and vice versa.
     * @param endCell the new cell the piece is moving to (will be converted to an OccupiedCell regardless of whether
     *                it was before)
     */
    private void executeMove(Cell endCell) {

        if (endCell.isOccupied()) {
            endCell.getPiece().getTeam().removePiece(endCell.getPiece());
        }

        endCell = new Cell.OccupiedCell(endCell.row(), endCell.col(), this);

        board.getBoard()[position.row()][position.col()]
                = new Cell.EmptyCell(position.row(), position.col());
        setPosition(endCell);
        board.getBoard()[endCell.row()][endCell.col()] = endCell;

    }

    /**
     * Checks whether a move between this piece's current position and any
     * other (possibly non-adjacent) cell is legal.
     * @param endCell cell moving to
     * @return true if the move is legal, IllegalMoveException() if endCell is occupied by this
     * piece's team, and NullCellException() if endCell is off the board.
     */
    private boolean validateMove(Cell endCell) {

        if (!endCell.isOccupied() || endCell.getPiece().getTeam() != team) {

            int numValidDirections = 0;
            for(Direction d : Direction.values()) {
                if (endCell.row() == d.findNewCell(position).row()
                 && endCell.col() == d.findNewCell(position).col()) {
                    numValidDirections++;
                }
            }

            if (numValidDirections > 0) {
                return true;
            }

        } else if (board.getBoard()[endCell.row()][endCell.col()] == null) {

            System.out.print(" (Illegal)\n");
            throw new NullCellException("You cannot move that direction!");

        }

        System.out.print(" (Illegal)\n");
        throw new IllegalMoveException("You can only move to adjacent Hexagons!");

    }


}


