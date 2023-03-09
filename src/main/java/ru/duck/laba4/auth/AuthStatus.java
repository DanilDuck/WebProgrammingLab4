package ru.duck.laba4.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthStatus {

    SUCCESS("Success"),
    USER_ALREADY_EXISTS("User already exists"),
    ALREADY_AUTHENTICATED("Already authenticated"),
    INVALID_CREDENTIALS("Incorrect login and password"),
    USER_DOES_NOT_EXIST("User not exists"),
    WRONG_PASSWORD("Incorrect password"),
    INVALID_TOKEN("Wrong token"),
    UNKNOWN_ERROR("ERROR");

    private final String message;
}
