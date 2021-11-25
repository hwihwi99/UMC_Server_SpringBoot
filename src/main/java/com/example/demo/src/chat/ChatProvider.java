package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChattingDetailReq;
import com.example.demo.src.chat.model.GetChattingRes;
import com.example.demo.src.chat.model.GetChattingRoomListRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ChatProvider {
    private final ChatDao chatDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public ChatProvider(ChatDao userDao, JwtService jwtService) {
        this.chatDao = userDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // 닉네임에 맞는 채팅방 목록들 나열해주기
    public List<GetChattingRoomListRes> getChattingRoomList(String nickname) throws BaseException{
        try {
            List<GetChattingRoomListRes> getChattingRoomListRes = chatDao.getChattingRoom(nickname);
            return getChattingRoomListRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 채팅방에 맞는 채팅 내용 자세히 보기
    public List<GetChattingRes> chattingList(GetChattingDetailReq getChattingDetailReq) throws BaseException{
        try {
            List<GetChattingRes> chattingListRes = chatDao.chattingList(getChattingDetailReq);
            return chattingListRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
