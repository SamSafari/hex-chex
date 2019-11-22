enum DIRECTIONS {N, S, NW, NE, SW, SE}

public class Piece {

    private int row, column;
    private final Team team;

    public Piece(int row, int column, Team team) {
        this.row = row;
        this.column = column;
        this.team = team;
    }

    public Team getTeam() {
        return this.team;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
