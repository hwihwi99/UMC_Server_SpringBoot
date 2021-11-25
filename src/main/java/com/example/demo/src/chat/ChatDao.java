package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChattingDetailReq;
import com.example.demo.src.chat.model.GetChattingRes;
import com.example.demo.src.chat.model.GetChattingRoomListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 개인별 채팅방 목록 보여주기
    public List<GetChattingRoomListRes> getChattingRoom (String nickname){
        String getChattingRoomQuery = "select roomTitle, userImg, address, updateAt, c.content as lastMessage\n" +
                "from \n" +
                "(select user2 as roomTitle, r1.roomIdx ,userImg ,address, no1\n" +
                "from\n" +
                "(select nickname as user1, userIdx as no1, roomIdx\n" +
                "from Room join User on Room.user1 = User.userIdx) r1\n" +
                "join (select nickname as user2, userIdx as no2, roomIdx, userImg ,address\n" +
                "from Room join User on Room.user2 = User.userIdx) r2 on r1.roomIdx = r2.roomIdx\n" +
                "where user1 = ?\n" +
                "union\n" +
                "select user1 as roomTitle, r1.roomIdx , userImg ,address, no1\n" +
                "from\n" +
                "(select nickname as user1, userIdx as no1, roomIdx, userImg ,address\n" +
                "from Room join User on Room.user1 = User.userIdx) r1\n" +
                "join (select nickname as user2, userIdx as no2, roomIdx\n" +
                "from Room join User on Room.user2 = User.userIdx) r2 on r1.roomIdx = r2.roomIdx\n" +
                "where user2 = ? ) as roomList \n" +
                "join \n" +
                "(select c.chatIdx, c.roomIdx, content, updateAt\n" +
                "from Chat as c join (select max(chatIdx) as chatIdx, roomIdx from Chat group by roomIdx)as lastMessage on c.chatIdx = lastMessage.chatIdx) as c\n" +
                " on roomList.roomIdx = c.roomIdx\n" +
                "group by roomList.roomIdx\n" +
                "order by updateAt desc";
        Object[] getChattingParms = new Object[]{nickname,nickname};
        return this.jdbcTemplate.query(getChattingRoomQuery,
                (rs, rowNum) -> new GetChattingRoomListRes(
                        rs.getString("roomTitle"),
                        rs.getString("userImg"),
                        rs.getString("address"),
                        rs.getString("updateAt"),
                        rs.getString("lastMessage")
                ), getChattingParms);
    }

    public List<GetChattingRes> chattingList(GetChattingDetailReq getChattingDetailReq){
        String getChattingDetailQuery = "select u.nickname,u.userImg, c.content, c.updateAt as chattingTime, c.roomIdx, review\n" +
                "from Chat as c, (select u.userIdx,u.nickname,u.userImg, review from User as u left join\n" +
                "(select u.userIdx,u.nickname,u.userImg,ifnull(avg(r.score),0.00000) as review\n" +
                "from User u left join Review r on r.user2 = u.userIdx\n" +
                "group by r.user2) r on u.userIdx = r.userIdx) u , Room r\n" +
                "where c.userIdx = u.userIdx and c.roomIdx = r.roomIdx \n" +
                "and (r.user1 = (select userIdx from User where nickname =?) or r.user2 =  (select userIdx from User where nickname =?) ) and r.roomIdx = ? \n" +
                "order by c.updateAt desc";
        Object [] getchattingParams = new Object[]{getChattingDetailReq.getNickname(), getChattingDetailReq.getNickname(),getChattingDetailReq.getRoomIdx()};
        return this.jdbcTemplate.query(getChattingDetailQuery,
                (rs,rowNum) -> new GetChattingRes(
                        rs.getString("nickname"),
                        rs.getDouble("review"),
                        rs.getString("content"),
                        rs.getString("chattingTime"),
                        rs.getString("userImg")
                ),getchattingParams);
    }
}
