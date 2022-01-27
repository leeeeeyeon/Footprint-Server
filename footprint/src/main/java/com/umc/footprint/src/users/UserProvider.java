package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.walks.WalkDao;
import com.umc.footprint.src.walks.model.Walk;

import java.time.Duration;

import static com.umc.footprint.config.BaseResponseStatus.DATABASE_ERROR;
import com.umc.footprint.config.BaseResponse;

import com.umc.footprint.src.users.model.GetUserTodayRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.umc.footprint.config.BaseException;
import com.umc.footprint.src.users.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import static com.umc.footprint.config.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final WalkDao walkDao;
    private final UserDao userDao;

    @Autowired
    public UserProvider(WalkDao walkDao,UserDao userDao) {
        this.walkDao = walkDao;
        this.userDao = userDao;
    }

    // 해당 userIdx를 갖는 오늘 산책 관련 정보 조회
    public List<GetUserTodayRes> getUserToday(int userIdx) throws BaseException {

        List<GetUserTodayRes> userTodayRes = userDao.getUserToday(userIdx);

        return userTodayRes;
    }


    //월별 발자국(일기) 갯수 조회
    public List<GetFootprintCount> getMonthFootprints(int userIdx, int year, int month) throws BaseException {
        try {
            List<GetFootprintCount> getMonthFootprints = userDao.getMonthFootprints(userIdx, year, month);
            return getMonthFootprints;
        } catch (Exception exception) {
          throw new BaseException(DATABASE_ERROR);
        }
    }
  

    // 해당 userIdx를 갖는 date의 산책 관련 정보 조회
    public List<GetUserDateRes> getUserDate(int userIdx, String date) throws BaseException {

        try {
            // Validation 2. Walk Table 안 존재하는 User인지 확인
            int existUserResult = userDao.checkUserInWalk(userIdx);
            if (existUserResult == 0)
                throw new BaseException(NOT_EXIST_USER_IN_WALK);

            // Validation 3. 해당 날짜에 User가 기록한 Walk가 있는지 확인
            int existUserDateResult = userDao.checkUserDateWalk(userIdx, date);
            if (existUserDateResult == 0)
                throw new BaseException(NO_EXIST_WALK);

            List<GetUserDateRes> userDateRes = userDao.getUserDate(userIdx, date);

            return userDateRes;
        } catch(Exception exception){

            throw new BaseException(DATABASE_ERROR);
        }
    }


    //월별 달성률 및 누적 정보 조회
    public GetMonthInfoRes getMonthInfoRes(int userIdx, int year, int month) throws BaseException {
        try {
            GetMonthInfoRes getMonthInfoRes = userDao.getMonthInfoRes(userIdx, year, month);
            return getMonthInfoRes;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해당 userIdx를 갖는 User의 정보 조회
    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            int userExist = userDao.userExist(userIdx);
            if (userExist == 0) {
                throw new BaseException(INVALID_USERIDX);
            }

            String status = userDao.getStatus(userIdx);
            if (status.equals("INACTIVE")) {
                throw new BaseException(INACTIVE_USER);
            }
            else if (status.equals("BLACK")) {
                throw new BaseException(BLACK_USER);
            }
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetUserBadges getUserBadges(int userIdx) throws BaseException {
        try {
            GetUserBadges getUserBadges = userDao.getUserBadges(userIdx);
            return getUserBadges;
          } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해당 userIdx를 갖는 "이번달" Goal 정보 조회
    public GetUserGoalRes getUserGoal(int userIdx) throws BaseException{
        try{
            GetUserGoalRes getUserGoalRes = userDao.getUserGoal(userIdx);
            return getUserGoalRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 "다음달" Goal 정보 조회
    public GetUserGoalRes getUserGoalNext(int userIdx) throws BaseException{
        try{
            GetUserGoalRes getUserGoalRes = userDao.getUserGoalNext(userIdx);
            return getUserGoalRes;
        } catch (Exception exception){
          throw new BaseException(DATABASE_ERROR);
        }
    }
  
    // 해당 userIdx를 갖는 User의 세부 정보 조회
    public GetUserInfoRes getUserInfo(int userIdx) throws BaseException {
        try {
            // 1. user 달성률 정보
            UserInfoAchieve userInfoAchieve = userDao.getUserInfoAchieve(userIdx);
            // 2. user 이번달 목표 정보
            GetUserGoalRes getUserGoalRes = userDao.getUserGoal(userIdx);
            // 3. user 통계 정보
            UserInfoStat userInfoStat = userDao.getUserInfoStat(userIdx);

            // 4. 1+2+3
            return new GetUserInfoRes(userInfoAchieve,getUserGoalRes,userInfoStat);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
