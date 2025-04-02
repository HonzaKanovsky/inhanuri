package com.Inhanuri.exchange_notice_board.exception;

public class UserAlreadyExistsException extends UserValidationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
