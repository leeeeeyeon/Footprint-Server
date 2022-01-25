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

import static com.umc.footprint.config.BaseResponseStatus.MAX_NICKNAME_LENGTH;
import static com.umc.footprint.config.BaseResponseStatus.MODIFY_NICKNAME_FAIL;


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
     * 유저 닉네임 변경 API
     * [PATCH] /users/:userIdx/nickname
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/nickname")
    public BaseResponse<String> modifyNickname(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
            PatchNicknameReq patchNicknameReq = new PatchNicknameReq(userIdx, user.getNickname());
            if (user.getNickname().length() > 15) { // 닉네임 15자 초과
                throw new BaseException(MAX_NICKNAME_LENGTH);
            }
            userService.modifyNickname(patchNicknameReq);

            String result = "닉네임이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
