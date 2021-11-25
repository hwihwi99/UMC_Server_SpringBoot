package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetChattingRes {
    private String nickname;
    private double review;
    private String content;
    private String chattingTime;
    private String userImg;
}
