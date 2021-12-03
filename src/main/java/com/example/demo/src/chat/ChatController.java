package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BasePageResponse;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.GetChattingDetailReq;
import com.example.demo.src.chat.model.GetChattingRes;
import com.example.demo.src.chat.model.GetChattingRoomListRes;
import com.example.demo.src.item.model.GetItemRes;
import com.example.demo.src.paging.PageProvider;
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
@RequestMapping("/app/chat")

public class ChatController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChatProvider chatProvider;
    @Autowired
    private final ChatService chatService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final PageProvider pageProvider;
    private int currentPosition,start,last;
    public ChatController(ChatProvider chatProvider, ChatService chatService, JwtService jwtService, PageProvider pageProvider){
        this.chatProvider = chatProvider;
        this.chatService = chatService;
        this.jwtService = jwtService;
        this.pageProvider = pageProvider;
    }

    // 해당 사용자에 맞는 모든 채팅방 목록 보여주기
    @ResponseBody
    @GetMapping("/list/{nickname}")
    public BasePageResponse<GetPageInfo,List<GetChattingRoomListRes>> chattingList(@PathVariable("nickname") String nickname){
        try{
            String userNicknameByJwt = jwtService.getNickname();
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }
            List<GetChattingRoomListRes> chattingList = chatProvider.getChattingRoomList(nickname);
            GetPageInfo getPageInfo = pageProvider.getPagingInfo(chattingList.size());
            List<GetChattingRoomListRes> listItemRes = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();


            for(int i = 0;i<chattingList.size(); i++){
                if(i>=start-1 && i<=last-1){
                    listItemRes.add(chattingList.get(i));
                }
            }
            return new BasePageResponse<>(getPageInfo,chattingList);
        } catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/detail/{roomIdx}")
    public BasePageResponse<GetPageInfo,List<GetChattingRes>> chattingDetail(@PathVariable("roomIdx") int roomIdx,@RequestBody GetChattingDetailReq getChattingDetailReq){
        getChattingDetailReq.setRoomIdx(roomIdx);
        try{
            String userNicknameByJwt = jwtService.getNickname();
            if(!getChattingDetailReq.getNickname().equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }
            List<GetChattingRes> chattingDetail = chatProvider.chattingList(getChattingDetailReq);
            GetPageInfo getPageInfo = pageProvider.getPagingInfo(chattingDetail.size());

            List<GetChattingRes> listItemRes = new ArrayList<>();

            currentPosition = (int)(Math.ceil(getPageInfo.getCurrentPage() / getPageInfo.getCountList())) ;
            if(getPageInfo.getCurrentPage() %3 != 0){
                currentPosition++;
            }
            start = (currentPosition-1) * getPageInfo.getCountList() + 1;
            last = (currentPosition) * getPageInfo.getCountList();


            for(int i = 0;i<chattingDetail.size(); i++){
                if(i>=start-1 && i<=last-1){
                    listItemRes.add(chattingDetail.get(i));
                }
            }

            return new BasePageResponse<>(getPageInfo,chattingDetail);
        }catch (BaseException exception){
            return new BasePageResponse<>((exception.getStatus()));
        }
    }
}
