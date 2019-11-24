package com.hexchex.engine.pieces;

public class IllegalMoveException extends IndexOutOfBoundsException {

    public IllegalMoveException(String s) {
        super(s);
    }

}
