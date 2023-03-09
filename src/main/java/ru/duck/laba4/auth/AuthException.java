package ru.duck.laba4.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException {

    private final AuthStatus status;

    @Override
    public String getMessage() {
        return status.getMessage();
    }
}
