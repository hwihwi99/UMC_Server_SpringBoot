package com.example.demo.src.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPurItemRes {
    private int itemIdx;
    private String itemTitle;
    private int itemPrice;
    private int chattingCnt;
    private String address;
    private String status;
    private String diffTime;
    private int interest;
    private String img;
}
