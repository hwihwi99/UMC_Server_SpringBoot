package com.example.demo.src.paging.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPageInfo {
    private int totalPage; // 전체 출력해야하는 게시글 수
    private int currentPage; // 꼭 출력을 원하는 게시글 수
    private int countList; // 한 면에 보여줄 수
    private int startPage; // 화면에 보여지는 시작 페이지
    private int endPage; // 화면에 보여지는 마지막 페이지
    private boolean prev; // 이전 페이지가 있는지
    private boolean next; // 이후 페이지가 있는지
}
