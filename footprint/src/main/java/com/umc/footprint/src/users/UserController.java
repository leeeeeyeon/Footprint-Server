package com.umc.footprint.src.users;


import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.config.Constant;
import com.umc.footprint.src.users.model.GetUserDateRes;
import com.umc.footprint.src.users.model.GetUserRes;
import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.umc.footprint.config.BaseResponseStatus.INVALID_DATE;
import static com.umc.footprint.config.BaseResponseStatus.NO_EXIST_WALK;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;

    public UserController(UserProvider userProvider, UserService userService) {
        this.userProvider = userProvider;
        this.userService = userService;
    }

    /**
     * 유저 오늘 산책관련 정보 조회 API
     * [GET] /users/:userIdx/today
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{useridx}/today")
    public BaseResponse<List<GetUserTodayRes>> getToday(@PathVariable("useridx") int userIdx){
        try{
            List<GetUserTodayRes> userTodayRes = userProvider.getUserToday(userIdx);

            return new BaseResponse<>(userTodayRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 날짜별 산책관련 정보 조회 API
     * [GET] /users/:userIdx/:date
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{useridx}/{date}")
    public BaseResponse<List<GetUserDateRes>> getDateWalk(@PathVariable("useridx") int userIdx,@PathVariable("date") String date){

        // Validation 1. 날짜 형식 검사
        if(!date.matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$")){
            return new BaseResponse<>(new BaseException(INVALID_DATE).getStatus());
        }

        // Provider 연결
        try{
            List<GetUserDateRes> userDateRes = userProvider.getUserDate(userIdx,date);

            return new BaseResponse<>(userDateRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 정보 조회 API
     * [GET] /users/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:3000/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        try {
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }



}
