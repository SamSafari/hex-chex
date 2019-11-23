package com.hexchex;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.pieces.Direction;
import com.hexchex.engine.pieces.IllegalMoveException;
import com.hexchex.engine.pieces.Team;

public class HexChex {

    public static void main(String[] args) throws IllegalMoveException {

        Board board = new Board(5, 7);

        Team red = new Team("Red");
        Team blu = new Team("Blu");

        board.setupDefaultBoard(red, blu);
        System.out.println(board);

        red.getPieces().get(0).move(Direction.S);
        System.out.println(board);

        red.getPieces().get(0).move(Direction.S);
        System.out.println(board);

        blu.getPieces().get(4).move((Direction.N));
        System.out.println(board);

        red.getPieces().get(0).move(Direction.S);
        System.out.println(board);

        red.getPieces().get(0).move(Direction.S);
        System.out.println(board);

        red.getPieces().get(0).move(Direction.S);
        System.out.println(board);




        //Game game = new Game();

    }

}
