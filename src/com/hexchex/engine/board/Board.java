package com.hexchex.engine.board;

import com.hexchex.engine.pieces.IllegalMoveException;
import com.hexchex.engine.pieces.Piece;
import com.hexchex.engine.pieces.Team;

import java.io.Serializable;

public class Board implements Serializable {

    private Cell[][] board;
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
        generateBoard();
    }

    public void setHeight(int height) {
        this.height = height;
        generateBoard();
    }

    /**
     * Generates the game board which appears as staggered columns of cells to mimic
     * a hexagonal cell structure.
     * @return a staggered board of EmptyCell objects with appropriate row and column values
     */
    public Cell[][] generateBoard() {

        board = new Cell[height * 2][width];
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
        this.width = width;
        this.height = height;
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

        team1.clearPieces();
        team2.clearPieces();

        for (int col = 0; col < width; col++) {
            if (board[0][col] != null) {
                addPiece(0, col, new Piece(0, col, team1, this));
            }
        }

        for (int col = 0; col < width; col++) {
            if (board[1][col] != null) {
                addPiece(1, col, new Piece(1, col, team1, this));
            }
        }

        for (int col = 0; col < width; col++) {
            if (board[height *2-1][col] != null) {
                addPiece(height * 2 - 1, col, new Piece(height * 2 - 1, col, team2, this));
            }
        }

        for (int col = 0; col < width; col++) {
            if (board[height * 2 - 2][col] != null) {
                addPiece(height * 2 - 2, col, new Piece(height * 2 - 2, col, team2, this));
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