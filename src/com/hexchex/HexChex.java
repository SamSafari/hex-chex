package com.hexchex;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.pieces.Team;
import com.hexchex.gui.Game;

import java.awt.*;
import java.io.Serializable;

public class HexChex implements Serializable {

    private Board board;
    private Team team1, team2;

    public HexChex(Board board, Team team1, Team team2) {
        this.board = board;
        this.team1 = team1;
        this.team2 = team2;
        board.setupDefaultBoard(team1, team2);
    }

    public Board getBoard() {
        return board;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }



    public static void main(String[] args) {

        Board board = new Board(9, 8);
        Team white = new Team("White", new Color(225, 220, 200));
        Team black = new Team("Black", new Color(35, 25, 25));

        HexChex hexChex = new HexChex(board, white, black);

        System.out.println(board);

        Game game = new Game(hexChex);

    }

}
