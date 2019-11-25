package com.hexchex;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.pieces.Team;
import com.hexchex.gui.Game;

import java.awt.*;

public class HexChex {

    public static void main(String[] args) {

        Board board = new Board(9, 8);

        Team white = new Team("White", new Color(225, 220, 200));
        Team black = new Team("Black", new Color(35, 25, 25));

        board.setupDefaultBoard(white, black);
        System.out.println(board);

        Game game = new Game(board);

    }

}
