package com.example.demo.src.item;

import com.example.demo.src.item.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ItemDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 유저아이디에 맞는 그 근처 주소의 사람의 아이템 목록을 나열해준다.
    public List<GetItemRes> getItems(int userIdx){
        String getItemsQuery = "select  i.itemIdx, i.itemTitle,i.itemPrice,i.chattingCnt, i.nickname as seller, u.address,i.status,(select CASE\n" +
                "\twhen((select i.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물' \n" +
                "end) as diffTime, sum(w.tempCnt) as interest , i.img\n" +
                "from User as u join (select  i.itemTitle,i.itemPrice,i.updateAt,i.chattingCnt, i.status, u.nickname, u.address,i.itemIdx,img\n" +
                "from User as u , (select Items.itemIdx, chattingCnt, userIdx, itemTitle,itemPrice,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) as i where u.userIdx = i.userIdx) as i on u.address=i.address  join (select i.*, case when w.itemIdx is null then 0 else 1 end as tempCnt\n" +
                "from Items i left join WatchList w on i.itemIdx = w.itemIdx) w on i.itemIdx = w.itemIdx \n" +
                "where u.userIdx = ? and i.status <> 'complete' \n" +
                "group by i.itemIdx\n" +
                "order by i.updateAt desc";
        int getItemParams = userIdx;
        return this.jdbcTemplate.query(getItemsQuery,
                (rs,rowNum) -> new GetItemRes(
                        rs.getInt("itemIdx"),
                        rs.getString("itemTitle"),
                        rs.getInt("itemPrice"),
                        rs.getInt("chattingCnt"),
                        rs.getString("seller"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("diffTime"),
                        rs.getInt("interest"),
                        rs.getString("img")
                ),getItemParams);
    }

    //물품 등록
    public int newItem(PostNewItemReq postNewItemReq){
        String newItemQuery = "insert into Items(userIdx, itemTitle, itemCategory, itemDetail, itemPrice) VALUES (?,?,?,?,?)";
        Object[] newItemParams = new Object[]{postNewItemReq.getUserIdx(), postNewItemReq.getItemTitle(), postNewItemReq.getItemCategory(), postNewItemReq.getItemDetail(),postNewItemReq.getItemPrice()};
        this.jdbcTemplate.update(newItemQuery,newItemParams);

        String lastInsertIdQuery = "select last_insert_id()";
        int num = this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);

        List<String> imgs = postNewItemReq.getImgs();
        if(imgs.isEmpty()){
            return num;
        }
        for(int i = 0; i< imgs.size(); i++){
            newItemImg(imgs.get(i),num);
        }
        return num;
    }

    private int newItemImg(String img, int num){
        String newItemImgQuery = "insert into ItemImgs(itemIdx, imgUrl) values (?,?)";
        Object[] newItemImgParams = new Object[]{num,img};
        this.jdbcTemplate.update(newItemImgQuery,newItemImgParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    // 물품 자세히 보기
    public GetItemDetailRes detailItem(int itemIdx){
        String detailItemQuery = "select interest.itemIdx, userImg, address, nickname,itemPrice, itemTitle,itemCategory,review, itemDetail, chattingCnt,status,review_content,interest,(select CASE\n" +
                "\twhen((select interest.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select interest.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물'\n" +
                "end) as diffTime ,img\n" +
                "from\n" +
                "(select i.itemIdx, sum(w.tempCnt), u.userIdx, i.itemPrice, i.itemTitle,i.itemDetail,i.itemCategory,sum(w.tempCnt)as interest, i.chattingCnt,i.updateAt, i.status, i.img\n" +
                "from User u, (select Items.itemIdx, itemPrice,chattingCnt, userIdx, itemTitle,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) as i, (select itemTitle, i.itemIdx, case when w.itemIdx is null then 0 else 1 end as tempCnt from Items i left join WatchList w on i.itemIdx = w.itemIdx) as w\n" +
                "where u.userIdx = i.userIdx and i.itemIdx = w.itemIdx\n" +
                "group by itemIdx) as interest left join ItemImgs as img on interest.itemIdx = img.itemIdx  \n" +
                "join\n" +
                "(select u.userIdx,u.nickname,u.address, u.userImg, ifnull(avg(r.score),0.00000) as review, group_concat(content) as review_content\n" +
                "from User u left join Review r on r.user2 = u.userIdx\n" +
                "group by r.user2) as review  on review.userIdx = interest.userIdx\n" +
                "where interest.itemIdx = ? \n" +
                "group by interest.itemIdx";
        int getItemIdx = itemIdx;
        return this.jdbcTemplate.queryForObject(detailItemQuery,
                (rs, rowNum) -> new GetItemDetailRes(
                        rs.getInt("itemIdx"),
                        rs.getString("userImg"),
                        rs.getString("address"),
                        rs.getString("nickname"),
                        rs.getInt("itemPrice"),
                        rs.getString("itemTitle"),
                        rs.getString("itemCategory"),
                        rs.getDouble("review"),
                        rs.getString("itemDetail"),
                        rs.getInt("interest"),
                        rs.getInt("chattingCnt"),
                        rs.getString("status"),
                        rs.getString("review_content"),
                        rs.getString("diffTime"),
                        rs.getString("img")
                        ),getItemIdx);
    }

    // nickname 사용자가 판매 중인 상품
    public List<GetItemRes> getBeforeItem(String nickname){
        String getItemsQuery = "select i.itemIdx, i.itemTitle,i.itemPrice,i.chattingCnt, nickname as seller, u.address,i.status,(select CASE\n" +
                "\twhen((select i.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물' \n" +
                "end) as diffTime,i.updateAt, sum(w.tempCnt) as interest , img\n" +
                "from\n" +
                "User u , (select Items.itemIdx, chattingCnt, userIdx, itemTitle,itemPrice,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) i , (select i.*, case when w.itemIdx is null then 0 else 1 end as tempCnt\n" +
                "from Items i left join WatchList w on i.itemIdx = w.itemIdx)as w\n" +
                "where i.userIdx = u.userIdx and i.itemIdx = w.itemIdx and nickname = ? and i.status = \"before\"\n" +
                "group by i.itemIdx\n" +
                "order by i.updateAt desc";
        String userNickname = nickname;
        return this.jdbcTemplate.query(getItemsQuery,
                (rs,rowNum) -> new GetItemRes(
                        rs.getInt("itemIdx"),
                        rs.getString("itemTitle"),
                        rs.getInt("itemPrice"),
                        rs.getInt("chattingCnt"),
                        rs.getString("seller"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("diffTime"),
                        rs.getInt("interest"),
                        rs.getString("img")
                ),nickname);
    }

    // nickname 사용자가 판매 완료한 상품
    public List<GetItemRes> getCompleteItem(String nickname){
        String getItemsQuery = "select i.itemIdx, i.itemTitle,i.itemPrice,i.chattingCnt, nickname as seller, u.address,i.status,(select CASE\n" +
                "\twhen((select i.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물' \n" +
                "end) as diffTime,i.updateAt, sum(w.tempCnt) as interest , img\n" +
                "from\n" +
                "User u , (select Items.itemIdx, chattingCnt, userIdx, itemTitle,itemPrice,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) i , (select i.*, case when w.itemIdx is null then 0 else 1 end as tempCnt\n" +
                "from Items i left join WatchList w on i.itemIdx = w.itemIdx)as w\n" +
                "where i.userIdx = u.userIdx and i.itemIdx = w.itemIdx and nickname = ? and i.status = \"complete\"\n" +
                "group by i.itemIdx\n" +
                "order by i.updateAt desc";
        String userNickname = nickname;
        return this.jdbcTemplate.query(getItemsQuery,
                (rs,rowNum) -> new GetItemRes(
                        rs.getInt("itemIdx"),
                        rs.getString("itemTitle"),
                        rs.getInt("itemPrice"),
                        rs.getInt("chattingCnt"),
                        rs.getString("seller"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("diffTime"),
                        rs.getInt("interest"),
                        rs.getString("img")
                ),nickname);
    }

    // nickname 사용자가 숨긴 상품
    public List<GetItemRes> getHideItem(String nickname){
        String getItemsQuery = "select i.itemIdx, i.itemTitle,i.itemPrice,i.chattingCnt, nickname as seller, u.address,i.status,(select CASE\n" +
                "\twhen((select i.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물' \n" +
                "end) as diffTime,i.updateAt, sum(w.tempCnt) as interest , img\n" +
                "from\n" +
                "User u , (select Items.itemIdx, chattingCnt, userIdx, itemTitle,itemPrice,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) i , (select i.*, case when w.itemIdx is null then 0 else 1 end as tempCnt\n" +
                "from Items i left join WatchList w on i.itemIdx = w.itemIdx)as w\n" +
                "where i.userIdx = u.userIdx and i.itemIdx = w.itemIdx and nickname = ? and i.status = \"hide\"\n" +
                "group by i.itemIdx\n" +
                "order by i.updateAt desc";
        String userNickname = nickname;
        return this.jdbcTemplate.query(getItemsQuery,
                (rs,rowNum) -> new GetItemRes(
                        rs.getInt("itemIdx"),
                        rs.getString("itemTitle"),
                        rs.getInt("itemPrice"),
                        rs.getInt("chattingCnt"),
                        rs.getString("seller"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("diffTime"),
                        rs.getInt("interest"),
                        rs.getString("img")
                ),nickname);
    }

    // 내가 구매한 상품 리스트
    public List<GetPurItemRes> purchaseItems(String nickname){
        String itemQuery = "select i.itemIdx,i.itemTitle,u.address, chattingCnt,itemPrice,i.status,(select CASE\n" +
                "\twhen((select i.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select i.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물'\n" +
                "end) as diffTime,sum(w.tempCnt) as interest , img\n" +
                "from User as u join \n" +
                "(select p.userIdx,i.itemTitle,i.itemPrice,p.updateAt,i.chattingCnt,i.itemIdx,img,status from (select Items.itemIdx, itemPrice,chattingCnt, userIdx, itemTitle,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) as i join Purchase as p on i.itemIdx = p.itemIdx) as i\n" +
                "on u.userIdx = i.userIdx left join (select itemTitle, case when w.itemIdx is null then 0 else 1 end as tempCnt\n" +
                "from Items i left join WatchList w on i.itemIdx = w.itemIdx) w on w.itemTitle = i.itemTitle\n" +
                "where u.nickname=?\n" +
                "group by itemIdx;";
        String getNickname = nickname;
        return this.jdbcTemplate.query(itemQuery,
                (rs,rowNum)-> new GetPurItemRes(
                        rs.getInt("itemIdx"),
                        rs.getString("itemTitle"),
                        rs.getInt("itemPrice"),
                        rs.getInt("chattingCnt"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("diffTime"),
                        rs.getInt("interest"),
                        rs.getString("img")
                ),
                getNickname);
    }


    // 내가 찜한 상품 리스트
    public List<GetPurItemRes> wishListItem(String nickname){
        String itemQuery = "select i.itemIdx, itemTitle, address,i.status, itemPrice, chattingCnt,sum(want.tempCnt) as interest,(select CASE\n" +
                "\twhen((select want.updateAt between date_add(now(),interval -1 day) and NOW())) then '0일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -2 day) and date_add(now(),interval -1 day))) then '1일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -3 day) and date_add(now(),interval -2 day))) then '2일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -4 day) and date_add(now(),interval -3 day))) then '3일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -5 day) and date_add(now(),interval -4 day))) then '4일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -6 day) and date_add(now(),interval -5 day))) then '5일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -7 day) and date_add(now(),interval -6 day))) then '6일전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -14 day) and date_add(now(),interval -7 day))) then '1주전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -21 day) and date_add(now(),interval -14 day))) then '2주전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -31 day) and date_add(now(),interval -21 day))) then '3주전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -2 month ) and date_add(now(),interval -1 month))) then '1달전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -3 month ) and date_add(now(),interval -2 month))) then '2달전'\n" +
                "    when((select want.updateAt between date_add(now(),interval -4 month ) and date_add(now(),interval -3 month))) then '3달전'\n" +
                "    else '3달 넘은 오래된 게시물'\n" +
                "end) as diffTime,img\n" +
                "from\n" +
                "(select nickname, address,itemIdx from WatchList w join User u on w.userIdx = u.userIdx) w join (select Items.itemIdx,itemPrice,chattingCnt, userIdx, itemTitle,itemCategory,itemDetail,updateAt, group_concat(imgUrl) as img,status\n" +
                "from Items left join ItemImgs on Items.itemIdx = ItemImgs.itemIdx\n" +
                "group by Items.itemIdx) as i on w.itemIdx = i.itemIdx join\n" +
                "(select i.itemIdx,case when w.itemIdx is null then 0 else 1 end as tempCnt,w.updateAt\n" +
                "from Items i left join WatchList w on i.itemIdx = w.itemIdx) want on want.itemIdx = i.itemIdx\n" +
                "where nickname = ? \n" +
                "group by i.itemIdx\n" +
                "order by want.updateAt desc";
        String getNickname = nickname;
        return this.jdbcTemplate.query(itemQuery,
                (rs,rowNum)-> new GetPurItemRes(
                        rs.getInt("itemIdx"),
                        rs.getString("itemTitle"),
                        rs.getInt("itemPrice"),
                        rs.getInt("chattingCnt"),
                        rs.getString("address"),
                        rs.getString("status"),
                        rs.getString("diffTime"),
                        rs.getInt("interest"),
                        rs.getString("img")
                ),getNickname);
    }
}
