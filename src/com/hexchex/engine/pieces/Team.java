package com.hexchex.engine.pieces;

import com.hexchex.engine.board.Cell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

    private List<Piece> pieces;
    private int numPieces;
    private String name;

    public Team(String name) {
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

}
