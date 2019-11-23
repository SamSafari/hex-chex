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

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {

                if (col % 2 == 0) {
                    if (row == 0) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(row, col);
                    }

                } else if (row == BOARD_HEIGHT - 1) {
                        board[row][col] = null;
                    } else {
                        board[row][col] = new Cell.EmptyCell(row, col);
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


    public void setupDefaultBoard(Team team1, Team team2) {

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 != 0) {
                Piece piece = new Piece(0, col, team1, this);
                board[0][col] = piece.getPosition();
                team1.addPiece(piece);
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 == 0) {
                Piece piece = new Piece(1, col, team1, this);
                board[1][col] = piece.getPosition();
                team1.addPiece(piece);
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 == 0) {
                Piece piece = new Piece(BOARD_HEIGHT - 1, col, team2, this);
                board[BOARD_HEIGHT - 1][col] = piece.getPosition();
                team2.addPiece(piece);
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (col % 2 != 0) {
                Piece piece = new Piece(BOARD_HEIGHT - 2, col, team2, this);
                board[BOARD_HEIGHT - 2][col] = piece.getPosition();
                team2.addPiece(piece);
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