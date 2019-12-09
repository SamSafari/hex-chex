package com.hexchex.engine.board;

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
     * Generates the game board which appears as a checkerboard of null spaces and empty cells
     * which mimics a "double height" hexagonal coordinate system. The real height of the board
     * is twice the defined height in order to do this.
     * (More info: https://www.redblobgames.com/grids/hexagons/#coordinates-doubled)
     * @return a staggered board of EmptyCell objects with appropriate row and column values
     */
    private Cell[][] generateBoard() {

        board = new Cell[height * 2][width];

        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board[row].length; col++) {

                if (col % 2 == 0) {
                    if (row % 2 == 0) {
                        board[row][col] = new Cell.EmptyCell(row, col);
                    } else {
                        board[row][col] = null;
                    }

                } else {
                    if (row % 2 != 0) {
                        board[row][col] = new Cell.EmptyCell(row, col);
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

    /**
     * Adds a piece to this Board at the specified row and col by setting
     * board[row][col] to a new OccupiedCell Object. Also adds the piece to
     * its Team's activePieces List
     */
    private void addPiece(int row, int col, Piece piece) {

        if (board[row][col] != null) {

            if (board[row][col].isOccupied()) {
                board[row][col].getPiece().getTeam().removePiece(board[row][col].getPiece());
            }

            board[row][col] = new Cell.OccupiedCell(row, col, piece);
            piece.getTeam().addPiece(piece);

        } else {

            throw new NullCellException("That cell doesn't exist!");
        }

    }

    /**
     * Populates the first two rows on the north and south sides of this Board with new Pieces for each team
     * @param team1 Team whose pieces start on the north side of the board
     * @param team2 Team whose pieces start on the south side of the board
     */
    public void setupDefaultBoard(Team team1, Team team2) {

        team1.clearActivePieces();
        team2.clearActivePieces();

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
        StringBuilder output = new StringBuilder();
        for(Cell[] cells : board) {
            for(Cell cell : cells) {

                if (cell == null) {
                    output.append("   ");
                } else {
                    output.append(cell);
                }

            }
            output.append("\n");
        }
        return output.toString();
    }

}