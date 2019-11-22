import java.util.ArrayList;

public class Team {

    private ArrayList<Piece> pieces;
    private int numPieces;
    private String name;

    public Team(ArrayList<Piece> pieces, int numPieces, String name) {
        this.pieces = pieces;
        this.numPieces = numPieces;
        this.name = name;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

}
