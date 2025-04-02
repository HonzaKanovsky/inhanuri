package com.Inhanuri.exchange_notice_board.exception;

public class UserDoesNotExistException extends UserValidationException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
