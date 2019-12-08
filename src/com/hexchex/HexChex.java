package com.hexchex;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.pieces.Team;
import com.hexchex.gui.Game;

import java.awt.*;
import java.io.Serializable;

public class HexChex implements Serializable {

    private Board board;
    private Team team1, team2;
    private String gameName = null;
    private Team currentMove;
    int hexRadius;

    public int getHexRadius() {
        return hexRadius;
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

    public HexChex(Board board, Team team1, Team team2) {
        this.board = board;
        this.team1 = team1;
        this.team2 = team2;
        board.setupDefaultBoard(team1, team2);
        currentMove = team1;
        System.out.println(board);
    }

    public HexChex() {
        this(new Board(), new Team("White", new Color(225, 220, 200)),
                new Team("Black", new Color(35, 25, 25)));
        System.out.println(board);
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

        new Game(new HexChex());

    }

}
