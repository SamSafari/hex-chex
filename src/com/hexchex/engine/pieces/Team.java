package com.hexchex.engine.pieces;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Have a List of activePieces (pieces currently in play), as well as a name and color
 * which determines the color of their pieces in the GUI
 */
public class Team implements Serializable {

    private List<Piece> activePieces;
    private int numActivePieces;
    private String name;
    private Color color;
    private Character abbr;

    public Team(String name, Color color) {
        this.color = color;
        this.activePieces = new ArrayList<>();
        this.numActivePieces = 0;
        this.name = name;
        abbr = name.charAt(0);
    }

    public void addPiece(Piece piece) {
        activePieces.add(piece);
        numActivePieces++;
    }

    public void removePiece(Piece piece) {
        activePieces.remove(piece);
        numActivePieces--;
    }

    public void clearActivePieces() {
        activePieces.clear();
        numActivePieces = 0;
    }

    public List<Piece> getActivePieces() {
        return Collections.unmodifiableList(activePieces);
    }

    public Character getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr.charAt(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getNumActivePieces() {
        return numActivePieces;
    }

}
