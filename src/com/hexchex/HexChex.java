package com.hexchex;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.pieces.IllegalMoveException;
import com.hexchex.engine.pieces.Team;
import com.hexchex.gui.Game;

import java.awt.*;

public class HexChex {

    public static void main(String[] args) throws IllegalMoveException {

        Board board = new Board(8, 8);

        Team red = new Team("Red", Color.RED);
        Team blu = new Team("Blu", Color.CYAN);

        board.setupDefaultBoard(red, blu);
        System.out.println(board);

        Game game = new Game(board);

    }

}
