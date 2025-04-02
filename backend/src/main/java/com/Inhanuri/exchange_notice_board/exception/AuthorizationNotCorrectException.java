package com.Inhanuri.exchange_notice_board.exception;

public class AuthorizationNotCorrectException extends UserValidationException {
    public AuthorizationNotCorrectException(String message) {
        super(message);
    }
}
