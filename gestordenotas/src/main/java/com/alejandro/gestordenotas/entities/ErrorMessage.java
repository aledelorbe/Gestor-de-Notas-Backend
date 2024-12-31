package com.alejandro.gestordenotas.entities;

import java.time.LocalDateTime;

// This class is used to create the error messages
// when an exception is fired 
public class ErrorMessage {

    private String message;

    private String error;

    private int status;

    private LocalDateTime dateTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
