package com.hexchex.engine.pieces;

import com.hexchex.engine.board.*;

import java.io.Serializable;

public class Piece implements Serializable {

    private int row, col;
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

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Cell getPosition() {
        return position;
    }

    private void setPosition(Cell position) {
        this.position = position;
    }

    private void setRow(int row) {
        this.row = row;
    }

    private void setCol(int col) {
        this.col = col;
    }

    /**
     * Moves a piece one space in the specified direction on the specified board. Will replace enemy pieces moved onto
     * with the piece being moved.
     * @throws IllegalMoveException if the Cell in the specified direction is an invalid place to move (i.e. it is off
     *                              the board or occupied by a piece of the same team)
     */
    public void move(Direction d) throws IllegalMoveException {

        Cell startCell = board.findCell(position);
        Cell endCell = board.findCell(d.findNewCell(startCell));

        if (board.getBoard()[endCell.row()][endCell.col()] == null) {

            throw new IllegalMoveException("You cannot move that direction!");

        } else if (!endCell.isOccupied()) {

            executeMove(startCell, endCell);

        } else if (endCell.getPiece().getTeam() == team) {

                throw new IllegalMoveException("You cannot move to a cell occupied by one of your pieces!");

        } else {

            assert endCell.getPiece().getTeam() != team;
            endCell.getPiece().getTeam().removePiece(endCell.getPiece());

            executeMove(startCell, endCell);

        }

    }

    public boolean move(Cell startCell, Cell endCell) {
        if(validateMove(startCell, endCell)) {
            executeMove(startCell, endCell);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method to move a piece from one cell to another and change the states of the Cell objects from
     * EmptyCell to OccupiedCell, and vice versa.
     * @param startCell the original cell the piece is moving from (will be converted to an EmptyCell)
     * @param endCell the new cell the piece is moving to (will be converted to an OccupiedCell regardless of whether
     *                it was before)
     */
    private void executeMove(Cell startCell, Cell endCell) {

        int oldCellID = position.getCellID();

        if (endCell.isOccupied()) {
            endCell.getPiece().getTeam().removePiece(endCell.getPiece());
        }

        setPosition(endCell);

        startCell = new Cell.EmptyCell(startCell.row(), startCell.col(), oldCellID);
        endCell = new Cell.OccupiedCell(endCell.row(), endCell.col(), endCell.getCellID(),this);

        board.getBoard()[startCell.row()][startCell.col()] = startCell;
        board.getBoard()[endCell.row()][endCell.col()] = endCell;

    }

    /**
     * Checks whether a move between any two (possibly non-adjacent) cells is legal.
     * @param startCell cell moving from
     * @param endCell cell moving to
     * @return true if the move is legal, IllegalMoveException() otherwise
     */
    private boolean validateMove(Cell startCell, Cell endCell) {

        if (!endCell.isOccupied() || endCell.getPiece().getTeam() != team) {

            int numValidDirections = 0;
            for(Direction d : Direction.values()) {
                if (endCell.row() == d.findNewCell(startCell).row()
                 && endCell.col() == d.findNewCell(startCell).col()) {
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


