package com.umc.footprint.src.users;

import com.umc.footprint.config.BaseException;
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

    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }


    public List<GetUserTodayRes> getUserToday(int userIdx) throws BaseException {

        List<GetUserTodayRes> userTodayRes = userDao.getUserToday(userIdx);

        return userTodayRes;
    }


    // 해당 userIdx를 갖는 User의 정보 조회
    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 User의 세부 정보 조회
    public GetUserInfoRes getUserInfo(int userIdx) throws BaseException {
        try {
            // 1.
            UserInfoAchieve userInfoAchieve = userDao.getUserInfoAchieve(userIdx);

            System.out.println(userInfoAchieve.getTodayGoalRate());
            System.out.println(userInfoAchieve.getMonthGoalRate());
            System.out.println(userInfoAchieve.getUserWalkCount());

            // 2.
            GetUserGoalRes getUserGoalRes = userDao.getUserGoal(userIdx);

            // 3.
            userDao.getUserInfoStat(userIdx);

            // 4. 1+2+3
            GetUserInfoRes getUserInfoRes = new GetUserInfoRes();

            return null;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
