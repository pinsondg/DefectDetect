package com.example.dpgra.defectdetect;

public class CSVFormatException extends Exception {

    private String message;

    public CSVFormatException() {
        this.message = "";
    }

    public CSVFormatException( String message ) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "CSV Format Error: " + message;
    }
}
