package com.hexchex.engine.pieces;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team implements Serializable {

    private List<Piece> pieces;
    private int numPieces;
    private String name;
    private Color color;

    public Team(String name, Color color) {
        this.color = color;
        this.pieces = new ArrayList<>();
        this.numPieces = 0;
        this.name = name;
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
        numPieces++;
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
        numPieces--;
    }

    public List<Piece> getPieces() {
        return Collections.unmodifiableList(pieces);
    }

    public char getAbbr() {
        return name.charAt(0);
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getNumPieces() {
        return numPieces;
    }

    public void clearPieces() {
        pieces.clear();
        numPieces = 0;
    }

    public void setName(String name) {
        this.name = name;
    }
}
