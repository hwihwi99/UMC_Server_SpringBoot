package com.example.demo.src.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostNewItemReq {
    private int userIdx;
    private String itemTitle;
    private String itemCategory;
    private String itemDetail;
    private int itemPrice;
    private List<String> imgs;
}
