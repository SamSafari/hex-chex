package com.hexchex;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.pieces.Team;
import com.hexchex.gui.Game;

import java.awt.*;
import java.io.Serializable;

/**
 * Stores unique game info for saving/loading purposes. When a game is loaded,
 * a new Game class is created using a loaded HexChex object.
 */
public class HexChex implements Serializable {

    private Board board;
    private Team team1, team2;
    private String gameName = null;
    private Team currentMove;
    private int hexRadius;

    public HexChex(Board board, Team team1, Team team2) {
        this.board = board;
        this.team1 = team1;
        this.team2 = team2;
        team2.setAbbr(team2.getAbbr().toString().toLowerCase());
        board.setupDefaultBoard(team1, team2);
        currentMove = team1;
        System.out.println(board);
    }

    public HexChex() {
        this(new Board(), new Team("White", new Color(225, 220, 200)),
                new Team("Black", new Color(35, 25, 25)));
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

    public void setHexRadius(int hexRadius) {
        this.hexRadius = hexRadius;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Team getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Team currentMove) {
        this.currentMove = currentMove;
    }

    public static void main(String[] args) {

        /**
         * Program always starts by creating a new default game (8x8 board, White vs Black)
         */
        new Game(new HexChex());

    }

}
