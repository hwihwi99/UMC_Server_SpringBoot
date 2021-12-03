package com.example.demo.src.item;

import com.example.demo.config.BaseException;
import com.example.demo.src.item.model.GetItemDetailRes;
import com.example.demo.src.item.model.GetItemRes;
import com.example.demo.src.item.model.GetPurItemRes;
import com.example.demo.src.paging.Paging;
import com.example.demo.src.paging.model.GetPageInfo;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;


@Service
public class ItemProvider {
    private final ItemDao itemDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ItemProvider(ItemDao itemDao, JwtService jwtService){
        this.itemDao = itemDao;
        this.jwtService = jwtService;
    }

    // 해당 userIdx를 갖는 Item의 정보 조회
    public List<GetItemRes> getItems(int userIdx) throws BaseException{
        try{
            //GetPageInfo getPageInfo = getPagingInfo();
            List<GetItemRes> getItemRes = itemDao.getItems(userIdx);
            //getPageInfo.setTotalPage(getItemRes.size());

            return getItemRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 아이템 상세보기
    public GetItemDetailRes detailItem(int itemIdx) throws BaseException{
        try {
            GetItemDetailRes detailItem = itemDao.detailItem(itemIdx);
            return detailItem;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 판매 중
    public List<GetItemRes> getBeforeItem(String nickname) throws BaseException{
        try {
            List<GetItemRes> beforeItem = itemDao.getBeforeItem(nickname);
            return beforeItem;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 판매 완료
    public List<GetItemRes> getCompleteItem(String nickname) throws BaseException{
        try {
            List<GetItemRes> completeItem = itemDao.getCompleteItem(nickname);
            return completeItem;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 숨김
    public List<GetItemRes> getHideItem(String nickname) throws BaseException{
        try {
            List<GetItemRes> hideItem = itemDao.getHideItem(nickname);
            return hideItem;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 구매 상품
    public List<GetPurItemRes> getPurchaseItems(String nickname) throws BaseException{
        try {
            List<GetPurItemRes> purchaseItem = itemDao.purchaseItems(nickname);
            return purchaseItem;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 찜한 상품
    public List<GetPurItemRes> watchListItmes(String nickname) throws BaseException{
        try {
            List<GetPurItemRes> wishListItem = itemDao.wishListItem(nickname);
            return wishListItem;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
