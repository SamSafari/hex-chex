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

        board = new Cell[BOARD_HEIGHT * 2][BOARD_WIDTH];
        int cellIDCounter = 0;

        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board[row].length; col++) {

                if (col % 2 == 0) {
                    if (row % 2 == 0) {
                        board[row][col] = new Cell.EmptyCell(row, col, cellIDCounter++);
                    } else {
                        board[row][col] = null;
                    }

                } else {
                    if (row % 2 != 0) {
                        board[row][col] = new Cell.EmptyCell(row, col, cellIDCounter++);
                    } else {
                        board[row][col] = null;
                    }

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

    private void addPiece(int row, int col, Piece piece) {

        if (board[row][col] != null) {

            if (board[row][col].isOccupied()) {
                board[row][col].getPiece().getTeam().removePiece(board[row][col].getPiece());
            }

            board[row][col] = new Cell.OccupiedCell(row, col, board[row][col].getCellID(), piece);
            piece.getTeam().addPiece(piece);
        }

    }

    public void setupDefaultBoard(Team team1, Team team2) {

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[0][col] != null) {
                addPiece(0, col, new Piece(0, col, team1, this));
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[1][col] != null) {
                addPiece(1, col, new Piece(1, col, team1, this));
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[BOARD_HEIGHT*2-1][col] != null) {
                addPiece(BOARD_HEIGHT * 2 - 1, col, new Piece(BOARD_HEIGHT * 2 - 1, col, team2, this));
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[BOARD_HEIGHT * 2 - 2][col] != null) {
                addPiece(BOARD_HEIGHT * 2 - 2, col, new Piece(BOARD_HEIGHT * 2 - 2, col, team2, this));
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