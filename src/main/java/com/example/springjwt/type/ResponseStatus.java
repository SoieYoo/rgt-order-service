package com.example.springjwt.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS("success"),
    FAILURE("failure");

    private final String status;
}
