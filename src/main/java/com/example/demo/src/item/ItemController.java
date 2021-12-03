package com.example.demo.src.item;

import com.example.demo.config.BaseException;
import com.example.demo.config.BasePageResponse;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.item.model.*;
import com.example.demo.src.paging.model.GetPageInfo;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/app/items")

public class ItemController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ItemProvider itemProvider;
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final JwtService jwtService;


    private int currentPosition,start,last;

    public ItemController(ItemProvider itemProvider, ItemService itemService, JwtService jwtService){
        this.itemProvider = itemProvider;
        this.itemService = itemService;
        this.jwtService = jwtService;
    }

    /**
     * 한 회원의 userIdx를 이용해서 해당 고객의 지역에 올라온 물품을 시간순으로 업로드한다.
     * */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BasePageResponse<GetPageInfo,List<GetItemRes>> getItmes(@PathVariable("userIdx") int userIdx){
        try{

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> getItemRes = itemProvider.getItems(userIdx);
            GetPageInfo getPageInfo = itemProvider.getPagingInfo(getItemRes.size());

            List<GetItemRes> listItemRes = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();


            for(int i = 0;i<getItemRes.size(); i++){
                if(i>=start-1 && i<=last-1){
                    listItemRes.add(getItemRes.get(i));
                }
            }

            return new BasePageResponse<>(getPageInfo,listItemRes);
        } catch(BaseException exception){
            return new BasePageResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/new-item")
    public BaseResponse<PostNewItemRes> newItem(@RequestBody PostNewItemReq postNewItemReq){
        try{

            // 요청받은 유저아이디와 jwt의 유저아이디가 같은지 다른지 확인하자.
            int userIdx = postNewItemReq.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx!=userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostNewItemRes postNewItemRes = itemService.newItem(postNewItemReq);
            return new BaseResponse<>(postNewItemRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/detail/{itemIdx}")
    public BaseResponse<GetItemDetailRes> detailItem(@PathVariable("itemIdx") int itemIdx){
        try{
            GetItemDetailRes detailRes = itemProvider.detailItem(itemIdx);
            return new BaseResponse<>(detailRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/before/{nickname}")
    public BasePageResponse<GetPageInfo,List<GetItemRes>> beforeItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> beforeItem = itemProvider.getBeforeItem(nickname);

            GetPageInfo getPageInfo = itemProvider.getPagingInfo(beforeItem.size());

            List<GetItemRes> listShow = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();

            for(int i = 0;i<beforeItem.size(); i++){
                if(i>=start-1 && i<=last-1){
                    System.out.println(1);
                    listShow.add(beforeItem.get(i));
                }
            }

            return new BasePageResponse<>(getPageInfo,listShow);
        } catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/complete/{nickname}")
    public BasePageResponse<GetPageInfo,List<GetItemRes>> completeItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> completeItem = itemProvider.getCompleteItem(nickname);
            GetPageInfo getPageInfo = itemProvider.getPagingInfo(completeItem.size());

            List<GetItemRes> listShow = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();

            for(int i = 0;i<completeItem.size(); i++){
                if(i>=start-1 && i<=last-1){
                    System.out.println(1);
                    listShow.add(completeItem.get(i));
                }
            }

            return new BasePageResponse<>(getPageInfo,completeItem);
        } catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/hide/{nickname}")
    public BasePageResponse<GetPageInfo,List<GetItemRes>> hideItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> hideItem = itemProvider.getHideItem(nickname);

            GetPageInfo getPageInfo = itemProvider.getPagingInfo(hideItem.size());

            List<GetItemRes> listShow = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();

            for(int i = 0;i<hideItem.size(); i++){
                if(i>=start-1 && i<=last-1){
                    System.out.println(1);
                    listShow.add(hideItem.get(i));
                }
            }
            return new BasePageResponse<>(getPageInfo,hideItem);
        } catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/purchase/{nickname}")
    public BasePageResponse<GetPageInfo,List<GetPurItemRes>> purchaseItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }

            List<GetPurItemRes> purchaseItem = itemProvider.getPurchaseItems(nickname);

            GetPageInfo getPageInfo = itemProvider.getPagingInfo(purchaseItem.size());

            List<GetPurItemRes> listShow = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();

            for(int i = 0;i<purchaseItem.size(); i++){
                if(i>=start-1 && i<=last-1){
                    listShow.add(purchaseItem.get(i));
                }
            }

            return new BasePageResponse<>(getPageInfo,purchaseItem);
        } catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/watchlist/{nickname}")
    public BasePageResponse<GetPageInfo,List<GetPurItemRes>> watchListItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }

            List<GetPurItemRes> watchListItem = itemProvider.watchListItmes(nickname);
            GetPageInfo getPageInfo = itemProvider.getPagingInfo(watchListItem.size());

            List<GetPurItemRes> listShow = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();

            for(int i = 0;i<watchListItem.size(); i++){
                if(i>=start-1 && i<=last-1){
                    System.out.println(1);
                    listShow.add(watchListItem.get(i));
                }
            }

            return new BasePageResponse<>(getPageInfo,watchListItem);
        } catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }

}
