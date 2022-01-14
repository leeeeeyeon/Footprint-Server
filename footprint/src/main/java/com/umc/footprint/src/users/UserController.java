package com.umc.footprint.src.users;


import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

}
