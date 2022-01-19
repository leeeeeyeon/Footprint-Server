package com.umc.footprint.src.users;




import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.umc.footprint.src.users.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;;
import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import org.springframework.web.bind.annotation.*;



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

    /**
     * 목표 수정 API
     * [PATCH] /users/:useridx/goals
     */
    // Path-variable
    @ResponseBody
    @PatchMapping("/{useridx}/goals") // [PATCH] /users/:useridx/goals
    public BaseResponse<String> modifyGoal(@PathVariable("useridx") int userIdx, @RequestBody PatchUserGoalReq patchUserGoalReq){
        try {
            userService.modifyGoal(userIdx, patchUserGoalReq);

            String result ="목표가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
