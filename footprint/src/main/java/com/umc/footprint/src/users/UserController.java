package com.umc.footprint.src.users;




import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.umc.footprint.src.users.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.umc.footprint.config.Constant;
import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.config.BaseResponseStatus;
import com.umc.footprint.config.BaseResponseStatus.*;
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

    /**
     * 목표 수정 API

     * [PATCH] /users/:useridx/goals
     */
    // Path-variable
    @ResponseBody
    @PatchMapping("/{useridx}/goals") // [PATCH] /users/:useridx/goals
    public BaseResponse<String> modifyGoal(@PathVariable("useridx") int userIdx, @RequestBody PatchUserGoalReq patchUserGoalReq){
      // Validaion 1. userIdx 가 0 이하일 경우 exception
        if(userIdx <= 0)
            return new BaseResponse<>(new BaseException(BaseResponseStatus.INVALID_USERIDX).getStatus());

        // Validaion 2. dayIdx 길이 확인
        if(patchUserGoalReq.getDayIdx().size() == 0) // 요일 0개 선택
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MIN_DAYIDX).getStatus());
        if(patchUserGoalReq.getDayIdx().size() > 7)  // 요일 7개 초과 선택
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MAX_DAYIDX).getStatus());

        // Validaion 3. dayIdx 숫자 범위 확인
        for (Integer dayIdx : patchUserGoalReq.getDayIdx()){
            if (dayIdx > 7 || dayIdx < 1)
                return new BaseResponse<>(new BaseException(BaseResponseStatus.INVALID_DAYIDX).getStatus());
        }

        // Validaion 4. dayIdx 중복된 숫자 확인
        Set<Integer> setDayIDx = new HashSet<>(patchUserGoalReq.getDayIdx());
        if(patchUserGoalReq.getDayIdx().size() != setDayIDx.size()) // dayIdx 크기를 set으로 변형시킨 dayIdx 크기와 비교. 크기가 다르면 중복된 값 존재
            return new BaseResponse<>(new BaseException(BaseResponseStatus.OVERLAP_DAYIDX).getStatus());

        // Validaion 5. walkGoalTime 범위 확인
        if(patchUserGoalReq.getWalkGoalTime() < 10) // 최소 산책 목표 시간 미만
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MIN_WALK_GOAL_TIME).getStatus());
        if(patchUserGoalReq.getWalkGoalTime() > 240) // 최대 산책 목표 시간 초과
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MAX_WALK_GOAL_TIME).getStatus());

        // Validaion 6. walkTimeSlot 범위 확인
        if(patchUserGoalReq.getWalkTimeSlot() > 7 || patchUserGoalReq.getWalkTimeSlot() < 1)
            return new BaseResponse<>(new BaseException(BaseResponseStatus.INVALID_WALK_TIME_SLOT).getStatus());

    /**
     * 유저 세부 정보 조회 API
     * [GET] /users/:userIdx/infos
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/infos") // (GET) 127.0.0.1:3000/users/:userIdx/infos
    public BaseResponse<GetUserInfoRes> getUserInfo(@PathVariable("userIdx") int userIdx) {
        try {
            GetUserInfoRes getUserInfoRes = userProvider.getUserInfo(userIdx);
            return new BaseResponse<>(getUserInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }



        try {
            userService.modifyGoal(userIdx, patchUserGoalReq);

            String result ="목표가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
  


   /**
     * 목표 등록 API
     * [POST] /users/:useridx/goals
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/{useridx}/goals") // [POST] /users/:useridx/goals
    public BaseResponse<String> postGoal(@PathVariable("useridx") int userIdx, @RequestBody PostUserGoalReq postUserGoalReq){


        // Validaion 1. userIdx 가 0 이하일 경우 exception
        if(userIdx <= 0)
            return new BaseResponse<>(new BaseException(BaseResponseStatus.INVALID_USERIDX).getStatus());

        // Validaion 2. dayIdx 길이 확인
        if(postUserGoalReq.getDayIdx().size() == 0) // 요일 0개 선택
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MIN_DAYIDX).getStatus());
        if(postUserGoalReq.getDayIdx().size() > 7)  // 요일 7개 초과 선택
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MAX_DAYIDX).getStatus());

        // Validaion 3. dayIdx 숫자 범위 확인
        for (Integer dayIdx : postUserGoalReq.getDayIdx()){
            if (dayIdx > 7 || dayIdx < 1)
                return new BaseResponse<>(new BaseException(BaseResponseStatus.INVALID_DAYIDX).getStatus());
        }

        // Validaion 4. dayIdx 중복된 숫자 확인
        Set<Integer> setDayIDx = new HashSet<>(postUserGoalReq.getDayIdx());
        if(postUserGoalReq.getDayIdx().size() != setDayIDx.size()) // dayIdx 크기를 set으로 변형시킨 dayIdx 크기와 비교. 크기가 다르면 중복된 값 존재
            return new BaseResponse<>(new BaseException(BaseResponseStatus.OVERLAP_DAYIDX).getStatus());

        // Validaion 5. walkGoalTime 범위 확인
        if(postUserGoalReq.getWalkGoalTime() < 10) // 최소 산책 목표 시간 미만
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MIN_WALK_GOAL_TIME).getStatus());
        if(postUserGoalReq.getWalkGoalTime() > 240) // 최대 산책 목표 시간 초과
            return new BaseResponse<>(new BaseException(BaseResponseStatus.MAX_WALK_GOAL_TIME).getStatus());

        // Validaion 6. walkTimeSlot 범위 확인
        if(postUserGoalReq.getWalkTimeSlot() > 7 || postUserGoalReq.getWalkTimeSlot() < 1)
            return new BaseResponse<>(new BaseException(BaseResponseStatus.INVALID_WALK_TIME_SLOT).getStatus());


        try {
            int result = userService.postGoal(userIdx, postUserGoalReq);

            String resultMsg = "목표 저장에 성공하였습니다.";
            if(result == 0)
                resultMsg = "목표 저장에 실패하였습니다.";

            return new BaseResponse<>(resultMsg);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
