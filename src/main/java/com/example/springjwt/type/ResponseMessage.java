package com.example.springjwt.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    LOGIN_SUCCESS("로그인 성공"),
    ORDER_PLACED_SUCCESS("주문 성공"),
    ORDER_RETRIEVED_SUCCESS("주문 조회 성공");

    private final String message;
}
