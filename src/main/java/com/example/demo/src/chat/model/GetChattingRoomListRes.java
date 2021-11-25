package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChattingRoomListRes {
    private String roomTitle;
    private String userImg;
    private String address;
    private String updateAt;
    private String lastMessage;
}
