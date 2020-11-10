package com.company;

public class LineOutOfBoundsException extends Exception{
    @Override
    public String getMessage() {
        return "Line non-existent!";
    }
}
