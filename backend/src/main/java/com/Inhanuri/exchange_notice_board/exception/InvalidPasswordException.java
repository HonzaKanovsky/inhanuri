package com.Inhanuri.exchange_notice_board.exception;

public class InvalidPasswordException extends UserValidationException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
