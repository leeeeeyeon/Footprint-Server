package com.umc.footprint.src.users;


import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.users.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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


    @ResponseBody
    @GetMapping("/{userIdx}/tmonth") // (GET) 127.0.0.1:3000/users/{userIdx}/tmonth
    public BaseResponse<GetMonthInfoRes> getMonthInfo(@PathVariable("userIdx") int userIdx) {
        // TO-DO-LIST
        // jwt 확인?
        // user테이블에 해당 userIdx가 존재하는지
        // GoalDay 테이블에 해당 userIdx가 존재하는지

        try {
            LocalDate now = LocalDate.now();
            int nowYear = now.getYear();
            int nowMonth = now.getMonthValue();

            GetMonthInfoRes getMonthInfoRes = userProvider.getMonthInfoRes(userIdx, nowYear, nowMonth);
            return new BaseResponse<>(getMonthInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @GetMapping("/{userIdx}/months/footprints") // (GET) 127.0.0.1:3000/users/{userIdx}/months/footprints?year=2021&month=2
    public BaseResponse<List<GetFootprintCount>> getMonthFootprints(@PathVariable("userIdx") int userIdx,@RequestParam(required = true) int year, @RequestParam(required = true) int month) throws BaseException {
        List<GetFootprintCount> getFootprintCounts = userProvider.getMonthFootprints(userIdx, year, month);
        return new BaseResponse<>(getFootprintCounts);
    }


    @ResponseBody
    @GetMapping("/{userIdx}/badges") // (GET) 127.0.0.1:3000/users/{userIdx}/badges
    public BaseResponse<GetUserBadges> getUsersBadges(@PathVariable("userIdx") int userIdx) throws BaseException {
            GetUserBadges getUserBadges = userProvider.getUserBadges(userIdx);
            return new BaseResponse<>(getUserBadges);
    }

    @ResponseBody
    @PatchMapping("/{userIdx}/badges/title/{badgeIdx}")
    public BaseResponse<BadgeInfo> patchRepBadge(@PathVariable("userIdx") int userIdx, @PathVariable("badgeIdx") int badgeIdx) throws BaseException {
        BadgeInfo patchRepBadgeInfo = userService.patchRepBadge(userIdx, badgeIdx);
        return new BaseResponse<>(patchRepBadgeInfo);
    }

    /*@ResponseBody
    @GetMapping("/{userIdx}/badges/status") // (GET) 127.0.0.1:3000/users/{userIdx}/badges/status
    public BaseResponse<GetUserBadges> getUsersBadges(@PathVariable("userIdx") int userIdx) throws BaseException {
        GetUserBadges getUserBadges = userProvider.getUserBadges(userIdx);
        return new BaseResponse<>(getUserBadges);
    }*/
}
