package com.hexchex.engine.pieces;

import com.hexchex.engine.board.*;

public class Piece {

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
        this.position = new Cell.OccupiedCell(row, col, this);
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

        } else if (endCell.isOccupied()) {
            if (endCell.getPiece().getTeam() == team) {

                throw new IllegalMoveException("You cannot move to a cell occupied by one of your pieces!");

            } else {

                assert endCell.getPiece().getTeam() != team;
                endCell.getPiece().getTeam().removePiece(endCell.getPiece());

                executeMove(startCell, endCell);

            }
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

        setPosition(endCell);

        startCell = new Cell.EmptyCell(startCell.row(), startCell.col());
        endCell = new Cell.OccupiedCell(endCell.row(), endCell.col(), this);

        board.getBoard()[startCell.row()][startCell.col()] = startCell;
        board.getBoard()[endCell.row()][endCell.col()] = endCell;

    }

}
