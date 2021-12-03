package com.example.demo.src.paging;

import com.example.demo.src.paging.model.GetPageInfo;
import org.springframework.stereotype.Service;

@Service
public class PageProvider {

    // 페이지에 대한 정보
    public GetPageInfo getPagingInfo(int total){
        Paging paging = new Paging();
        paging.setTotalCount(total);
        GetPageInfo getPageInfo = new GetPageInfo(paging.getTotalPage(), paging.getPage(),paging.getCountList(),paging.getStartPage(),paging.getEndPage(),paging.isPrev(),paging.isNext());
        return getPageInfo;
    }
}
