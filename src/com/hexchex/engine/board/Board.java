package com.hexchex.engine.board;

import com.hexchex.engine.pieces.IllegalMoveException;
import com.hexchex.engine.pieces.Piece;
import com.hexchex.engine.pieces.Team;

public class Board {

    private Cell[][] board;
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;

    public int BOARD_WIDTH() {
        return BOARD_WIDTH;
    }

    public int BOARD_HEIGHT() {
        return BOARD_HEIGHT;
    }

    /**
     * Generates the game board which appears as staggered columns of cells to mimic
     * a hexagonal cell structure.
     * @return a staggered board of EmptyCell objects with appropriate row and column values
     */
    public Cell[][] generateBoard() {

        board = new Cell[BOARD_HEIGHT][BOARD_WIDTH];
        int cellIDCounter = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {

                if (col % 2 == 0) {
                    if (row == 0) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(row, col, cellIDCounter++);
                    }

                } else if (row == BOARD_HEIGHT - 1) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(row, col, cellIDCounter++);
                    }
                }
            }

        return board;
    }

    public Board(int width, int height) {
        this.BOARD_WIDTH = width;
        this.BOARD_HEIGHT = height;
        board = generateBoard();
    }

    public Board() {
        this(8, 8);
        board = generateBoard();
    }

    public Cell[][] getBoard() {
        return board;
    }

    public int getNumTiles() {
        int numTiles = 0;
        for(int i = 0; i < board.length; i++) {
            numTiles += board[i].length;
        }
        return numTiles;
    }

    public Cell findCell(Cell other) throws IllegalMoveException {
        try {
            return board[other.row()][other.col()];
        } catch(NullPointerException e) {
            throw new IllegalMoveException("That cell doesn't exist!");
        }
    }

    public void replaceCell(Cell other) throws IllegalMoveException {
        try {
            board[other.row()][other.col()] = other;
        } catch(NullPointerException e) {
            throw new IllegalMoveException("That cell doesn't exist!");
        }
    }

    public void addPiece(int row, int col, Piece piece) {
        if (board[row][col].isOccupied()) {
            board[row][col].getPiece().getTeam().removePiece(board[row][col].getPiece());
        }

        board[row][col] = new Cell.OccupiedCell(row, col, board[row][col].getCellID(), piece);
        piece.getTeam().addPiece(piece);
    }


    public void setupDefaultBoard(Team team1, Team team2) {

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 != 0) {
                addPiece(0, col, new Piece(0, col, team1, this));
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 == 0) {
                addPiece(1, col, new Piece(1, col, team1, this));
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 == 0) {
                addPiece(BOARD_HEIGHT - 1, col, new Piece(BOARD_HEIGHT - 1, col, team2, this));
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 != 0) {
                addPiece(BOARD_HEIGHT - 2, col, new Piece(BOARD_HEIGHT - 2, col, team2, this));
            }
        }

    }

    @Override
    public String toString() {
        String output = "";
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {

                if (board[row][col] == null) {
                    output += "   ";
                } else {
                    output += board[row][col];
                }

            }
            output += "\n";
        }
        return output;
    }


}