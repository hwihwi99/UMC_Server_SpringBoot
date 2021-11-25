package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.GetChattingDetailReq;
import com.example.demo.src.chat.model.GetChattingRes;
import com.example.demo.src.chat.model.GetChattingRoomListRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    public ChatController(ChatProvider chatProvider, ChatService chatService, JwtService jwtService){
        this.chatProvider = chatProvider;
        this.chatService = chatService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/list/{nickname}")
    public BaseResponse<List<GetChattingRoomListRes>> chattingList(@PathVariable("nickname") String nickname){
        try{
            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            System.out.println(nickname);
            if(!nickname.equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            System.out.println("1");
            List<GetChattingRoomListRes> chattingList = chatProvider.getChattingRoomList(nickname);
            return new BaseResponse<>(chattingList);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/detail/{roomIdx}")
    public BaseResponse<List<GetChattingRes>> chattingDetail(@PathVariable("roomIdx") int roomIdx,@RequestBody GetChattingDetailReq getChattingDetailReq){
        getChattingDetailReq.setRoomIdx(roomIdx);
        try{
            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt+"1");
            System.out.println(getChattingDetailReq.getNickname()+"2");
            if(!getChattingDetailReq.getNickname().equals(userNicknameByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetChattingRes> chattingDetail = chatProvider.chattingList(getChattingDetailReq);
            return new BaseResponse<>(chattingDetail);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
