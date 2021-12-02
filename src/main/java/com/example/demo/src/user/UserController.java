package com.example.demo.src.user;

import com.example.demo.config.BasePageResponse;
import com.example.demo.src.paging.Criteria;
import com.example.demo.src.paging.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/app/users")
// method가 어떤 HTTP 요청을 처리할 것인가를 작성한다.
// 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
// URL(/app/users)을 컨트롤러의 메서드와 매핑할 때 사용
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class UserController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션

        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!


        // userName에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if(postUserReq.getUserName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_USERNAME);
        }

        // address에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if(postUserReq.getAddress() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ADDRESS);
        }

        // nickname에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if(postUserReq.getNickname() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }

        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        // password에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        // phoneNum에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getPhoneNum() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }

        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        // 비밀번호가 8자리 이상인지 확인하기
        if (postUserReq.getPassword().length() < 8){
            return new BaseResponse<>(POST_USERS_LENGTH_PASSWORD);
        }

        // 닉네임의 길이가 5자리 이상인지 확인하기
        if (postUserReq.getNickname().length() < 5){
            return new BaseResponse<>(POST_USERS_LENGTH_NICKNAME);
        }

        // 전화번호 정규 표현
        if(!isRegexPhoneNum(postUserReq.getPhoneNum())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }

        // 이름 형식이 한글로만 이루어져야합니다.
        if(!isRegexUserName(postUserReq.getUserName())){
            return new BaseResponse<>(POST_USERS_INVALID_USERNAME);
        }

        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 모든 회원들의  조회 API
     * [GET] /users
     *
     * 또는
     *
     * 해당 닉네임을 같는 유저들의 정보 조회 API
     * [GET] /users? NickName=
     */
    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BasePageResponse<GetUserCountRes,List<GetUserRes>> getUsers(Criteria cri,  @RequestParam(required = false) String nickname) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
        try {
            GetUserCountRes getUserCountRes = userProvider.getTotalCount();
            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BasePageResponse<>(getUserCountRes,getUsersRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.

            // 일단 jwt에서 id로 인증이 가능한데 이 아이디를 받아서 만약 닉네임이 일치한다면 받아오자.
            String userNicknameByJwt = jwtService.getNickname();
            System.out.println(userNicknameByJwt);
            if(!nickname.equals(userNicknameByJwt)){
                return new BasePageResponse<>(INVALID_USER_JWT);
            }
            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);

            return new BasePageResponse<>(getUserCountRes,getUsersRes);
        } catch (BaseException exception) {
            return new BasePageResponse<>((exception.getStatus()));
        }
    }
    /**




    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 정보 조회 시켜주기

            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * get으로 유저idx를 가져오면 이것은 유저정보조회, post면 유저 정보 수정
     * 유저정보변경 API
     * 1. 주소
     * [PATCH] /users/address/:userIdx
     *
     * 2. 전화번호
     * [PATCH] /users/phonenum/:userIdx
     *
     * 3. 비밀번호
     * [PATCH] /users/password/:userIdx
     */
    @ResponseBody
    @PatchMapping("/address/{userIdx}")
    public BaseResponse<String> modifyUserAddress(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 주소값 변경

            PatchAddressReq patchAddressReq = new PatchAddressReq(userIdx, user.getAddress());
            userService.modifyUserAddress(patchAddressReq);

            String result = "회원 정보의 주소가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/phonenum/{userIdx}")
    public BaseResponse<String> modifyUserPhoneNum(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 전화번호 변경

            PatchPhoneNumReq patchPhoneNumReq = new PatchPhoneNumReq(userIdx, user.getPhoneNum());
            userService.modifyUserPhoneNum(patchPhoneNumReq);

            String result = "회원 정보의 전화번호가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/password/{userIdx}")
    public BaseResponse<String> modifyUserPassword(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 비밀번호호 변경

            PatchPasswordReq patchPasswordReq = new PatchPasswordReq(userIdx, user.getPassword(),user.getSalt());
            userService.modifyUserPassword(patchPasswordReq);

            String result = "회원 정보의 비밀번호가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 탈퇴 및 회원 비활성화 구분하기
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/withdrawal")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx){
        try{

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 탈퇴 해주기

            PatchUserStatueReq patchUserStatueReq = new PatchUserStatueReq(userIdx);
            userService.deleteUser(patchUserStatueReq);
            String result = "탈퇴가 완료되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/{userIdx}/deactivate")
    public BaseResponse<String> deactiveUser(@PathVariable("userIdx") int userIdx){
        try{

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 비활성화 해주기

            PatchUserStatueReq patchUserStatueReq = new PatchUserStatueReq(userIdx);
            userService.deactiveUser(patchUserStatueReq);
            String result = "비활성화가 완료되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/{userIdx}/reactivate")
    public BaseResponse<String> reactiveUser(@PathVariable("userIdx") int userIdx){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 재활성화 해주기

            PatchUserStatueReq patchUserStatueReq = new PatchUserStatueReq(userIdx);
            userService.reactiveUser(patchUserStatueReq);
            String result = "재활성화 되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
