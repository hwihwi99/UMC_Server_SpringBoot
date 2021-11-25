package com.example.demo.src.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetItemDetailRes {
    private int itemIdx;
    private String userImg;
    private String address;
    private String nickname;
    private int itemPrice;
    private String itemTitle;
    private String itemCategory;
    private double review;
    private String itemDetail;
    private int interest;
    private int chattingCnt;
    private String status;
    private String review_content;
    private String diffTime;
    private String img;
}
