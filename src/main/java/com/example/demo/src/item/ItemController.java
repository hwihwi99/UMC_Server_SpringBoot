package com.example.demo.src.item;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.item.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<List<GetItemRes>> getItmes(@PathVariable("userIdx") int userIdx){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetItemRes> getItemRes = itemProvider.getItems(userIdx);
            return new BaseResponse<>(getItemRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
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
    public BaseResponse<List<GetItemRes>> beforeItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            System.out.println(nickname);
            if(!nickname.equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> beforeItem = itemProvider.getBeforeItem(nickname);
            return new BaseResponse<>(beforeItem);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/complete/{nickname}")
    public BaseResponse<List<GetItemRes>> completeItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> completeItem = itemProvider.getCompleteItem(nickname);
            return new BaseResponse<>(completeItem);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/hide/{nickname}")
    public BaseResponse<List<GetItemRes>> hideItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetItemRes> hideItem = itemProvider.getHideItem(nickname);
            return new BaseResponse<>(hideItem);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/purchase/{nickname}")
    public BaseResponse<List<GetPurItemRes>> purchaseItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetPurItemRes> purchaseItem = itemProvider.getPurchaseItems(nickname);
            return new BaseResponse<>(purchaseItem);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/watchlist/{nickname}")
    public BaseResponse<List<GetPurItemRes>> watchListItem(@PathVariable("nickname") String nickname){
        try{

            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetPurItemRes> watchListItem = itemProvider.watchListItmes(nickname);
            return new BaseResponse<>(watchListItem);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
