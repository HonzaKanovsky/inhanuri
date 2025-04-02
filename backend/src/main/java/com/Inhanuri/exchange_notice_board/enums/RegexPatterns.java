package com.Inhanuri.exchange_notice_board.enums;

import java.util.regex.Pattern;

public enum RegexPatterns {
    USERNAME("^[A-Za-z\\d]{3,}$"),
    PASSWORD("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{6,}$"),
    PIN("^\\d{4}$");

    private final String pattern;

    RegexPatterns(String pattern) {
        this.pattern = pattern;
    }

    public boolean matches(String input) {
        return Pattern.compile(this.pattern).matcher(input).matches();
    }
}
