package com.umc.footprint.src.users;


import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.users.model.GetFootprintCount;
import com.umc.footprint.src.users.model.GetMonthInfoRes;
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

    //이번달 날짜 별 달성률, 이번 달의 누적 산책시간, 누적 산책 거리, 평균 칼로리
    // 1. 목표 요일만 받아와보자!
    @ResponseBody
    @GetMapping("/{userIdx}/tmonth") // (GET) 127.0.0.1:3000/users/{userIdx}/tmonth
    public BaseResponse<GetMonthInfoRes> getMonthInfo(@PathVariable("userIdx") int userIdx) {
        try {
            // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
            LocalDate now = LocalDate.now();
            int year = now.getYear(); //현재 년도
            int month = now.getMonthValue(); //현재 월(달)

            GetMonthInfoRes getMonthInfoRes = userProvider.getMonthRes(userIdx, year, month);
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

}
