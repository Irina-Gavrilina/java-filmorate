package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;

@Getter
public class ErrorResponse {

    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}